package com.tm220.manager.ui.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.squareup.picasso.Picasso;
import com.tm220.manager.BuildConfig;
import com.tm220.manager.R;
import com.tm220.manager.di.DaggerService;
import com.tm220.manager.di.components.AppComponent;
import com.tm220.manager.di.modules.PicassoCacheModule;
import com.tm220.manager.di.modules.RootModule;
import com.tm220.manager.di.scopes.DaggerScope;
import com.tm220.manager.flow.TreeKeyDispatcher;
import com.tm220.manager.mvp.presenters.RootPresenter;
import com.tm220.manager.mvp.views.IRootView;
import com.tm220.manager.mvp.views.IView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import flow.Flow;
import mortar.MortarScope;
import mortar.bundler.BundleServiceRunner;

public class RootActivity extends AppCompatActivity implements IRootView{
    public static final String TAG = "RootActivity";

    @Inject
    RootPresenter mRootPresenter;

    protected static ProgressDialog mProgressDialog;


    @BindView(R.id.root_frame)
    FrameLayout mRootFrame;



    @Override
    protected void attachBaseContext(Context newBase) {
        newBase = Flow.configure(newBase, this)
//                    .defaultKey(new PhotoListScreen())
                .dispatcher(new TreeKeyDispatcher(this))
                .install();
        super.attachBaseContext(newBase);
    }

    @Override
    public Object getSystemService(String name) {
        MortarScope mRootActivityScope = MortarScope.findChild(getApplicationContext(), RootActivity.class.getName());
        return mRootActivityScope.hasService(name) ? mRootActivityScope.getService(name) : super.getSystemService(name);
    }

    private void initBottomMenu(){
//            mBottomBar.setOnNavigationItemSelectedListener(this::navigationItemSelected);
    }

    private void initToolbar() {
//            setSupportActionBar(mToolbar);
//            mActionBar = getSupportActionBar();
    }


    //region ===================== Life cycle =========================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_root);
        BundleServiceRunner.getBundleServiceRunner(this).onCreate(savedInstanceState);
        ButterKnife.bind(this);

        RootComponent rootComponent = DaggerService.getDaggerComponent(this);
        rootComponent.inject(this);


        initBottomMenu();

        initToolbar();

        mRootPresenter.takeView(this);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        BundleServiceRunner.getBundleServiceRunner(this).onSaveInstanceState(outState);
    }


    @Override
    protected void onDestroy() {
        mRootPresenter.dropView(this);
        super.onDestroy();
    }
    //endregion ================== Life cycle =========================


    //region ===================== IView =========================

    @Override
    public boolean viewOnBackPressed() {
        return false;
    }

    //endregion ================== IView =========================

    //region ===================== IRootView =========================

    @Override
    public void showMessage(String message) {
//            Snackbar.make(mRootFrame, message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void showError(Throwable e) {
        if (BuildConfig.DEBUG) {
            showMessage(e.getMessage());
            e.printStackTrace();
        } else {
            showMessage(getString(R.string.error_message));
            //TODO: send error stacktrace to crash analytics
        }
    }

    @Override
    public void showLoad() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setCancelable(false);
            mProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            mProgressDialog.show();
            mProgressDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            mProgressDialog.setContentView(R.layout.progress_root);
        } else {
            mProgressDialog.show();
            mProgressDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            mProgressDialog.setContentView(R.layout.progress_root);
        }
    }

    @Override
    public void hideLoad() {
        if (mProgressDialog != null) {
            if (mProgressDialog.isShowing()) {
                mProgressDialog.hide();
            }
        }
    }

    @Nullable
    @Override
    public IView getCurrentScreen() {
        return (IView) mRootFrame.getChildAt(0);
    }

    //endregion ================== IRootView =========================


    //region ===================== IActionBarView =========================
//
//
//        @Override
//        public void setActionBarTitle(CharSequence title) {
//            mActionBar.setTitle(title);
//        }
//
//        @Override
//        public void setVisible(boolean visible) {
//            if (visible){
//                mActionBar.show();
//            }else{
//                mActionBar.hide();
//            }
//
//
//        }
//
//        @Override
//        public void setBackArrow(boolean enabled) {
//            mActionBar.setDisplayHomeAsUpEnabled(enabled);
//        }
//
//        @Override
//        public void setMenuItem(List<MenuItemHolder> items) {
//            mActionBarMenuItem = items;
//            supportInvalidateOptionsMenu();
//        }
//
//        private void addMenuItem(Menu menu, MenuItemHolder menuItem){
//            MenuItem item;
//            if (menuItem.hasSubMenu()) {
//                SubMenu subMenu = menu.addSubMenu(menuItem.getItemTitle());
//                item = subMenu.getItem();
//
//                for (MenuItemHolder subItem: menuItem.getSubMenu()){
//                    addMenuItem(subMenu, subItem);
//                }
//            } else{
//                item = menu.add(menuItem.getItemTitle());
//            }
//            item.setShowAsActionFlags(menuItem.isAction()? MenuItem.SHOW_AS_ACTION_ALWAYS : MenuItem.SHOW_AS_ACTION_NEVER)
//                    .setIcon(menuItem.getIconResId())
//                    .setOnMenuItemClickListener(menuItem.getListener());
//        }
//
//        @Override
//        public boolean onPrepareOptionsMenu(Menu menu) {
//            if(mActionBarMenuItem != null && !mActionBarMenuItem.isEmpty()) {
//                menu.clear();
//                for(MenuItemHolder menuItem: mActionBarMenuItem) {
//                    addMenuItem(menu, menuItem);
//                }
//            } else {
//                menu.clear();
//            }
//
//
//            return super.onPrepareOptionsMenu(menu);
//        }
//
//        @Override
//        public void setTabLayout(ViewPager tabs) {
//            View view = mAppBarLayout.getChildAt(1);
//            TabLayout tabView;
//            if(view == null) {
//                tabView = new TabLayout(this); //создаем TabLayout
//                tabView.setupWithViewPager(tabs); //связываем его с ViewPager
//                tabView.setTabGravity(TabLayout.GRAVITY_FILL);
//                mAppBarLayout.addView(tabView); //добавляем табы в Appbar
//                tabs.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabView)); // регистрируем обработчик переключения по табам для ViewPager
//            } else {
//                tabView = (TabLayout) view;
//                tabView.setupWithViewPager(tabs); //связываем его с ViewPager
//                tabs.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabView));
//            }
//        }
//
//        @Override
//        public void removeTabLayout() {
//            View tabView = mAppBarLayout.getChildAt(1);
//            if(tabView != null && tabView instanceof TabLayout) { //проверяем если у аппбара есть дочерняя View являющаяся TabLayout
//                mAppBarLayout.removeView(tabView); //то удаляем ее
//            }
//        }
//
    //endregion ================== IActionBarView =========================


    //region ======================== DI =======================================

    @dagger.Component(dependencies = AppComponent.class, modules = {RootModule.class, PicassoCacheModule.class})
    @DaggerScope(RootActivity.class)
    public interface RootComponent {
        void inject(RootActivity activity);

        void inject(SplashActivity activity);

        void inject(RootPresenter presenter);

        //AccountModel getAccountModel();

        RootPresenter getRootPresenter();

        Picasso getPicasso();
    }
    //endregion ======================== DI =======================================

//        //region ===================== Events =========================
//        public boolean navigationItemSelected(@NonNull MenuItem item) {
//            Object key = null;
//
//            switch (item.getItemId()) {
//                case R.id.bottomBarMainScreen:
//                    key = new PhotoListScreen();
//                    break;
//                case R.id.bottomBarProfile:
//                    // TODO: 08.06.2017 Проверить залогиненность иначе открыть auth_screen
//                    key = new ProfileScreen();
//                    break;
//                case R.id.bottomBarUpload:
//                    // TODO: 08.06.2017 Проверить залогиненность иначе открыть auth_screen
//                    key = new PhotoEditorScreen();
//                    break;
//            }
//
//            if (key != null) {
//                Flow.get(this).set(key);
//            }
//            return true;
//        }

    @Override
    public void onBackPressed() {
        if (getCurrentScreen() != null && !getCurrentScreen().viewOnBackPressed() && !Flow.get(this).goBack()) {
            super.onBackPressed();
        }
    }

    //endregion ================== Events =========================
}


package com.sokolua.manager.ui.activities;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.sokolua.manager.BuildConfig;
import com.sokolua.manager.R;
import com.sokolua.manager.data.managers.ConstantManager;
import com.sokolua.manager.data.managers.DataManager;
import com.sokolua.manager.di.DaggerService;
import com.sokolua.manager.di.components.AppComponent;
import com.sokolua.manager.di.modules.RootModule;
import com.sokolua.manager.di.scopes.DaggerScope;
import com.sokolua.manager.flow.TreeKeyDispatcher;
import com.sokolua.manager.mvp.presenters.MenuItemHolder;
import com.sokolua.manager.mvp.presenters.RootPresenter;
import com.sokolua.manager.mvp.views.IActionBarView;
import com.sokolua.manager.mvp.views.IRootView;
import com.sokolua.manager.mvp.views.IView;
import com.sokolua.manager.ui.screens.auth.AuthScreen;
import com.sokolua.manager.ui.screens.customer_list.CustomerListScreen;
import com.sokolua.manager.ui.screens.goods.GoodsScreen;
import com.sokolua.manager.ui.screens.main.MainScreen;
import com.sokolua.manager.ui.screens.order_list.OrderListScreen;
import com.sokolua.manager.ui.screens.routes.RoutesScreen;
import com.sokolua.manager.utils.App;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import flow.Direction;
import flow.Flow;
import mortar.MortarScope;
import mortar.bundler.BundleServiceRunner;

public class RootActivity extends AppCompatActivity implements IRootView, IActionBarView {
    public static final String TAG = "RootActivity";

    @Inject
    RootPresenter mRootPresenter;

    protected static ProgressDialog mProgressDialog;


    @BindView(R.id.root_frame)
    FrameLayout mRootFrame;
    @BindView(R.id.bottom_bar)
    BottomNavigationView mBottomBar;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.appbar)
    AppBarLayout mAppBarLayout;


    private ActionBar mActionBar;
    private List<MenuItemHolder> mActionBarMenuItem;
    private Menu mOptionsMenu;


    @Override
    protected void attachBaseContext(Context newBase) {
        newBase = Flow.configure(newBase, this)
                .defaultKey((DataManager.getInstance().isUserAuth() ? new MainScreen() : new AuthScreen()))
                .dispatcher(new TreeKeyDispatcher(this))
                .install();
        super.attachBaseContext(newBase);
    }

    @Override
    public Object getSystemService(@NonNull String name) {
        MortarScope mRootActivityScope = MortarScope.findChild(getApplicationContext(), RootActivity.class.getName());
        return mRootActivityScope.hasService(name) ? mRootActivityScope.getService(name) : super.getSystemService(name);
    }

    private void initBottomMenu() {
        mBottomBar.setOnNavigationItemSelectedListener(this::navigationItemSelected);
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(v -> onBackPressed());
        mActionBar = getSupportActionBar();
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
    protected void onSaveInstanceState(@NonNull Bundle outState) {
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
        Snackbar.make(mRootFrame, message, Snackbar.LENGTH_LONG).show();
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
            if (!this.isFinishing() && mProgressDialog.getWindow() != null) {
                mProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                mProgressDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            }
        }
        if (!this.isFinishing() && mProgressDialog.getWindow() != null) {
            mProgressDialog.show();
            mProgressDialog.setContentView(R.layout.progress_root);
            ProgressBar mProgressBar = mProgressDialog.findViewById(R.id.progress_horizontal);
            mProgressBar.setVisibility(View.GONE);
        }
    }


    @Override
    public void showLoad(int progressBarMax) {
        if (this.getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED)) {
            showLoad();
            ProgressBar mProgressBar = mProgressDialog.findViewById(R.id.progress_horizontal);
            mProgressBar.setMax(progressBarMax);
            mProgressBar.setProgress(0);
            mProgressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void updateProgress(int currentProgress) {
        if (mProgressDialog != null && mProgressDialog.isShowing()){
            ProgressBar mProgressBar = mProgressDialog.findViewById(R.id.progress_horizontal);
            if (mProgressBar.getVisibility() == View.VISIBLE){
                mProgressBar.setProgress(currentProgress);
            }
        }
    }

    @Override
    public void hideLoad() {
        if (this.getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED) && mProgressDialog != null && mProgressDialog.isShowing()) {
            ProgressBar mProgressBar = mProgressDialog.findViewById(R.id.progress_horizontal);
            if (mProgressBar.getVisibility() == View.VISIBLE){
                mProgressBar.setVisibility(View.GONE);
            }
           mProgressDialog.dismiss();
        }
    }

    @Nullable
    @Override
    public IView getCurrentScreen() {
        return (IView) mRootFrame.getChildAt(0);
    }


    @Override
    public void setBottomBarVisibility(boolean state) {
        if (state){
            mBottomBar.setTranslationY(0);
            mBottomBar.setVisibility(View.VISIBLE);
        }else{
            mBottomBar.setTranslationY(mBottomBar.getHeight());
            mBottomBar.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean getBottomBarVisibility() {
        return mBottomBar.getVisibility() == View.VISIBLE;
    }


    //endregion ================== IRootView =========================


    //region ===================== IActionBarView =========================


    @Override
    public void setActionBarTitle(CharSequence title) {
        mActionBar.setTitle(title);
    }


    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            mActionBar.show();
        } else {
            mActionBar.hide();
        }


    }

    @Override
    public void setBackArrow(boolean enabled) {
        mActionBar.setDisplayHomeAsUpEnabled(enabled);
    }

    @Override
    public void setMenuItem(List<MenuItemHolder> items) {
        mActionBarMenuItem = items;
        supportInvalidateOptionsMenu();
    }


    private void addMenuItem(Menu menu, MenuItemHolder menuItem) {
        MenuItem item;
        int mId = generateMenuItemId();
        if (menuItem.getItemType() == ConstantManager.MENU_ITEM_TYPE_SEARCH){
            SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            if (searchManager != null) {
                getMenuInflater().inflate(R.menu.search_menu, menu);
                item = menu.findItem(R.id.search);
                item.setTitle(menuItem.getItemTitle());
                SearchView searchView = (SearchView) item.getActionView();
                searchView.setQueryHint(App.getStringRes(R.string.search_hint));
                searchView.setOnQueryTextListener(menuItem.getQueryListener());
                searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            }
            return;
        }

        if (menuItem.hasSubMenu()) {
            SubMenu subMenu = menu.addSubMenu(Menu.NONE, mId, Menu.NONE , menuItem.getItemTitle());
            item = subMenu.getItem();

            for (MenuItemHolder subItem : menuItem.getSubMenu()) {
                addMenuItem(subMenu, subItem);
            }
        } else {
            if (menuItem.isCheckable()) {
                item = menu.add(menuItem.getGroupId(), mId, Menu.NONE, menuItem.getItemTitle());
                item.setCheckable(menuItem.isCheckable());
                item.setChecked(menuItem.isChecked());
                menu.setGroupCheckable(menuItem.getGroupId(), true, true);
            }else{
                item = menu.add(Menu.NONE, mId, Menu.NONE, menuItem.getItemTitle());
            }
        }
        int flags;
        switch (menuItem.getItemType()){
            case ConstantManager.MENU_ITEM_TYPE_ACTION:
                flags = MenuItem.SHOW_AS_ACTION_IF_ROOM;
                break;
             default:
                 flags = MenuItem.SHOW_AS_ACTION_NEVER;
        }
        item.setShowAsActionFlags(flags)
            .setOnMenuItemClickListener(menuItem.getListener());

        if (menuItem.getIconResId() != 0){
            item.setIcon(menuItem.getIconResId());
            this.tintMenuIcon(this, item, R.color.menu_item_icon_color);
        }

    }

    private int generateMenuItemId(){
        while (mOptionsMenu!=null) {
            int rnd = (int) (Math.random()*Integer.MAX_VALUE);
            if (mOptionsMenu.findItem(rnd) == null){
                return rnd;
            }
        }
        return 0;
    }

    @Nullable
    private MenuItem checkMenu(MenuItem currentItem, Menu menu, int itemId){
        for (int i=0;i<menu.size();i++){
            MenuItem item = menu.getItem(i);
            if (item.getItemId() == itemId){
                return currentItem;
            }
            if (item.hasSubMenu()) {
                if (checkMenu(item, item.getSubMenu(), itemId) != null) {
                    return item;
                }
            }
        }
        return null;
    }

    @Nullable
    public MenuItem getMainMenuItemParent(int itemId) {
        if (mOptionsMenu != null) {
            return checkMenu(null, mOptionsMenu, itemId);
        }
        return null;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mActionBarMenuItem != null && !mActionBarMenuItem.isEmpty()) {
            menu.clear();
            for (MenuItemHolder menuItem : mActionBarMenuItem) {
                addMenuItem(menu, menuItem);
            }
        } else {
            menu.clear();
        }

        mOptionsMenu = menu;

        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public void setTabLayout(ViewPager tabs) {
        View view = mAppBarLayout.getChildAt(1);
        TabLayout tabView;
        if (view == null) {
            tabView = new TabLayout(this); //создаем TabLayout
            tabView.setupWithViewPager(tabs); //связываем его с ViewPager
            tabView.setTabGravity(TabLayout.GRAVITY_FILL);
            mAppBarLayout.addView(tabView); //добавляем табы в Appbar
            tabs.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabView)); // регистрируем обработчик переключения по табам для ViewPager
        } else {
            tabView = (TabLayout) view;
            tabView.setupWithViewPager(tabs); //связываем его с ViewPager
            tabs.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabView));
        }
    }

    @Override
    public void removeTabLayout() {
        View tabView = mAppBarLayout.getChildAt(1);
        if (tabView instanceof TabLayout) { //проверяем если у аппбара есть дочерняя View являющаяся TabLayout
            mAppBarLayout.removeView(tabView); //то удаляем ее
        }
    }


    //endregion ================== IActionBarView =========================


    //region ======================== DI =======================================

    @dagger.Component(dependencies = AppComponent.class, modules = {RootModule.class})
    @DaggerScope(RootActivity.class)
    public interface RootComponent {
        void inject(RootActivity activity);
        void inject(StartActivity activity);
        void inject(RootPresenter presenter);

        RootPresenter getRootPresenter();

    }
    //endregion ======================== DI =======================================

    //region ===================== Events =========================
    public boolean navigationItemSelected(@NonNull MenuItem item) {
        Object key = null;

        switch (item.getItemId()) {
            case R.id.bottomBarMainScreen:
                key = new MainScreen();
                break;
            case R.id.bottomBarCustomers:
                key = new CustomerListScreen();
                break;
            case R.id.bottomBarOrders:
                key = new OrderListScreen();
                break;
            case R.id.bottomBarRoute:
                key = new RoutesScreen();
                break;
            case R.id.bottomBarGoods:
                key = new GoodsScreen();
                break;
        }

        if (key != null) {
            Flow.get(this).replaceHistory(key, Direction.REPLACE);
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (getCurrentScreen() == null  || getCurrentScreen().viewOnBackPressed()){
            return;
        }
        if (Flow.get(this).getHistory().size()<=1){
            AlertDialog.Builder alert = new AlertDialog.Builder(this)
                    .setTitle(App.getStringRes(R.string.question_quit))
                    .setCancelable(false)
                    .setPositiveButton(App.getStringRes(R.string.button_yes_text), (dialog, whichButton) -> {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            this.finishAndRemoveTask();
                        }else{
                            this.finishAffinity();
                        }
                    })
                    .setNegativeButton(App.getStringRes(R.string.button_no_text), (dialog, whichButton) -> {});
            alert.show();
        }else {
            if (!Flow.get(this).goBack()) {
                super.onBackPressed();
            }
        }

    }

    //endregion ================== Events =========================


    public void selectNavigationMenu(int menuItemId) {
        mBottomBar.setSelectedItemId(menuItemId);
    }

}


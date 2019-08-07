package com.sokolua.manager.ui.activities;

import android.content.Intent;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.sokolua.manager.BuildConfig;
import com.sokolua.manager.R;
import com.sokolua.manager.di.DaggerService;
import com.sokolua.manager.mvp.presenters.RootPresenter;
import com.sokolua.manager.mvp.views.IRootView;
import com.sokolua.manager.mvp.views.IView;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import mortar.MortarScope;

public class SplashActivity extends AppCompatActivity implements IRootView {

    @BindView(R.id.root_frame)
    FrameLayout mRootFrame;

    @Inject
    RootPresenter mRootPresenter;

    private Disposable mLoader;


    @Override
    public Object getSystemService(@NonNull String name) {
        MortarScope mRootActivityScope = MortarScope.findChild(getApplicationContext(), RootActivity.class.getName());
        return mRootActivityScope.hasService(name) ? mRootActivityScope.getService(name) : super.getSystemService(name);
    }

//region ==========   Life Cycle ==================


    @Override
    protected void onStop() {
        mRootPresenter.dropView(this);
        super.onStop();
    }

    @Override
    protected void onStart() {
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);

        RootActivity.RootComponent rootComponent = DaggerService.getDaggerComponent(this);
        rootComponent.inject(this);

        mRootPresenter.takeView(this);
        super.onStart();


        mLoader=Observable
                .interval( 1000, TimeUnit.MILLISECONDS )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(o -> {
                    startRootActivity();
                    return true;
                } )
                .subscribe();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    //endregion==========  Life Cycle ==================


//region==========   IRootView    ==================
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
    }

    @Override
    public void showLoad(int progressBarMax) {

    }

    @Override
    public void updateProgress(int currentProgress) {

    }

    @Override
    public void hideLoad() {
    }

    @Override
    public void setBottomBarVisibility(boolean state) {

    }

    @Override
    public boolean getBottomBarVisibility() {
        return false;
    }

    @Nullable
    @Override
    public IView getCurrentScreen() {
        return (IView) mRootFrame.getChildAt(0);
    }


//endregion==========  IRootView ==================


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean viewOnBackPressed() {
        return false;
    }

    public void startRootActivity() {
        mLoader.dispose();
        Intent intent = new Intent(this, RootActivity.class);
        startActivity(intent);
        finish();
    }

}

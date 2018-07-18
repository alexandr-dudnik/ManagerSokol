package com.sokolua.manager.ui.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.FrameLayout;

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
import mortar.MortarScope;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.sokolua.manager.ui.activities.RootActivity.mProgressDialog;

public class SplashActivity extends AppCompatActivity implements IRootView {

    @BindView(R.id.root_frame)
    FrameLayout mRootFrame;

    @Inject
    RootPresenter mRootPresenter;


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

        Observable
                .timer( 3000, TimeUnit.MILLISECONDS )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                //        .flatMap(aBoolean -> DataManager.getInstance().getPhotoCardsObsFromNetwork())
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
    public void hideLoad() {
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
        Intent intent = new Intent(this, RootActivity.class);
        startActivity(intent);
        finish();
    }

}

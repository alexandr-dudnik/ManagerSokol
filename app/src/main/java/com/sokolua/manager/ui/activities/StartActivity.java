package com.sokolua.manager.ui.activities;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.sokolua.manager.BuildConfig;
import com.sokolua.manager.R;
import com.sokolua.manager.data.managers.ConstantManager;
import com.sokolua.manager.di.DaggerService;
import com.sokolua.manager.mvp.presenters.RootPresenter;
import com.sokolua.manager.mvp.views.IRootView;
import com.sokolua.manager.mvp.views.IView;
import com.sokolua.manager.utils.AppConfig;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import java.io.Reader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import mortar.MortarScope;

public class StartActivity extends AppCompatActivity implements IRootView {

    @BindView(R.id.root_frame)
    FrameLayout mRootFrame;
    @BindView(R.id.logo_bird)
    ImageView mLogo;

    @Inject
    RootPresenter mRootPresenter;

    @Inject
    FirebaseRemoteConfig mFirebaseRemoteConfig;

    private ValueAnimator logoAnimator;


    @Override
    public Object getSystemService(@NonNull String name) {
        MortarScope mRootActivityScope = MortarScope.findChild(getApplicationContext(), RootActivity.class.getName());
        return mRootActivityScope.hasService(name) ? mRootActivityScope.getService(name) : super.getSystemService(name);
    }

//region ==========   Life Cycle ==================


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        logoAnimator = ValueAnimator.ofFloat(1f, 0.8f);
        logoAnimator.setDuration(1000L);
        logoAnimator.setInterpolator(new LinearInterpolator());
        logoAnimator.setRepeatMode(ValueAnimator.REVERSE);
        logoAnimator.setRepeatCount(ValueAnimator.INFINITE);
        logoAnimator.addUpdateListener(
                animation -> {
                    float scale;
                    if (animation.isRunning()) {
                        scale = (float)animation.getAnimatedValue();
                    } else {
                        scale = 1.0f;
                    }
                    mLogo.setScaleX(scale);
                    mLogo.setScaleY(scale);
                }
        );
    }

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

        mFirebaseRemoteConfig
                .fetchAndActivate()
                .addOnCompleteListener(
                        task -> {
                            if (task.isSuccessful()) {
                                try {
                                    AppConfig.API_URL = mFirebaseRemoteConfig.getString(ConstantManager.FIREBASE_API_URL_TEMPLATE_KEY);
                                    Moshi moshi = new Moshi.Builder().build();
                                    Type arrayType = Types.newParameterizedType(List.class, String.class);
                                    JsonAdapter<List<String>> adapter = moshi.adapter(arrayType);
                                    AppConfig.API_SERVERS = adapter.fromJson(mFirebaseRemoteConfig.getString(ConstantManager.FIREBASE_API_SERVERS_LIST_KEY));
                                    hideLoad();
                                    startRootActivity();
                                } catch (Exception ex) {
                                    showError(ex);
                                }
                            }
                        }
                );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        showLoad();
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
        logoAnimator.start();
    }

    @Override
    public void showLoad(int progressBarMax) {
        logoAnimator.end();
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
        Intent intent = new Intent(this, RootActivity.class);
        startActivity(intent);
        finish();
    }

}

package com.sokolua.manager.ui.activities;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.sokolua.manager.BuildConfig;
import com.sokolua.manager.R;
import com.sokolua.manager.data.managers.ConstantManager;
import com.sokolua.manager.data.managers.DataManager;
import com.sokolua.manager.di.DaggerService;
import com.sokolua.manager.mvp.presenters.RootPresenter;
import com.sokolua.manager.mvp.views.IRootView;
import com.sokolua.manager.mvp.views.IView;
import com.sokolua.manager.utils.AppConfig;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import java.lang.reflect.Type;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
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
    private OnCompleteListener<Boolean> mFirebaseListener;
    private final DataManager mDataManager = DataManager.getInstance();


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

    private void fetchFirebaseConfig() {
        showLoad();
        mFirebaseRemoteConfig
                .fetchAndActivate()
                .addOnCompleteListener(mFirebaseListener);
    }

    @Override
    protected void onStart() {
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);

        RootActivity.RootComponent rootComponent = DaggerService.getDaggerComponent(this);
        rootComponent.inject(this);

        mRootPresenter.takeView(this);
        super.onStart();

        mFirebaseListener = task -> {
            if (task.isSuccessful()) {
                try {
                    AppConfig.API_URL = mFirebaseRemoteConfig.getString(ConstantManager.FIREBASE_API_URL_TEMPLATE_KEY);
                    Moshi moshi = new Moshi.Builder().build();
                    Type arrayType = Types.newParameterizedType(List.class, String.class);
                    JsonAdapter<List<String>> adapter = moshi.adapter(arrayType);
                    AppConfig.API_SERVERS = adapter.fromJson(mFirebaseRemoteConfig.getString(ConstantManager.FIREBASE_API_SERVERS_LIST_KEY));
                    if(!AppConfig.API_URL.isEmpty()) {
                        mDataManager.storeApiUrl(AppConfig.API_URL);
                    } else {
                        AppConfig.API_URL = mDataManager.getApiUrl();
                    }
                    if (!AppConfig.API_SERVERS.isEmpty()) {
                        mDataManager.storeApiServers(AppConfig.API_SERVERS);
                    } else {
                        AppConfig.API_SERVERS = mDataManager.getApiServers();
                    }
                } catch (Exception ex) {
                    if (BuildConfig.DEBUG) {
                        ex.printStackTrace();
                    }
                    AppConfig.API_URL = mDataManager.getApiUrl();
                    AppConfig.API_SERVERS = mDataManager.getApiServers();
                }
            } else {
                AppConfig.API_URL = mDataManager.getApiUrl();
                AppConfig.API_SERVERS = mDataManager.getApiServers();
            }

            hideLoad();
            if (!AppConfig.API_URL.isEmpty() && !AppConfig.API_SERVERS.isEmpty()) {
                mDataManager.updateServerAddress(AppConfig.getDefaultServer());
                startRootActivity();
            } else {
                showMessage(getString(R.string.configuration_error), R.string.retry, view -> fetchFirebaseConfig());
            }
        };

        fetchFirebaseConfig();
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
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showMessage(String message, @StringRes int button,  View.OnClickListener callback) {
        Snackbar.make(this, mRootFrame, message, Snackbar.LENGTH_LONG)
                .setAction(button, callback)
                .show();
    }

    @Override
    public void showError(Throwable e) {
        showMessage(e.getMessage());
        if (BuildConfig.DEBUG) {
            e.printStackTrace();
        }
    }

    @Override
    public void showLoad() {
        logoAnimator.start();
    }

    @Override
    public void showLoad(int progressBarMax) {
    }

    @Override
    public void updateProgress(int currentProgress) {

    }

    @Override
    public void hideLoad() {
        logoAnimator.end();
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

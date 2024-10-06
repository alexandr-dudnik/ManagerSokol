package com.sokolua.manager.mvp.presenters;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sokolua.manager.mvp.models.AbstractModel;
import com.sokolua.manager.mvp.views.AbstractView;
import com.sokolua.manager.mvp.views.IRootView;
import com.sokolua.manager.ui.activities.RootActivity;
import com.sokolua.manager.ui.activities.StartActivity;
import com.sokolua.manager.utils.App;
import com.sokolua.manager.utils.AppConfig;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import mortar.ViewPresenter;


public abstract class AbstractPresenter<V extends AbstractView, M extends AbstractModel>  extends ViewPresenter<V> {
    @Inject
    protected M mModel;

    @Inject
    protected RootPresenter mRootPresenter;

    protected CompositeDisposable mCompSubs;


    @Override
    protected void onLoad(Bundle savedInstanceState) {
        super.onLoad(savedInstanceState);
        mCompSubs = new CompositeDisposable();
        if (AppConfig.API_URL.isEmpty()) {
            Intent intent = new Intent(App.getContext(), StartActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            App.getContext().startActivity(intent);
            RootActivity activity = ((RootActivity)getRootView());
            if (activity != null) {
                activity.forceFinish();
            }
            return;
        }
        initActionBar();
    }

    @Override
    public void dropView(V view) {
        if (mCompSubs != null && mCompSubs.size()>0){
            mCompSubs.clear();
        }
        super.dropView(view);
    }


    protected abstract void initActionBar();


    @Nullable
    protected IRootView getRootView(){
        return mRootPresenter.getRootView();
    }

    protected abstract class ViewSubscriber<T> extends DisposableObserver<T> {
        @Override
        public abstract void onNext(T t);

        @Override
        public void onError(Throwable e) {
            if (getRootView()!=null) {
                getRootView().showError(e);
            }
        }
    }

    protected <T> Disposable subscribe(@NonNull Observable<T> observable, @NonNull ViewSubscriber<T> subscriber){
        return observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(subscriber);
    }

}

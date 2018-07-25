package com.sokolua.manager.mvp.presenters;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.sokolua.manager.mvp.models.AbstractModel;
import com.sokolua.manager.mvp.views.AbstractView;
import com.sokolua.manager.mvp.views.IRootView;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import mortar.MortarScope;
import mortar.ViewPresenter;



public abstract class AbstractPresenter<V extends AbstractView, M extends AbstractModel>  extends ViewPresenter<V> {
    @Inject
    protected M mModel;

    @Inject
    protected RootPresenter mRootPresenter;

    protected CompositeDisposable mCompSubs;

//    @Override
//    protected void onEnterScope(MortarScope scope) {
//        super.onEnterScope(scope);
//        initDagger(scope);
//    }

    @Override
    protected void onLoad(Bundle savedInstanceState) {
        super.onLoad(savedInstanceState);
        mCompSubs = new CompositeDisposable();
        initActionBar();
    }

    @Override
    public void dropView(V view) {
        if (mCompSubs.size()>0){
            mCompSubs.clear();
        }
        super.dropView(view);
    }

//    protected abstract void initDagger(MortarScope scope);

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

    protected <T> Disposable subscribe(Observable<T> observable, ViewSubscriber<T> subscriber){
        return observable
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(subscriber);
    }

}

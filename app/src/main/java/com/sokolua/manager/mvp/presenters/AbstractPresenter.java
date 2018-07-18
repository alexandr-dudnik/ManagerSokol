package com.sokolua.manager.mvp.presenters;

import android.os.Bundle;
import android.support.annotation.Nullable;


import com.sokolua.manager.mvp.models.AbstractModel;
import com.sokolua.manager.mvp.views.AbstractView;
import com.sokolua.manager.mvp.views.IRootView;

import javax.inject.Inject;

import mortar.ViewPresenter;


public abstract class AbstractPresenter<V extends AbstractView, M extends AbstractModel>  extends ViewPresenter<V> {
    @Inject
    protected M mModel;

    @Inject
    protected RootPresenter mRootPresenter;

//    protected CompositeSubscription mCompSubs;

//    @Override
//    protected void onEnterScope(MortarScope scope) {
//        super.onEnterScope(scope);
//        initDagger(scope);
//    }
//
//    @Override
//    protected void onLoad(Bundle savedInstanceState) {
//        super.onLoad(savedInstanceState);
//        mCompSubs = new CompositeSubscription();
//        initActionBar();
//    }

//    @Override
//    public void dropView(V view) {
////        if (mCompSubs.hasSubscriptions()){
////            mCompSubs.unsubscribe();
////        }
//        super.dropView(view);
//    }

//    protected abstract void initDagger(MortarScope scope);

    protected abstract void initActionBar();

    @Nullable
    protected IRootView getRootView(){
        return mRootPresenter.getRootView();
    }

//    protected abstract class ViewSubscriber<T> extends Subscriber<T> {
//        @Override
//        public abstract void onNext(T t);
//
//        @Override
//        public void onError(Throwable e) {
//            if (getRootView()!=null) {
//                getRootView().showError(e);
//            }
//        }
//
////        @Override
////        public void onCompleted() {
////
////        }
//    }

//    protected <T> Subscription subscribe(Observable<T> observable, ViewSubscriber<T> subscriber){
//        return observable
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(subscriber);
//    }

}

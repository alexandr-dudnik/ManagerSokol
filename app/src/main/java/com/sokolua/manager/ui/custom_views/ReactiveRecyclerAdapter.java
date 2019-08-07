package com.sokolua.manager.ui.custom_views;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sokolua.manager.data.managers.ConstantManager;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ReactiveRecyclerAdapter<T> extends RecyclerView.Adapter<ReactiveRecyclerAdapter.ReactiveViewHolder<T>> {
    private Observable<List<T>> observable;
    private final ReactiveViewHolderFactory<T> viewHolderFactory;
    private List<T> currentList;
    private Disposable listSub;
    private boolean useBlankItemOnEmptyList;

    public ReactiveRecyclerAdapter(Observable<List<T>> observable, ReactiveViewHolderFactory<T> viewHolderFactory, boolean useBlankItemOnEmptyList) {
        this.viewHolderFactory = viewHolderFactory;
        this.currentList = Collections.emptyList();
        this.useBlankItemOnEmptyList = useBlankItemOnEmptyList;
        refreshList(observable);
    }

    public void refreshList(Observable<List<T>> observable){
        if (listSub!=null && !listSub.isDisposed()){
            listSub.dispose();
        }
        this.observable = observable;
        listSub = this.observable
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(AndroidSchedulers.mainThread())
                .doOnNext(items -> {
                    this.currentList = items;
                    this.notifyDataSetChanged();
                })
                .subscribe();
    }



    @NonNull
    @Override
    public ReactiveViewHolder<T> onCreateViewHolder(@NonNull ViewGroup parent, int pViewType) {
        ReactiveViewHolderFactory.ViewAndHolder<T> viewAndHolder = viewHolderFactory.createViewAndHolder(parent, pViewType);
        return viewAndHolder.viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ReactiveViewHolder<T> holder, int position) {
        if (currentList.size()>0) {
            T item = currentList.get(position);
            holder.setCurrentItem(item);
        }
    }

    @Override
    public int getItemCount() {
        if (currentList.size()==0) {
            return useBlankItemOnEmptyList?1:0;
        } else {
            return currentList.size();
        }
    }

    @Override
    public int getItemViewType(int position) {
        int viewType;
        if (currentList.size()==0){
            viewType = ConstantManager.RECYCLER_VIEW_TYPE_EMPTY;
        }else {
            viewType = ConstantManager.RECYCLER_VIEW_TYPE_ITEM;
            T item = currentList.get(position);
            try {
                Method mm = item.getClass().getMethod("isHeader", (Class<?>[]) null);
                viewType = (boolean) mm.invoke(item, (Object[]) null) ? ConstantManager.RECYCLER_VIEW_TYPE_HEADER : ConstantManager.RECYCLER_VIEW_TYPE_ITEM;
            } catch (Throwable ignore) {
            }
        }
        return viewType;
    }

    public static abstract class ReactiveViewHolder<T> extends RecyclerView.ViewHolder {
        protected T currentItem;

        public ReactiveViewHolder(View itemView) {
            super(itemView);
        }

        public void setCurrentItem(T currentItem) {
            this.currentItem = currentItem;
        }

        public T getCurrentItem() {
            return currentItem;
        }
    }

    public interface ReactiveViewHolderFactory<T> {
        class ViewAndHolder<T> {
            public final View view;
            public final ReactiveViewHolder<T> viewHolder;

            public ViewAndHolder(View view, ReactiveViewHolder<T> viewHolder) {
                this.view = view;
                this.viewHolder = viewHolder;
            }
        }
        ViewAndHolder<T> createViewAndHolder(@NonNull ViewGroup parent, int pViewType);
    }
}
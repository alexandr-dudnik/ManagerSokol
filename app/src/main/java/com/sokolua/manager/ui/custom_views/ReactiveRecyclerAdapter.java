package com.sokolua.manager.ui.custom_views;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.sokolua.manager.data.managers.ConstantManager;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class ReactiveRecyclerAdapter<T> extends RecyclerView.Adapter<ReactiveRecyclerAdapter.ReactiveViewHolder<T>> {
    private final Observable<List<T>> observable;
    private final ReactiveViewHolderFactory<T> viewHolderFactory;
    private List<T> currentList;

    public ReactiveRecyclerAdapter(Observable<List<T>> observable, ReactiveViewHolderFactory<T> viewHolderFactory) {
        this.viewHolderFactory = viewHolderFactory;
        this.currentList = Collections.emptyList();
        this.observable = observable;
        this.observable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(items -> {
                        this.currentList = items;
                        this.notifyDataSetChanged();
                });
    }
//    private PublishSubject<T> mViewClickSubject = PublishSubject.create();

//    public Observable<T> getViewClickedObservable() {
//        return mViewClickSubject;
//    }


    @Override
    public ReactiveViewHolder<T> onCreateViewHolder(@NonNull ViewGroup parent, int pViewType) {
        ReactiveViewHolderFactory.ViewAndHolder<T> viewAndHolder = viewHolderFactory.createViewAndHolder(parent, pViewType);
        ReactiveViewHolder<T> viewHolder = viewAndHolder.viewHolder;

//        RxView.clicks(viewAndHolder.view)
//                .takeUntil(RxView.detaches(parent))
//                .map(aVoid -> viewHolder.getCurrentItem())
//                .subscribe(mViewClickSubject);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ReactiveViewHolder<T> holder, int position) {
        T item = currentList.get(position);
        holder.setCurrentItem(item);
    }

    @Override
    public int getItemCount() {
        return currentList.size();
    }

    @Override
    public int getItemViewType(int position) {
        T item = currentList.get(position);
        int viewType = ConstantManager.RECYCLER_VIEW_TYPE_ITEM;
        try {
            Method mm = item.getClass().getMethod("isHeader", (Class<?>[]) null);
            viewType = (boolean)mm.invoke(item, (Object[]) null)? ConstantManager.RECYCLER_VIEW_TYPE_HEADER:ConstantManager.RECYCLER_VIEW_TYPE_ITEM;
        } catch (Throwable ignore) {
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
        }    }

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
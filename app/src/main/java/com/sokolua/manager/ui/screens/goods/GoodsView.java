package com.sokolua.manager.ui.screens.goods;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.sokolua.manager.R;
import com.sokolua.manager.di.DaggerService;
import com.sokolua.manager.mvp.views.AbstractView;
import com.sokolua.manager.ui.custom_views.ReactiveRecyclerAdapter;
import com.sokolua.manager.utils.App;

import butterknife.BindView;

public class GoodsView extends AbstractView<GoodsScreen.Presenter> {
    @BindView(R.id.groups_grid)
    RecyclerView mGrid;
    @BindView(R.id.item_list)
    RecyclerView mItems;


    public GoodsView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void initDagger(Context context) {
        if (!isInEditMode()) {
            DaggerService.<GoodsScreen.Component>getDaggerComponent(context).inject(this);
        }


    }

    @Override
    public boolean viewOnBackPressed() {
        return mPresenter.goGroupBack() ;
    }


    public void setGroupsAdapter(ReactiveRecyclerAdapter mAdapter) {
        mGrid.setHasFixedSize(true);
        mGrid.setLayoutManager(new GridLayoutManager(getContext(), 3)); //в три колонки
        mGrid.setAdapter(mAdapter);
    }

    public void setItemsAdapter(ReactiveRecyclerAdapter mAdapter) {
        mItems.setHasFixedSize(true);
        mItems.setLayoutManager(new LinearLayoutManager(App.getContext(), LinearLayoutManager.VERTICAL, false));
        mItems.setAdapter(mAdapter);
    }

    public void showGroups() {
        if (mGrid.getAlpha() == 0f) {
            mItems.setAlpha(0f);
            mItems.setVisibility(GONE);
            mGrid.setVisibility(VISIBLE);
            mGrid.animate()
                    .setDuration(500)
                    .alpha(1f)
                    .start();
        }
    }
    public void showItems() {
        if (mItems.getAlpha() == 0f) {
            mGrid.setAlpha(0f);
            mGrid.setVisibility(GONE);
            mItems.setVisibility(VISIBLE);
            mItems.animate()
                    .setDuration(500)
                    .alpha(1f)
                    .start();
        }
    }
}

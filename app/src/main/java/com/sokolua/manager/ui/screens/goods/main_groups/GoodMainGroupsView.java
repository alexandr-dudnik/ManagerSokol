package com.sokolua.manager.ui.screens.goods.main_groups;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.sokolua.manager.R;
import com.sokolua.manager.di.DaggerService;
import com.sokolua.manager.mvp.views.AbstractView;
import com.sokolua.manager.ui.custom_views.ReactiveRecyclerAdapter;

import butterknife.BindView;

public class GoodMainGroupsView extends AbstractView<GoodMainGroupsScreen.Presenter> {
    @BindView(R.id.main_groups_grid)
    RecyclerView mGrid;


    public GoodMainGroupsView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void initDagger(Context context) {
        if (!isInEditMode()) {
            DaggerService.<GoodMainGroupsScreen.Component>getDaggerComponent(context).inject(this);
        }


    }

    @Override
    public boolean viewOnBackPressed() {
        return false ;
    }


    public void setAdapter(ReactiveRecyclerAdapter mAdapter) {
        mGrid.setHasFixedSize(true);
        mGrid.setLayoutManager(new GridLayoutManager(getContext(), 3)); //в три колонки
        mGrid.setAdapter(mAdapter);
    }

}

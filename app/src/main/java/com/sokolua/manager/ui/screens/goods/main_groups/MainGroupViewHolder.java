package com.sokolua.manager.ui.screens.goods.main_groups;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sokolua.manager.R;
import com.sokolua.manager.data.storage.realm.GoodsGroupRealm;
import com.sokolua.manager.di.DaggerService;
import com.sokolua.manager.ui.custom_views.ReactiveRecyclerAdapter;

import javax.inject.Inject;

import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainGroupViewHolder extends ReactiveRecyclerAdapter.ReactiveViewHolder<GoodsGroupRealm> {

    @BindView(R.id.good_group_image)      ImageView mGroupImage;
    @BindView(R.id.good_group_name)       TextView mGroupName;

    @BindDrawable(R.drawable.no_image)    Drawable noImage;

    @Inject
    GoodMainGroupsScreen.Presenter mPresenter;

    public MainGroupViewHolder(View itemView) {
        super(itemView);
        DaggerService.<GoodMainGroupsScreen.Component>getDaggerComponent(itemView.getContext()).inject(this);
        ButterKnife.bind(this, itemView);
    }


    @Override
    public void setCurrentItem(GoodsGroupRealm currentItem) {
        super.setCurrentItem(currentItem);

        mGroupImage.setImageDrawable(noImage);
        mGroupName.setText(currentItem.getName());

    }



}

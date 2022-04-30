package com.sokolua.manager.ui.screens.goods;

import android.graphics.drawable.Drawable;
import android.net.Uri;
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
import butterknife.OnClick;

public class GroupViewHolder extends ReactiveRecyclerAdapter.ReactiveViewHolder<GoodsGroupRealm> {

    @BindView(R.id.good_group_image)      ImageView mGroupImage;
    @BindView(R.id.good_group_name)       TextView mGroupName;

    @BindDrawable(R.drawable.no_image)    Drawable noImage;

    @Inject
    GoodsScreen.Presenter mPresenter;


    public GroupViewHolder(View itemView) {
        super(itemView);
        DaggerService.<GoodsScreen.Component>getDaggerComponent(itemView.getContext()).inject(this);
        ButterKnife.bind(this, itemView);
    }


    @Override
    public void setCurrentItem(GoodsGroupRealm currentItem) {
        super.setCurrentItem(currentItem);

        if (currentItem.isValid() && currentItem.isLoaded()) {
            if (currentItem.getImageURI()==null || currentItem.getImageURI().isEmpty()){
                mGroupImage.setImageDrawable(noImage);
            }else{
                mGroupImage.setImageURI(Uri.parse(currentItem.getImageURI()));
            }
            mGroupName.setText(currentItem.getName());
        }

    }

    @OnClick({R.id.good_group_image, R.id.good_group_name})
    void clickOnGroup(View view){
        mPresenter.mainGroupSelected(currentItem);
    }

}

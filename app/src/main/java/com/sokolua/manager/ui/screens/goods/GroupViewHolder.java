package com.sokolua.manager.ui.screens.goods;

import android.net.Uri;
import android.view.View;

import com.sokolua.manager.R;
import com.sokolua.manager.data.storage.realm.GoodsGroupRealm;
import com.sokolua.manager.databinding.GoodsGroupsItemBinding;
import com.sokolua.manager.di.DaggerService;
import com.sokolua.manager.ui.custom_views.ReactiveRecyclerAdapter;

import javax.inject.Inject;

public class GroupViewHolder extends ReactiveRecyclerAdapter.ReactiveViewHolder<GoodsGroupRealm> {
    private GoodsGroupsItemBinding binding;
    @Inject
    GoodsScreen.Presenter mPresenter;

    public GroupViewHolder(View itemView) {
        super(itemView);
        DaggerService.<GoodsScreen.Component>getDaggerComponent(itemView.getContext()).inject(this);
        binding = GoodsGroupsItemBinding.bind(itemView);
    }

    @Override
    public void setCurrentItem(GoodsGroupRealm currentItem) {
        super.setCurrentItem(currentItem);

        if (currentItem.isValid() && currentItem.isLoaded()) {
            if (currentItem.getImageURI() == null || currentItem.getImageURI().isEmpty()) {
                binding.goodGroupImage.setImageResource(R.drawable.no_image);
            } else {
                binding.goodGroupImage.setImageURI(Uri.parse(currentItem.getImageURI()));
            }
            binding.goodGroupName.setText(currentItem.getName());
            itemView.setOnClickListener(view -> mPresenter.mainGroupSelected(currentItem));
        }
    }

}

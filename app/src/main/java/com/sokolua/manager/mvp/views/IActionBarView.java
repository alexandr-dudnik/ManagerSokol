package com.sokolua.manager.mvp.views;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorRes;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;

import com.sokolua.manager.mvp.presenters.MenuItemHolder;

import java.util.List;

public interface IActionBarView {
    void setActionBarTitle(CharSequence title);
    void setVisible(boolean visible);
    void setBackArrow(boolean enabled);
    void setMenuItem(List<MenuItemHolder> items);
    void setTabLayout(ViewPager tabs);
    void removeTabLayout();

    default void tintMenuIcon(Context context, MenuItem item, @ColorRes int color) {
        Drawable normalDrawable = item.getIcon();
        Drawable wrapDrawable = DrawableCompat.wrap(normalDrawable);
        DrawableCompat.setTint(wrapDrawable, context.getResources().getColor(color));

        item.setIcon(wrapDrawable);
    }
}

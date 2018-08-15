package com.sokolua.manager.mvp.presenters;

import android.support.v7.widget.SearchView;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

public class MenuItemHolder {
    private final CharSequence itemTitle;
    private final int iconResId;
    private final MenuItem.OnMenuItemClickListener listener;
    private final SearchView.OnQueryTextListener queryListener;
    private final int itemType;
    private final List<MenuItemHolder> subMenu= new ArrayList<>();

    public MenuItemHolder(CharSequence itemTitle, int iconResId, MenuItem.OnMenuItemClickListener listener, int type) {
        this.itemTitle = itemTitle;
        this.iconResId = iconResId;
        this.listener = listener;
        this.itemType = type;
        this.queryListener = null;
    }

    public MenuItemHolder(CharSequence itemTitle, int iconResId, SearchView.OnQueryTextListener listener, int type) {
        this.itemTitle = itemTitle;
        this.iconResId = iconResId;
        this.listener = null;
        this.itemType = type;
        this.queryListener = listener;
    }

    //region ===================== Getters =========================

    public CharSequence getItemTitle() {
        return itemTitle;
    }

    public int getIconResId() {
        return iconResId;
    }

    public MenuItem.OnMenuItemClickListener getListener() {
        return listener;
    }

    public SearchView.OnQueryTextListener getQueryListener() {
        return queryListener;
    }

    public int getItemType() {
        return itemType;
    }

    public List<MenuItemHolder> getSubMenu() {
        return subMenu;
    }

    //endregion ================== Getters =========================

    //region ===================== SubMenu =========================

    public void addSubMenuItem(MenuItemHolder item){
        this.subMenu.add(item);
    }

    public void clearSubMenu(){
        this.subMenu.clear();
    }

    public boolean hasSubMenu(){
        return (!this.subMenu.isEmpty());
    }

    //endregion ================== SubMenu =========================
}

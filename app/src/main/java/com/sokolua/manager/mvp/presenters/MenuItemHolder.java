package com.sokolua.manager.mvp.presenters;

import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

public class MenuItemHolder {
    private final CharSequence itemTitle;
    private final int iconResId;
    private final MenuItem.OnMenuItemClickListener listener;
    private final boolean action;
    private final List<MenuItemHolder> subMenu= new ArrayList<>();

    public MenuItemHolder(CharSequence itemTitle, int iconResId, MenuItem.OnMenuItemClickListener listener, boolean action) {
        this.itemTitle = itemTitle;
        this.iconResId = iconResId;
        this.listener = listener;
        this.action = action;
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

    public boolean isAction() {
        return action;
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

package com.sokolua.manager.mvp.presenters;

import android.view.MenuItem;

import androidx.appcompat.widget.SearchView;

import com.sokolua.manager.R;
import com.sokolua.manager.data.managers.ConstantManager;

import java.util.ArrayList;
import java.util.List;

public class MenuItemHolder {
    private final CharSequence itemTitle;
    private final int iconResId;
    private final MenuItem.OnMenuItemClickListener listener;
    private final SearchView.OnQueryTextListener queryListener;
    private final int itemType;
    private final int groupId;
    private final boolean checkable;
    private final boolean checked;
    private final List<MenuItemHolder> subMenu= new ArrayList<>();

    //General
    public MenuItemHolder(CharSequence itemTitle, int iconResId, MenuItem.OnMenuItemClickListener listener, int type) {
        this.itemTitle = itemTitle;
        this.iconResId = iconResId;
        this.listener = listener;
        this.itemType = type;
        this.queryListener = null;
        this.checkable = false;
        this.checked = false;
        this.groupId = 0;
    }

    //Checkable
    public MenuItemHolder(CharSequence itemTitle, MenuItem.OnMenuItemClickListener listener, int groupId, boolean checked) {
        this.itemTitle = itemTitle;
        this.iconResId = 0;
        this.listener = listener;
        this.itemType = ConstantManager.MENU_ITEM_TYPE_ITEM;
        this.queryListener = null;
        this.checkable = true;
        this.checked = checked;
        this.groupId = groupId;
    }

    //Search
    public MenuItemHolder(CharSequence itemTitle, SearchView.OnQueryTextListener listener) {
        this.itemTitle = itemTitle;
        this.iconResId = R.drawable.ic_search;
        this.listener = null;
        this.itemType = ConstantManager.MENU_ITEM_TYPE_SEARCH;
        this.queryListener = listener;
        this.checkable = false;
        this.checked = false;
        this.groupId = 0;
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

    public boolean isCheckable() {
        return checkable;
    }

    public boolean isChecked() {
        return checked;
    }

    public int getGroupId() {
        return groupId;
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

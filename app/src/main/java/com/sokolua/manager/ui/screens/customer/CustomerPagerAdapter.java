package com.sokolua.manager.ui.screens.customer;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sokolua.manager.data.storage.dto.CustomerDto;
import com.sokolua.manager.di.DaggerService;
import com.sokolua.manager.flow.AbstractScreen;

import java.util.ArrayList;
import java.util.List;

import mortar.MortarScope;

public class CustomerPagerAdapter extends PagerAdapter {
    private List<String> mTitles;
    private CustomerDto mCustomerDto;

    public CustomerPagerAdapter(CustomerDto customer) {
        mCustomerDto = customer;

        mTitles = new ArrayList<>();
        mTitles.add("Информация");
        mTitles.add("Задачи");
        mTitles.add("Заказы");
    }

    @Override
    public int getCount() {
        return mTitles.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    @NonNull
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View newView;

        AbstractScreen screen = null;
        switch (position){
            case 0:
                //screen = new FindScreen();
                break;
            case 1:
                //screen = new FilterScreen();
                break;
        }
        if (screen != null) {

            MortarScope screenScope = createScreenScopeFromMortar(container.getContext(), screen);
            Context screenContext = screenScope.createContext(container.getContext());

            newView = LayoutInflater.from(screenContext).inflate(screen.getLayoutResId(), container, false);
            container.addView(newView);

        }else{
            newView = new View(container.getContext());
        }
        return newView;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles.get(position);
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    private MortarScope createScreenScopeFromMortar(Context context, @NonNull AbstractScreen screen){
        MortarScope parentScope = MortarScope.getScope(context);
        MortarScope childScope = parentScope.findChild(screen.getScopeName());

        if (childScope == null) {
            Object screenComponent = screen.createScreenComponent(parentScope.getService(DaggerService.SERVICE_NAME));
            if (screenComponent == null){
                throw new IllegalStateException("cannot create screen for "+screen.getScopeName());
            }

            childScope=parentScope.buildChild()
                    .withService(DaggerService.SERVICE_NAME, screenComponent)
                    .build(screen.getScopeName());
        }

        return childScope;
    }
}

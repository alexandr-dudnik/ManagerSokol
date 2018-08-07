package com.sokolua.manager.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.IdRes;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.util.ArrayList;

public class UiHelper {

    public static void getCachedImagePicasso(final Picasso picasso, final String uri, final ImageView view, final Drawable dummy, final boolean fited) {
        RequestCreator rc = picasso.load(uri)
                .placeholder(dummy)
                .error(dummy)
                .networkPolicy(NetworkPolicy.OFFLINE);
        if (fited) {
            rc.fit()
                    .centerCrop();
        }

        rc.into(view, new Callback() {
                    @Override
                    public void onSuccess() {
                    }

                    @Override
                    public void onError(Exception e) {
                        RequestCreator rc = picasso
                                .load(uri)
                                .placeholder(dummy)
                                .error(dummy);

                        if (fited) {
                            rc.fit()
                                    .centerCrop();
                        }

                        rc.into(view, new Callback() {
                            @Override
                            public void onSuccess() {
                            }

                            @Override
                            public void onError(Exception e) {

                            }

                        });
                    }
                }
        );

    }


    public static int lerp(int start, int end, float friction) {
        return (int) (start + (end - start) * friction);
    }

    public static float currentFriction(int start, int end, int currentValue) {
        return (float) (currentValue - start) / (end - start);
    }

    public static float getDensity(Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(displayMetrics);

        return displayMetrics.density;
    }

    public static ArrayList<View> getChildsExcludeView(ViewGroup container, @IdRes int... excludeChild) {
        ArrayList<View> childs = new ArrayList<>();
        for (int i = 0; i < container.getChildCount(); i++) {
            View child = container.getChildAt(i);
            boolean good = true;
            for (@IdRes int ex : excludeChild) {
                if (child.getId() == ex) {
                    good = false;
                    break;
                }
            }
            if (good) {
                childs.add(child);
            }
        }
        return childs;
    }

    public static void waitForMeasure(final View view, final OnMeasureCallBack callBack) {
        int width = view.getWidth();
        int height = view.getHeight();

        if (width > 0 && height > 0) {
            callBack.onMeasure(view, width, height);
            return;
        }

        view.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                final ViewTreeObserver observer = view.getViewTreeObserver();
                if (observer.isAlive()) {
                    observer.removeOnPreDrawListener(this);
                }

                callBack.onMeasure(view, view.getWidth(), view.getHeight());

                return true;
            }
        });
    }

    public interface OnMeasureCallBack {
        void onMeasure(View view, int width, int height);
    }
}

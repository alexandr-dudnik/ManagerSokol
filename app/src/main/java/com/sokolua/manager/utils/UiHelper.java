package com.sokolua.manager.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.IdRes;

import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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

    public static String saveImageFromBase64(final String imageSource, String name){
        File cacheDir = App.getContext().getFilesDir();
        String result = cacheDir.getPath() + "/" + name+".png";
        FileOutputStream fos = null;
        try {
            if (imageSource != null) {
                fos = new FileOutputStream(result);
                //fos = App.getContext().openFileOutput(result, Context.MODE_PRIVATE);
                byte[] decodedString = android.util.Base64.decode(imageSource, android.util.Base64.DEFAULT);
                fos.write(decodedString);
                fos.flush();
                fos.close();
            }

        } catch (Exception e) {
            return "";
        } finally {
            if (fos != null) {
                fos = null;
            }
        }
        return result;
    }

    public static String getBase64FromImage(String name){
        FileInputStream fis = null;
        String result = "";
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            fis = new FileInputStream(new File(name));

            byte[] buf = new byte[1024];
            int n;
            while (-1 != (n = fis.read(buf)))
                baos.write(buf, 0, n);
            byte[] bytes = baos.toByteArray();
            result = android.util.Base64.encodeToString(bytes, android.util.Base64.DEFAULT);
        } catch (Throwable ignore) {
        } finally {
            if (fis != null) {
                fis = null;
            }
        }
        return result;
    }



    public static String saveImageBitmap(final Bitmap imageSource, String name){
        File cacheDir = App.getContext().getFilesDir();
        String result = cacheDir.getPath() + "/" + name+".png";
        FileOutputStream fos = null;
        try {
            if (imageSource != null) {
                fos = new FileOutputStream(result);
                imageSource.compress(Bitmap.CompressFormat.JPEG, 75, fos);
                fos.flush();
                fos.close();
            }

        } catch (Exception e) {
            return "";
        } finally {
            if (fos != null) {
                fos = null;
            }
        }
        return result;
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

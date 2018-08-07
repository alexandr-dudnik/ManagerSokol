package com.sokolua.manager.ui.custom_views;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.ViewGroup;

public class SquareImageView extends AppCompatImageView {
    public SquareImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // получаем рассчитанные размеры кнопки
        //final int height = getMeasuredHeight();	// высота
        final int width = getMeasuredWidth();	// ширина

        // теперь задаем новый размер
        setMeasuredDimension(width, width);

        ViewGroup.LayoutParams lp = getLayoutParams();
        lp.height = width;
        lp.width = width;
        setLayoutParams(lp);

    }
}

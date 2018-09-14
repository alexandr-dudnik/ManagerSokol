package com.sokolua.manager.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.sokolua.manager.R;

public abstract class SwipeToDeleteCallback extends ItemTouchHelper.SimpleCallback {

    private Paint clearPaint;
    private int backgroundColor;
    private int intrinsicWidth;
    private int intrinsicHeight;
    private Drawable deleteIcon;
    private Drawable background;



    //**********************************************************************************************
    //      Creates a Callback for the given drag and swipe allowance. These values serve as
    //      defaults
    //      and if you want to customize behavior per ViewHolder, you can override
    //      {@link #getSwipeDirs(RecyclerView, ViewHolder)}
    //      and / or {@link #getDragDirs(RecyclerView, ViewHolder)}.
    //
    //      @param dragDirs  Binary OR of direction flags in which the Views can be dragged. Must be
    //                       composed of {@link #LEFT}, {@link #RIGHT}, {@link #START}, {@link
    //                       #END},
    //                       {@link #UP} and {@link #DOWN}.
    //      @param swipeDirs Binary OR of direction flags in which the Views can be swiped. Must be
    //                       composed of {@link #LEFT}, {@link #RIGHT}, {@link #START}, {@link
    //                       #END},
    //                       {@link #UP} and {@link #DOWN}.
    //**********************************************************************************************
    private SwipeToDeleteCallback(int dragDirs, int swipeDirs) {
        super(dragDirs, swipeDirs);
    }

    public SwipeToDeleteCallback(Context context){
        super(0, ItemTouchHelper.START | ItemTouchHelper.END | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);

        deleteIcon = ContextCompat.getDrawable(context, R.drawable.ic_delete);
        intrinsicWidth = deleteIcon.getIntrinsicWidth();
        intrinsicHeight = deleteIcon.getIntrinsicHeight();
        backgroundColor = ContextCompat.getColor(context, R.color.color_red);
        background = new ColorDrawable(backgroundColor);
        clearPaint = new Paint();
    }


    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        /*
         * To disable "swipe" for specific item return 0 here.
         * For example:
         * if (viewHolder?.itemViewType == YourAdapter.SOME_TYPE) return 0
         * if (viewHolder?.adapterPosition == 0) return 0
         */
        return super.getMovementFlags(recyclerView, viewHolder);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        int deleteIconTop;
        int deleteIconMargin;
        int deleteIconLeft;
        int deleteIconRight;
        int deleteIconBottom;

        View itemView = viewHolder.itemView;
        int itemHeight = itemView.getHeight();
        boolean isCanceled = dX == 0f && !isCurrentlyActive;

        if (isCanceled) {
            clearCanvas(c, itemView.getRight() + dX, itemView.getTop()+0f, itemView.getRight()+0f, itemView.getBottom()+0f);
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            return;
        }

        // Draw the red delete background
        background.setAlpha((int)(255*Math.abs(dX)/itemView.getWidth()));

        if (dX<0) {
            background.setBounds(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
            background.draw(c);

            // Calculate position of delete icon
            deleteIconTop = itemView.getTop() + (itemHeight - intrinsicHeight) / 2;
            deleteIconMargin = (itemHeight - intrinsicHeight) / 2;
            deleteIconLeft = itemView.getRight() - deleteIconMargin - intrinsicWidth;
            deleteIconRight = itemView.getRight() - deleteIconMargin;
            deleteIconBottom = deleteIconTop + intrinsicHeight;
        }else{
            background.setBounds(0 , itemView.getTop(), (int) dX, itemView.getBottom());
            background.draw(c);

            // Calculate position of delete icon
            deleteIconTop = itemView.getTop() + (itemHeight - intrinsicHeight) / 2;
            deleteIconMargin = (itemHeight - intrinsicHeight) / 2;
            deleteIconLeft = deleteIconMargin ;
            deleteIconRight = deleteIconMargin + intrinsicWidth;
            deleteIconBottom = deleteIconTop + intrinsicHeight;

        }
        // Draw the delete icon
        deleteIcon.setBounds(deleteIconLeft, deleteIconTop, deleteIconRight, deleteIconBottom);
        deleteIcon.draw(c);

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }

    private void clearCanvas(Canvas c, Float left, Float top, Float right, Float bottom) {
        c.drawRect(left, top, right, bottom, clearPaint);
    }


}

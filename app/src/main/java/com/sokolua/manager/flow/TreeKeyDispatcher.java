package com.sokolua.manager.flow;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;

import com.sokolua.manager.R;
import com.sokolua.manager.mortar.ScreenScoper;
import com.sokolua.manager.utils.UiHelper;

import java.util.Collections;
import java.util.Map;

import flow.Direction;
import flow.Dispatcher;
import flow.KeyChanger;
import flow.State;
import flow.Traversal;
import flow.TraversalCallback;
import flow.TreeKey;

public class TreeKeyDispatcher implements Dispatcher, KeyChanger {
    FrameLayout mRootFrame;
    private Activity mActivity;
    private Object inKey;
    @Nullable
    private Object outKey;

    public TreeKeyDispatcher(Activity activity) {
        mActivity = activity;
    }

    @Override
    public void dispatch(Traversal traversal, TraversalCallback callback) {
        Map<Object, Context> contexts;
        State inState = traversal.getState(traversal.destination.top());
        inKey = inState.getKey();
        State outState = traversal.origin == null ? null : traversal.getState(traversal.origin.top());
        outKey = outState == null ? null : outState.getKey();

        mRootFrame = mActivity.findViewById(R.id.root_frame);

        if (inKey.equals(outKey)) {
            callback.onTraversalCompleted();
            return;
        }

        if (inKey instanceof TreeKey) {
            //TODO: implement treekey case
        }

        Context flowContext = traversal.createContext(inKey, mActivity);
        Context mortarContext = ScreenScoper.getScreenScope((AbstractScreen) inKey).createContext(flowContext);
        contexts = Collections.singletonMap(inKey, mortarContext);
        changeKey(outState, inState, traversal.direction, contexts, callback);
    }

    @Override
    public void changeKey(@Nullable State outgoingState, State incomingState, final Direction direction, Map<Object, Context> incomingContexts, final TraversalCallback callback) {
        Context context = incomingContexts.get(inKey);

        //save prev view
        if (outgoingState != null) {
            outgoingState.save(mRootFrame.getChildAt(0));
        }

        //create new view
        AbstractScreen<?> screen = (AbstractScreen<?>) inKey;
        int layout = screen.getLayoutResId();

        LayoutInflater inflater = LayoutInflater.from(context);
        final View newView = inflater.inflate(layout, mRootFrame, false);
        final View oldView = mRootFrame.getChildAt(0);

        //restore state to new view
        incomingState.restore(newView);


        //delete old view
        if (mRootFrame.getChildAt(0) != null) {
            mRootFrame.removeView(mRootFrame.getChildAt(0));
        }

        mRootFrame.addView(newView);

        //start animation
        UiHelper.waitForMeasure(newView, (view, width, height) -> runAnimation(mRootFrame, oldView, newView, direction, () -> {
            //animation completed - do thmsg.
            if ((outKey != null) && !(inKey instanceof TreeKey)) {
                ((AbstractScreen<?>) outKey).unregisterScope();
            }

            callback.onTraversalCompleted();
        }));
    }

    private void runAnimation(final FrameLayout parent, final View from, View to, Direction direction, final TraversalCallback callback) {
        Animator animator = createAnimation(from, to, direction);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (from != null) {
                    parent.removeView(from); //remove view from container
                }
                callback.onTraversalCompleted(); //callback of success  change
            }
        });
        animator.setInterpolator(new FastOutSlowInInterpolator());
        animator.start();
    }

    @NonNull
    private Animator createAnimation(@Nullable View from, View to, Direction direction) {
        boolean backward = direction == Direction.BACKWARD;

        AnimatorSet set = new AnimatorSet();
        int fromTranslation;
        ObjectAnimator outAnimation;

        if (from != null) {
            fromTranslation = backward ? from.getWidth() : -from.getWidth();
            outAnimation = ObjectAnimator.ofFloat(from, "translationX", fromTranslation);
            set.play(outAnimation);
        }

        int toTranslation = backward ? -to.getWidth() : to.getWidth();
        ObjectAnimator toAnimation = ObjectAnimator.ofFloat(to, "translationY", toTranslation, 0);
        set.play(toAnimation);

        return set;
    }
}

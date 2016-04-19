package com.handy.portal.ui.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LayoutAnimationController;

/**
 * Created by jtse on 4/18/16.
 */
public class AnimatedRecyclerView extends RecyclerView
{

    public AnimatedRecyclerView(Context context)
    {
        super(context);
    }

    public AnimatedRecyclerView(Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
    }

    public AnimatedRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    @Override
    protected void attachLayoutAnimationParameters(View child, ViewGroup.LayoutParams params, int index, int count)
    {
        LayoutAnimationController.AnimationParameters animationParams =
                params.layoutAnimationParameters;
        if (animationParams == null)
        {
            animationParams = new LayoutAnimationController.AnimationParameters();
            params.layoutAnimationParameters = animationParams;
        }

        animationParams.count = count;
        animationParams.index = index;
    }
}

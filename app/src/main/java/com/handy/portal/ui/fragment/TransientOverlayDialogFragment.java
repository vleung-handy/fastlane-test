package com.handy.portal.ui.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.handy.portal.R;

import butterknife.ButterKnife;

public class TransientOverlayDialogFragment extends DialogFragment
{
    private int viewId;
    private int animationId = R.anim.overlay_fade_in_then_out;

    public TransientOverlayDialogFragment()
    {
    }

    public static TransientOverlayDialogFragment newInstance(int viewId, int animationId)
    {
        TransientOverlayDialogFragment transientOverlayDialogFragment = new TransientOverlayDialogFragment();
        transientOverlayDialogFragment.setResourceIds(viewId, animationId);
        return transientOverlayDialogFragment;
    }

    public void setResourceIds(int viewId, int animationId)
    {
        setViewId(viewId);
        setAnimationId(animationId);
    }

    public int getAnimationId()
    {
        return animationId;
    }

    public void setAnimationId(int animationId)
    {
        this.animationId = animationId;
    }

    public int getViewId()
    {
        return viewId;
    }

    public void setViewId(int viewId)
    {
        this.viewId = viewId;
    }

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Light);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        Dialog dialog = new Dialog(getActivity(), android.R.style.Theme_Translucent_NoTitleBar);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(getViewId(), container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    protected void showThenDismiss()
    {
        View view = getView();
        view.setVisibility(View.VISIBLE);
        Animation animation = AnimationUtils.loadAnimation(view.getContext(), getAnimationId());
        view.startAnimation(animation);
        animation.setAnimationListener(new Animation.AnimationListener()
        {
            @Override
            public void onAnimationStart(Animation animation)
            {
            }

            @Override
            public void onAnimationEnd(Animation animation)
            {
                TransientOverlayDialogFragment.this.dismiss();
            }

            @Override
            public void onAnimationRepeat(Animation animation)
            {

            }
        });
    }

    public void onStart()//dialog becomes visible
    {
        super.onStart();
        showThenDismiss();
    }

}

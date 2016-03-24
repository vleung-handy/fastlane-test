package com.handy.portal.ui.fragment.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.handy.portal.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TransientOverlayDialogFragment extends DialogFragment //TODO: make other transient dialog fragments extend from this
{
    private int animationId = R.anim.overlay_fade_in_then_out;
    private int imageId = R.drawable.ic_success_circle;
    private int textId = R.string.special_empty_string;

    @Bind(R.id.transition_overlay_image)
    ImageView icon;
    @Bind(R.id.transition_overlay_text)
    TextView display;

    public static TransientOverlayDialogFragment newInstance(int animationId, int imageId, int textId)
    {
        TransientOverlayDialogFragment transientOverlayDialogFragment = new TransientOverlayDialogFragment();
        transientOverlayDialogFragment.setResources(animationId, imageId, textId);
        return transientOverlayDialogFragment;
    }

    private void setResources(int animationId, int imageId, int textId)
    {
        this.animationId = animationId;
        this.imageId = imageId;
        this.textId = textId;
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
        View view = inflater.inflate(R.layout.fragment_transition_overlay, container, false);
        ButterKnife.bind(this, view);

        icon.setImageResource(imageId);
        display.setText(textId);

        return view;
    }

    protected void showThenDismiss()
    {
        View view = getView();
        if (view != null)
        {
            view.setVisibility(View.VISIBLE);
            Animation animation = AnimationUtils.loadAnimation(view.getContext(), animationId);
            view.startAnimation(animation);
            animation.setAnimationListener(new Animation.AnimationListener()
            {
                @Override
                public void onAnimationStart(Animation animation) { }

                @Override
                public void onAnimationEnd(Animation animation)
                {
                    try
                    //handles IllegalStateException: Can not perform this action after onSaveInstanceState
                    {
                        TransientOverlayDialogFragment.this.dismiss();
                    }
                    catch (Exception e)
                    {
                        //do nothing
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) { }
            });
        }
    }

    public void onStart()//dialog becomes visible
    {
        super.onStart();
        showThenDismiss();
    }

}

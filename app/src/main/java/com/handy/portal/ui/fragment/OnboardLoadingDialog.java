package com.handy.portal.ui.fragment;

import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import com.handy.portal.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * This is a fancy "loading" dialog fragment.
 * Created by jtse on 4/19/16.
 * <p/>
 * TODO: JIA: Style the onboarding dialog when the design specs come in
 */
public class OnboardLoadingDialog extends DialogFragment
{
    public static final String TAG = OnboardLoadingDialog.class.getName();

    @Bind(R.id.image_view)
    ImageView mImageView;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_FRAME, android.R.style.Theme_Holo_Light);
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.dialog_onboard_loading, container, false);
        ButterKnife.bind(this, view);

        // Get the background, which has been compiled to an AnimationDrawable object.
        AnimationDrawable frameAnimation = (AnimationDrawable) mImageView.getBackground();

        // Start the animation (looped playback by default).
        frameAnimation.start();

        return view;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation_slide_out;
        return dialog;
    }
}

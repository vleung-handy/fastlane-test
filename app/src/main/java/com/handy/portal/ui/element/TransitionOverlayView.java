package com.handy.portal.ui.element;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.constant.TransitionStyle;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by cdavis on 6/2/15.
 */
public class TransitionOverlayView extends RelativeLayout
{
    @InjectView(R.id.transition_overlay_text)
    protected TextView overlayText;

    @InjectView(R.id.transition_overlay_image)
    protected ImageView overlayImage;

    public TransitionOverlayView(final Context context)
    {
        super(context);
    }

    public TransitionOverlayView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
    }

    public TransitionOverlayView(final Context context, final AttributeSet attrs, final int defStyle)
    {
        super(context, attrs, defStyle);
    }

    public void init()
    {
        ButterKnife.inject(this);
        this.setVisibility(GONE);
    }

    public void setText(int textId)
    {
        overlayText.setText(textId);
    }

    public void setText(String text)
    {
        overlayText.setText(text);
    }

    public void setImage(int imageResourceId)
    {
        overlayImage.setImageResource(imageResourceId);
    }

    public void showThenHideOverlay()
    {
        this.setVisibility(VISIBLE);
        Animation showThenHide = AnimationUtils.loadAnimation(getContext(), R.anim.overlay_fade_in_then_out);
        this.startAnimation(showThenHide);
    }

    //If this gets back and complex setup a basic state machine for tab transitions with the relevant overlays and anims along the transitions
    public void setupOverlay()
    {
        setupOverlay(null);
    }

    public void setupOverlay(TransitionStyle transitionStyle)
    {
        setText("");
        setImage(android.R.color.transparent);

        if(transitionStyle != null)
        {
            if(transitionStyle.shouldShowOverlay())
            {
                if(transitionStyle.getOverlayStringId() != -1)
                {
                    setText(transitionStyle.getOverlayStringId());
                }
                if(transitionStyle.getOverlayImageId() != -1)
                {
                    setImage(transitionStyle.getOverlayImageId());
                }
            }
        }
    }



}

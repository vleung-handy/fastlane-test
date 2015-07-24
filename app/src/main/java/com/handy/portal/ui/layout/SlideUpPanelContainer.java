package com.handy.portal.ui.layout;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.ui.activity.BaseActivity;

public class SlideUpPanelContainer extends RelativeLayout
{
    private boolean panelShown = false;

    public SlideUpPanelContainer(Context context)
    {
        super(context);
    }

    public SlideUpPanelContainer(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public SlideUpPanelContainer(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SlideUpPanelContainer(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public interface ContentInitializer
    {
        void initialize(ViewGroup panel);
    }

    public void showPanel(int titleStringId, @NonNull ContentInitializer contentInitializer)
    {
        if (!panelShown)
        {
            View panelOverlay = LayoutInflater.from(getContext()).inflate(R.layout.layout_slide_up_panel_overlay, this, false);
            ViewGroup panel = (ViewGroup) LayoutInflater.from(getContext()).inflate(R.layout.layout_slide_up_panel, this, false);

            ((TextView) panel.findViewById(R.id.slide_up_panel_title)).setText(titleStringId);

            contentInitializer.initialize(panel);

            showElement(panelOverlay, R.anim.fade_in);
            showElement(panel, R.anim.slide_up);

            OnClickListener hidePanelListener = new OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    SlideUpPanelContainer.this.hidePanel();
                }
            };
            panelOverlay.setOnClickListener(hidePanelListener);
            panel.findViewById(R.id.slide_up_panel_close).setOnClickListener(hidePanelListener);

            setOnBackPressedListener();

            panelShown = true;
        }
    }

    private void setOnBackPressedListener()
    {
        BaseActivity baseActivity = (BaseActivity) getContext();
        baseActivity.addOnBackPressedListener(new BaseActivity.OnBackPressedListener()
        {
            @Override
            public void onBackPressed()
            {
                SlideUpPanelContainer.this.dismiss();
            }
        });
    }

    private void showElement(View view, int animId)
    {
        Animation animation = AnimationUtils.loadAnimation(getContext(), animId);
        view.setAnimation(animation);
        addView(view);
    }

    public void hidePanel()
    {
        if (panelShown)
        {
            ((Activity) getContext()).onBackPressed();
        }
    }

    private void dismiss()
    {
        View panel = findViewById(R.id.slide_up_panel);
        hideElement(panel, R.anim.slide_down);

        View panelOverlay = findViewById(R.id.slide_up_panel_overlay);
        hideElement(panelOverlay, R.anim.fade_out);

        panelShown = false;
    }

    private void hideElement(View view, int animId)
    {
        Animation animation = AnimationUtils.loadAnimation(getContext(), animId);
        animation.setAnimationListener(new PostAnimationViewRemover(view));
        view.setAnimation(animation);
        view.setVisibility(View.GONE);
    }

    private class PostAnimationViewRemover implements Animation.AnimationListener
    {
        private View view;

        PostAnimationViewRemover(View view)
        {
            this.view = view;
        }

        @Override
        public void onAnimationStart(Animation animation)
        {
        }

        @Override
        public void onAnimationEnd(Animation animation)
        {
            new Handler().post(new Runnable()
            {
                @Override
                public void run()
                {
                    SlideUpPanelContainer.this.removeView(view);
                }
            });
        }

        @Override
        public void onAnimationRepeat(Animation animation)
        {
        }
    }

}

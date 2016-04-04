package com.handy.portal.ui.layout;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.ui.activity.BaseActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SlideUpPanelLayout extends RelativeLayout
{
    private LinearLayout mPanel;
    private View mPanelOverlay;
    private boolean mPanelShown = false;

    @Bind(R.id.slide_up_panel_title)
    TextView mPanelTitle;
    @Bind(R.id.slide_up_panel_content)
    FrameLayout mPanelContent;
    @Bind(R.id.slide_up_panel_close)
    View mIconX;


    public SlideUpPanelLayout(Context context)
    {
        super(context);
        init();
    }

    public SlideUpPanelLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public SlideUpPanelLayout(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SlideUpPanelLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    // TODO: considering breaking this up to showPanel and SetPanel
    public void showPanel(int titleStringId, View content)
    {
        if (!mPanelShown)
        {
            mPanelTitle.setText(titleStringId);
            mPanelContent.removeAllViews();
            mPanelContent.addView(content);

            showElement(mPanelOverlay, R.anim.fade_in);
            showElement(mPanel, R.anim.slide_up);

            setOnBackPressedListener();

            mPanelShown = true;
        }
    }

    @OnClick(R.id.slide_up_panel_close)
    public void hidePanel()
    {
        if (mPanelShown)
        {
            hideElement(mPanel, R.anim.slide_down);
            hideElement(mPanelOverlay, R.anim.fade_out);

            ((BaseActivity) getContext()).clearOnBackPressedListenerStack();

            mPanelShown = false;
        }
    }

    private void init()
    {
        mPanel = (LinearLayout) LayoutInflater.from(getContext()).inflate(
                R.layout.layout_slide_up_panel, this, false);
        ButterKnife.bind(this, mPanel);

        mPanelOverlay = LayoutInflater.from(getContext()).inflate(
                R.layout.layout_slide_up_panel_overlay, this, false);
        mPanelOverlay.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                hidePanel();
            }
        });
    }

    private void setOnBackPressedListener()
    {
        BaseActivity baseActivity = (BaseActivity) getContext();
        baseActivity.addOnBackPressedListener(new BaseActivity.OnBackPressedListener()
        {
            @Override
            public void onBackPressed()
            {
                hidePanel();
            }
        });
    }

    private void showElement(View view, int animId)
    {
        Animation animation = AnimationUtils.loadAnimation(getContext(), animId);
        view.setAnimation(animation);
        addView(view);
    }

    private void hideElement(View view, int animId)
    {
        Animation animation = AnimationUtils.loadAnimation(getContext(), animId);
        view.setAnimation(animation);
        removeView(view);
    }
}

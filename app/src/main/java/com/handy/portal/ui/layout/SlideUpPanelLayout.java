package com.handy.portal.ui.layout;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.handy.portal.R;

public class SlideUpPanelLayout extends RelativeLayout
{
    private boolean panelShown = false;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SlideUpPanelLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public SlideUpPanelLayout(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    public SlideUpPanelLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public SlideUpPanelLayout(Context context)
    {
        super(context);
    }

    public void showPanel()
    {
        if (!panelShown)
        {
            LayoutInflater.from(getContext()).inflate(R.layout.layout_panel_overlay, this);
            LayoutInflater.from(getContext()).inflate(R.layout.layout_panel_container, this); // with animation please
            findViewById(R.id.panel_overlay).setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    hidePanel();
                }
            });
            panelShown = true;
        }
    }

    public void hidePanel()
    {
        if (panelShown)
        {
            findViewById(R.id.panel_container).setVisibility(View.GONE); // with animation please
            removeView(findViewById(R.id.panel_overlay));
            removeView(findViewById(R.id.panel_container));
            panelShown = false;
        }
    }

}

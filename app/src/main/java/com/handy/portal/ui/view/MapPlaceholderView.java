package com.handy.portal.ui.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.handy.portal.R;
import com.handy.portal.util.Utils;

public class MapPlaceholderView extends FrameLayout
{
    private static String GOOGLE_PLAY_SERVICES_INSTALL_URL = "https://play.google.com/store/apps/details?id=com.google.android.gms";

    public MapPlaceholderView(Context context)
    {
        super(context);
        init();
    }

    public MapPlaceholderView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public MapPlaceholderView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(21)
    public MapPlaceholderView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init()
    {
        inflate(getContext(), R.layout.element_map_placeholder, this);
        Button mapsInstallButton = (Button) findViewById(R.id.map_placeholder_install_button);
        if (mapsInstallButton != null)
        {
            mapsInstallButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(GOOGLE_PLAY_SERVICES_INSTALL_URL));
                    Utils.safeLaunchIntent(browserIntent, getContext());
                }
            });
        }
    }
}

package com.handy.portal.ui.widget;


import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.util.AttributeSet;
import android.widget.RadioButton;

import com.handy.portal.R;
import com.handy.portal.util.Utils;

public class DimBadgeRadioButton extends RadioButton
{

    public DimBadgeRadioButton(final Context context)
    {
        super(context);
    }

    public DimBadgeRadioButton(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
    }

    public DimBadgeRadioButton(final Context context, final AttributeSet attrs, final int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(21)
    public DimBadgeRadioButton(final Context context, final AttributeSet attrs, final int defStyleAttr, final int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void setChecked(final boolean checked)
    {
        super.setChecked(checked);
        if (isChecked())
        {

            setIconAlpha(Utils.RGBA_ALPHA_100_PERCENT);
            setTextColor(getTextColors().withAlpha(Utils.RGBA_ALPHA_100_PERCENT));
        }
        else
        {
            setIconAlpha(Utils.RGBA_ALPHA_50_PERCENT);
            setTextColor(getTextColors().withAlpha(Utils.RGBA_ALPHA_50_PERCENT));
        }
    }

    private void setIconAlpha(int alpha)
    {
        LayerDrawable layerList = (LayerDrawable) getCompoundDrawables()[Utils.DRAWABLE_TOP_INDEX];
        if (layerList != null)
        {
            Drawable icon = layerList.findDrawableByLayerId(R.id.ic_notifications_bell_icon);

            if (icon != null)
            {
                icon.setAlpha(alpha);
                icon.mutate();
                layerList.setDrawableByLayerId(R.id.ic_notifications_bell_icon, icon);
            }
        }
    }
}

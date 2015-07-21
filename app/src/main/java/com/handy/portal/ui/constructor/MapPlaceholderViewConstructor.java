package com.handy.portal.ui.constructor;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.ViewGroup;

import com.handy.portal.R;
import com.handy.portal.model.Booking;

public class MapPlaceholderViewConstructor extends BookingDetailsViewConstructor
{

    public MapPlaceholderViewConstructor(@NonNull Context context, Bundle arguments)
    {
        super(context, arguments);
    }

    protected int getLayoutResourceId()
    {
        return R.layout.element_map_placeholder;
    }

    @Override
    protected boolean constructView(ViewGroup container, Booking item)
    {
        return true;
    }
}

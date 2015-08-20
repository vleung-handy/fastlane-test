package com.handy.portal.ui.constructor;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.handy.portal.R;
import com.handy.portal.util.UIUtils;

public class MapPlaceholderViewConstructor extends DetailMapViewConstructor
{

    public MapPlaceholderViewConstructor(@NonNull Context context, Bundle arguments)
    {
        super(context, arguments);
    }

    @Override
    protected void inflateMapView(RelativeLayout mapViewStub)
    {
        View view = ((LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.element_map_placeholder, null);
        UIUtils.replaceView(mapViewStub, view);
    }

}

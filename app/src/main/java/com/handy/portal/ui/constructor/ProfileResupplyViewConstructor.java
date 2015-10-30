package com.handy.portal.ui.constructor;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.constant.MainViewTab;
import com.handy.portal.constant.TransitionStyle;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.model.ResupplyInfo;
import com.handy.portal.util.Utils;
import com.squareup.otto.Bus;

import javax.inject.Inject;

import butterknife.InjectView;

public class ProfileResupplyViewConstructor extends ViewConstructor<ResupplyInfo>
{
    @Inject
    Bus bus;

    @InjectView(R.id.get_resupply_kit_button)
    Button resupplyButton;
    @InjectView(R.id.get_resupply_kit_helper_text)
    TextView resupplyHelperText;

    public ProfileResupplyViewConstructor(@NonNull Context context)
    {
        super(context);
        Utils.inject(getContext(), this);
    }

    @Override
    protected int getLayoutResourceId()
    {
        return R.layout.element_profile_resupply;
    }

    @Override
    protected boolean constructView(ViewGroup container, final ResupplyInfo resupplyInfo)
    {
        if (resupplyInfo.providerCanRequestSupplies())
        {
            if (resupplyInfo.providerCanRequestSuppliesNow())
            {
                resupplyButton.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        final Bundle args = new Bundle();
                        args.putSerializable(BundleKeys.RESUPPLY_INFO, resupplyInfo);
                        bus.post(new HandyEvent.SetLoadingOverlayVisibility(true));
                        bus.post(new HandyEvent.NavigateToTab(MainViewTab.REQUEST_SUPPLIES, args, TransitionStyle.SLIDE_UP));
                    }
                });
            }
            else
            {
                resupplyButton.setEnabled(false);
            }

            resupplyHelperText.setText(resupplyInfo.getHelperText());
            resupplyHelperText.setVisibility(View.VISIBLE);

            return true;
        }
        else
        {
            return false;
        }
    }
}

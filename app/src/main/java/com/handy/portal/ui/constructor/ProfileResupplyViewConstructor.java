package com.handy.portal.ui.constructor;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.handy.portal.R;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.model.ResupplyInfo;
import com.handy.portal.util.Utils;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

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
        bus.register(this);
    }

    @Override
    protected int getLayoutResourceId()
    {
        return R.layout.element_profile_resupply;
    }

    @Override
    protected boolean constructView(ViewGroup container, ResupplyInfo resupplyInfo)
    {
        if (resupplyInfo.isRequestSuppliesAllowed())
        {
            if (resupplyInfo.providerCanRequestSupplies())
            {
                resupplyButton.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        bus.post(new HandyEvent.SetLoadingOverlayVisibility(true));
                        bus.post(new HandyEvent.RequestSendResupplyKit());
                    }
                });
            }
            else
            {
                resupplyButton.setEnabled(false);
                resupplyHelperText.setText(resupplyInfo.getHelperText());
                resupplyHelperText.setVisibility(View.VISIBLE);
            }
            return true;
        }
        else
        {
            return false;
        }
    }

    @Subscribe
    public void onReceiveSendResupplyKitSuccess(HandyEvent.ReceiveSendResupplyKitSuccess event)
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        resupplyButton.setEnabled(false);
        resupplyHelperText.setVisibility(View.VISIBLE);
        resupplyHelperText.setText(R.string.resupply_kit_on_its_way);
        Toast.makeText(getContext(), R.string.resupply_kit_on_its_way, Toast.LENGTH_LONG).show();
    }

    @Subscribe
    public void onReceiveSendResupplyKitError(HandyEvent.ReceiveSendResupplyKitError event)
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        String message = event.error.getMessage();
        if (message == null)
        {
            message = getContext().getString(R.string.unable_to_process_request);
        }

        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }
}

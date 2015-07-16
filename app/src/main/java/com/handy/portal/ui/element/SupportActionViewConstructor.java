package com.handy.portal.ui.element;

import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.constant.SupportAction;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.model.Booking.Action;
import com.handy.portal.util.SupportActionUtils;
import com.squareup.otto.Bus;

import butterknife.InjectView;
import butterknife.OnClick;

public class SupportActionViewConstructor extends ViewConstructor<Action>
{
    @InjectView(R.id.support_action_icon)
    ImageView icon;
    @InjectView(R.id.support_action_text)
    TextView text;

    private Bus bus;
    private Action action;

    public SupportActionViewConstructor(Bus bus)
    {
        super();
        this.bus = bus;
    }

    @Override
    int getLayoutResourceId()
    {
        return R.layout.element_support_action;
    }

    @OnClick(R.id.support_action)
    public void triggerSupportAction()
    {
        bus.post(new HandyEvent.TriggerSupportAction(action));
    }

    @Override
    boolean constructView(ViewGroup container, Action action)
    {
        this.action = action;
        SupportAction supportAction = SupportActionUtils.getSupportAction(action);
        icon.setImageResource(supportAction.getIconId());
        text.setText(supportAction.getTextId());
        return true;
    }
}

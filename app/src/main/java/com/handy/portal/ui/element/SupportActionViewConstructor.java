package com.handy.portal.ui.element;

import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.model.Booking.Action;
import com.handy.portal.util.ActionUtils;

import butterknife.InjectView;

public class SupportActionViewConstructor extends ViewConstructor<Action>
{
    @InjectView(R.id.support_action_icon)
    ImageView icon;
    @InjectView(R.id.support_action_text)
    TextView text;

    @Override
    int getLayoutResourceId()
    {
        return R.layout.element_support_action;
    }

    @Override
    boolean constructView(ViewGroup container, Action action)
    {
        String actionName = action.getActionName();
        icon.setImageResource(ActionUtils.getActionIcon(actionName));
        text.setText(ActionUtils.getActionText(actionName));
        return true;
    }
}

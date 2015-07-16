package com.handy.portal.ui.element;

import android.view.ViewGroup;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.handy.portal.R;
import com.handy.portal.model.Booking;
import com.handy.portal.model.Booking.Action;
import com.handy.portal.util.ActionUtils;

import java.util.Collection;

import butterknife.InjectView;

public class SupportActionContainerViewConstructor extends ViewConstructor<Booking>
{
    @InjectView(R.id.support_actions_container)
    ViewGroup supportActionsContainer;

    @Override
    protected int getLayoutResourceId()
    {
        return R.layout.element_support_actions_container;
    }

    @Override
    void constructView(ViewGroup container, Booking booking)
    {
        Collection<Action> supportActions = Collections2.filter(booking.getAllowedActions(), new Predicate<Action>()
        {
            @Override
            public boolean apply(Action input)
            {
                return ActionUtils.SUPPORT_ACTION_NAMES.contains(input.getActionName());
            }
        });

        for (Action action : supportActions)
        {
            new SupportActionViewConstructor().create(getContext(), supportActionsContainer, action);
        }
    }
}

package com.handy.portal.ui.element;

import android.support.annotation.NonNull;
import android.view.ViewGroup;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.handy.portal.R;
import com.handy.portal.model.Booking;
import com.handy.portal.model.Booking.Action;
import com.squareup.otto.Bus;

import java.util.Collection;
import java.util.Set;

import butterknife.InjectView;

public class SupportActionContainerViewConstructor extends ViewConstructor<Booking>
{
    @InjectView(R.id.support_actions_container)
    ViewGroup supportActionsContainer;

    private Bus bus;
    private Set<String> allowedActionNames;

    public SupportActionContainerViewConstructor(Bus bus, @NonNull Set<String> allowedActionNames)
    {
        super();
        this.bus = bus;
        this.allowedActionNames = allowedActionNames;
    }

    @Override
    protected int getLayoutResourceId()
    {
        return R.layout.element_support_actions_container;
    }

    @Override
    boolean constructView(ViewGroup container, Booking booking)
    {
        Collection<Action> supportActions = Collections2.filter(booking.getAllowedActions(), new Predicate<Action>()
        {
            @Override
            public boolean apply(Action input)
            {
                return allowedActionNames.contains(input.getActionName());
            }
        });

        for (Action action : supportActions)
        {
            new SupportActionViewConstructor(bus)
                    .create(getContext(), supportActionsContainer, action);
        }

        return supportActions.size() > 0;
    }
}

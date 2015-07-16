package com.handy.portal.ui.element;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.ViewGroup;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.handy.portal.R;
import com.handy.portal.model.Booking;
import com.handy.portal.model.Booking.Action;

import java.util.Collection;
import java.util.Set;

import butterknife.InjectView;

public class SupportActionContainerViewInitializer extends ViewInitializer<Booking>
{
    @InjectView(R.id.support_actions_container)
    ViewGroup supportActionsContainer;

    private Set<String> allowedActionNames;

    public SupportActionContainerViewInitializer(Context context, @NonNull Set<String> allowedActionNames)
    {
        super(context);
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
            new SupportActionViewInitializer(getContext())
                    .create(supportActionsContainer, action);
        }

        return supportActions.size() > 0;
    }
}

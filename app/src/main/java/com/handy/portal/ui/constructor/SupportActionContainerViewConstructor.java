package com.handy.portal.ui.constructor;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.ViewGroup;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.handy.portal.R;
import com.handy.portal.constant.SupportActionType;
import com.handy.portal.model.Booking;
import com.handy.portal.model.Booking.Action;
import com.handy.portal.util.SupportActionUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.Bind;

public class SupportActionContainerViewConstructor extends ViewConstructor<Booking>
{
    @Bind(R.id.support_actions_container)
    ViewGroup supportActionsContainer;

    private Collection<String> allowedActionNames;

    public SupportActionContainerViewConstructor(Context context, @NonNull Collection<String> allowedActionNames)
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
    protected boolean constructView(ViewGroup container, Booking booking)
    {
        List<Action> supportActions = Lists.newArrayList(Collections2.filter(booking.getAllowedActions(), new Predicate<Action>()
        {
            @Override
            public boolean apply(Action input)
            {
                return allowedActionNames.contains(input.getActionName());
            }
        }));

        if (supportActions.size() > 0)
        {
            sortSupportActions(supportActions);

            for (Action action : supportActions)
            {
                new SupportActionViewConstructor(getContext()).create(supportActionsContainer, action);
            }
            return true;
        }
        else
        {
            return false;
        }
    }

    private void sortSupportActions(List<Action> supportActions)
    {
        // enums are sorted based on their declaration order in the enum class
        Collections.sort(supportActions, new Comparator<Action>()
        {
            @Override
            public int compare(Action action1, Action action2)
            {
                SupportActionType supportActionType1 = SupportActionUtils.getSupportActionType(action1);
                SupportActionType supportActionType2 = SupportActionUtils.getSupportActionType(action2);
                return supportActionType1.compareTo(supportActionType2);
            }
        });
    }
}

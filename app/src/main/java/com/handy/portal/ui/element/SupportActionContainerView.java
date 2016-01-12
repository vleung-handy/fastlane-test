package com.handy.portal.ui.element;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.handy.portal.R;
import com.handy.portal.constant.SupportActionType;
import com.handy.portal.model.Booking;
import com.handy.portal.util.SupportActionUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SupportActionContainerView extends FrameLayout
{
    @Bind(R.id.support_actions_container)
    ViewGroup supportActionsContainer;


    public SupportActionContainerView(@NonNull final Context context,
                                      @NonNull final Collection<String> allowedActionNames,
                                      @NonNull final Booking booking)
    {
        super(context);
        inflate(getContext(), R.layout.element_support_actions_container, this);
        ButterKnife.bind(this);

        Predicate<Booking.Action> predicate = new Predicate<Booking.Action>()
        {
            @Override
            public boolean apply(Booking.Action input)
            {
                return allowedActionNames.contains(input.getActionName());
            }
        };

        List<Booking.Action> supportActions =
                Lists.newArrayList(Collections2.filter(booking.getAllowedActions(), predicate));

        if (supportActions.size() > 0)
        {
            sortSupportActions(supportActions);

            for (Booking.Action action : supportActions)
            {
                supportActionsContainer.addView(new SupportActionView(getContext(), action));
            }
        }
        else
        {
            setVisibility(GONE);
        }
    }

    private void sortSupportActions(@NonNull List<Booking.Action> supportActions)
    {
        // enums are sorted based on their declaration order in the enum class
        Collections.sort(supportActions, new Comparator<Booking.Action>()
        {
            @Override
            public int compare(Booking.Action action1, Booking.Action action2)
            {
                SupportActionType supportActionType1 = SupportActionUtils.getSupportActionType(action1);
                SupportActionType supportActionType2 = SupportActionUtils.getSupportActionType(action2);
                return supportActionType1.compareTo(supportActionType2);
            }
        });
    }
}
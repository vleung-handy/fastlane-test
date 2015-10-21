package com.handy.portal.constant;

import com.handy.portal.ui.fragment.AvailableBookingsFragment;
import com.handy.portal.ui.fragment.BookingDetailsFragment;
import com.handy.portal.ui.fragment.ComplementaryBookingsFragment;
import com.handy.portal.ui.fragment.HelpContactFragment;
import com.handy.portal.ui.fragment.HelpFragment;
import com.handy.portal.ui.fragment.ProfileFragment;
import com.handy.portal.ui.fragment.ScheduledBookingsFragment;
import com.handy.portal.ui.fragment.payments.PaymentsDetailFragment;
import com.handy.portal.ui.fragment.payments.PaymentsFragment;
import com.handy.portal.ui.fragment.payments.PaymentsUpdateBankAccountFragment;
import com.handy.portal.ui.fragment.payments.PaymentsUpdateDebitCardFragment;
import com.handy.portal.ui.fragment.payments.SelectPaymentMethodFragment;

import java.io.Serializable;

public enum MainViewTab implements Serializable
{
    AVAILABLE_JOBS(AvailableBookingsFragment.class),
    SCHEDULED_JOBS(ScheduledBookingsFragment.class),
    COMPLEMENTARY_JOBS(ComplementaryBookingsFragment.class),
    SELECT_PAYMENT_METHOD(SelectPaymentMethodFragment.class),
    UPDATE_BANK_ACCOUNT(PaymentsUpdateBankAccountFragment.class),
    UPDATE_DEBIT_CARD(PaymentsUpdateDebitCardFragment.class),
    PAYMENTS(PaymentsFragment.class),
    PAYMENTS_DETAIL(PaymentsDetailFragment.class),
    PROFILE(ProfileFragment.class),
    HELP(HelpFragment.class),
    DETAILS(BookingDetailsFragment.class),
    HELP_CONTACT(HelpContactFragment.class),
    ;

    private Class classType;

    MainViewTab(Class classType)
    {
        this.classType = classType;
    }

    public Class getClassType()
    {
        return classType;
    }

    //If this gets complex setup small state machines to have a transition for each to/from tab
    public TransitionStyle getDefaultTransitionStyle(MainViewTab targetTab)
    {
        if (this.equals(targetTab))
        {
            return TransitionStyle.REFRESH_TAB;
        }

        if (this.equals(MainViewTab.AVAILABLE_JOBS) && targetTab.equals(MainViewTab.DETAILS))
        {
            return TransitionStyle.JOB_LIST_TO_DETAILS;
        }

        return TransitionStyle.NATIVE_TO_NATIVE;
    }

}

package com.handy.portal.payments;

import com.handy.portal.data.DataManager;
import com.handy.portal.payments.ui.adapter.PaymentBatchListAdapter;
import com.handy.portal.payments.ui.element.PaymentFeeBreakdownView;
import com.handy.portal.payments.ui.element.PaymentsBatchListView;
import com.handy.portal.payments.ui.fragment.BookingTransactionsFragment;
import com.handy.portal.payments.ui.fragment.BookingTransactionsWrapperFragment;
import com.handy.portal.payments.ui.fragment.OutstandingFeesFragment;
import com.handy.portal.payments.ui.fragment.PaymentBlockingFragment;
import com.handy.portal.payments.ui.fragment.PaymentsDetailFragment;
import com.handy.portal.payments.ui.fragment.PaymentsFragment;
import com.handy.portal.payments.ui.fragment.PaymentsUpdateBankAccountFragment;
import com.handy.portal.payments.ui.fragment.PaymentsUpdateDebitCardFragment;
import com.handy.portal.payments.ui.fragment.SelectPaymentMethodFragment;
import com.handy.portal.payments.ui.fragment.dialog.PaymentBillBlockerDialogFragment;
import com.handy.portal.payments.ui.fragment.dialog.PaymentFailedDialogFragment;
import com.handy.portal.payments.ui.fragment.dialog.PaymentSupportDialogFragment;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        library = true,
        complete = false,
        injects = {
                OutstandingFeesFragment.class,
                PaymentFeeBreakdownView.class,
                PaymentBatchListAdapter.class,
                PaymentsFragment.class,
                PaymentsDetailFragment.class,
                PaymentBillBlockerDialogFragment.class,
                PaymentsUpdateBankAccountFragment.class,
                PaymentsUpdateDebitCardFragment.class,
                SelectPaymentMethodFragment.class,
                PaymentsBatchListView.class,
                PaymentBlockingFragment.class,
                BookingTransactionsFragment.class,
                BookingTransactionsWrapperFragment.class,
                PaymentSupportDialogFragment.class,
                PaymentFailedDialogFragment.class,
        })
public final class PaymentsModule
{
    @Provides
    @Singleton
    final PaymentsManager providePaymentsManager(EventBus bus, final DataManager dataManager)
    {
        return new PaymentsManager(bus, dataManager);
    }
}

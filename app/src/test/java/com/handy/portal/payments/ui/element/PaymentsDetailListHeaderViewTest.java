package com.handy.portal.payments.ui.element;

import android.view.LayoutInflater;
import android.view.View;

import com.handy.portal.R;
import com.handy.portal.RobolectricGradleTestWrapper;
import com.handy.portal.core.ui.activity.TestActivity;
import com.handy.portal.payments.model.NeoPaymentBatch;
import com.handy.portal.payments.viewmodel.PaymentDetailHeaderViewModel;

import org.junit.Before;
import org.junit.Test;
import org.robolectric.Robolectric;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PaymentsDetailListHeaderViewTest extends RobolectricGradleTestWrapper {
    private PaymentsDetailListHeaderView paymentsDetailListHeaderView;
    private View expectDepositLayout;
    private NeoPaymentBatch neoPaymentBatch;

    @Before
    public void setUp() throws Exception {
        paymentsDetailListHeaderView = (PaymentsDetailListHeaderView) LayoutInflater
                .from(Robolectric.setupActivity(TestActivity.class)).inflate(R.layout.element_payment_details_list_header, null);
        expectDepositLayout = paymentsDetailListHeaderView.findViewById(R.id.payments_detail_list_header_payment_status_expected_deposit_date);

        neoPaymentBatch = mock(NeoPaymentBatch.class);
        Date date = new Date();
        when(neoPaymentBatch.getExpectedDepositDate()).thenReturn(date);
        when(neoPaymentBatch.getRemainingFeeAmount()).thenReturn(100);
    }

    @Test
    public void shouldNotDisplayDepositDateIfFailedOrPaid() {
        when(neoPaymentBatch.getStatus()).thenReturn("Failed");
        paymentsDetailListHeaderView.updateDisplay(new PaymentDetailHeaderViewModel(neoPaymentBatch, true));

        assertEquals("Should not see deposit date if failed", View.GONE, expectDepositLayout.getVisibility());

        when(neoPaymentBatch.getStatus()).thenReturn("Paid");
        paymentsDetailListHeaderView.updateDisplay(new PaymentDetailHeaderViewModel(neoPaymentBatch, true));

        assertEquals("Should not see deposit date if paid", View.GONE, expectDepositLayout.getVisibility());
    }

    @Test
    public void shouldDisplayDepositDateIfInReviewOrPending() {
        when(neoPaymentBatch.getStatus()).thenReturn("Pending");
        paymentsDetailListHeaderView.updateDisplay(new PaymentDetailHeaderViewModel(neoPaymentBatch, true));

        assertEquals("Should see deposit date if pending", View.VISIBLE, expectDepositLayout.getVisibility());

        when(neoPaymentBatch.getStatus()).thenReturn("In Review");
        paymentsDetailListHeaderView.updateDisplay(new PaymentDetailHeaderViewModel(neoPaymentBatch, true));

        assertEquals("Should see deposit date if in review", View.VISIBLE, expectDepositLayout.getVisibility());
    }

    @Test
    public void shouldSetCashOutButtonContainerVisibilityAccordingToViewModel() {
        PaymentDetailHeaderViewModel paymentDetailHeaderViewModel = mock(PaymentDetailHeaderViewModel.class);
        when(paymentDetailHeaderViewModel.shouldShowPaymentStatusLayout()).thenReturn(true);

        when(paymentDetailHeaderViewModel.shouldShowCashOutButton()).thenReturn(true);
        paymentsDetailListHeaderView.updateDisplay(paymentDetailHeaderViewModel);
        assertEquals("cash out button container is visible when view model denotes it", View.VISIBLE, paymentsDetailListHeaderView.mCashOutButtonContainerView.getVisibility());

        when(paymentDetailHeaderViewModel.shouldShowCashOutButton()).thenReturn(false);
        paymentsDetailListHeaderView.updateDisplay(paymentDetailHeaderViewModel);
        assertEquals("cash out button container is not visible when view model denotes it", View.GONE, paymentsDetailListHeaderView.mCashOutButtonContainerView.getVisibility());
    }

    @Test
    public void shouldSetCashOutButtonApparentlyEnabledAccordingToViewModel() {
        PaymentDetailHeaderViewModel paymentDetailHeaderViewModel = mock(PaymentDetailHeaderViewModel.class);
        when(paymentDetailHeaderViewModel.shouldShowPaymentStatusLayout()).thenReturn(true);

        when(paymentDetailHeaderViewModel.shouldApparentlyEnableCashOutButton()).thenReturn(true);
        paymentsDetailListHeaderView.updateDisplay(paymentDetailHeaderViewModel);
        assertEquals("cash out button container is apparently enabled when view model denotes it", 1f, paymentsDetailListHeaderView.mCashOutButtonContainerView.getAlpha(), 0.0001f);

        when(paymentDetailHeaderViewModel.shouldApparentlyEnableCashOutButton()).thenReturn(false);
        paymentsDetailListHeaderView.updateDisplay(paymentDetailHeaderViewModel);
        assertNotEquals("cash out button container is apparently disabled when view model denotes it", 1f, paymentsDetailListHeaderView.mCashOutButtonContainerView.getAlpha(), 0.0001f);
    }
}

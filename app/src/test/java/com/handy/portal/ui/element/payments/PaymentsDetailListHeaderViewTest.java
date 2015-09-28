package com.handy.portal.ui.element.payments;

import android.view.LayoutInflater;
import android.view.View;

import com.handy.portal.R;
import com.handy.portal.RobolectricGradleTestWrapper;
import com.handy.portal.model.payments.NeoPaymentBatch;
import com.handy.portal.ui.activity.TestActivity;

import org.junit.Before;
import org.junit.Test;
import org.robolectric.Robolectric;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PaymentsDetailListHeaderViewTest extends RobolectricGradleTestWrapper
{
    private PaymentsDetailListHeaderView paymentsDetailListHeaderView;
    private View expectDepositLayout;
    private NeoPaymentBatch neoPaymentBatch;

    @Before
    public void setUp() throws Exception
    {
        paymentsDetailListHeaderView = (PaymentsDetailListHeaderView) LayoutInflater
                .from(Robolectric.setupActivity(TestActivity.class)).inflate(R.layout.element_payment_details_list_header, null);
        expectDepositLayout = paymentsDetailListHeaderView.findViewById(R.id.payments_detail_expect_deposit_date_layout);

        neoPaymentBatch = mock(NeoPaymentBatch.class);
        Date date = new Date();
        when(neoPaymentBatch.getExptectedDepositDate()).thenReturn(date);
    }

    @Test
    public void shouldNotDisplayDepositDateIfFailedOrPaid()
    {
        when(neoPaymentBatch.getStatus()).thenReturn("Failed");
        paymentsDetailListHeaderView.updateDisplay(neoPaymentBatch);

        assertEquals("Should not see deposit date if failed", View.GONE, expectDepositLayout.getVisibility());

        when(neoPaymentBatch.getStatus()).thenReturn("Paid");
        paymentsDetailListHeaderView.updateDisplay(neoPaymentBatch);

        assertEquals("Should not see deposit date if paid", View.GONE, expectDepositLayout.getVisibility());
    }

    @Test
    public void shouldDisplayDepositDateIfInReviewOrPending()
    {
        when(neoPaymentBatch.getStatus()).thenReturn("Pending");
        paymentsDetailListHeaderView.updateDisplay(neoPaymentBatch);

        assertEquals("Should see deposit date if pending", View.VISIBLE, expectDepositLayout.getVisibility());

        when(neoPaymentBatch.getStatus()).thenReturn("In Review");
        paymentsDetailListHeaderView.updateDisplay(neoPaymentBatch);

        assertEquals("Should see deposit date if in review", View.VISIBLE, expectDepositLayout.getVisibility());
    }
}
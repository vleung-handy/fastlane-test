package com.handy.portal.ui.element.payments;

import android.view.LayoutInflater;
import android.view.View;

import com.handy.portal.R;
import com.handy.portal.RobolectricGradleTestWrapper;
import com.handy.portal.model.payments.NeoPaymentBatch;
import com.handy.portal.model.payments.PaymentBatches;
import com.handy.portal.ui.activity.TestActivity;

import org.junit.Before;
import org.junit.Test;
import org.robolectric.Robolectric;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PaymentsBatchListHeaderViewTest extends RobolectricGradleTestWrapper
{

    private PaymentsBatchListHeaderView batchListHeaderView;
    private View remainingRow;
    private NeoPaymentBatch neoPaymentBatch;
    private PaymentBatches paymentBatches;

    @Before
    public void setUp() throws Exception
    {
        batchListHeaderView = (PaymentsBatchListHeaderView) LayoutInflater
                .from(Robolectric.setupActivity(TestActivity.class)).inflate(R.layout.element_payments_batch_list_current_week_header, null);
        remainingRow = batchListHeaderView.findViewById(R.id.payments_current_week_remaining_withholdings_row);

        neoPaymentBatch = mock(NeoPaymentBatch.class);
        paymentBatches = mock(PaymentBatches.class);
        NeoPaymentBatch[] neoPaymentBatches = new NeoPaymentBatch[]{neoPaymentBatch};
        when(paymentBatches.getNeoPaymentBatches()).thenReturn(neoPaymentBatches);
    }

    @Test
    public void shouldNotDisplayRemainingIfItIsZero()
    {
        when(neoPaymentBatch.getRemainingWithholdingAmount()).thenReturn(0);
        batchListHeaderView.updateDisplay(paymentBatches);

        assertEquals("Should not see remaining payments row if remaining withholding is 0", View.GONE, remainingRow.getVisibility());
    }

    @Test
    public void shouldDisplayRemainingIfItIsNotZero()
    {
        when(neoPaymentBatch.getRemainingWithholdingAmount()).thenReturn(100);
        batchListHeaderView.updateDisplay(paymentBatches);

        assertEquals("Should see remaining payments row if remaining withholding is not 0", View.VISIBLE, remainingRow.getVisibility());
    }
}
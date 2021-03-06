package com.handy.portal.payments.ui.element;

import android.view.View;

import com.handy.portal.R;
import com.handy.portal.RobolectricGradleTestWrapper;
import com.handy.portal.core.ui.activity.TestActivity;
import com.handy.portal.payments.model.NeoPaymentBatch;
import com.handy.portal.payments.model.PaymentBatches;
import com.handy.portal.payments.viewmodel.PaymentBatchListHeaderViewModel;

import org.junit.Before;
import org.junit.Test;
import org.robolectric.Robolectric;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PaymentsBatchListHeaderViewTest extends RobolectricGradleTestWrapper {

    private PaymentsBatchListHeaderView batchListHeaderView;
    private View remainingRow;
    private NeoPaymentBatch neoPaymentBatch;
    private PaymentBatches paymentBatches;

    @Before
    public void setUp() throws Exception {
        batchListHeaderView = new PaymentsBatchListHeaderView(Robolectric.setupActivity(TestActivity.class));
        remainingRow = batchListHeaderView.findViewById(R.id.payments_current_week_remaining_fees_row);

        neoPaymentBatch = mock(NeoPaymentBatch.class);
        paymentBatches = mock(PaymentBatches.class);
        NeoPaymentBatch[] neoPaymentBatches = new NeoPaymentBatch[]{neoPaymentBatch};
        when(paymentBatches.getNeoPaymentBatches()).thenReturn(neoPaymentBatches);
    }

    @Test
    public void shouldNotDisplayRemainingIfItIsZero() {
        when(neoPaymentBatch.getRemainingFeeAmount()).thenReturn(0);
        batchListHeaderView.updateDisplay(new PaymentBatchListHeaderViewModel(paymentBatches.getNeoPaymentBatches()[0], null, true));

        assertEquals("Should not see remaining payments row if remaining fee is 0", View.GONE, remainingRow.getVisibility());
    }

    @Test
    public void shouldDisplayRemainingIfItIsNotZero() {
        when(neoPaymentBatch.getRemainingFeeAmount()).thenReturn(100);
        batchListHeaderView.updateDisplay(new PaymentBatchListHeaderViewModel(paymentBatches.getNeoPaymentBatches()[0], null, true));

        assertEquals("Should see remaining payments row if remaining fee is not 0", View.VISIBLE, remainingRow.getVisibility());
    }
}

package com.handy.portal.payments.ui.fragment.dialog;

import android.text.Html;
import android.text.Spanned;
import android.view.View;

import com.handy.portal.R;
import com.handy.portal.RobolectricGradleTestWrapper;
import com.handy.portal.core.TestBaseApplication;
import com.handy.portal.data.DataManager;
import com.handy.portal.payments.model.AdhocCashOutInfo;
import com.handy.portal.payments.model.AdhocCashOutRequest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.robolectric.shadows.ShadowApplication;
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class AdhocCashOutDialogFragmentTest extends RobolectricGradleTestWrapper {
    private AdhocCashOutDialogFragment mDialogFragment;

    @Mock
    private AdhocCashOutInfo mAdhocCashOutInfo;
    @Mock
    private AdhocCashOutInfo.PaymentMethodInfo mPaymentMethodInfo;

    private final String mPaymentMethodLast4Digits = "1234";
    private final String mHelpCenterUrl = "www.handy.com";

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        ((TestBaseApplication) ShadowApplication.getInstance().getApplicationContext()).inject(this);

        when(mPaymentMethodInfo.getLast4Digits()).thenReturn(mPaymentMethodLast4Digits);
        Date date = new Date();
        when(mAdhocCashOutInfo.getPaymentMethodInfo()).thenReturn(mPaymentMethodInfo);
        when(mAdhocCashOutInfo.getDateStart()).thenReturn(date);
        when(mAdhocCashOutInfo.getDateEnd()).thenReturn(date);
        when(mAdhocCashOutInfo.getCashOutFeeCents()).thenReturn(200);
        when(mAdhocCashOutInfo.getCurrencyCode()).thenReturn("USD");
        when(mAdhocCashOutInfo.getCurrencySymbol()).thenReturn("$");
        when(mAdhocCashOutInfo.getNetEarningsCents()).thenReturn(2000);
        when(mAdhocCashOutInfo.getExpectedPaymentCents()).thenReturn(1800);
        when(mAdhocCashOutInfo.getHelpCenterArticleUrl()).thenReturn(mHelpCenterUrl);

        mDialogFragment = AdhocCashOutDialogFragment.newInstance();
        SupportFragmentTestUtil.startFragment(mDialogFragment);

        mDialogFragment.updateWithModel(mAdhocCashOutInfo);
    }

    @Test
    public void shouldShowPaymentMethodInfo() {
        assertEquals("payment method info view is visible", View.VISIBLE, mDialogFragment.mPaymentMethodDetailsText.getVisibility());
        assertEquals("payment method info view shows formatted payment method info",
                mDialogFragment.getContext().getString(R.string.payment_method_details_button_formatted,
                        mPaymentMethodLast4Digits),
                mDialogFragment.mPaymentMethodDetailsText.getText());
    }

    @Test
    public void headerTextShouldContainHelpCenterLink() {
        String headerHtml = Html.toHtml((Spanned) mDialogFragment.mHeaderText.getText());

        assertEquals("info text contains help center link", true, headerHtml.contains(mHelpCenterUrl));
    }

    @Test
    public void shouldRequestCashOutWhenConfirmButtonClicked() {
        mDialogFragment.getView().findViewById(R.id.payments_cash_out_button).performClick();
        verify(mDialogFragment.mPaymentsManager).requestAdhocCashOut(
                any(AdhocCashOutRequest.class),
                any(DataManager.Callback.class)
        );
    }
}

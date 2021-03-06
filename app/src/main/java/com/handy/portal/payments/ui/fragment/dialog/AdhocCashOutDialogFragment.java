package com.handy.portal.payments.ui.fragment.dialog;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.text.Layout;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.handy.portal.R;
import com.handy.portal.core.manager.ProviderManager;
import com.handy.portal.core.model.SuccessWrapper;
import com.handy.portal.core.ui.activity.FragmentContainerActivity;
import com.handy.portal.data.DataManager;
import com.handy.portal.data.callback.FragmentSafeCallback;
import com.handy.portal.library.ui.fragment.dialog.FullScreenDialogFragment;
import com.handy.portal.library.util.CurrencyUtils;
import com.handy.portal.library.util.DateTimeUtils;
import com.handy.portal.library.util.TextUtils;
import com.handy.portal.logger.handylogger.model.PaymentsLog;
import com.handy.portal.payments.PaymentsManager;
import com.handy.portal.payments.model.AdhocCashOutInfo;
import com.handy.portal.payments.model.AdhocCashOutRequest;
import com.handy.portal.payments.ui.element.PaymentBreakdownLineItemView;
import com.handy.portal.webview.PortalWebViewFragment;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AdhocCashOutDialogFragment extends FullScreenDialogFragment {

    @BindView(R.id.payments_cash_out_content_container)
    View mContentContainer;

    /**
     * loading/error states are going to be made consistent throughout the app and
     * will be refactored in a separate PR. doing what other views are doing for now.
     */
    @BindView(R.id.fetch_error_view)
    View mErrorView;

    @BindView(R.id.fetch_error_text)
    TextView mErrorText;

    @BindView(R.id.payments_cash_out_copy_block_1)
    TextView mHeaderText;

    @BindView(R.id.payments_cash_out_date_range_text)
    TextView mDateRangeText;

    @BindView(R.id.payments_cash_out_expected_payment)
    TextView mExpectedPaymentText;

    @BindView(R.id.payments_cash_out_payment_method_details)
    TextView mPaymentMethodDetailsText;

    @BindView(R.id.payments_cash_out_net_earnings_line_item_view)
    PaymentBreakdownLineItemView mNetEarningsLineItem;

    @BindView(R.id.payments_cash_out_fee_line_item_view)
    PaymentBreakdownLineItemView mFeeLineItem;

    @Inject
    PaymentsManager mPaymentsManager;

    @Inject
    ProviderManager mProviderManager;

    @Inject
    EventBus mBus;

    private AdhocCashOutInfo mAdhocCashOutInfo;

    public static final String TAG = AdhocCashOutDialogFragment.class.getName();

    public static AdhocCashOutDialogFragment newInstance() {

        Bundle args = new Bundle();
        AdhocCashOutDialogFragment fragment = new AdhocCashOutDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private void refresh() {
        mErrorView.setVisibility(View.GONE);
        mContentContainer.setVisibility(View.GONE);
        showLoadingOverlay();
        mPaymentsManager.requestAdhocCashOutInfo(new FragmentSafeCallback<AdhocCashOutInfo>(this) {
            @Override
            public void onCallbackSuccess(final AdhocCashOutInfo response) {
                if (response.getSuccess() != null
                        && !response.getSuccess()) {
                    showErrorMessage(android.text.TextUtils.isEmpty(response.getErrorMessage()) ?
                            getString(R.string.an_error_has_occurred) : response.getErrorMessage());
                    mErrorView.setVisibility(View.VISIBLE);
                }
                else {
                    mContentContainer.setVisibility(View.VISIBLE);
                    updateWithModel(response);
                }
                hideLoadingOverlay();
            }

            @Override
            public void onCallbackError(final DataManager.DataManagerError error) {
                mErrorView.setVisibility(View.VISIBLE);
                hideLoadingOverlay();
            }
        });
    }

    @OnClick(R.id.try_again_button)
    public void onTryAgainButtonClicked() {
        refresh();
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh(); //don't want to risk showing outdated payment information
    }

    @VisibleForTesting
    protected void updateWithModel(@NonNull final AdhocCashOutInfo adhocCashOutInfo) {
        mAdhocCashOutInfo = adhocCashOutInfo;

        String headerHtml = getString(R.string.payment_cash_out_dialog_subtitle_html_formatted,
                adhocCashOutInfo.getHelpCenterArticleUrl());
        mHeaderText.setText(TextUtils.Support.fromHtml(headerHtml));

        /*
        copied from consumer
        don't know how to get callback for a specific link now
        but may figure that out later and put this in a shared place
         */
        mHeaderText.setMovementMethod(new LinkMovementMethod() {
            @Override
            public boolean onTouchEvent(
                    final TextView widget,
                    final Spannable buffer, final MotionEvent event
            ) {
                final int action = event.getAction();
                if (action == MotionEvent.ACTION_DOWN) {
                    final int x = (int) event.getX() - widget.getTotalPaddingLeft() +
                            widget.getScrollX();
                    final int y = (int) event.getY() - widget.getTotalPaddingTop() +
                            widget.getScrollY();
                    final Layout layout = widget.getLayout();
                    final int line = layout.getLineForVertical(y);
                    //get the tap position
                    final int off = layout.getOffsetForHorizontal(line, x);

                    //get the link at the tap position
                    final ClickableSpan[] link = buffer.getSpans(off, off, ClickableSpan.class);
                    if (link.length != 0 && off < buffer.length()) {
                        onBankHelpButtonClicked(adhocCashOutInfo.getHelpCenterArticleUrl());
                        return true;
                    }
                }
                return false;
            }
        });
        TextUtils.stripUnderlines(mHeaderText);

        mDateRangeText.setText(DateTimeUtils.formatDayRange(
                DateTimeUtils.SHORT_DAY_OF_WEEK_MONTH_DAY_FORMATTER,
                adhocCashOutInfo.getDateStart(),
                adhocCashOutInfo.getDateEnd()));

        mNetEarningsLineItem.updatePrice(adhocCashOutInfo.getNetEarningsCents(), adhocCashOutInfo.getCurrencySymbol());

        Integer cashOutFeeCents = adhocCashOutInfo.getCashOutFeeCents();
        if (cashOutFeeCents != null && cashOutFeeCents > 0) {
            //cash out fee should always show as negative
            cashOutFeeCents = -cashOutFeeCents;
        }
        mFeeLineItem.updatePrice(cashOutFeeCents, adhocCashOutInfo.getCurrencySymbol());
        if(mAdhocCashOutInfo.getExpectedPaymentCents() == null)
        {
            mExpectedPaymentText.setText(getString(R.string.no_data));
        }
        else
        {
            mExpectedPaymentText.setText(
                    CurrencyUtils.formatPrice(
                            mAdhocCashOutInfo.getExpectedPaymentCents(),
                            adhocCashOutInfo.getCurrencySymbol(),
                            true
                    ));
        }

        if (adhocCashOutInfo.getPaymentMethodInfo() == null ||
                android.text.TextUtils.isEmpty(adhocCashOutInfo.getPaymentMethodInfo().getLast4Digits())) {
            showErrorMessage(getString(R.string.an_error_has_occurred));
            dismiss();
        }
        else {
            mPaymentMethodDetailsText.setText(
                    getString(R.string.payment_method_details_button_formatted,
                            adhocCashOutInfo.getPaymentMethodInfo().getLast4Digits())
            );
        }

    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_payments_cash_out, container);
    }


    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        mErrorText.setText(R.string.error_missing_server_data);
    }

    @OnClick(R.id.payments_cash_out_dismiss_button)
    public void onDismissButtonClicked() {
        dismiss();
    }

    @OnClick(R.id.payments_cash_out_button)
    public void onCashOutButtonClicked() {

        if (mAdhocCashOutInfo.getCashOutFeeCents() == null) {
            //should never happen
            Crashlytics.logException(new Exception("Got null expected payment"));
            showErrorMessage(getString(R.string.an_error_has_occurred));
            return;
        }
        int expectedPaymentCents = mAdhocCashOutInfo.getExpectedPaymentCents();
        mBus.post(new PaymentsLog.CashOut.Adhoc.CashOutEarlyConfirmSelected(expectedPaymentCents));

        showLoadingOverlay();

        AdhocCashOutRequest adhocCashOutRequest
                = new AdhocCashOutRequest(mProviderManager.getLastProviderId(),
                expectedPaymentCents);

        mPaymentsManager.requestAdhocCashOut(
                adhocCashOutRequest,
                new FragmentSafeCallback<SuccessWrapper>(this) {
                    @Override
                    public void onCallbackSuccess(final SuccessWrapper response) {
                        hideLoadingOverlay();
                        if (!response.getSuccess()) {
                            showErrorMessage(response.getMessage());
                            refresh();
                            /*
                            refresh cash out info to ensure it is fresh.
                            we do not know the error reason without comparing the specific message.
                            error could be due to outdated info
                             */
                            return;
                        }

                        if (getParentFragment() != null) {
                            ((OnCashOutSuccessListener) getParentFragment()).onCashOutSuccess(response.getMessage());
                        }
                        dismissAllowingStateLoss();
                    }

                    @Override
                    public void onCallbackError(final DataManager.DataManagerError error) {
                        hideLoadingOverlay();
                        showErrorMessage(getString(R.string.an_error_has_occurred));
                    }
                });
    }

    private void onBankHelpButtonClicked(@NonNull String helpUrl)
    {
        mBus.post(new PaymentsLog.CashOut.Adhoc.CashOutEarlyBankHelpSelected());

        Bundle arguments = PortalWebViewFragment.createBundle(helpUrl, getString(R.string.help));
        Intent webviewIntent = FragmentContainerActivity.getIntent(
                getContext(),
                PortalWebViewFragment.class,
                arguments
        );
        startActivity(webviewIntent);
    }

    private void showErrorMessage(@Nullable String errorMessage) {
        Toast.makeText(getContext(), android.text.TextUtils.isEmpty(errorMessage) ?
                getString(R.string.an_error_has_occurred) : errorMessage, Toast.LENGTH_SHORT).show();
    }

    public interface OnCashOutSuccessListener {
        void onCashOutSuccess(@NonNull String message);
    }
}

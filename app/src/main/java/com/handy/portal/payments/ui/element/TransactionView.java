package com.handy.portal.payments.ui.element;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.library.util.CurrencyUtils;
import com.handy.portal.library.util.DateTimeUtils;
import com.handy.portal.library.util.FontUtils;
import com.handy.portal.library.util.TextUtils;
import com.handy.portal.payments.model.Transaction;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TransactionView extends FrameLayout
{
    @Bind(R.id.transaction_title_text)
    TextView mTitleText;
    @Bind(R.id.transaction_amount_text)
    TextView mAmountText;
    @Bind(R.id.transaction_batches_layout)
    ViewGroup mBatchesLayout;
    @Bind(R.id.transaction_policy_description_text)
    TextView mPolicyDescriptionText;
    @Bind(R.id.transaction_outstanding_text)
    TextView mOutstandingText;

    public TransactionView(final Context context)
    {
        super(context);
        init();
    }

    public TransactionView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public TransactionView(final Context context, final AttributeSet attrs, final int defStyle)
    {
        super(context, attrs, defStyle);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TransactionView(final Context context, final AttributeSet attrs, final int defStyleAttr, final int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init()
    {
        inflate(getContext(), R.layout.view_transaction, this);
        ButterKnife.bind(this);
    }

    public void setDisplay(@NonNull Transaction transaction, @Nullable TextUtils.LaunchWebViewCallback launchWebViewCallback)
    {
        mTitleText.setText(transaction.getTitle());
        mAmountText.setText(CurrencyUtils.formatPriceWithCents(
                transaction.getAmountInCents(), transaction.getCurrencySymbol()));
        mAmountText.setTextColor(ContextCompat.getColor(getContext(),
                transaction.getAmountInCents() < 0 ? R.color.plumber_red : R.color.black));
        for (Transaction.Batch batch : transaction.getPaymentBatches())
        {
            TextView textView = new TextView(getContext());
            String startDate = DateTimeUtils.formatDateMonthDay(batch.getDateStart());
            String endDate = DateTimeUtils.formatDateMonthDay(batch.getDateEnd());
            textView.setText(getResources().getString(R.string.payment_batch_formatted, startDate, endDate));
            textView.setTypeface(FontUtils.getFont(getContext(), FontUtils.CIRCULAR_BOOK));
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.default_text_size));
            textView.setTextColor(ContextCompat.getColor(getContext(), R.color.tertiary_gray));
            mBatchesLayout.addView(textView);
        }

        if (transaction.getPaymentBatches().length == 0)
        {
            mOutstandingText.setVisibility(VISIBLE);
        }

        if (transaction.getPolicy() != null)
        {
            Transaction.Policy policy = transaction.getPolicy();
            mPolicyDescriptionText.setVisibility(VISIBLE);
            String description = getResources().getString(
                    R.string.policy_description_formatted, policy.getDescription(), policy.getPolicyUrl());
            mPolicyDescriptionText.setLinkTextColor(ContextCompat.getColor(getContext(), R.color.partner_blue));
            mPolicyDescriptionText.setMovementMethod(LinkMovementMethod.getInstance());
            TextUtils.setTextViewHTML(mPolicyDescriptionText, description, launchWebViewCallback);
        }
    }
}

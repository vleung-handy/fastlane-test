package com.handy.portal.payments.ui.element;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.library.util.CurrencyUtils;
import com.handy.portal.library.util.TextUtils;
import com.handy.portal.library.util.ViewUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PaymentBreakdownLineItemView extends FrameLayout{

    @BindView(R.id.payment_breakdown_line_item_label_text)
    TextView mLabelText;
    @BindView(R.id.payment_breakdown_line_item_help_button)
    ImageView mQuestionMarkImage;
    @BindView(R.id.payment_breakdown_line_item_amount_text)
    TextView mAmountText;

    public PaymentBreakdownLineItemView(@NonNull final Context context) {
        super(context);
        init(null, 0, 0);
    }

    public PaymentBreakdownLineItemView(@NonNull final Context context, @Nullable final AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0, 0);
    }

    public PaymentBreakdownLineItemView(@NonNull final Context context, @Nullable final AttributeSet attrs, @AttrRes final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PaymentBreakdownLineItemView(@NonNull final Context context, @Nullable final AttributeSet attrs, @AttrRes final int defStyleAttr, @StyleRes final int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs, defStyleAttr, defStyleRes);
    }

    protected void init(final AttributeSet attributeSet, final int defStyleAttr, final int defStyleRes) {
        setSaveEnabled(true);
        inflate(getContext(), R.layout.element_payment_breakdown_line_item, this);
        ButterKnife.bind(this);

        if (attributeSet != null) {
            final TypedArray typedArray = getContext().obtainStyledAttributes(
                    attributeSet, R.styleable.PaymentBreakdownLineItemView);
            String labelText = typedArray.getString(R.styleable.PaymentBreakdownLineItemView_labelText);
            mLabelText.setText(labelText);

            String helpText = typedArray.getString(R.styleable.PaymentBreakdownLineItemView_helpText);
            updateHelpText(helpText);

            typedArray.recycle();
        }

        mLabelText.setId(ViewUtils.Support.generateViewId());
        mAmountText.setId(ViewUtils.Support.generateViewId());
    }

    public void updateLabelText(@Nullable String labelText)
    {
        mLabelText.setText(labelText);
    }

    public void updatePrice(@Nullable Integer priceCents, String currencySymbol)
    {
        if(priceCents == null)
        {
            mAmountText.setText(R.string.no_data);
        }
        else
        {
            mAmountText.setText(CurrencyUtils.formatPrice(priceCents, currencySymbol, true));
        }
    }

    /**
     * TODO fully implement when we need this functionality
     * @param helpText
     */
    public void updateHelpText(@Nullable String helpText)
    {
        if(TextUtils.isNullOrEmpty(helpText))
        {
            mQuestionMarkImage.setVisibility(GONE);
            mQuestionMarkImage.setOnClickListener(null);
        }
        else
        {
            mQuestionMarkImage.setVisibility(VISIBLE);
            mQuestionMarkImage.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(final View v) {
                    //todo implement when we need this functionality
                }
            });
        }
    }


}

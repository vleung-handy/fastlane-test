package com.handy.portal.library.ui.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.library.util.ViewUtils;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PriceView extends FrameLayout {

    @BindView(R.id.price_view_cardinal_text)
    TextView mCardinalText;
    @BindView(R.id.price_view_decimal_text)
    TextView mDecimalText;

    private String mCurrencySymbol = "";
    private Integer mPriceCents;
    private boolean mShouldDisplayEmptyDecimals; // If false, zero cents will be hidden

    public PriceView(Context context) {
        super(context);
        init(null, R.style.PriceView_TotalPayment);
    }

    @TargetApi(21)
    public PriceView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs, defStyleAttr);
    }

    private void init(AttributeSet attrs, int defStyle) {
        inflate(getContext(), R.layout.layout_price_view, this);
        ButterKnife.bind(this);
        mCardinalText.setId(ViewUtils.Support.generateViewId());
        mDecimalText.setId(ViewUtils.Support.generateViewId());

        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.PriceView, 0, 0);
        try {
            mShouldDisplayEmptyDecimals = ta
                    .getBoolean(R.styleable.PriceView_priceShowZeroCents, false);

            //for layout preview purposes
            mCurrencySymbol = ta.getString(R.styleable.PriceView_priceCurrencySymbol);
            if (ta.hasValue(R.styleable.PriceView_priceCents)) {
                mPriceCents = ta.getInteger(R.styleable.PriceView_priceCents, 0);
            }
        }
        finally {
            ta.recycle();
        }

        ta = getContext().obtainStyledAttributes(attrs, R.styleable.PriceView, defStyle, 0);
        try {
            int cardinalTextAppearance = ta.getResourceId(R.styleable.PriceView_cardinalTextAppearance, 0);
            if (cardinalTextAppearance > 0) {
                com.handy.portal.library.util.TextUtils.Support.setTextAppearance(
                        mCardinalText,
                        cardinalTextAppearance
                );
            }

            int decimalTextAppearance = ta.getResourceId(R.styleable.PriceView_decimalTextAppearance, 0);
            if (decimalTextAppearance > 0) {
                com.handy.portal.library.util.TextUtils.Support.setTextAppearance(
                        mDecimalText,
                        decimalTextAppearance
                );
            }
        }
        finally {
            ta.recycle();
        }

        updateUi();
    }

    private void updateUi() {
        if (mPriceCents == null) {
            mCardinalText.setText(getResources().getString(R.string.no_data));
            mDecimalText.setVisibility(GONE);
        }
        else {
            setCardinalText(getCardinalValue(mPriceCents), mCurrencySymbol);
            setDecimalText(getDecimalValue(mPriceCents));
        }

    }

    private void setCardinalText(final int cardinalValue, final String currencySymbol) {
        //show the negative symbol in front of the currency symbol
        String cardinalString = String.format(
                Locale.getDefault(),
                (cardinalValue < 0 ? "-" : "") + "%s%d",
                currencySymbol,
                Math.abs(cardinalValue));
        mCardinalText.setText(cardinalString);
    }

    private void setDecimalText(final int decimalValue) {
        String decimalString;
        if (decimalValue == 0 && !mShouldDisplayEmptyDecimals) {
            decimalString = "";
        }
        else {
            decimalString = String.format(Locale.getDefault(), "%02d", decimalValue);
        }
        mDecimalText.setText(decimalString);
        if (TextUtils.isEmpty(decimalString)) {
            mDecimalText.setVisibility(GONE);
        }
        else {
            mDecimalText.setVisibility(VISIBLE);
        }
    }

    public PriceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, R.style.PriceView_TotalPayment);
    }

    public PriceView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    public void setCurrencySymbol(final String currencySymbol) {
        mCurrencySymbol = currencySymbol == null ? "" : currencySymbol;
        updateUi();
    }

    public void setPriceCents(@Nullable final Integer priceCents) {
        mPriceCents = priceCents;
        updateUi();
    }

    public void setPrice(@Nullable final Integer priceCents, @NonNull String currencySymbol) {
        mCurrencySymbol = currencySymbol;
        setPriceCents(priceCents);
    }

    private static int getCardinalValue(final int cents) {
        return cents / 100;
    }

    private static int getDecimalValue(final int cents) {
        return cents % 100;
    }


}

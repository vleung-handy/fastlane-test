package com.handy.portal.bookings.ui.element;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Paint;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.handy.portal.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BookingCancellationPolicyListItemView extends RelativeLayout
{
    @BindView(R.id.cancellation_policy_list_item_left_text)
    TextView mLeftText;
    @BindView(R.id.cancellation_policy_list_item_waived_fee_text)
    TextView mWaivedFeeText;
    @BindView(R.id.cancellation_policy_list_item_fee_text)
    TextView mFeeText;
    @BindView(R.id.cancellation_policy_list_item_active_indicator)
    ImageView mActiveItemIndicator;
    @BindView(R.id.cancellation_policy_list_item_divider)
    View mDivider;

    public BookingCancellationPolicyListItemView(final Context context)
    {
        super(context);
        init();
    }

    public BookingCancellationPolicyListItemView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public BookingCancellationPolicyListItemView(final Context context, final AttributeSet attrs, final int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BookingCancellationPolicyListItemView(final Context context, final AttributeSet attrs, final int defStyleAttr, final int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init()
    {
        inflate(getContext(), R.layout.element_cancellation_policy_list_item, this);
        ButterKnife.bind(this);
        mWaivedFeeText.setPaintFlags(mWaivedFeeText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
    }

    public BookingCancellationPolicyListItemView setLeftText(String leftText)
    {
        mLeftText.setText(leftText);
        return this;
    }

    public BookingCancellationPolicyListItemView setWaivedFeeText(final String waivedFeeText)
    {
        if (waivedFeeText != null)
        {
            mWaivedFeeText.setText(waivedFeeText);
            mWaivedFeeText.setVisibility(VISIBLE);
        }
        else
        {
            mWaivedFeeText.setVisibility(GONE);
        }
        return this;
    }

    public BookingCancellationPolicyListItemView setFeeText(String feeText)
    {
        mFeeText.setText(feeText);
        return this;
    }

    public BookingCancellationPolicyListItemView setHighlighted(boolean highlighted)
    {
        int colorResourceId;
        if (highlighted)
        {
            colorResourceId = R.color.handy_blue;
            mActiveItemIndicator.setVisibility(VISIBLE);
        }
        else
        {
            colorResourceId = R.color.tertiary_gray;
            mActiveItemIndicator.setVisibility(GONE);
        }
        final int textColor = ContextCompat.getColor(getContext(), colorResourceId);
        mLeftText.setTextColor(textColor);
        mFeeText.setTextColor(textColor);
        return this;
    }

    public BookingCancellationPolicyListItemView setDividerVisible(boolean visible)
    {
        mDivider.setVisibility(visible ? VISIBLE : GONE);
        return this;
    }
}

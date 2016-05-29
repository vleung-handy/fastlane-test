package com.handy.portal.onboarding.ui.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.bookings.ui.element.BookingDetailsPaymentView;
import com.handy.portal.onboarding.model.BookingViewModel;
import com.handy.portal.payments.model.PaymentInfo;
import com.handy.portal.library.util.UIUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * This is a custom view used for showing jobs in the onboarding screen. It'll hold a check box,
 * job title & hours, and pricing
 * <p/>
 */
public class OnboardJobView extends FrameLayout implements CompoundButton.OnCheckedChangeListener
{

    private int mCornerRadius;

    @Bind(R.id.check_box)
    CheckBox mCheckBox;

    @Bind(R.id.onboard_payment)
    BookingDetailsPaymentView mPayment;

    @Bind(R.id.onboard_payment_bonus)
    TextView mBonusPaymentText;

    @Bind(R.id.tv_title)
    TextView mTitle;

    @Bind(R.id.tv_subtitle)
    TextView mSubTitle;

    private Drawable mCheckedDrawable;
    private Drawable mUncheckedDrawable;

    private CompoundButton.OnCheckedChangeListener mOnCheckedChangeListener;

    private BookingViewModel mBookingViewModel;

    public OnboardJobView(Context context)
    {
        super(context);
        init();
    }

    public OnboardJobView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public OnboardJobView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init()
    {
        mCornerRadius = getResources().getDimensionPixelSize(R.dimen.medium_corner_radius);
        inflate(getContext(), R.layout.onboard_job_layout, this);
        ButterKnife.bind(this);

        mUncheckedDrawable = ContextCompat.getDrawable(getContext(), R.drawable.border_gray_bg_white);
        mCheckedDrawable = ContextCompat.getDrawable(getContext(), R.drawable.border_green_bg_white);

        setBackground(mUncheckedDrawable);
        mCheckBox.setOnCheckedChangeListener(this);
        setClickable(true);

        setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mCheckBox.setChecked(!mBookingViewModel.isSelected());
            }
        });
    }

    public void setOnCheckedChangeListener(final CompoundButton.OnCheckedChangeListener onCheckedChangeListener)
    {
        mOnCheckedChangeListener = onCheckedChangeListener;
    }

    public void bind(BookingViewModel bookingViewModel)
    {
        mBookingViewModel = bookingViewModel;

        //Payment
        mPayment.init(bookingViewModel.getBooking());

        //Bonus Payment
        PaymentInfo paymentInfo = bookingViewModel.getBooking().getBonusPaymentToProvider();
        if (paymentInfo != null && paymentInfo.getAmount() > 0)
        {
            UIUtils.setPaymentInfo(mBonusPaymentText, null, paymentInfo,
                    getResources().getString(R.string.bonus_payment_value));
        }

        mTitle.setText(bookingViewModel.getTitle());
        mSubTitle.setText(bookingViewModel.getSubTitle());
        mCheckBox.setChecked(bookingViewModel.isSelected());
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
    {
        mBookingViewModel.setSelected(isChecked);
        if (isChecked)
        {
            this.setBackground(mCheckedDrawable);
        }
        else
        {
            this.setBackground(mUncheckedDrawable);
        }
        if (mOnCheckedChangeListener != null)
        {
            mOnCheckedChangeListener.onCheckedChanged(buttonView, isChecked);
        }
    }
}

package com.handy.portal.payments.ui.element;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.FrameLayout;

import com.handy.portal.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CashOutButtonContainerView extends FrameLayout {
    @BindView(R.id.payments_cash_out_button)
    Button mCashOutButton;

    public CashOutButtonContainerView(final Context context) {
        super(context);
        init();
    }

    public CashOutButtonContainerView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CashOutButtonContainerView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CashOutButtonContainerView(final Context context, final AttributeSet attrs, final int defStyleAttr, final int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.element_payments_cash_out_button_container, this);
        ButterKnife.bind(this);
    }

    public void setButtonOnClickListener(OnClickListener onClickListener) {
        mCashOutButton.setOnClickListener(onClickListener);
    }

    /**
     * this cannot be put into styles.xml and has to be done at runtime
     * because we cannot use a native state, such as "state_enabled"
     * because we still need it to be clickable
     */
    public void setApparentlyEnabled(boolean apparentlyEnabled) {
        if (apparentlyEnabled) {
            setAlpha(1f);
        }
        else {
            setAlpha(0.5f);
        }
    }

}

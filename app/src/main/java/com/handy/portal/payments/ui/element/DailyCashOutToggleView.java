package com.handy.portal.payments.ui.element;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.SwitchCompat;
import android.text.Layout;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.library.util.TextUtils;
import com.handy.portal.payments.model.PaymentBatches;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DailyCashOutToggleView extends FrameLayout {
    @BindView(R.id.payments_daily_cash_out_toggle)
    SwitchCompat mDailyCashOutToggle;

    @BindView(R.id.payments_daily_cash_out_toggle_info_text)
    TextView mDailyCashOutInfoText;

    public DailyCashOutToggleView(final Context context) {
        super(context);
        init();
    }

    public DailyCashOutToggleView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DailyCashOutToggleView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public DailyCashOutToggleView(final Context context, final AttributeSet attrs, final int defStyleAttr, final int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.element_payments_daily_cash_out_toggle, this);
        ButterKnife.bind(this);

        TextUtils.stripUnderlines(mDailyCashOutInfoText);
    }

    public void setToggleChecked(boolean checked) {
        mDailyCashOutToggle.setChecked(checked);
    }

    OnClickListener mOnHelpCenterUrlClickedListener;
    public void setBodyText(@Nullable String text) {
        mDailyCashOutInfoText.setText(TextUtils.Support.fromHtml(text));
        mDailyCashOutInfoText.setMovementMethod(new LinkMovementMethod() {
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
                        if (mOnHelpCenterUrlClickedListener != null) {
                            mOnHelpCenterUrlClickedListener.onClick(widget);
                        }
//                        onBankHelpButtonClicked(paymentCashOutInfo.getHelpCenterArticleUrl());
                        return true;
                    }
                }
                return false;
            }
        });
    }

    public void setModel(@NonNull PaymentBatches.DailyCashOutInfo dailyCashOutInfo) {
        mDailyCashOutToggle.setChecked(dailyCashOutInfo.isEnabled());
    }

    public void setClickListeners(CompoundButton.OnCheckedChangeListener onToggleChangedListener,
                                  OnClickListener helpCenterUrlClickedListener) {
        //FIXME remove, test only
//        mDailyCashOutToggle.setOnTouchListener(new OnTouchListener() {
//            @Override
//            public boolean onTouch(final View v, final MotionEvent event) {
//                AlertDialog alertDialog = new AlertDialog.Builder(getContext())
//                        .setPositiveButton("yes", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(final DialogInterface dialog, final int which) {
//                                Toast.makeText(getContext(), "checked: " + mDailyCashOutToggle.isChecked(), Toast.LENGTH_SHORT).show();
//                            }
//                        })
//                        .setNegativeButton("no", null)
//                        .setMessage("Some message")
//                        .create();
//                alertDialog.show();
//                return true;
//            }
//        });

        mDailyCashOutToggle.setOnCheckedChangeListener(onToggleChangedListener);
        mOnHelpCenterUrlClickedListener = helpCenterUrlClickedListener;

    }
}

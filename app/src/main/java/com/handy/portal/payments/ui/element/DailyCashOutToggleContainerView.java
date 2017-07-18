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
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.library.util.TextUtils;
import com.handy.portal.payments.viewmodel.DailyCashOutToggleContainerViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DailyCashOutToggleContainerView extends FrameLayout {
    @BindView(R.id.payments_daily_cash_out_toggle)
    SwitchCompat mDailyCashOutToggle;

    @BindView(R.id.payments_daily_cash_out_toggle_info_text)
    TextView mDailyCashOutInfoText;

    @BindView(R.id.payments_daily_cash_out_toggle_container)
    View mContainer;

    private ToggleContainerClickListener mToggleContainerClickListener;
    private DailyCashOutToggleContainerViewModel mDailyCashOutToggleContainerViewModel;

    public DailyCashOutToggleContainerView(final Context context) {
        super(context);
        init();
    }

    public DailyCashOutToggleContainerView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DailyCashOutToggleContainerView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public DailyCashOutToggleContainerView(final Context context, final AttributeSet attrs, final int defStyleAttr, final int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.element_payments_daily_cash_out_toggle_container, this);
        ButterKnife.bind(this);

        TextUtils.stripUnderlines(mDailyCashOutInfoText);
    }

    public void updateWithModel(@NonNull DailyCashOutToggleContainerViewModel dailyCashOutToggleContainerViewModel) {
        mDailyCashOutToggleContainerViewModel = dailyCashOutToggleContainerViewModel;

        if (dailyCashOutToggleContainerViewModel.isViewVisible()) {
            mDailyCashOutToggle.setChecked(dailyCashOutToggleContainerViewModel.isToggleChecked());
            setBodyText(dailyCashOutToggleContainerViewModel.getInfoTextFormatted(getContext()));
            setApparentlyEnabled(dailyCashOutToggleContainerViewModel.isViewApparentlyEnabled());
            setVisibility(VISIBLE);
        }
        else {
            setVisibility(GONE);
        }
    }

    public void setApparentlyEnabled(final boolean enabled) {
        if (enabled) {
            mContainer.setAlpha(1f);
        }
        else {
            mContainer.setAlpha(0.5f);
        }
    }

    public void setClickListeners(@Nullable ToggleContainerClickListener toggleContainerClickListener) {
        mToggleContainerClickListener = toggleContainerClickListener;
        if (toggleContainerClickListener == null) {
            mDailyCashOutToggle.setOnTouchListener(null);
            return;
        }
        mDailyCashOutToggle.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(final View v, final MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP
                        && mToggleContainerClickListener != null) {
                    mToggleContainerClickListener.onToggleClicked(mDailyCashOutToggle);
                }
                return true;
            }
        });

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (mDailyCashOutToggleContainerViewModel != null
                        && !mDailyCashOutToggleContainerViewModel.isViewApparentlyEnabled()
                        && mToggleContainerClickListener != null) {
                    mToggleContainerClickListener.onApparentlyDisabledContainerClicked();
                }
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(final MotionEvent ev) {
        if (mDailyCashOutToggleContainerViewModel != null
                && !mDailyCashOutToggleContainerViewModel.isViewApparentlyEnabled()) {
            return true; //don't propagate this event to children
        }
        return false;
    }

    public interface ToggleContainerClickListener {

        /**
         * only gets triggered if the container is apparently enabled
         */
        void onToggleClicked(@NonNull SwitchCompat toggleView);

        /**
         * only gets triggered if the container is apparently enabled
         */
        void onToggleInfoHelpCenterLinkClicked(@NonNull SwitchCompat toggleView);

        /**
         * only gets triggered if the container is apparently disabled
         */
        void onApparentlyDisabledContainerClicked();
    }

    private void setBodyText(@Nullable String text) {
        mDailyCashOutInfoText.setText(TextUtils.Support.fromHtml(text));

        //TODO copied from somewhere; this only handles the case in which there is one link. put this in a util
        mDailyCashOutInfoText.setMovementMethod(new LinkMovementMethod() {
            @Override
            public boolean onTouchEvent(
                    final TextView widget,
                    final Spannable buffer, final MotionEvent event
            ) {
                final int action = event.getAction();
                if (action == MotionEvent.ACTION_UP) {
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
                        if (mToggleContainerClickListener != null) {
                            mToggleContainerClickListener.onToggleInfoHelpCenterLinkClicked(mDailyCashOutToggle);
                            return true;
                        }
                    }
                }
                return false;
            }
        });
        TextUtils.stripUnderlines(mDailyCashOutInfoText);
    }
}

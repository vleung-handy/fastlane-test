package com.handy.portal.payments.ui.fragment.dialog;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.core.constant.MainViewPage;
import com.handy.portal.core.constant.TransitionStyle;
import com.handy.portal.core.event.NavigationEvent;
import com.handy.portal.library.ui.fragment.dialog.PopupDialogFragment;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

import butterknife.BindView;

public class PaymentBillBlockerDialogFragment extends PopupDialogFragment //TODO: consolidate some of this logic with other dialog fragments
{
    @Inject
    EventBus mBus;

    @BindView(R.id.payments_bill_blocker_content)
    TextView mPaymentBlockerContentText;
    @BindView(R.id.payments_bill_blocker_update_now_button)
    protected Button updateNowButton;

    @BindView(R.id.payments_bill_blocker_later_button)
    protected Button laterButton;

    public static final String FRAGMENT_TAG = "fragment_dialog_payment_bill_blocker";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_dialog_payment_bill_blocker, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        String coloredText = getString(R.string.payment_bill_blocker_content_colored);
        String content = getString(R.string.payments_bill_blocker_content_formatted, coloredText);

        SpannableString spannableString = new SpannableString(content);
        int startIndex = content.indexOf(coloredText);
        spannableString.setSpan(
                new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.plumber_red)),
                startIndex,
                startIndex + coloredText.length(), 0);
        mPaymentBlockerContentText.setText(spannableString, TextView.BufferType.SPANNABLE);

        updateNowButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mBus.post(new NavigationEvent.NavigateToPage(MainViewPage.SELECT_PAYMENT_METHOD, new Bundle(), TransitionStyle.REFRESH_PAGE, true));
                PaymentBillBlockerDialogFragment.this.dismiss();
            }
        });
        laterButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                PaymentBillBlockerDialogFragment.this.dismiss();
            }
        });
    }
}

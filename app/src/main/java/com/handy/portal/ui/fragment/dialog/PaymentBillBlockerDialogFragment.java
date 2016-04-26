package com.handy.portal.ui.fragment.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.constant.MainViewTab;
import com.handy.portal.constant.TransitionStyle;
import com.handy.portal.event.NavigationEvent;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PaymentBillBlockerDialogFragment extends InjectedDialogFragment //TODO: consolidate some of this logic with other dialog fragments
{

    @Bind(R.id.payments_bill_blocker_content)
    TextView mPaymentBlockerContentText;
    @Bind(R.id.payments_bill_blocker_update_now_button)
    protected Button updateNowButton;

    @Bind(R.id.payments_bill_blocker_later_button)
    protected Button laterButton;

    public static final String FRAGMENT_TAG = "fragment_dialog_payment_bill_blocker";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_dialog_payment_bill_blocker, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState)
    {
        Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations =
                R.style.dialog_animation_slide_down_up_from_top;
        return dialog;
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
                new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.error_red)),
                startIndex,
                startIndex + coloredText.length(), 0);
        mPaymentBlockerContentText.setText(spannableString, TextView.BufferType.SPANNABLE);

        updateNowButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mBus.post(new NavigationEvent.NavigateToTab(MainViewTab.SELECT_PAYMENT_METHOD, new Bundle(), TransitionStyle.REFRESH_TAB, true));
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

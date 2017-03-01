package com.handy.portal.payments.ui.fragment.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.handy.portal.R;
import com.handy.portal.core.constant.MainViewPage;
import com.handy.portal.core.constant.TransitionStyle;
import com.handy.portal.core.event.NavigationEvent;
import com.handy.portal.library.ui.fragment.dialog.PopupDialogFragment;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

import butterknife.BindView;

/**
 * dialog that shows when payment status is failed
 * links to update payment method
 */
public class PaymentFailedDialogFragment extends PopupDialogFragment {
    public static final String FRAGMENT_TAG = PaymentFailedDialogFragment.class.getName();

    @BindView(R.id.fragment_dialog_payment_failed_update_payment_method_now_button)
    Button mUpdateNowButton;

    @BindView(R.id.fragment_dialog_payment_failed_update_payment_method_later_button)
    Button mNotNowButton;

    @Inject
    EventBus mBus;

    public static PaymentFailedDialogFragment newInstance() {
        return new PaymentFailedDialogFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dialog_payment_failed, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mUpdateNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBus.post(
                        new NavigationEvent.NavigateToPage(
                                MainViewPage.SELECT_PAYMENT_METHOD,
                                new Bundle(),
                                TransitionStyle.REFRESH_PAGE,
                                true
                        ));
                PaymentFailedDialogFragment.this.dismiss();
            }
        });
        mNotNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PaymentFailedDialogFragment.this.dismiss();
            }
        });
    }
}

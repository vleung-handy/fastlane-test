package com.handy.portal.ui.fragment.payments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.constant.MainViewTab;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.event.PaymentEvents;
import com.handy.portal.ui.fragment.ActionBarFragment;
import com.handy.portal.util.UIUtils;
import com.squareup.otto.Subscribe;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class PaymentMethodFragment extends ActionBarFragment
{
    @InjectView(R.id.payment_method_title)
    TextView titleText;

    @InjectView(R.id.payment_method_account_details_text)
    TextView accountDetailsText;

    @InjectView(R.id.payment_method_update_button)
    TextView updatePaymentMethodButton;

    @InjectView(R.id.payment_method_container)
    LinearLayout paymentMethodContainer;

    @InjectView(R.id.payment_method_account_detail_row)
    View accountDetailRow;

    @Override
    protected MainViewTab getTab()
    {
        return MainViewTab.PAYMENTS;
    }

    @Override
    public void onCreate(Bundle savedInstance)
    {
        super.onCreate(savedInstance);
        setOptionsMenuEnabled(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.menu_x_back, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case R.id.action_exit:
                onBackButtonPressed();
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        setActionBar(R.string.payment_method, false);
        bus.post(new PaymentEvents.RequestPaymentFlow());
        paymentMethodContainer.setVisibility(View.GONE);
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(true));

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_payments_info, container, false);
        ButterKnife.inject(this, view);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        updatePaymentMethodButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                launchUpdatePaymentMethodFragment();
            }
        });
    }

    private void launchUpdatePaymentMethodFragment()
    {
        UIUtils.launchFragmentInMainActivityOnBackStack(getActivity(), new UpdatePaymentFragment());
    }

    @Subscribe
    public void onGetPaymentFlowSuccess(PaymentEvents.ReceivePaymentFlowSuccess event)
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));

        if (event.response.getAccountDetails() != null)
        {
            accountDetailsText.setText(event.response.getAccountDetails());
        }
        else
        {
            titleText.setText(R.string.add_payment_method);
            accountDetailRow.setVisibility(View.GONE);
        }
        paymentMethodContainer.setVisibility(View.VISIBLE);
    }

    @Subscribe
    public void onGetPaymentFlowError(PaymentEvents.ReceivePaymentFlowError event)
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        showToast(R.string.payment_flow_error);
    }
}

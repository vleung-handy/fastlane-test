package com.handy.portal.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.handy.portal.R;
import com.handy.portal.model.payments.NeoPaymentBatch;
import com.handy.portal.model.payments.PaymentBatch;
import com.handy.portal.model.payments.PaymentBatches;
import com.handy.portal.ui.element.payments.PaymentsBatchListItemView;

public class PaymentBatchListElementAdapter extends ArrayAdapter<PaymentBatch>
{
    public PaymentBatchListElementAdapter(Context context)
    {
        super(context, R.layout.element_payments_batch_list_entry, 0);
    }

    public void setData(PaymentBatches paymentBatches)
    {
        clear();
        appendData(paymentBatches);
    }

    public void appendData(PaymentBatches paymentBatches)
    {
        addAll(paymentBatches.getAggregateBatchList());
        notifyDataSetChanged();
    }

    @Override
    public boolean areAllItemsEnabled() //supposed to fix (only in Android <5.0) issue in which dividers for disabled items are invisible
    {
        return true;
    }

    @Override
    public boolean isEnabled(int position)
    {
        PaymentBatch paymentBatch = getItem(position);
        return paymentBatch instanceof NeoPaymentBatch;//we're not allowing users to view legacy payment batch details
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        PaymentBatch paymentBatch = getItem(position);
        View v = convertView;
        if (v == null)
        {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.element_payments_batch_list_entry, null);
        }
        ((PaymentsBatchListItemView) v).updateDisplay(paymentBatch);

        v.setEnabled((paymentBatch instanceof NeoPaymentBatch)); //isEnabled doesn't trigger the enabled styling
        return v;
    }
}

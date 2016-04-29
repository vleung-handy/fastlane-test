package com.handy.portal.payments.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;

import com.handy.portal.R;
import com.handy.portal.payments.model.NeoPaymentBatch;
import com.handy.portal.payments.model.Payment;
import com.handy.portal.payments.model.PaymentGroup;
import com.handy.portal.payments.ui.element.PaymentsDetailGroupView;
import com.handy.portal.payments.ui.element.PaymentsDetailItemView;

public class PaymentDetailExpandableListAdapter extends BaseExpandableListAdapter
{
    private NeoPaymentBatch neoPaymentBatch;

    public PaymentDetailExpandableListAdapter(NeoPaymentBatch neoPaymentBatch)
    {
        this.neoPaymentBatch = neoPaymentBatch;
    }

    @Override
    public boolean areAllItemsEnabled()
    {
        return true;
    }

    public void setData(NeoPaymentBatch neoPaymentBatch)
    {
        this.neoPaymentBatch = neoPaymentBatch;
    }

    @Override
    public int getGroupCount()
    {
        return neoPaymentBatch.getPaymentGroups().length;
    }

    @Override
    public int getChildrenCount(int groupPosition)
    {
        return getGroup(groupPosition).getPayments().length;
    }

    @Override
    public PaymentGroup getGroup(int groupPosition)
    {
        return neoPaymentBatch.getPaymentGroups()[groupPosition];
    }

    @Override
    public Payment getChild(int groupPosition, int childPosition)
    {
        return getGroup(groupPosition).getPayments()[childPosition];
    }

    @Override
    public long getGroupId(int groupPosition)
    {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition)
    {
        return 0;
    }

    @Override
    public boolean hasStableIds()
    {
        return false;
    }


    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent)
    {
        PaymentGroup paymentGroup = getGroup(groupPosition);
        if(convertView==null)
        {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            convertView = inflater.inflate(R.layout.element_payments_detail_list_group_header, null);
        }
        ((PaymentsDetailGroupView)convertView).updateDisplay(paymentGroup, neoPaymentBatch);

        //TODO: see if these are all necessary
        convertView.setEnabled(false);
        convertView.setFocusable(false);
        convertView.setOnClickListener(null);

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent)
    {
        Payment payment = getChild(groupPosition, childPosition);
        if(convertView==null)
        {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            convertView = inflater.inflate(R.layout.element_payments_detail_list_entry, null);
        }
        ((PaymentsDetailItemView)convertView).updateDisplay(payment, neoPaymentBatch);

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition)
    {
        Payment payment = getChild(groupPosition, childPosition);
        return payment.getBookingId() != null;
    }
}

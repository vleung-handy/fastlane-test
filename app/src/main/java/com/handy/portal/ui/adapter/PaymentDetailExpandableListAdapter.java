package com.handy.portal.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;

import com.handy.portal.R;
import com.handy.portal.model.payments.NeoPaymentBatch;
import com.handy.portal.model.payments.Payment;
import com.handy.portal.model.payments.PaymentGroup;
import com.handy.portal.ui.element.payments.PaymentsDetailGroupView;
import com.handy.portal.ui.element.payments.PaymentsDetailItemView;

public class PaymentDetailExpandableListAdapter extends BaseExpandableListAdapter
{

    NeoPaymentBatch neoPaymentBatch;
    public PaymentDetailExpandableListAdapter()
    {

    }

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
            LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.element_payments_detail_group_header, null);
        }
        ((PaymentsDetailGroupView)convertView).updateDisplay(paymentGroup, neoPaymentBatch);

        convertView.setEnabled(false);
        convertView.setFocusable(false);
        convertView.setOnClickListener(null);
        if(paymentGroup.getPayments().length == 0)
        {
            convertView.setVisibility(View.GONE); //temporary hack for now until server side is changed
        }else{
            convertView.setVisibility(View.VISIBLE);
        }
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent)
    {
        Payment payment = getChild(groupPosition, childPosition);
        if(convertView==null)
        {
            LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.element_payments_detail_list_entry, null);
        }
        ((PaymentsDetailItemView)convertView).updateDisplay(payment, neoPaymentBatch);

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition)
    {
        return false;
    }
}

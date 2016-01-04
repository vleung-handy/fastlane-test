package com.handy.portal.ui.element;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handy.portal.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SupplyListItemView extends LinearLayout
{
    @Bind(R.id.supply_list_item_amount)
    protected TextView supplyListItemAmountView;

    @Bind(R.id.supply_list_item_type)
    protected TextView supplyListItemTypeView;

    public SupplyListItemView(Context context)
    {
        super(context);
    }

    public SupplyListItemView(Context context, AttributeSet attributeSet)
    {
        super(context, attributeSet);
    }

    @Override
    protected void onFinishInflate()
    {
        super.onFinishInflate();
        ButterKnife.bind(this);
    }

    public void updateDisplay(String supplyItemType, String supplyItemAmount)
    {
        supplyListItemAmountView.setText(supplyItemAmount);
        supplyListItemTypeView.setText(supplyItemType);
    }
}

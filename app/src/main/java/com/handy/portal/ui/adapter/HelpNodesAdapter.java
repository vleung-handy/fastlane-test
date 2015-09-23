package com.handy.portal.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.model.HelpNode;

import java.util.List;

public class HelpNodesAdapter extends ArrayAdapter<HelpNode>
{
    private static final int PAYMENTS_NODE_ID = 922;
    private int layoutResourceId;
    private List<HelpNode> data;

    public HelpNodesAdapter(Context context, int resource, List<HelpNode> objects)
    {
        super(context, resource, objects);
        layoutResourceId = resource;
        data = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        if (convertView == null)
        {
            convertView = LayoutInflater.from(getContext()).inflate(layoutResourceId, parent, false);
        }

        TextView text = (TextView) convertView.findViewById(R.id.support_action_text);
        ImageView icon = (ImageView) convertView.findViewById(R.id.support_action_icon);

        // The Payments item need to display a different icon and text
        if (data.get(position).getId() == PAYMENTS_NODE_ID)
        {
            text.setText(getContext().getString(R.string.different_issue));
            icon.setImageResource(R.drawable.ic_support);
        }
        else
        {
            text.setText(data.get(position).getLabel());
            icon.setImageResource(R.drawable.ic_document);
        }
        return convertView;
    }
}

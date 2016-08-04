package com.handy.portal.layer;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.handy.portal.R;

import java.util.List;

public class ChatRecyclerAdapter extends RecyclerView.Adapter<ChatViewHolder>
{

    private final int mDefaultMargin;
    private List<ChatItem> mList;


    public ChatRecyclerAdapter(List<ChatItem> itemList, int defaultMargin)
    {
        mList = itemList;
        mDefaultMargin = defaultMargin;
    }

    @Override
    public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View layoutView = null;

        if (viewType == ChatItem.Type.MESSAGE.ordinal())
        {
            layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item, parent, false);
        }
        else if (viewType == ChatItem.Type.TITLE.ordinal())
        {
            layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_header, parent, false);
        }
        else if (viewType == ChatItem.Type.DATE_DIVIDER.ordinal())
        {
            layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_divider, parent, false);
        }
        else if (viewType == ChatItem.Type.IMAGE.ordinal())
        {
            layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_image, parent, false);
        }

        ChatViewHolder rcv = new ChatViewHolder(layoutView, mDefaultMargin);
        return rcv;
    }

    @Override
    public int getItemViewType(final int position)
    {
        ChatItem item = mList.get(position);
        return item.getType().ordinal();
    }

    @Override
    public void onBindViewHolder(ChatViewHolder holder, int position)
    {
        if (holder.getItemViewType() == ChatItem.Type.MESSAGE.ordinal())
        {
            holder.updateState(mList.get(position));
        }
        else if (holder.getItemViewType() == ChatItem.Type.IMAGE.ordinal())
        {
            holder.setupImage(mList.get(position));
        }
        else
        {
            ChatItem item = mList.get(position);
            ((TextView) holder.itemView.findViewById(R.id.text_message)).setText(item.getMessage().getMessage());
        }
    }

    @Override
    public int getItemCount()
    {
        return this.mList.size();
    }
}

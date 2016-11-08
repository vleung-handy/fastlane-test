package com.handy.portal.clients.ui.adapter;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.handy.portal.R;
import com.handybook.shared.LayerRecyclerAdapter;
import com.layer.sdk.LayerClient;

public class ConversationsAdapter extends LayerRecyclerAdapter<ConversationHolder>
{
    public ConversationsAdapter(@NonNull final LayerClient layerClient)
    {
        super(layerClient);
    }

    @Override
    protected void onConversationUpdated()
    {
        notifyDataSetChanged();
    }

    @Override
    public ConversationHolder onCreateViewHolder(final ViewGroup parent, final int viewType)
    {
        final View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_conversation_list_item, parent, false);
        return new ConversationHolder(itemView, mLayerClient.getAuthenticatedUser());
    }

    @Override
    public void onBindViewHolder(final ConversationHolder holder, final int position)
    {
        super.onBindViewHolder(holder, position);
        holder.bind(getAllConversations().get(position));
    }

    @Override
    public int getItemCount()
    {
        return getAllConversations().size();
    }
}

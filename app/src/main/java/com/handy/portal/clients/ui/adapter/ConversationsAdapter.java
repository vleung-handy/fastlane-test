package com.handy.portal.clients.ui.adapter;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.common.collect.Lists;
import com.handy.portal.R;
import com.handybook.shared.layer.LayerHelper;
import com.handybook.shared.layer.ui.LayerRecyclerAdapter;
import com.layer.sdk.messaging.Conversation;
import com.layer.sdk.messaging.Identity;
import com.layer.sdk.query.Predicate;
import com.layer.sdk.query.RecyclerViewController;

import java.util.List;

public class ConversationsAdapter extends LayerRecyclerAdapter<ConversationHolder> {
    private final Identity mLayerIdentity;
    private Listener mListener;

    public ConversationsAdapter(@NonNull final LayerHelper layerHelper,
                                @NonNull final Listener listener) {
        super(layerHelper);
        mListener = listener;
        mLayerIdentity = mLayerHelper.getLayerClient().getAuthenticatedUser();
    }

    @NonNull
    @Override
    protected List<Predicate> additionalPredicates() {
        return Lists.newArrayList(new Predicate(Conversation.Property.LAST_MESSAGE,
                Predicate.Operator.NOT_EQUAL_TO, null));
    }

    @Override
    public ConversationHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_conversation_list_item, parent, false);
        return new ConversationHolder(itemView, mLayerIdentity);
    }

    @Override
    public void onBindViewHolder(final ConversationHolder holder, final int position) {
        super.onBindViewHolder(holder, position);
        holder.bind(getConversationItem(position));
    }

    @Override
    public void onQueryDataSetChanged(final RecyclerViewController controller) {
        super.onQueryDataSetChanged(controller);
        mListener.onConversationsChanged();
        notifyDataSetChanged();
    }

    @Override
    public void onQueryItemChanged(final RecyclerViewController controller, final int position) {
        super.onQueryItemChanged(controller, position);
        mListener.onConversationsChanged();
        notifyItemChanged(position);
    }

    @Override
    public void onQueryItemRangeChanged(final RecyclerViewController controller,
                                        final int positionStart,
                                        final int itemCount) {
        super.onQueryItemRangeChanged(controller, positionStart, itemCount);
        mListener.onConversationsChanged();
        notifyItemRangeChanged(positionStart, itemCount);
    }

    @Override
    public void onQueryItemInserted(final RecyclerViewController controller, final int position) {
        super.onQueryItemInserted(controller, position);
        mListener.onConversationsChanged();
        notifyItemInserted(position);
    }

    @Override
    public void onQueryItemRangeInserted(final RecyclerViewController controller,
                                         final int positionStart,
                                         final int itemCount) {
        super.onQueryItemRangeInserted(controller, positionStart, itemCount);
        mListener.onConversationsChanged();
        notifyItemRangeInserted(positionStart, itemCount);
    }

    @Override
    public void onQueryItemRemoved(final RecyclerViewController controller, final int position) {
        super.onQueryItemRemoved(controller, position);
        mListener.onConversationsChanged();
        notifyItemRemoved(position);
    }

    @Override
    public void onQueryItemRangeRemoved(final RecyclerViewController controller,
                                        final int positionStart,
                                        final int itemCount) {
        super.onQueryItemRangeRemoved(controller, positionStart, itemCount);
        mListener.onConversationsChanged();
        notifyItemRangeRemoved(positionStart, itemCount);
    }

    @Override
    public void onQueryItemMoved(final RecyclerViewController controller,
                                 final int fromPosition,
                                 final int toPosition) {
        super.onQueryItemMoved(controller, fromPosition, toPosition);
        mListener.onConversationsChanged();
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public int getItemCount() {
        return getConversationsCount();
    }

    public interface Listener {
        void onConversationsChanged();
    }
}

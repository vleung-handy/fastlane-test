package com.handy.portal.clients.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.library.util.DateTimeUtils;
import com.handybook.shared.LayerUtil;
import com.handybook.shared.PushNotificationReceiver;
import com.handybook.shared.builtin.MessagesListActivity;
import com.layer.sdk.messaging.Conversation;
import com.layer.sdk.messaging.Identity;
import com.layer.sdk.messaging.Message;

import java.util.HashSet;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ConversationHolder extends RecyclerView.ViewHolder
{
    @BindView(R.id.conversation_list_item_title)
    TextView mTitle;
    @BindView(R.id.conversation_list_item_content)
    TextView mContent;
    @BindView(R.id.conversation_list_item_timestamp)
    TextView mTimestamp;
    @BindView(R.id.conversation_list_item_timestamp_container)
    ViewGroup mTimestampContainer;

    @OnClick(R.id.conversation_list_item)
    public void onConversationListItemClicked(final View view)
    {
        final Context context = view.getContext();
        final Intent intent = new Intent(context, MessagesListActivity.class);
        intent.putExtra(PushNotificationReceiver.LAYER_CONVERSATION_KEY, mConversation.getId());
        context.startActivity(intent);
    }

    private Conversation mConversation;
    private final Identity mLayerIdentity;

    public ConversationHolder(final View itemView, final Identity layerIdentity)
    {
        super(itemView);
        ButterKnife.bind(this, itemView);
        mLayerIdentity = layerIdentity;
    }

    public void bind(final Conversation conversation)
    {
        mConversation = conversation;
        initTitle(conversation);
        initContent(conversation);
        initTimestamp(conversation);
    }

    private void initTitle(final Conversation conversation)
    {
        final HashSet<Identity> participants = new HashSet<>(conversation.getParticipants());
        participants.remove(mLayerIdentity);
        mTitle.setVisibility(View.INVISIBLE);
        for (final Identity participant : participants)
        {
            // There should only be one participant in this case
            mTitle.setText(participant.getDisplayName());
            mTitle.setVisibility(View.VISIBLE);
        }
    }

    private void initContent(final Conversation conversation)
    {
        final Message lastMessage = conversation.getLastMessage();
        if (lastMessage != null)
        {
            mContent.setVisibility(View.VISIBLE);
            mContent.setText(LayerUtil.getLastMessageString(mContent.getContext(), lastMessage));
        }
        else
        {
            mContent.setVisibility(View.GONE);
        }
    }

    private void initTimestamp(final Conversation conversation)
    {
        final Message lastMessage = conversation.getLastMessage();
        if (lastMessage != null)
        {
            mTimestampContainer.setVisibility(View.VISIBLE);
            mTimestamp.setText(DateTimeUtils.formatDateTo12HourClock(lastMessage.getSentAt()));
        }
        else
        {
            mTimestampContainer.setVisibility(View.GONE);
        }
    }
}

package com.handy.portal.clients.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.library.util.DateTimeUtils;
import com.handy.portal.library.util.FontUtils;
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

    private final Context mContext;

    @OnClick(R.id.conversation_list_item)
    public void onConversationListItemClicked(final View view)
    {
        final Intent intent = new Intent(mContext, MessagesListActivity.class);
        intent.putExtra(PushNotificationReceiver.LAYER_CONVERSATION_KEY, mConversation.getId());
        mContext.startActivity(intent);
    }

    private Conversation mConversation;
    private final Identity mLayerIdentity;

    public ConversationHolder(final View itemView, final Identity layerIdentity)
    {
        super(itemView);
        mContext = itemView.getContext();
        ButterKnife.bind(this, itemView);
        mLayerIdentity = layerIdentity;
    }

    public void bind(final Conversation conversation)
    {
        mConversation = conversation;

        final Message lastMessage = conversation.getLastMessage();
        final boolean isUnread = lastMessage != null
                && lastMessage.getRecipientStatus(mLayerIdentity) != Message.RecipientStatus.READ;

        final HashSet<Identity> participants = new HashSet<>(conversation.getParticipants());
        participants.remove(mLayerIdentity);
        mTitle.setVisibility(View.INVISIBLE);
        for (final Identity participant : participants)
        {
            // There should only be one participant in this case
            mTitle.setVisibility(View.VISIBLE);
            mTitle.setText(participant.getDisplayName());
            if (isUnread)
            {
                mTitle.setTypeface(FontUtils.getFont(mContext, FontUtils.CIRCULAR_BOLD));
            }
            break;
        }

        if (lastMessage != null)
        {
            mContent.setVisibility(View.VISIBLE);
            mContent.setText(LayerUtil.getLastMessageString(mContent.getContext(), lastMessage));
            if (isUnread)
            {
                mContent.setTypeface(FontUtils.getFont(mContext, FontUtils.CIRCULAR_BOLD));
            }
            mTimestampContainer.setVisibility(View.VISIBLE);
            mTimestamp.setText(DateTimeUtils.formatDateToRelativeAccuracy(lastMessage.getSentAt()));
        }
        else
        {
            mContent.setVisibility(View.GONE);
            mTimestampContainer.setVisibility(View.GONE);
        }
    }

}

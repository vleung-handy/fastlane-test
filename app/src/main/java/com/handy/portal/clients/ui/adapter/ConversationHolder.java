package com.handy.portal.clients.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.library.util.DateTimeUtils;
import com.handy.portal.library.util.FontUtils;
import com.handy.portal.library.util.Utils;
import com.handy.portal.logger.handylogger.model.ConversationsLog;
import com.handybook.shared.layer.LayerConstants;
import com.handybook.shared.layer.LayerUtil;
import com.handybook.shared.layer.ui.MessagesListActivity;
import com.layer.sdk.messaging.Conversation;
import com.layer.sdk.messaging.Identity;
import com.layer.sdk.messaging.Message;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ConversationHolder extends RecyclerView.ViewHolder {
    @Inject
    EventBus mBus;

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
    public void onConversationListItemClicked(final View view) {
        final Intent intent = new Intent(mContext, MessagesListActivity.class);
        intent.putExtra(LayerConstants.LAYER_CONVERSATION_KEY, mConversation.getId());
        intent.putExtra(LayerConstants.KEY_HIDE_ATTACHMENT_BUTTON, true);
        mContext.startActivity(intent);

        final Identity opposingParticipant = LayerUtil.getOpposingParticipant(mConversation);
        mBus.post(
                new ConversationsLog.ConversationSelected(
                        opposingParticipant != null ? opposingParticipant.getUserId() : null,
                        mConversation.getId().toString()));
    }

    private Conversation mConversation;
    @Nullable
    private final Identity mLayerIdentity;

    public ConversationHolder(final View itemView, @Nullable final Identity layerIdentity) {
        super(itemView);
        mContext = itemView.getContext();
        Utils.inject(mContext, this);
        ButterKnife.bind(this, itemView);
        mLayerIdentity = layerIdentity;
    }

    @SuppressLint("SetTextI18n")
    public void bind(final Conversation conversation) {
        mConversation = conversation;

        final Message lastMessage = conversation.getLastMessage();
        final String typeface = isUnreadByRecipient(lastMessage) ?
                FontUtils.CIRCULAR_BOLD : FontUtils.CIRCULAR_BOOK;

        final Identity opposingParticipant = LayerUtil.getOpposingParticipant(conversation);
        mTitle.setVisibility(View.INVISIBLE);
        if (opposingParticipant != null) {
            mTitle.setVisibility(View.VISIBLE);
            mTitle.setText(opposingParticipant.getDisplayName());
            mTitle.setTypeface(FontUtils.getFont(mContext, typeface));
        }

        if (lastMessage != null) {
            final String messagePrefix = wasSentByMe(lastMessage) ? "Me: " : "";
            final String message = LayerUtil.getLastMessageString(mContent.getContext(), lastMessage);
            mContent.setVisibility(View.VISIBLE);
            mContent.setText(messagePrefix + message);
            mContent.setTypeface(FontUtils.getFont(mContext, typeface));
            mTimestampContainer.setVisibility(View.VISIBLE);
            mTimestamp.setText(DateTimeUtils.formatDateToRelativeAccuracy(lastMessage.getReceivedAt()));
        }
        else {
            mContent.setVisibility(View.GONE);
            mTimestampContainer.setVisibility(View.GONE);
        }
    }

    private boolean isUnreadByRecipient(final Message message) {
        return message != null
                && mLayerIdentity != null
                && message.getRecipientStatus(mLayerIdentity) != Message.RecipientStatus.READ;
    }

    private boolean wasSentByMe(final Message message) {
        if (message != null && mLayerIdentity != null) {
            final Identity sender = message.getSender();
            return sender != null && mLayerIdentity.getUserId().equals(sender.getUserId());
        }
        else {
            return false;
        }
    }

}

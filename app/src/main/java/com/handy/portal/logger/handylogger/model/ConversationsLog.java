package com.handy.portal.logger.handylogger.model;

import com.google.gson.annotations.SerializedName;

public abstract class ConversationsLog extends EventLog
{
    private static final String EVENT_CONTEXT = "conversations";

    private ConversationsLog(final String eventType)
    {
        super(eventType, EVENT_CONTEXT);
    }

    public static class ConversationsShown extends ConversationsLog
    {
        private static final String EVENT_TYPE = "conversations_shown";

        @SerializedName("unread_conversations_count")
        private int mUnreadConversationsCount;
        @SerializedName("total_conversations_count")
        private int mTotalConversationsCount;

        public ConversationsShown(final int unreadConversationsCount,
                                  final int totalConversationsCount)
        {
            super(EVENT_TYPE);
            mUnreadConversationsCount = unreadConversationsCount;
            mTotalConversationsCount = totalConversationsCount;
        }
    }

    public static class ConversationSelected extends ConversationsLog
    {
        private static final String EVENT_TYPE = "conversation_selected";

        @SerializedName("layer_user_id")
        private String mLayerUserId;
        @SerializedName("layer_conversation_id")
        private String mLayerConversationId;

        public ConversationSelected(final String layerUserId, final String layerConversationId)
        {
            super(EVENT_TYPE);
            mLayerUserId = layerUserId;
            mLayerConversationId = layerConversationId;
        }
    }

    public static class PushNotificationReceived extends ConversationsLog
    {
        private static final String EVENT_TYPE = "push_notification_received";

        public PushNotificationReceived()
        {
            super(EVENT_TYPE);
        }
    }
}

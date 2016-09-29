package com.handy.portal.chat;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.handy.portal.R;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.constant.MainViewPage;
import com.handy.portal.ui.fragment.ActionBarFragment;
import com.layer.atlas.AtlasHistoricMessagesFetchLayout;
import com.layer.atlas.AtlasMessageComposer;
import com.layer.atlas.AtlasMessagesRecyclerView;
import com.layer.atlas.AtlasTypingIndicator;
import com.layer.atlas.messagetypes.generic.GenericCellFactory;
import com.layer.atlas.messagetypes.location.LocationCellFactory;
import com.layer.atlas.messagetypes.singlepartimage.SinglePartImageCellFactory;
import com.layer.atlas.messagetypes.text.TextCellFactory;
import com.layer.atlas.messagetypes.text.TextSender;
import com.layer.atlas.messagetypes.threepartimage.ThreePartImageCellFactory;
import com.layer.atlas.typingindicators.BubbleTypingIndicatorFactory;
import com.layer.sdk.LayerClient;
import com.layer.sdk.messaging.Conversation;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MessagesListFragment extends ActionBarFragment
{
    @Inject
    LayerClient mLayerClient;
    @Inject
    AuthenticationProvider mLayerAuthenticationProvider;
    @Inject
    Picasso mPicasso;

    @BindView(R.id.historic_sync_layout)
    AtlasHistoricMessagesFetchLayout mHistoricFetchLayout;
    @BindView(R.id.messages_list)
    AtlasMessagesRecyclerView mMessagesList;
    @BindView(R.id.message_composer)
    AtlasMessageComposer mMessageComposer;

    private static final int MESSAGE_SYNC_AMOUNT = 20;
    private UiState mState;

    private Conversation mConversation;
    private AtlasTypingIndicator mTypingIndicator;

    @Override
    protected MainViewPage getAppPage()
    {
        return MainViewPage.MESSAGES_LIST;
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater,
                             @Nullable final ViewGroup container,
                             @Nullable final Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_messages_list, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        mHistoricFetchLayout.init(mLayerClient)
                .setHistoricMessagesPerFetch(MESSAGE_SYNC_AMOUNT);
        mMessagesList.init(mLayerClient, mPicasso)
                .addCellFactories(
                        new TextCellFactory(),
                        new ThreePartImageCellFactory(getActivity(), mLayerClient, mPicasso),
                        new LocationCellFactory(getActivity(), mPicasso),
                        new SinglePartImageCellFactory(getActivity(), mLayerClient, mPicasso),
                        new GenericCellFactory()
                );
        mTypingIndicator = new AtlasTypingIndicator(getActivity())
                .init(mLayerClient)
                .setTypingIndicatorFactory(new BubbleTypingIndicatorFactory())
                .setTypingActivityListener(new AtlasTypingIndicator.TypingActivityListener()
                {
                    @Override
                    public void onTypingActivityChange(
                            AtlasTypingIndicator typingIndicator,
                            boolean active
                    )
                    {
                        mMessagesList.setFooterView(active ? typingIndicator : null);
                    }
                });
        mMessageComposer.init(mLayerClient)
                .setTextSender(new TextSender())
                .setOnMessageEditTextFocusChangeListener(new View.OnFocusChangeListener()
                {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus)
                    {
                        if (hasFocus)
                        {
                            setUiState(UiState.CONVERSATION_COMPOSER);
//                            setTitle();
                        }
                    }
                });
        // Get or create Conversation from Intent extras
        Conversation conversation = null;
        Bundle args = getArguments();
        if (args != null && !args.isEmpty())
        {
            Uri conversationId = args.getParcelable(PushNotificationReceiver.LAYER_CONVERSATION_KEY);
            conversation = mLayerClient.getConversation(conversationId);
            setConversation(conversation, conversation != null);
            setActionBarTitle(args.getString(BundleKeys.BOOKING_USER));
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        // Clear any notifications for this conversation
        PushNotificationReceiver.getNotifications(getActivity()).clear(mConversation);
    }

    @Override
    public void onPause()
    {
        super.onPause();

        // Update the notification position to the latest seen
        PushNotificationReceiver.getNotifications(getActivity()).clear(mConversation);
    }

    @Override
    public void onStop()
    {
        super.onStop();
        if (mMessagesList != null)
        {
            mMessagesList.onDestroy();
        }
    }

    public void setTitle()
    {
        setActionBarTitle("Conversation between 2 people");
    }

    private void setConversation(Conversation conversation, boolean hideLauncher)
    {
        mConversation = conversation;
        mHistoricFetchLayout.setConversation(conversation);
        mMessagesList.setConversation(conversation);
        mTypingIndicator.setConversation(conversation);
        mMessageComposer.setConversation(conversation);

        // UI state
        if (conversation == null)
        {
            setUiState(UiState.ADDRESS);
            return;
        }

        if (hideLauncher)
        {
            setUiState(UiState.CONVERSATION_COMPOSER);
            return;
        }

        if (conversation.getHistoricSyncStatus() == Conversation.HistoricSyncStatus.INVALID)
        {
            // New "temporary" conversation
            setUiState(UiState.ADDRESS_COMPOSER);
        }
        else
        {
            setUiState(UiState.ADDRESS_CONVERSATION_COMPOSER);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        mMessageComposer.onActivityResult(getActivity(), requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            String[] permissions,
            int[] grantResults
    )
    {
        mMessageComposer.onRequestPermissionsResult(requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private enum UiState
    {
        ADDRESS,
        ADDRESS_COMPOSER,
        ADDRESS_CONVERSATION_COMPOSER,
        CONVERSATION_COMPOSER
    }

    private void setUiState(UiState state)
    {
        if (mState == state) { return; }
        mState = state;
        switch (state)
        {
            case ADDRESS:
//                mAddressBar.setVisibility(View.VISIBLE);
//                mAddressBar.setSuggestionsVisibility(View.VISIBLE);
                mHistoricFetchLayout.setVisibility(View.GONE);
                mMessageComposer.setVisibility(View.GONE);
                break;

            case ADDRESS_COMPOSER:
//                mAddressBar.setVisibility(View.VISIBLE);
//                mAddressBar.setSuggestionsVisibility(View.VISIBLE);
                mHistoricFetchLayout.setVisibility(View.GONE);
                mMessageComposer.setVisibility(View.VISIBLE);
                break;

            case ADDRESS_CONVERSATION_COMPOSER:
//                mAddressBar.setVisibility(View.VISIBLE);
//                mAddressBar.setSuggestionsVisibility(View.GONE);
                mHistoricFetchLayout.setVisibility(View.VISIBLE);
                mMessageComposer.setVisibility(View.VISIBLE);
                break;

            case CONVERSATION_COMPOSER:
//                mAddressBar.setVisibility(View.GONE);
//                mAddressBar.setSuggestionsVisibility(View.GONE);
                mHistoricFetchLayout.setVisibility(View.VISIBLE);
                mMessageComposer.setVisibility(View.VISIBLE);
                break;
        }
    }
}

package com.handy.portal.layer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.handy.portal.R;
import com.handy.portal.bookings.model.Booking;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.constant.MainViewPage;
import com.handy.portal.library.util.DateTimeUtils;
import com.handy.portal.ui.fragment.ActionBarFragment;
import com.layer.sdk.LayerClient;
import com.layer.sdk.changes.LayerChange;
import com.layer.sdk.changes.LayerChangeEvent;
import com.layer.sdk.exceptions.LayerConversationException;
import com.layer.sdk.listeners.LayerChangeEventListener;
import com.layer.sdk.listeners.LayerSyncListener;
import com.layer.sdk.messaging.Conversation;
import com.layer.sdk.messaging.LayerObject;
import com.layer.sdk.messaging.Message;
import com.layer.sdk.messaging.MessagePart;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class LayerFragment extends ActionBarFragment
{
    private static final String TAG = "LayerActivity";
    private static final String APP_ID = "layer:///apps/staging/6178a72e-4e8d-11e6-aca9-940102005074";
    private static final String KEY_MY_ID = "my_id";
    private static final String KEY_CONSUMER_ID = "consumer_id";
    private static final String KEY_BOOKING_ID = "booking_id";
    private static final String KEY_CONSUMER_NAME = "consumer_name";
    LayerClient layerClient;
    Conversation convo;

    @BindView(R.id.main_container)
    CoordinatorLayout mMainContainer;

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    @BindView(R.id.edit_message)
    EditText mEditMessage;

    @BindView(R.id.empty_view)
    View mEmptyView;

    @BindColor(R.color.white)
    int mMyTextColor;

    @BindColor(R.color.black)
    int mOpponentTextColor;

    @BindColor(R.color.handy_blue)
    int mMyBgColor;

    @BindColor(R.color.white)
    int mOppoenentBgColor;

    boolean mIsInitialized = false;

    private LinearLayoutManager mLayoutManager;
    private ChatRecyclerAdapter mAdapter;
    private List<ChatItem> mChatItems = new ArrayList<>();
    private String mMyId;
    private String mConsumerId;
    private String mBookingId;
    private String mConsumerName;
    private boolean mSync = false;

    private Booking mBooking;

    @Override
    protected MainViewPage getAppPage()
    {
        return MainViewPage.LAYER_MESSAGE;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if (getArguments() != null)
        {
            mBooking = (Booking) getArguments().get(BundleKeys.BOOKING);
        }

        mConsumerName = mBooking.getUser().getFullName();
        mBookingId = mBooking.getId();
        mMyId = mBooking.getProviderId();
        mConsumerId = mBooking.getAddress().getUserId();
        setupLayer();
    }


    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_layer, container, false);
        ButterKnife.bind(this, view);

        setActionBarTitle(mMyId + ":" + mConsumerId + ":" + mBookingId);

        mEditMessage.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(final TextView textView, final int i, final KeyEvent keyEvent)
            {
                if (i == EditorInfo.IME_NULL)
                {
                    //the enter key
                    sendClicked();
                    return true;
                }
                return false;
            }
        });

        mLayoutManager = new LinearLayoutManager(getActivity());
        mAdapter = new ChatRecyclerAdapter(
                mChatItems,
                getResources().getDimensionPixelSize(R.dimen.default_margin)
        );

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        addHeader();

        return view;
    }

    public String getMyId()
    {
        return mMyId;
    }

    private void setupLayer()
    {
        LayerClient.Options options = new LayerClient.Options();
        options.historicSyncPolicy(LayerClient.Options.HistoricSyncPolicy.ALL_MESSAGES);

        layerClient = LayerClient.newInstance(getActivity(), APP_ID, options);
        layerClient.registerConnectionListener(new MyConnectionListener());
        layerClient.registerAuthenticationListener(new MyAuthenticationListener(this));

        //looks like certain times, the client need to call sync before it can find conversations.
        layerClient.registerSyncListener(new SimpleLayerSyncListener()
        {
            @Override
            public void onAfterSync(LayerClient layerClient, LayerSyncListener.SyncType syncType)
            {
                Log.d(TAG, "onAfterSync() called with: " + "layerClient = [" + layerClient + "], syncType = [" + syncType + "]");
                mSync = true;
                initialize();
            }
        });

        layerClient.registerEventListener(new LayerChangeEventListener()
        {
            @Override
            public void onChangeEvent(final LayerChangeEvent event)
            {
                if (!mIsInitialized)
                {
                    Log.d(TAG, "onChangeEvent: ignoring change event because not yet initialized.");
                    return;
                }
                Log.d(TAG, "onChangeEvent() called with: event = [" + event + "]");
                //You can choose to handle changes to conversations or messages however you'd like:
                List<LayerChange> changes = event.getChanges();
                for (int i = 0; i < changes.size(); i++)
                {
                    LayerChange change = changes.get(i);
                    if (change.getObjectType() == LayerObject.Type.MESSAGE)
                    {
                        Message message = (Message) change.getObject();
                        Log.d(TAG, "onChangeEvent: TYPE: " + change.getChangeType());
                        switch (change.getChangeType())
                        {
                            case INSERT:
                                Log.d(TAG, "message: INSERT");
                                updateRecyclerWithMessage(message);
                                break;
                            case UPDATE:
                                Log.d(TAG, "onChangeEvent: UPDATE");
                                updateExistingMessage(message);
                        }
                    }
                }
            }
        });

        // Asks the LayerSDK to establish a network connection with the Layer service
        layerClient.connect();
    }

    public void initialize()
    {
        if (mIsInitialized)
        {
            Log.d(TAG, "initialize: not initializing because it has already been done.");
            return;
        }

        if (!mSync)
        {
            Log.d(TAG, "initialize: not initializing because layer hasn't finished synching yet.");
            return;
        }

        if (!layerClient.isAuthenticated())
        {
            Log.d(TAG, "initialize: not initializing because layer hasn't been authenticated yet.");
            return;
        }

        Log.d(TAG, "initialize: ");
        List<Conversation> conversations = layerClient.getConversationsWithParticipants(mMyId, mConsumerId, mBookingId);
        if (conversations != null && !conversations.isEmpty())
        {
            convo = conversations.get(0);
        }
        if (convo == null)
        {
            Log.d(TAG, "initialize: No conversation found with participants : " + mMyId + " and " + mConsumerId + " and booking id: " + mBookingId);
//            Toast.makeText(this, "initialize: No conversation found with participants : " + mMyId + " and " + mConsumerId + " and booking id: " + mBookingId, Toast.LENGTH_SHORT).show();

            try
            {
                convo = layerClient.newConversation(mMyId, mConsumerId, mBookingId);
                Log.d(TAG, "initialize: new convo created with id:" + convo.getId());
                mIsInitialized = true;
                Toast.makeText(getActivity(), "New Conversation Created", Toast.LENGTH_SHORT).show();
            }
            catch (LayerConversationException e)
            {
                Log.d(TAG, "initialize: conversation already exists, getting existing conversation");
                convo = e.getConversation();
                Log.d(TAG, "initialize: existing conversation is:" + convo.getId());
                mIsInitialized = true;
            }

        }
        else
        {
            Log.d(TAG, "conversation exists with ID: " + convo.getId());
            Toast.makeText(getActivity(), "Conversation Found", Toast.LENGTH_SHORT).show();
            mIsInitialized = true;
            convo.syncAllHistoricMessages();
            drawMessages();
        }
    }

    private void showSnackBar(String str)
    {
        Snackbar snack = Snackbar.make(mMainContainer, str, Snackbar.LENGTH_SHORT);
        View view = snack.getView();
        TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(Color.WHITE);
        snack.show();
    }

    private void drawMessages()
    {
        List<Message> allMsgs = layerClient.getMessages(convo);
        mChatItems.clear();
        Log.d(TAG, "conversation has " + allMsgs.size() + " messages");
        Date previousDate = null;
        for (int i = 0; i < allMsgs.size(); i++)
        {
            addHeader();

            Message message = allMsgs.get(i);
            if (previousDate == null || previousDate.getDate() != message.getSentAt().getDate())
            {
                //insert divider chat item
                previousDate = message.getSentAt();
                String msg = DateTimeUtils.getMonthDay(previousDate);
                ChatMessage cm = new ChatMessage();
                cm.setMessage("-- " + msg + " --");
                mChatItems.add(new ChatItem(cm, ChatItem.Type.DATE_DIVIDER));
            }

            updateRecyclerWithMessage(message);
        }
    }


    private ChatItem getChatItem(Message msg)
    {

        String message = getMessage(msg);
        ChatMessage chatMessage = new ChatMessage(msg.getId().toString(), message, msg.getSender().getUserId(), msg.getSentAt(), msg.isSent());
        if (!message.toLowerCase().startsWith("http://"))
        {
            return new ChatItem(chatMessage, ChatItem.Type.MESSAGE);
        }
        else
        {
            return new ChatItem(chatMessage, ChatItem.Type.IMAGE);
        }
    }

    private void updateRecyclerWithMessage(Message msg)
    {
        //Make sure the message is valid
        if (msg == null)
        {
            Log.d(TAG, "updateRecyclerWithMessage: exiting, msg = null");
            return;
        }
        else if (msg.getSender() == null || msg.getSender().getUserId() == null)
        {
            Log.d(TAG, "updateRecyclerWithMessage: exiting, message contains no user");
            return;
        }

        ChatItem item = getChatItem(msg);

        if (mMyId.equals(msg.getSender().getUserId()))
        {
            item.setBgColor(mMyBgColor);
            item.setTextColor(mMyTextColor);
            item.setGravity(Gravity.RIGHT);
        }
        else
        {
            item.setBgColor(mOppoenentBgColor);
            item.setTextColor(mOpponentTextColor);
            item.setGravity(Gravity.LEFT);
        }

        mChatItems.add(item);

        if (mAdapter != null)
        {
            mAdapter.notifyDataSetChanged();
            mRecyclerView.smoothScrollToPosition(mAdapter.getItemCount());
            updateViewStatus();
        }
    }

    private void updateExistingMessage(Message msg)
    {
        for (int i = mChatItems.size() - 1; i >= 0; i--)
        {
            ChatItem item = mChatItems.get(i);
            if (item.getMessage().getId().equals(msg.getId().toString()))
            {
                //item found, just update whatever that was changed.
                item.getMessage().setMessage(getMessage(msg));
                item.getMessage().setDate(msg.getSentAt());
                item.getMessage().setRead(msg.isSent());
                item.getMessage().setSenderId(msg.getSender().getUserId());

                mAdapter.notifyDataSetChanged();
                return;
            }
        }
    }

    private void addHeader()
    {
        if (mChatItems.isEmpty())
        {
            //construct header.
            ChatMessage message = new ChatMessage();
            message.setMessage("Meet your client, " + mConsumerName + ".");
            mChatItems.add(new ChatItem(message, ChatItem.Type.TITLE));
        }
    }

    private String getMessage(Message message)
    {
        List<MessagePart> parts = message.getMessageParts();
        for (MessagePart part : parts)
        {
            switch (part.getMimeType())
            {

                case "text/plain":
                    String textMsg = new String(part.getData());
                    return textMsg;
                case "image/jpeg":
                    Bitmap imageMsg = BitmapFactory.decodeByteArray(part.getData(), 0, part.getData().length);
                    return "image/jpeg";
            }
        }

        return "Can't decode message";
    }

    @OnClick(R.id.image_send)
    public void sendClicked()
    {
        String message = mEditMessage.getText().toString();
        if (!TextUtils.isEmpty(message))
        {
            MessagePart messagePart = layerClient.newMessagePart(message);
            Message msg = layerClient.newMessage(Arrays.asList(messagePart));
            convo.send(msg);
            mEditMessage.setText("");
        }
    }

    private void updateViewStatus()
    {
        if (mAdapter.getItemCount() > 0)
        {
            mRecyclerView.setVisibility(View.VISIBLE);
            mEmptyView.setVisibility(View.GONE);
        }
        else
        {
            mRecyclerView.setVisibility(View.GONE);
            mEmptyView.setVisibility(View.VISIBLE);
        }
    }

    public void authenticated()
    {
        initialize();
    }
}

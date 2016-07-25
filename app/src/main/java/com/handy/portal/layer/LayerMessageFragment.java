package com.handy.portal.layer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import com.handy.portal.ui.fragment.ActionBarFragment;
import com.layer.sdk.LayerClient;
import com.layer.sdk.changes.LayerChange;
import com.layer.sdk.changes.LayerChangeEvent;
import com.layer.sdk.exceptions.LayerException;
import com.layer.sdk.listeners.LayerAuthenticationListener;
import com.layer.sdk.listeners.LayerChangeEventListener;
import com.layer.sdk.listeners.LayerConnectionListener;
import com.layer.sdk.listeners.LayerProgressListener;
import com.layer.sdk.listeners.LayerTypingIndicatorListener;
import com.layer.sdk.messaging.Conversation;
import com.layer.sdk.messaging.LayerObject;
import com.layer.sdk.messaging.Message;
import com.layer.sdk.messaging.MessagePart;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class LayerMessageFragment extends ActionBarFragment implements LayerChangeEventListener,
        TextWatcher, LayerAuthenticationListener, LayerConnectionListener
{
    @Inject
    LayerClient mLayerClient;

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

    private Conversation mConversation;
    private LinearLayoutManager mLayoutManager;
    private ChatRecyclerAdapter mAdapter;
    private Booking mBooking;

    private List<ChatItem> mChatItems = new ArrayList<>();

    private static final String TAG = LayerMessageFragment.class.getSimpleName();

    @Override
    protected MainViewPage getAppPage()
    {
        return MainViewPage.LAYER_MESSAGE;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null)
        {
            mBooking = (Booking) bundle.getSerializable(BundleKeys.BOOKING);
        }

        //When conversations/messages change, capture them
        mLayerClient.registerEventListener(this);
        mLayerClient.registerConnectionListener(this);
        mLayerClient.registerAuthenticationListener(this);
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_layer_message, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
//        mMessageInputText.addTextChangedListener(this);
        setActionBarTitle(mBooking.getUser().getFullName());

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

        mLayoutManager = new LinearLayoutManager(getContext());
        mAdapter = new ChatRecyclerAdapter(
                mChatItems,
                getResources().getDimensionPixelSize(R.dimen.default_margin)
        );

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mLayerClient.connect();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        mLayerClient.registerEventListener(this);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        mLayerClient.unregisterEventListener(this);
    }

    @Override
    public void onChangeEvent(final LayerChangeEvent layerChangeEvent)
    {
        Log.d(TAG, "onChangeEvent() called with: event = [" + layerChangeEvent + "]");

        //You can choose to handle changes to conversations or messages however you'd like:
        List<LayerChange> changes = layerChangeEvent.getChanges();
        for (int i = 0; i < changes.size(); i++)
        {
            LayerChange change = changes.get(i);
            if (change.getObjectType() == LayerObject.Type.CONVERSATION)
            {

                Conversation conversation = (Conversation) change.getObject();
                Log.v(TAG, "Conversation " + conversation.getId() + " attribute " +
                        change.getAttributeName() + " was changed from " + change.getOldValue() +
                        " to " + change.getNewValue());

                switch (change.getChangeType())
                {
                    case INSERT:
                        break;

                    case UPDATE:
                        break;

                    case DELETE:
                        break;
                }

            }
            else if (change.getObjectType() == LayerObject.Type.MESSAGE)
            {

                Message message = (Message) change.getObject();
                Log.v(TAG, "Message " + message.getId() + " attribute " + change
                        .getAttributeName() + " was changed from " + change.getOldValue() + " to " +
                        "" + change.getNewValue());

                switch (change.getChangeType())
                {
                    case INSERT:
                        break;

                    case UPDATE:
                        break;

                    case DELETE:
                        break;
                }
            }
        }

        //If anything in the conversation changes, re-draw it in the GUI
        redrawMessages();

    }

    @Override
    public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after)
    {

    }

    @Override
    public void onTextChanged(final CharSequence s, final int start, final int before, final int count)
    {

    }

    @Override
    public void afterTextChanged(final Editable s)
    {
        //After the user has changed some text, we notify other participants that they are typing
        if (mConversation != null)
        { mConversation.send(LayerTypingIndicatorListener.TypingIndicator.STARTED); }
    }

    @Override
    public void onAuthenticated(LayerClient client, String arg1)
    {
        System.out.println("Authentication successful");
        init();
    }

    @Override
    public void onAuthenticationChallenge(final LayerClient client, final String nonce)
    {
        //You can use any identifier you wish to track users, as long as the value is unique
        //This identifier will be used to add a user to a conversation in order to send them messages
        final String mUserId = mBooking.getProviderId();

  /*
   * 2. Acquire an identity token from the Layer Identity Service
   */
        (new AsyncTask<Void, Void, Void>()
        {
            @Override
            protected Void doInBackground(Void... params)
            {
                try
                {
                    HttpPost post = new HttpPost("https://layer-identity-provider.herokuapp.com/identity_tokens");
                    post.setHeader("Content-Type", "application/json");
                    post.setHeader("Accept", "application/json");

                    JSONObject json = new JSONObject()
                            .put("app_id", client.getAppId())
                            .put("user_id", mUserId)
                            .put("nonce", nonce);
                    post.setEntity(new StringEntity(json.toString()));

                    HttpResponse response = (new DefaultHttpClient()).execute(post);
                    String eit = (new JSONObject(EntityUtils.toString(response.getEntity())))
                            .optString("identity_token");

            /*
             * 3. Submit identity token to Layer for validation
             */
                    client.answerAuthenticationChallenge(eit);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                return null;
            }
        }).execute();
    }

    @Override
    public void onAuthenticationError(LayerClient layerClient, LayerException e)
    {
        // TODO Auto-generated method stub
        System.out.println("There was an error authenticating");
    }

    @Override
    public void onDeauthenticated(LayerClient client) { }

    @Override
    public void onConnectionConnected(LayerClient client)
    {
        if (!client.isAuthenticated())
        { client.authenticate(); }
        else
        {
            init();
        }
    }

    @Override
    public void onConnectionDisconnected(LayerClient arg0) { }

    @Override
    public void onConnectionError(LayerClient arg0, LayerException e) { }

    @OnClick(R.id.image_send)
    public void sendClicked()
    {
        String message = mEditMessage.getText().toString();
        if (!TextUtils.isEmpty(message) && mConversation != null)
        {
            MessagePart messagePart = mLayerClient.newMessagePart(message);
            Message msg = mLayerClient.newMessage(Arrays.asList(messagePart));
            mConversation.send(msg, new LayerProgressListener()
            {
                @Override
                public void onProgressStart(final MessagePart messagePart, final Operation operation)
                {
                    Log.d(TAG, "onProgressStart() called with: messagePart = [" + messagePart + "], operation = [" + operation + "]");
                }

                @Override
                public void onProgressUpdate(final MessagePart messagePart, final Operation operation, final long l)
                {
                    Log.d(TAG, "onProgressUpdate() called with: messagePart = [" + messagePart + "], operation = [" + operation + "], l = [" + l + "]");
                }

                @Override
                public void onProgressComplete(final MessagePart messagePart, final Operation operation)
                {
                    Log.d(TAG, "onProgressComplete() called with: messagePart = [" + messagePart + "], operation = [" + operation + "]");
                    Toast.makeText(getContext(), "Message Sent", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onProgressError(final MessagePart messagePart, final Operation operation, final Throwable throwable)
                {
                    Log.d(TAG, "onProgressError() called with: messagePart = [" + messagePart + "], operation = [" + operation + "], throwable = [" + throwable + "]");
                    Snackbar.make(mMainContainer, "There was an error sending the message", Snackbar.LENGTH_LONG).show();
                }
            });
            mEditMessage.setText("");
        }
    }


    private void redrawMessages()
    {
        List<Message> allMsgs = mLayerClient.getMessages(mConversation);
        mChatItems.clear();
        Log.d(TAG, "conversation exists with " + allMsgs.size() + " messages");
        for (int i = 0; i < allMsgs.size(); i++)
        {
            updateRecyclerWithMessage(allMsgs.get(i));
        }
    }

    private void updateRecyclerWithMessage(Message msg)
    {
        //Make sure the message is valid
        if (msg == null || msg.getSender() == null || msg.getSender().getUserId() == null)
        { return; }

        if (mChatItems.isEmpty())
        {
            //construct header.
            ChatMessage message = new ChatMessage();
            message.setMessage("Meet your pro, Samantha. Let her know of any preferences, or just say 'hi'!");
            mChatItems.add(new ChatItem(message, ChatItem.Type.TITLE));
        }

        if (!msg.getSender().getUserId().equalsIgnoreCase(mBooking.getProviderId()))
        {
            msg.markAsRead();
        }

        String message = getMessage(msg);

        Log.d(TAG, "updateRecyclerWithMessage: " + message + " at position:" + msg.getPosition());
        ChatMessage chatMessage = new ChatMessage(message, msg.getSender().getUserId(), msg.getSentAt(), msg.isSent());

        ChatItem item = new ChatItem(chatMessage, ChatItem.Type.MESSAGE);

        if (mBooking.getProviderId().equals(chatMessage.getSenderId()))
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

    private void init()
    {
        mConversation = mLayerClient.getConversation(Uri.parse(mBooking.getConversationId()));

        if (mConversation != null)
        {
            Log.d(TAG, "conversation exists with ID: " + mConversation.getId());
            redrawMessages();
        }
        else
        {
            Log.d(TAG, "conversation does not exist");
        }

    }
}

package com.handy.portal.clients.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.handy.portal.R;
import com.handy.portal.clients.ui.adapter.ConversationsAdapter;
import com.handy.portal.library.ui.fragment.InjectedFragment;
import com.handybook.shared.layer.LayerConstants;
import com.handybook.shared.layer.LayerHelper;
import com.handybook.shared.layer.receiver.PushNotificationReceiver;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.handybook.shared.layer.LayerConstants.LAYER_CONVERSATION_KEY;


public class ClientConversationsFragment extends InjectedFragment
        implements ConversationsAdapter.Listener
{
    @Inject
    LayerHelper mLayerHelper;

    @BindView(R.id.client_conversations_recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.client_conversations_empty_view)
    View mEmptyView;

    private BroadcastReceiver mPushNotificationReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(final Context context, final Intent intent)
        {
            final Bundle extras = intent.getExtras();
            if (extras == null) { return; }
            final Uri conversationId = extras.getParcelable(LAYER_CONVERSATION_KEY);
            if (conversationId != null && ClientConversationsFragment.this.getUserVisibleHint())
            {
                // Assuming this receiver has a high system priority, this will prevent push
                // notifications regarding any conversation from being displayed.
                abortBroadcast();
            }
        }
    };

    private ConversationsAdapter mAdapter;

    public static ClientConversationsFragment newInstance()
    {
        return new ClientConversationsFragment();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState)
    {
        final View view = inflater.inflate(R.layout.fragment_client_conversations, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState)
    {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new ConversationsAdapter(mLayerHelper, this);
        mAdapter.refreshConversations();
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onResume()
    {
        final IntentFilter filter = new IntentFilter(LayerConstants.ACTION_SHOW_NOTIFICATION);
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        getActivity().registerReceiver(mPushNotificationReceiver, filter);
        if (getUserVisibleHint())
        {
            clearNotifications();
        }
        super.onResume();
    }

    @Override
    public void onConversationsChanged()
    {
        if (mAdapter.getItemCount() > 0)
        {
            mEmptyView.setVisibility(View.GONE);
        }
        else
        {
            mEmptyView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void setUserVisibleHint(final boolean isVisibleToUser)
    {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser)
        {
            clearNotifications();
        }
    }

    private void clearNotifications()
    {
        if (mAdapter == null)
        {
            return;
        }
        for (int i = 0; i < mAdapter.getItemCount(); i++)
        {
            PushNotificationReceiver.getNotifications(getActivity())
                    .clear(mAdapter.getConversationItem(i));
        }
    }

    @Override
    public void onPause()
    {
        getActivity().unregisterReceiver(mPushNotificationReceiver);
        super.onPause();
    }
}

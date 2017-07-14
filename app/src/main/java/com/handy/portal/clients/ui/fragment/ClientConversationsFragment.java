package com.handy.portal.clients.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.handy.portal.R;
import com.handy.portal.clients.ui.adapter.ConversationsAdapter;
import com.handy.portal.core.ui.fragment.ActionBarFragment;
import com.handy.portal.logger.handylogger.LogEvent;
import com.handy.portal.library.ui.fragment.InjectedFragment;
import com.handy.portal.logger.handylogger.model.ConversationsLog;
import com.handybook.shared.layer.LayerConstants;
import com.handybook.shared.layer.LayerHelper;
import com.handybook.shared.layer.receiver.PushNotificationReceiver;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.handybook.shared.layer.LayerConstants.LAYER_CONVERSATION_KEY;


public class ClientConversationsFragment extends ActionBarFragment
        implements ConversationsAdapter.Listener {
    private static final int REFRESH_DURATION_MILLIS = 3000;

    @Inject
    LayerHelper mLayerHelper;

    @BindView(R.id.client_conversations_recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.client_conversations_refresh_layout)
    SwipeRefreshLayout mRefreshLayout;
    @BindView(R.id.client_conversations_empty_refresh_layout)
    SwipeRefreshLayout mEmptyRefreshLayout;

    private BroadcastReceiver mPushNotificationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            final Bundle extras = intent.getExtras();
            if (extras == null) { return; }
            final Uri conversationId = extras.getParcelable(LAYER_CONVERSATION_KEY);
            if (conversationId != null && ClientConversationsFragment.this.getUserVisibleHint()) {
                // Assuming this receiver has a high system priority, this will prevent push
                // notifications regarding any conversation from being displayed.
                abortBroadcast();
            }
        }
    };

    private ConversationsAdapter mAdapter;
    private boolean mIsConversationsShownLogged = false;

    public static ClientConversationsFragment newInstance() {
        return new ClientConversationsFragment();
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_client_conversations, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
        setActionBar(R.string.messages, false);
        initRefreshLayout(mRefreshLayout);
        initRefreshLayout(mEmptyRefreshLayout);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new ConversationsAdapter(mLayerHelper, this);
        mAdapter.refreshConversations();
        mRecyclerView.setAdapter(mAdapter);
    }

    private void initRefreshLayout(final SwipeRefreshLayout refreshLayout) {
        refreshLayout.setColorSchemeResources(R.color.handy_blue);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.setRefreshing(true);
                mAdapter.refreshConversations();
                // This will fake the finishing of loading state. The reason is because
                // onConversationsChanged() doesn't get called if data did not change.
                refreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.setRefreshing(false);
                    }
                }, REFRESH_DURATION_MILLIS);
            }
        });
    }

    @Override
    public void onResume() {
        final IntentFilter filter = new IntentFilter(LayerConstants.ACTION_SHOW_NOTIFICATION);
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        getActivity().registerReceiver(mPushNotificationReceiver, filter);
        if (getUserVisibleHint()) {
            clearNotifications();
            logConversationsShown();
        }
        super.onResume();
    }

    @Override
    public void onConversationsChanged() {
        mRefreshLayout.setRefreshing(false);
        mEmptyRefreshLayout.setRefreshing(false);
        if (mAdapter.getItemCount() > 0) {
            mEmptyRefreshLayout.setVisibility(View.GONE);
        }
        else {
            mEmptyRefreshLayout.setVisibility(View.VISIBLE);
        }
        if (getUserVisibleHint()) {
            logConversationsShown();
        }
    }

    @Override
    public void setUserVisibleHint(final boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            clearNotifications();
            logConversationsShown();
        }
    }

    private void logConversationsShown() {
        if (bus != null && mAdapter != null && !mIsConversationsShownLogged) {
            bus.post(
                    new ConversationsLog.ConversationsShown(
                            (int) mLayerHelper.getUnreadConversationsCount(),
                            mAdapter.getConversationsCount()));
            mIsConversationsShownLogged = true;
        }
    }

    private void clearNotifications() {
        if (mAdapter == null) {
            return;
        }
        for (int i = 0; i < mAdapter.getItemCount(); i++) {
            PushNotificationReceiver.getNotifications(getActivity())
                    .clear(mAdapter.getConversationItem(i));
        }
    }

    @Override
    public void onPause() {
        getActivity().unregisterReceiver(mPushNotificationReceiver);
        super.onPause();
    }
}

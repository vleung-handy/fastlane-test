package com.handy.portal.clients.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.handy.portal.R;
import com.handy.portal.clients.ui.adapter.ConversationsAdapter;
import com.handy.portal.library.ui.fragment.InjectedFragment;
import com.handy.portal.library.ui.widget.SafeSwipeRefreshLayout;
import com.handybook.shared.LayerHelper;
import com.handybook.shared.LayerIntent;
import com.layer.sdk.LayerClient;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ClientConversationsFragment extends InjectedFragment
{
    @Inject
    LayerHelper mLayerHelper;

    @BindView(R.id.client_conversations_recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.client_conversations_swipe_refresh_layout)
    SafeSwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.client_conversations_empty_swipe_refresh_layout)
    SafeSwipeRefreshLayout mEmptySwipeRefreshLayout;

    private ConversationsAdapter mAdapter;
    private LayerClient mLayerClient;
    private BroadcastReceiver mUnreadCountChangedReceiver;

    public static ClientConversationsFragment newInstance()
    {
        return new ClientConversationsFragment();
    }

    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mLayerClient = mLayerHelper.getLayerClient();
        mUnreadCountChangedReceiver = new BroadcastReceiver()
        {
            @Override
            public void onReceive(final Context context, final Intent intent)
            {
                intent.getIntExtra(LayerIntent.EXTRA_UNREAD_MESSAGES_COUNT, 0);
                // FIXME: Update badge counter
            }
        };
        LocalBroadcastManager.getInstance(getActivity())
                .registerReceiver(mUnreadCountChangedReceiver,
                        new IntentFilter(LayerIntent.ACTION_UNREAD_MESSAGES_COUNT_CHANGED));
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
        initSwipeRefreshLayouts();
        setRefreshing(true);
        initRecyclerView();
        resetRecyclerView();
    }

    private void initRecyclerView()
    {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new ConversationsAdapter(mLayerClient);
    }

    private void initSwipeRefreshLayouts()
    {
        final SwipeRefreshLayout.OnRefreshListener onRefreshListener =
                new SwipeRefreshLayout.OnRefreshListener()
                {
                    @Override
                    public void onRefresh()
                    {
                        resetRecyclerView();
                    }
                };
        mEmptySwipeRefreshLayout.setOnRefreshListener(onRefreshListener);
        mSwipeRefreshLayout.setOnRefreshListener(onRefreshListener);
        mEmptySwipeRefreshLayout.setColorSchemeResources(R.color.handy_blue);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.handy_blue);
    }

    private void resetRecyclerView()
    {
        mAdapter.refreshLayer();
        if (mAdapter.getItemCount() > 0)
        {
            mSwipeRefreshLayout.setVisibility(View.VISIBLE);
            mEmptySwipeRefreshLayout.setVisibility(View.GONE);
            mRecyclerView.setAdapter(mAdapter);
            setRefreshing(false);
        }
        else
        {
            mSwipeRefreshLayout.setVisibility(View.GONE);
            mEmptySwipeRefreshLayout.setVisibility(View.VISIBLE);
            setRefreshing(false);
        }
    }

    public void setRefreshing(final boolean isRefreshing)
    {
        mEmptySwipeRefreshLayout.setRefreshing(isRefreshing);
        mSwipeRefreshLayout.setRefreshing(isRefreshing);
    }

    @Override
    public void onDestroy()
    {
        LocalBroadcastManager.getInstance(getActivity())
                .unregisterReceiver(mUnreadCountChangedReceiver);
        super.onDestroy();
    }
}

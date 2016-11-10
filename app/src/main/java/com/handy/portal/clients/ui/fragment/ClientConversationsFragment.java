package com.handy.portal.clients.ui.fragment;

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
import com.handy.portal.library.ui.fragment.InjectedFragment;
import com.handy.portal.library.ui.widget.SafeSwipeRefreshLayout;
import com.handybook.shared.LayerHelper;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ClientConversationsFragment extends InjectedFragment
        implements ConversationsAdapter.Listener
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
        initSwipeRefreshLayouts();
        setRefreshing(true);
        initRecyclerView();
        mAdapter.refreshConversations();
    }

    private void initRecyclerView()
    {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new ConversationsAdapter(mLayerHelper, this);
    }

    private void initSwipeRefreshLayouts()
    {
        final SwipeRefreshLayout.OnRefreshListener onRefreshListener =
                new SwipeRefreshLayout.OnRefreshListener()
                {
                    @Override
                    public void onRefresh()
                    {
                        mAdapter.refreshConversations();
                    }
                };
        mEmptySwipeRefreshLayout.setOnRefreshListener(onRefreshListener);
        mSwipeRefreshLayout.setOnRefreshListener(onRefreshListener);
        mEmptySwipeRefreshLayout.setColorSchemeResources(R.color.handy_blue);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.handy_blue);
    }

    @Override
    public void onConversationsInitialized()
    {
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
}

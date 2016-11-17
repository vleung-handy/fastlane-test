package com.handy.portal.clients.ui.fragment;

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
    @BindView(R.id.client_conversations_empty_view)
    View mEmptyView;

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
}

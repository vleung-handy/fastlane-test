package com.handy.portal.clients.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.handy.portal.R;
import com.handy.portal.library.ui.fragment.InjectedFragment;
import com.handy.portal.library.ui.widget.SafeSwipeRefreshLayout;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ClientConversationsFragment extends InjectedFragment
{
    @BindView(R.id.client_messages_empty_swipe_refresh_layout)
    SafeSwipeRefreshLayout mEmptyMessagesSwipeRefreshLayout;

    public static ClientConversationsFragment newInstance()
    {
        return new ClientConversationsFragment();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState)
    {
        final View view = inflater.inflate(R.layout.fragment_client_messages, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState)
    {
        mEmptyMessagesSwipeRefreshLayout.setColorSchemeResources(R.color.handy_blue);
    }
}

package com.handy.portal.clients.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.handy.portal.R;
import com.handy.portal.clients.model.Client;
import com.handy.portal.clients.model.ClientList;
import com.handy.portal.clients.ui.adapter.ClientListRecyclerViewAdapter;
import com.handy.portal.core.constant.MainViewPage;
import com.handy.portal.core.constant.TransitionStyle;
import com.handy.portal.core.event.HandyEvent;
import com.handy.portal.core.manager.PageNavigationManager;
import com.handy.portal.core.manager.ProviderManager;
import com.handy.portal.core.ui.view.SimpleDividerItemDecoration;
import com.handy.portal.data.DataManager;
import com.handy.portal.library.ui.fragment.ProgressSpinnerFragment;
import com.handy.portal.library.ui.listener.PaginationScrollListener;
import com.handy.portal.logger.handylogger.model.ClientsLog;
import com.handy.portal.retrofit.HandyRetrofit2Callback;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ClientsListFragment extends ProgressSpinnerFragment {

    //This is the max number of clients to send back in the client list request
    private static final int CLIENT_REQUEST_LIST_LIMIT = 20;

    @Inject
    ProviderManager mProviderManager;
    @Inject
    PageNavigationManager mNavigationManager;

    @BindView(R.id.fragment_clients_list_recycler_view)
    RecyclerView mClientsListRecyclerView;
    @BindView(R.id.client_list_empty)
    LinearLayout mEmptyResultsLayout;
    @BindView(R.id.fetch_error_view)
    LinearLayout mFetchErrorView;
    @BindView(R.id.fetch_error_text)
    TextView mFetchErrorText;

    // Indicates if footer ProgressBar is shown (i.e. next page is loading)
    private boolean mIsLoading = false;
    // There will be no more clients if empty is returned
    private boolean mHasMoreClients = true;

    private ClientListRecyclerViewAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;

    public static ClientsListFragment newInstance() {
        return new ClientsListFragment();
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bus.register(this);
        mAdapter = new ClientListRecyclerViewAdapter(getActivity(), new ArrayList<Client>());
    }

    @NonNull
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        bus.post(new ClientsLog.ListShown());
        View view = getActivity().findViewById(R.id.progress_spinner_layout);
        //this saves the exact view state, including scroll position
        if (view == null) {
            view = super.onCreateView(inflater, container, savedInstanceState);
            inflater.inflate(R.layout.fragment_clients_list, (ViewGroup) view, true);
        }
        return view;
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mClientsListRecyclerView.setLayoutManager(mLayoutManager);
        mClientsListRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(getContext()));
        mClientsListRecyclerView.setAdapter(mAdapter);
        mClientsListRecyclerView.addOnScrollListener(new PaginationScrollListener(mLayoutManager) {
            @Override
            protected void loadMoreItems() {
                requestClientList(true);
            }

            @Override
            public boolean hasMoreItems() {
                return mHasMoreClients;
            }

            @Override
            public boolean isLoading() {
                return mIsLoading;
            }
        });
        mAdapter.setOnItemClickListener(new ClientListRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(final Client client) {
                mNavigationManager.navigateToPage(getActivity().getSupportFragmentManager(),
                        MainViewPage.CLIENT_DETAILS,
                        ClientDetailFragment.getBundle(client),
                        TransitionStyle.JOB_LIST_TO_DETAILS, true);

                bus.post(ClientDetailFragment.newInstance(client));
            }
        });

        if (mAdapter.getItemCount() == 0) { requestClientList(false); }
    }

    @Override
    public void onResume() {
        super.onResume();
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
    }

    @Override
    public void onDestroy() {
        bus.unregister(this);
        super.onDestroy();
    }

    /**
     * hides all the content views in this fragment
     * except the given content view
     */
    private void showContentViewAndHideOthers(@NonNull View contentView) {
        //Hide all views then set visible the correct one
        mClientsListRecyclerView.setVisibility(View.GONE);
        mFetchErrorView.setVisibility(View.GONE);

        contentView.setVisibility(View.VISIBLE);
    }

    @Override
    protected void hideProgressSpinner() {
        super.hideProgressSpinner();
        mIsLoading = false;
        mAdapter.removeLoadingFooter();
    }

    /**
     * requests jobs for which this pro was requested by customers for
     */
    private void requestClientList(final boolean isForPaginating) {
        mIsLoading = true;
        if (!isForPaginating) {
            showProgressSpinner(true);
        } else {
            mAdapter.addLoadingFooter();
        }

        dataManager.getClientList(mProviderManager.getLastProviderId(),
                mAdapter.getLastClientId(),
                CLIENT_REQUEST_LIST_LIMIT,
                new HandyRetrofit2Callback<ClientList>() {

                    @Override
                    public void onSuccess(@NonNull final ClientList response) {
                        hideProgressSpinner();
                        List<Client> clients = response.getClients();
                        if (clients.size() > 0) {
                            //If the size of the response is less then the limit, then there's no more clients
                            if (clients.size() < CLIENT_REQUEST_LIST_LIMIT) {
                                mHasMoreClients = false;
                            }
                            showContentViewAndHideOthers(mClientsListRecyclerView);
                            mAdapter.addAll(clients);
                        }
                        else {
                            //If there was no more clients and the existing item count is > 0 then show it
                            if (mAdapter.getItemCount() > 0) {
                                mHasMoreClients = false;
                                showContentViewAndHideOthers(mClientsListRecyclerView);
                            }
                            else {
                                showContentViewAndHideOthers(mEmptyResultsLayout);
                            }
                        }
                    }

                    @Override
                    public void onError(final DataManager.DataManagerError error) {
                        hideProgressSpinner();

                        @StringRes int errorMsgId;
                        if (error != null && error.getType() == DataManager.DataManagerError.Type.NETWORK) {
                            errorMsgId = R.string.error_fetching_connectivity_issue;
                        }
                        else {
                            errorMsgId = R.string.client_list_error;
                        }
                        //If there was no more clients and the existing item count is > 0 then show it
                        if (mAdapter.getItemCount() > 0) {
                            showContentViewAndHideOthers(mClientsListRecyclerView);
                            Toast.makeText(getContext(), getString(errorMsgId), Toast.LENGTH_LONG).show();
                        }
                        else {
                            showContentViewAndHideOthers(mFetchErrorView);
                            mFetchErrorText.setText(errorMsgId);
                        }
                    }
                });
    }

    @OnClick(R.id.try_again_button)
    public void onFetchErrorViewTryAgainButtonClicked() {
        requestClientList(false);
    }

    @OnClick(R.id.client_list_find_job)
    public void onFindJobsButtonClicked() {
        mNavigationManager.navigateToPage(getActivity().getSupportFragmentManager(),
                MainViewPage.AVAILABLE_JOBS, null, TransitionStyle.NONE, true);
    }
}

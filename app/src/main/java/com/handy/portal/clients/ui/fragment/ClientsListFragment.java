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
import com.handy.portal.bookings.model.Booking;
import com.handy.portal.clients.model.Client;
import com.handy.portal.clients.model.ClientList;
import com.handy.portal.clients.ui.adapter.ClientListRecyclerViewAdapter;
import com.handy.portal.core.constant.BundleKeys;
import com.handy.portal.core.constant.MainViewPage;
import com.handy.portal.core.constant.TransitionStyle;
import com.handy.portal.core.manager.PageNavigationManager;
import com.handy.portal.core.manager.ProviderManager;
import com.handy.portal.core.ui.view.SimpleDividerItemDecoration;
import com.handy.portal.data.DataManager;
import com.handy.portal.library.ui.fragment.ProgressSpinnerFragment;
import com.handy.portal.library.ui.listener.PaginationScrollListener;
import com.handy.portal.logger.handylogger.model.EventContext;
import com.handy.portal.logger.handylogger.model.RequestedJobsLog;
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
        mAdapter = new ClientListRecyclerViewAdapter(getActivity(), new ArrayList<Client>());
        //todo       bus.register(this);
    }

    @NonNull
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
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

        if (mAdapter.getItemCount() == 0) { requestClientList(false); }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        //todo       bus.unregister(this);
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
                            //If we're paginating there was no more additional clients and there are
                            // existing items, then show it
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

    //TODO sammy
    private void navigateToClientDetails(@NonNull Booking booking) {
        Bundle arguments = new Bundle();
        arguments.putSerializable(BundleKeys.BOOKING, booking);
        arguments.putString(BundleKeys.BOOKING_ID, booking.getId());
        arguments.putString(BundleKeys.BOOKING_TYPE, booking.getType().toString());
        arguments.putLong(BundleKeys.BOOKING_DATE, booking.getStartDate().getTime());
        arguments.putString(BundleKeys.EVENT_CONTEXT, EventContext.REQUESTED_JOBS);
        bus.post(new RequestedJobsLog.Clicked(booking));
//        mNavigationManager.navigateToPage(getActivity().getSupportFragmentManager(),
//                MainViewPage.JOB_DETAILS, arguments, TransitionStyle.JOB_LIST_TO_DETAILS, true);
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

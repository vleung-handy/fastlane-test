package com.handy.portal.clients.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.bookings.model.Booking;
import com.handy.portal.clients.model.Client;
import com.handy.portal.clients.ui.adapter.ClientListRecyclerViewAdapter;
import com.handy.portal.core.constant.BundleKeys;
import com.handy.portal.core.constant.MainViewPage;
import com.handy.portal.core.constant.TransitionStyle;
import com.handy.portal.core.event.HandyEvent;
import com.handy.portal.core.manager.PageNavigationManager;
import com.handy.portal.core.manager.ProviderManager;
import com.handy.portal.core.model.ProviderProfile;
import com.handy.portal.core.ui.view.SimpleDividerItemDecoration;
import com.handy.portal.data.DataManager;
import com.handy.portal.library.ui.fragment.ProgressSpinnerFragment;
import com.handy.portal.logger.handylogger.model.EventContext;
import com.handy.portal.logger.handylogger.model.RequestedJobsLog;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ClientsListFragment extends ProgressSpinnerFragment {

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

    private ClientListRecyclerViewAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    public static ClientsListFragment newInstance() {
        return new ClientsListFragment();
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
    }

    @Override
    public void onResume() {
        super.onResume();
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        if (mAdapter == null) {
            requestClientList();
        }
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

    /**
     * requests jobs for which this pro was requested by customers for
     */
    private void requestClientList() {
        showProgressSpinner(true);

        ProviderProfile provider = mProviderManager.getCachedProviderProfile();
        dataManager.getClientList(provider.getProviderId(), null, new DataManager.Callback<List<Client>>() {

            @Override
            public void onSuccess(final List<Client> response) {
                hideProgressSpinner();

                if(false) {//response.size() > 0) {
                    showContentViewAndHideOthers(mClientsListRecyclerView);
                    updateClientsListView(response);
                } else {
                    showContentViewAndHideOthers(mEmptyResultsLayout);
                }
            }

            @Override
            public void onError(final DataManager.DataManagerError error) {
                hideProgressSpinner();
                if (error != null && error.getType() == DataManager.DataManagerError.Type.NETWORK) {
                    mFetchErrorText.setText(R.string.error_fetching_connectivity_issue);
                }
                else {
                    mFetchErrorText.setText(R.string.client_list_error);
                }
                showContentViewAndHideOthers(mFetchErrorView);
            }
        });
    }

    /**
     * updates and shows the client list
     *
     * @param clientList sorted by date
     */
    private void updateClientsListView(@NonNull List<Client> clientList) {
        mAdapter = new ClientListRecyclerViewAdapter(getActivity(), clientList);
        mClientsListRecyclerView.setAdapter(mAdapter);
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
        requestClientList();
    }

    @OnClick(R.id.client_list_find_job)
    public void onFindJobsButtonClicked() {
        mNavigationManager.navigateToPage(getActivity().getSupportFragmentManager(),
                MainViewPage.AVAILABLE_JOBS, null, TransitionStyle.NONE, true);
    }
}

package com.handy.portal.clients.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.handy.portal.R;
import com.handy.portal.clients.model.Client;
import com.handy.portal.clients.model.ClientDetail;
import com.handy.portal.clients.model.Price;
import com.handy.portal.clients.ui.element.ClientMapProvider;
import com.handy.portal.clients.ui.element.ClientMapView;
import com.handy.portal.core.manager.PageNavigationManager;
import com.handy.portal.core.manager.ProviderManager;
import com.handy.portal.core.model.Address;
import com.handy.portal.core.ui.activity.MainActivity;
import com.handy.portal.core.ui.fragment.ActionBarFragment;
import com.handy.portal.data.DataManager;
import com.handy.portal.library.util.CurrencyUtils;
import com.handy.portal.library.util.UIUtils;
import com.handy.portal.logger.handylogger.model.ClientsLog;
import com.handy.portal.retrofit.HandyRetrofit2Callback;
import com.handybook.shared.core.HandyLibrary;
import com.handybook.shared.layer.LayerConstants;
import com.handybook.shared.layer.model.CreateConversationResponse;
import com.handybook.shared.layer.ui.MessagesListActivity;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ClientDetailFragment extends ActionBarFragment {
    private static final String KEY_CLIENT = "client";
    @Inject
    ProviderManager mProviderManager;
    @Inject
    PageNavigationManager mNavigationManager;

    @BindView(R.id.client_detail_img_view)
    ImageView mProfileImgView;
    @BindView(R.id.client_detail_initials_layout)
    View mInitialsLayout;
    @BindView(R.id.client_detail_initials)
    TextView mInitialsTextView;
    @BindView(R.id.client_detail_city)
    TextView mCityTextView;
    @BindView(R.id.client_detail_green_dot)
    ImageView mGreenDotImageView;
    @BindView(R.id.client_detail_description)
    TextView mDescriptionTextView;
    @BindView(R.id.client_detail_total_earnings)
    TextView mTotalEarningsText;
    @BindView(R.id.client_detail_activity)
    TextView mActivityText;
    @BindView(R.id.client_details_map_placeholder)
    ViewGroup mMapLayout;

    private Client mClient;

    public static ClientDetailFragment newInstance() {
        return new ClientDetailFragment();
    }

    public static Bundle getBundle(@NonNull Client client) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_CLIENT, client);
        return bundle;
    }

    public static ClientDetailFragment newInstance(@NonNull Client client) {
        ClientDetailFragment fragment = new ClientDetailFragment();
        fragment.setArguments(getBundle(client));
        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mClient = (Client) bundle.getSerializable(KEY_CLIENT);
        }
    }

    @NonNull
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        bus.post(new ClientsLog.DetailViewShown(mClient.getId()));

        View view = inflater.inflate(R.layout.fragment_client_details, container, false);
        ButterKnife.bind(this, view);

        setActionBar(getString(R.string.client_details_titlebar_text, mClient.getFirstName()), true);
        initializeUI();
        initializeMaps();
        requestClientDetails();

        return view;
    }

    private void initializeUI() {
        //If there's no profile url then just display initials
        if (android.text.TextUtils.isEmpty(mClient.getProfileImageUrl())) {
            mProfileImgView.setVisibility(View.GONE);
            mInitialsTextView.setVisibility(View.VISIBLE);
            mInitialsTextView.setText(mClient.getFirstName().substring(0, 1) +
                    mClient.getLastName().substring(0, 1));
        }
        else {
            mProfileImgView.setVisibility(View.VISIBLE);
            mInitialsLayout.setVisibility(View.GONE);
            Picasso.with(getContext())
                    .load(mClient.getProfileImageUrl())
                    .placeholder(R.drawable.img_pro_placeholder)
                    .noFade()
                    .into(mProfileImgView);
        }

        if(mClient.getAddress() == null) {
            mCityTextView.setVisibility(View.GONE);
        } else {
            mCityTextView.setText(mClient.getAddress().getCityState());
        }

        Client.Context clientContext = mClient.getContext();

        //If there's no client context then don't show a green dot.
        if (clientContext == null) {
            mDescriptionTextView.setVisibility(View.GONE);
            mGreenDotImageView.setVisibility(View.GONE);
        }
        else {
            mDescriptionTextView.setText(clientContext.getDescription());
            //Green dot only displays for upcoming bookings
            mGreenDotImageView.setVisibility(
                    clientContext.getContextType() == Client.ContextType.UpcomingBooking
                            ? View.VISIBLE : View.GONE);
        }
    }

    private void initializeMaps() {
        ClientMapView mapView;
        Address address = mClient.getAddress();
        if(address == null) {
            mMapLayout.setVisibility(View.GONE);
            return;
        } else {
            mapView = ((ClientMapProvider) getContext()).getClientMapView();
        }


        if(mapView.getLayoutParams() == null) {
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            mapView.setLayoutParams(layoutParams);
        }

        if (mapView.getParent() != null) {
            ((ViewGroup) mapView.getParent()).removeView(mapView);
        }

        mMapLayout.addView(mapView);
        mapView.onStart();
        mapView.onResume();
        mapView.getMapAsync(new LatLng(address.getLatitude(), address.getLongitude()));
    }

    /**
     * requests jobs for which this pro was requested by customers for
     */
    private void requestClientDetails() {
        dataManager.getClientDetail(mProviderManager.getLastProviderId(),
                mClient.getId(),
                new HandyRetrofit2Callback<ClientDetail>() {
                    @Override
                    public void onSuccess(@NonNull final ClientDetail response) {
                        //If it's detached this means the fragment is in limbo. Just return
                        if(isDetached())
                            return;

                        //Bind the stats data
                        Price price = response.getStats().getTotalEarnings();
                        if(price != null) {
                            mTotalEarningsText.setText(CurrencyUtils.formatPriceWithCents(
                                    price.getAmount(),
                                    price.getSymbol()));
                        }
                        mActivityText.setText(getContext().
                                getResources().getQuantityString(R.plurals.client_details_jobs_completed,
                                response.getStats().getTotalJobsCount(),
                                response.getStats().getTotalJobsCount()));
                    }

                    @Override
                    public void onError(final DataManager.DataManagerError error) {
                        //Do nothing
                    }
                });
    }

    @OnClick(R.id.client_detail_send_message)
    public void sendMessage() {
        bus.post(new ClientsLog.SendMessageTapped(mClient.getId()));
        //Only works on Release builds
        HandyLibrary.getInstance().getHandyService().createConversationForPro(
                mClient.getId(), "", new Callback<CreateConversationResponse>() {
                    @Override
                    public void success(
                            final CreateConversationResponse conversationResponse,
                            final Response response) {
                        Intent intent = new Intent(getContext(), MessagesListActivity.class);
                        intent.putExtra(LayerConstants.LAYER_CONVERSATION_KEY,
                                Uri.parse(conversationResponse.getConversationId()));
                        intent.putExtra(LayerConstants.KEY_HIDE_ATTACHMENT_BUTTON, true);
                        startActivity(intent);
                    }

                    @Override
                    public void failure(final RetrofitError error) {
                        showToast(R.string.an_error_has_occurred);
                    }
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ClientMapView mapView = ((ClientMapProvider) getContext()).getClientMapView();
        mapView.onPause();
        mapView.onStop();
    }
}

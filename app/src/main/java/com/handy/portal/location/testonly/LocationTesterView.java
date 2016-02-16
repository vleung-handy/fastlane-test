package com.handy.portal.location.testonly;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.handy.portal.R;
import com.handy.portal.location.LocationService;
import com.handy.portal.location.model.LocationQuerySchedule;
import com.handy.portal.location.model.LocationQueryStrategy;
import com.handy.portal.util.DateTimeUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

/**
 * used only for testing the location service!
 */
public class LocationTesterView extends LinearLayout
{

    @Bind(R.id.location_services_toggle_switch)
    Switch mLocationServicesToggle;
    @Bind(R.id.location_services_toggle_accuracy_switch)
    Switch mLocationServicesAccuracyToggle;
    @Bind(R.id.location_services_server_posting_interval_text)
    EditText mLocationServicesServerPostingIntervalText;
    @Bind(R.id.location_services_polling_interval_text)
    EditText mLocationServicesPollingIntervalText;
    @Bind(R.id.location_services_distance_filter_text)
    EditText mDistanceFilterText;

    LocationQueryStrategy mLocationQueryStrategy = new LocationQueryStrategy();

    static final int DEFAULT_SERVER_POSTING_INTERVAL_SEC = 10 * DateTimeUtils.SECONDS_IN_MINUTE;
    static final int DEFAULT_POLLING_INTERVAL_SEC = 30;
    static final int DEFAULT_DISTANCE_FILTER_METERS = 40;

    public LocationTesterView(final Context context)
    {
        super(context);
        init(context);
    }

    public LocationTesterView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
        init(context);

    }

    public LocationTesterView(final Context context, final AttributeSet attrs, final int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init(context);

    }

    @TargetApi(21)
    public LocationTesterView(final Context context, final AttributeSet attrs, final int defStyleAttr, final int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }


    @OnCheckedChanged(R.id.location_services_toggle_switch)
    public void onLocationServicesToggle()
    {
        updateParams();
        if(mLocationServicesToggle.isChecked())
        {
            startLocationService();
        }
        else
        {
            stopLocationService();
        }
    }

    @OnCheckedChanged(R.id.location_services_toggle_accuracy_switch)
    public void onLocationServicesAccuracyToggle()
    {
        if(mLocationServicesAccuracyToggle.isChecked())
        {
            mLocationQueryStrategy.setLocationAccuracyPriority(2);
        }
        else
        {
            mLocationQueryStrategy.setLocationAccuracyPriority(1);
        }
    }

    private void updateParams()
    {
        try
        {
            int serverPostingIntervalSec = Integer.parseInt(mLocationServicesServerPostingIntervalText.getText().toString());
            mLocationQueryStrategy.setServerPollingIntervalSeconds(serverPostingIntervalSec);

            int pollingIntervalSec = Integer.parseInt(mLocationServicesPollingIntervalText.getText().toString());
            mLocationQueryStrategy.setLocationPollingIntervalSeconds(pollingIntervalSec);

            int distanceFilterMeters = Integer.parseInt(mDistanceFilterText.getText().toString());
            mLocationQueryStrategy.setDistanceFilterMeters(distanceFilterMeters);
        }
        catch (Exception e)
        {
            Toast.makeText(getContext(), "Invalid input", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @OnClick(R.id.location_services_update_params_button)
    public void onUpdateParamsButtonClicked()
    {
        updateParams();

        if(mLocationServicesToggle.isChecked())
        {
            stopLocationService();
            startLocationService();
        }
    }

    private void stopLocationService()
    {
        getContext().stopService(new Intent(getContext(), LocationService.class));
        Toast.makeText(getContext(), "stopped location service", Toast.LENGTH_SHORT).show();
    }

    private void startLocationService()
    {
        Intent i = new Intent(getContext(), LocationService.class);
        //TODO: pass the strategy object
        Date now = new Date();
        mLocationQueryStrategy.setStartDate(now);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(Calendar.DAY_OF_YEAR, 21);
        Date end = calendar.getTime();
        mLocationQueryStrategy.setEndDate(end);
        LinkedList<LocationQueryStrategy> locationQueryStrategies = new LinkedList<>();
        locationQueryStrategies.add(mLocationQueryStrategy);
        LocationQuerySchedule locationQuerySchedule = new LocationQuerySchedule(locationQueryStrategies);
        Bundle bundle = new Bundle();
        bundle.putParcelable(LocationQuerySchedule.EXTRA_LOCATION_SCHEDULE, locationQuerySchedule);
        i.putExtras(bundle);
        getContext().startService(i);
    }

    private void init(Context context)
    {
        inflate(context, R.layout.element_test_location_services, this);
        ButterKnife.bind(this);

        mLocationServicesPollingIntervalText.setText("" + DEFAULT_POLLING_INTERVAL_SEC);
        mLocationServicesServerPostingIntervalText.setText("" + DEFAULT_SERVER_POSTING_INTERVAL_SEC);
        mDistanceFilterText.setText("" + DEFAULT_DISTANCE_FILTER_METERS);
    }
}

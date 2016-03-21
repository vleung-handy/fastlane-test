package com.handy.portal.logger.handylogger;

import com.handy.portal.constant.LocationKey;
import com.handy.portal.manager.ProviderManager;
import com.handy.portal.model.Address;
import com.handy.portal.model.LocationData;
import com.handy.portal.model.Provider;

public class EventLogFactory
{
    private ProviderManager mProviderManager;

    public EventLogFactory(ProviderManager providerManager)
    {
        mProviderManager = providerManager;
    }

    // private helpers
    private String getProviderId()
    {
        Provider provider = mProviderManager.getCachedActiveProvider();
        if (provider != null && provider.getId() != null)
        {
            return provider.getId();
        }
        else
        {
            return "";
        }
    }

    private String getVersionTrack()
    {
        Provider provider = mProviderManager.getCachedActiveProvider();
        if (provider != null && provider.getVersionTrack() != null)
        {
            return provider.getVersionTrack();
        }
        else
        {
            return "";
        }
    }

    private static String getZipCode(Address address)
    {
        if (address != null)
        {
            return address.getZip();
        }
        else
        {
            return "";
        }
    }

    private double getLatitude(Address address)
    {

        if (address != null)
        {
            return address.getLatitude();
        }
        else
        {
            return 0;
        }
    }

    private double getLongitude(Address address)
    {

        if (address != null)
        {
            return address.getLongitude();
        }
        else
        {
            return 0;
        }
    }

    private double getLatitude(LocationData location)
    {
        try
        {
            return Double.parseDouble(location.getLocationMap().get(LocationKey.LATITUDE));
        }
        catch (Exception e)
        {
            return 0;
        }
    }

    private double getLongitude(LocationData location)
    {
        try
        {
            return Double.parseDouble(location.getLocationMap().get(LocationKey.LONGITUDE));
        }
        catch (Exception e)
        {
            return 0;
        }
    }

    private double getAccuracy(LocationData location)
    {
        try
        {
            return Double.parseDouble(location.getLocationMap().get(LocationKey.ACCURACY));
        }
        catch (Exception e)
        {
            return 0;
        }
    }
}

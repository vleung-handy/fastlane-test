package com.handy.portal.location;

import com.handy.portal.location.model.LocationBatchUpdate;

//TODO: test only, will possibly remove
public interface OnLocationBatchUpdateReadyListener
{
    void onLocationBatchUpdateReady(LocationBatchUpdate locationBatchUpdate);
}

package com.handy.portal.event;

import com.handy.portal.data.DataManager;
import com.handy.portal.model.ZipClusterPolygons;

public abstract class BookingEvent extends HandyEvent
{
    public static class RequestZipClusterPolygons extends RequestEvent
    {
        public final String zipClusterId;

        public RequestZipClusterPolygons(String zipClusterId)
        {
            this.zipClusterId = zipClusterId;
        }
    }

    public static class ReceiveZipClusterPolygonsSuccess extends ReceiveSuccessEvent
    {
        public final ZipClusterPolygons zipClusterPolygons;

        public ReceiveZipClusterPolygonsSuccess(ZipClusterPolygons zipClusterPolygons)
        {
            this.zipClusterPolygons = zipClusterPolygons;
        }
    }

    public static class ReceiveZipClusterPolygonsError extends ReceiveErrorEvent
    {
        public ReceiveZipClusterPolygonsError(DataManager.DataManagerError error)
        {
            this.error = error;
        }
    }
}

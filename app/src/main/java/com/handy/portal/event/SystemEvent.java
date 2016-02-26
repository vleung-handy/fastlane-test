package com.handy.portal.event;

/**
 * events that are Android system-wide
 */
public abstract class SystemEvent
{
    /**
     * the device regained network connection from no connection
     */
    public static class NetworkReconnected extends HandyEvent.RequestEvent
    {
    }
}

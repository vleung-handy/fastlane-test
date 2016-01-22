package com.handy.portal.helpcenter;

import com.handy.portal.annotation.Track;
import com.handy.portal.data.DataManager;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.helpcenter.model.HelpNode;

import retrofit.mime.TypedInput;

/**
 * Created by vleung on 1/22/16.
 */
public abstract class HelpEvent extends HandyEvent
{
    //Help Node
    public static class RequestHelpNode extends HandyEvent
    {
        public String nodeId;
        public String bookingId;

        public RequestHelpNode(String nodeId, String bookingId)
        {
            this.nodeId = nodeId;
            this.bookingId = bookingId;
        }
    }


    public static class ReceiveHelpNodeSuccess extends ReceiveSuccessEvent
    {
        public HelpNode helpNode;

        public ReceiveHelpNodeSuccess(HelpNode helpNode)
        {
            this.helpNode = helpNode;
        }
    }


    public static class ReceiveHelpNodeError extends ReceiveErrorEvent
    {
        public ReceiveHelpNodeError(DataManager.DataManagerError error)
        {
            this.error = error;
        }
    }


    //Help Booking Node
    public static class RequestHelpBookingNode extends HandyEvent
    {
        public String nodeId;
        public String bookingId;

        public RequestHelpBookingNode(String nodeId, String bookingId)
        {
            this.nodeId = nodeId;
            this.bookingId = bookingId;
        }
    }


    public static class ReceiveHelpBookingNodeSuccess extends ReceiveSuccessEvent
    {
        public HelpNode helpNode;

        public ReceiveHelpBookingNodeSuccess(HelpNode helpNode)
        {
            this.helpNode = helpNode;
        }
    }


    public static class ReceiveHelpBookingNodeError extends ReceiveErrorEvent
    {
        public ReceiveHelpBookingNodeError(DataManager.DataManagerError error)
        {
            this.error = error;
        }
    }


    //Help Payments Node
    public static class RequestHelpPaymentsNode extends RequestEvent {}


    public static class ReceiveHelpPaymentsNodeSuccess extends ReceiveSuccessEvent
    {
        public HelpNode helpNode;

        public ReceiveHelpPaymentsNodeSuccess(HelpNode helpNode)
        {
            this.helpNode = helpNode;
        }
    }


    public static class ReceiveHelpPaymentsNodeError extends ReceiveErrorEvent
    {
        public ReceiveHelpPaymentsNodeError(DataManager.DataManagerError error)
        {
            this.error = error;
        }
    }


}

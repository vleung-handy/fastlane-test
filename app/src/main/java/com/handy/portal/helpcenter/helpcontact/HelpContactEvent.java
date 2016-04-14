package com.handy.portal.helpcenter.helpcontact;

import com.handy.portal.data.DataManager;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.logger.mixpanel.annotation.Track;

import okhttp3.RequestBody;

public abstract class HelpContactEvent extends HandyEvent
{
    //Help Contact Message
    @Track("pro help contact form submitted")
    public static class RequestNotifyHelpContact extends HandyEvent
    {
        public RequestBody body;

        public RequestNotifyHelpContact(RequestBody body)
        {
            this.body = body;
        }
    }


    public static class ReceiveNotifyHelpContactSuccess extends ReceiveSuccessEvent
    {
        public ReceiveNotifyHelpContactSuccess() { }
    }


    public static class ReceiveNotifyHelpContactError extends ReceiveErrorEvent
    {
        public ReceiveNotifyHelpContactError(DataManager.DataManagerError error)
        {
            this.error = error;
        }
    }
}

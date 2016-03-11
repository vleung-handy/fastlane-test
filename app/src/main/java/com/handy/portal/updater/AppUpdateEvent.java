package com.handy.portal.updater;

import android.app.Activity;

import com.handy.portal.data.DataManager;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.logger.mixpanel.annotation.Track;
import com.handy.portal.updater.model.UpdateDetails;

public abstract class AppUpdateEvent
{
    public static class RequestUpdateCheck extends HandyEvent.RequestEvent
    {
        public Activity sender = null;

        public RequestUpdateCheck(Activity sender)
        {
            this.sender = sender;
        }
    }


    public static class ReceiveUpdateAvailableSuccess extends HandyEvent.ReceiveSuccessEvent
    {
        public UpdateDetails updateDetails;

        public ReceiveUpdateAvailableSuccess(UpdateDetails updateDetails)
        {
            this.updateDetails = updateDetails;
        }
    }


    public static class ReceiveUpdateAvailableError extends HandyEvent.ReceiveErrorEvent
    {
        public ReceiveUpdateAvailableError(DataManager.DataManagerError error)
        {
            this.error = error;
        }
    }


    public static class DownloadUpdateSuccessful extends HandyEvent {}


    @Track("portal app update download failed")
    public static class DownloadUpdateFailed extends HandyEvent {}
}

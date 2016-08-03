package com.handy.portal.event;

import com.handy.portal.data.DataManager;
import com.handy.portal.model.ProviderSettings;

public class ProviderSettingsEvent extends HandyEvent
{
    public static class RequestProviderSettings extends RequestEvent {}


    public static class ReceiveProviderSettingsSuccess extends ReceiveSuccessEvent
    {
        private ProviderSettings mProviderSettings;

        public ReceiveProviderSettingsSuccess(ProviderSettings providerSettings)
        {
            mProviderSettings = providerSettings;
        }

        public ProviderSettings getProviderSettings()
        {
            return mProviderSettings;
        }
    }


    public static class ReceiveProviderSettingsError extends ReceiveErrorEvent
    {
        public ReceiveProviderSettingsError(DataManager.DataManagerError error)
        {
            this.error = error;
        }
    }


    public static class RequestProviderSettingsUpdate extends RequestEvent
    {
        private final ProviderSettings mProviderSettings;

        public RequestProviderSettingsUpdate(ProviderSettings providerSettings)
        {
            mProviderSettings = providerSettings;
        }

        public ProviderSettings getProviderSettings()
        {
            return mProviderSettings;
        }
    }


    public static class ReceiveProviderSettingsUpdateSuccess extends ReceiveSuccessEvent
    {
        private ProviderSettings mProviderSettings;

        public ReceiveProviderSettingsUpdateSuccess(ProviderSettings providerSettings)
        {
            mProviderSettings = providerSettings;
        }

        public ProviderSettings getProviderSettings()
        {
            return mProviderSettings;
        }
    }


    public static class ReceiveProviderSettingsUpdateError extends ReceiveErrorEvent
    {
        public ReceiveProviderSettingsUpdateError(DataManager.DataManagerError error)
        {
            this.error = error;
        }
    }


    // No success or error callbacks for now
    public static class RequestIdVerificationFinish extends RequestEvent
    {
        private final String mFinishIdVerificationUrl;

        public RequestIdVerificationFinish(final String finishIdVerificationUrl)
        {
            mFinishIdVerificationUrl = finishIdVerificationUrl;
        }

        public String getFinishIdVerificationUrl()
        {
            return mFinishIdVerificationUrl;
        }
    }
}

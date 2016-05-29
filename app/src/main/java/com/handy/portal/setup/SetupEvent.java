package com.handy.portal.setup;

import com.handy.portal.data.DataManager;
import com.handy.portal.event.HandyEvent;

public class SetupEvent
{
    public static class RequestSetupData extends HandyEvent.RequestEvent {}


    public static class ReceiveSetupDataSuccess extends HandyEvent.ReceiveSuccessEvent
    {
        private SetupData mSetupData;

        public ReceiveSetupDataSuccess(final SetupData setupData)
        {
            mSetupData = setupData;
        }

        public SetupData getSetupData()
        {
            return mSetupData;
        }
    }


    public static class ReceiveSetupDataError extends HandyEvent.ReceiveErrorEvent
    {
        public ReceiveSetupDataError(final DataManager.DataManagerError error)
        {
            this.error = error;
        }
    }
}

package com.handy.portal.event;

import com.handy.portal.annotation.Track;
import com.handy.portal.data.DataManager;
import com.handy.portal.model.ProviderPersonalInfo;
import com.handy.portal.model.ProviderProfile;

public abstract class ProfileEvent extends HandyEvent
{
    public static class RequestProviderProfile extends RequestEvent {}

    public static class ReceiveProviderProfileSuccess extends ReceiveSuccessEvent
    {
        public ProviderProfile providerProfile;

        public ReceiveProviderProfileSuccess(ProviderProfile providerProfile)
        {
            this.providerProfile = providerProfile;
        }
    }

    public static class ReceiveProviderProfileError extends ReceiveErrorEvent {}

    public static class RequestSendResupplyKit extends RequestEvent {}

    public static class ReceiveSendResupplyKitSuccess extends ReceiveSuccessEvent
    {
        public final ProviderProfile providerProfile;

        public ReceiveSendResupplyKitSuccess(ProviderProfile providerProfile)
        {
            this.providerProfile = providerProfile;
        }
    }

    public static class ReceiveSendResupplyKitError extends ReceiveErrorEvent
    {
        public ReceiveSendResupplyKitError(DataManager.DataManagerError error)
        {
            this.error = error;
        }
    }

    public static class RequestProfileUpdate extends RequestEvent
    {
        public String email;
        public String phone;
        public String address1;
        public String address2;
        public String city;
        public String state;
        public String zipCode;

        public RequestProfileUpdate(CharSequence email, CharSequence phone, CharSequence address1, CharSequence address2, CharSequence city, CharSequence state, CharSequence zipCode)
        {
            this.email = email.toString();
            this.phone = phone.toString();
            this.address1 = address1.toString();
            this.address2 = address2.toString();
            this.city = city.toString();
            this.state = state.toString();
            this.zipCode = zipCode.toString();
        }
    }

    public static class ReceiveProfileUpdateSuccess extends ReceiveSuccessEvent
    {
        public ProviderPersonalInfo providerPersonalInfo;

        public ReceiveProfileUpdateSuccess(ProviderPersonalInfo providerPersonalInfo)
        {
            this.providerPersonalInfo = providerPersonalInfo;
        }
    }

    public static class ReceiveProfileUpdateError extends ReceiveErrorEvent
    {
        public ReceiveProfileUpdateError(DataManager.DataManagerError error)
        {
            this.error = error;
        }
    }

    @Track("provider edit profile submitted")
    public static class SubmittedProfileUpdate extends HandyEvent {}
}

package com.handy.portal.event;

import com.handy.portal.data.DataManager;
import com.handy.portal.model.ProviderPersonalInfo;

public abstract class ProfileEvent extends HandyEvent
{
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

}

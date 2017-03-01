package com.handy.portal.terms;

import android.support.annotation.NonNull;

import com.handy.portal.core.event.HandyEvent;

/**
 * events used by the bus
 */
abstract class TermsEvent {
    static class AcceptTerms extends HandyEvent.AnalyticsEvent {
        private final String mTermsCode;
        private final TermsDetails mTermsDetails;

        AcceptTerms(@NonNull TermsDetails termsDetails) {
            mTermsDetails = termsDetails;
            mTermsCode = termsDetails.getCode();
        }

        TermsDetails getTermsDetails() {
            return mTermsDetails;
        }
    }


    static class AcceptTermsError extends HandyEvent.AnalyticsEvent {
    }


    static class AcceptTermsSuccess extends HandyEvent {
        private final String mTermsCode;

        AcceptTermsSuccess(String termsCode) {
            mTermsCode = termsCode;
        }

        public String getTermsCode() {
            return mTermsCode;
        }
    }
}

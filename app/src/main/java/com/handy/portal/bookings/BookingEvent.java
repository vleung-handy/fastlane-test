package com.handy.portal.bookings;

import android.support.annotation.Nullable;

import com.handy.portal.bookings.model.BookingsWrapper;
import com.handy.portal.bookings.model.PostCheckoutInfo;
import com.handy.portal.core.event.HandyEvent;
import com.handy.portal.core.model.ZipClusterPolygons;
import com.handy.portal.data.DataManager;

import java.util.List;

public abstract class BookingEvent extends HandyEvent {
    public static class RequestZipClusterPolygons extends RequestEvent {
        public final String zipClusterId;

        public RequestZipClusterPolygons(String zipClusterId) {
            this.zipClusterId = zipClusterId;
        }
    }


    public static class ReceiveProRequestedJobsSuccess extends ReceiveSuccessEvent {
        public final List<BookingsWrapper> mProRequestedJobs;

        public ReceiveProRequestedJobsSuccess(List<BookingsWrapper> proRequestedJobs) {
            mProRequestedJobs = proRequestedJobs;
        }

        public List<BookingsWrapper> getProRequestedJobs() {
            return mProRequestedJobs;
        }
    }


    public static class ReceiveProRequestedJobsError extends ReceiveErrorEvent {
        public ReceiveProRequestedJobsError(@Nullable DataManager.DataManagerError error) {
            this.error = error;
        }
    }


    public static class ReceiveZipClusterPolygonsSuccess extends ReceiveSuccessEvent {
        public final ZipClusterPolygons zipClusterPolygons;

        public ReceiveZipClusterPolygonsSuccess(ZipClusterPolygons zipClusterPolygons) {
            this.zipClusterPolygons = zipClusterPolygons;
        }
    }


    public static class ReceiveZipClusterPolygonsError extends ReceiveErrorEvent {
        public ReceiveZipClusterPolygonsError(DataManager.DataManagerError error) {
            this.error = error;
        }
    }


    public static class RateCustomerSuccess extends ReceiveSuccessEvent {}


    public static class RateCustomerError extends ReceiveErrorEvent {
        public RateCustomerError(DataManager.DataManagerError error) { this.error = error; }
    }


    public static class ReceiveProRequestedJobsCountSuccess extends ReceiveSuccessEvent {
        private int mCount;

        public ReceiveProRequestedJobsCountSuccess(final int count) {
            mCount = count;
        }

        public int getCount() {
            return mCount;
        }
    }


    public static class ReceivePostCheckoutInfoSuccess extends ReceiveSuccessEvent {
        private PostCheckoutInfo mPostCheckoutInfo;

        public ReceivePostCheckoutInfoSuccess(final PostCheckoutInfo postCheckoutInfo) {
            mPostCheckoutInfo = postCheckoutInfo;
        }

        public PostCheckoutInfo getPostCheckoutInfo() {
            return mPostCheckoutInfo;
        }
    }


    public static class ReceivePostCheckoutInfoError extends ReceiveErrorEvent {

    }
}

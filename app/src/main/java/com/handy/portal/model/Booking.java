package com.handy.portal.model;

import android.support.annotation.NonNull;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.gson.annotations.SerializedName;
import com.handy.portal.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class Booking implements Comparable<Booking>, Serializable
{
    public enum BookingType
    {
        BOOKING_PROXY,
        BOOKING,;
    }

    @SerializedName("id")
    private String mId;
    @SerializedName("type")
    private String mType;
    @SerializedName("service_name")
    private String mService;
    @SerializedName("service")
    private ServiceInfo mServiceInfo;
    @SerializedName("start_date")
    private Date mStartDate;
    @SerializedName("status")
    private String mStatus;
    @SerializedName("end_date")
    private Date mEndDate;
    @SerializedName("reveal_date")
    private Date mRevealDate;

    @SerializedName("check_in_summary")
    private CheckInSummary mCheckInSummary;
    @SerializedName("eta_lateness_minutes")
    private Integer mProviderMinutesLate;//value returned from server can be null

    @SerializedName("address")
    private Address mAddress;

    @SerializedName("is_requested")
    private boolean mIsRequested;
    @SerializedName("payment_to_provider")
    private PaymentInfo mPaymentToProvider;
    @SerializedName("bonus")
    private PaymentInfo mBonusPayment;
    @SerializedName("frequency")
    private int mFrequency;

    @SerializedName("provider_id")
    private String mProviderId;
    @SerializedName("partner")
    private String mPartner;
    @SerializedName("country")
    private String mCountry;
    @SerializedName("user")
    private User mUser;
    @SerializedName("actions")
    private List<Action> mActionList;
    @SerializedName("booking_phone")
    private String mBookingPhone;

    @SerializedName("booking_instructions")
    private List<BookingInstruction> mBookingInstructions;
    @SerializedName("booking_instruction_groups")
    private List<BookingInstructionGroup> mBookingInstructionGroups;
    @SerializedName("booking_extras")
    private ArrayList<ExtraInfoWrapper> mExtrasInfo;
    @SerializedName("description")
    private String mDescription;

    @SerializedName("distance")
    private String mFormattedDistance;
    @SerializedName("location_name")
    private String mLocationName;
    @SerializedName("claimed_by_me")
    private boolean mClaimedByMe;
    @SerializedName("midpoint")
    private Coordinates mMidpoint;
    @SerializedName("radius")
    private float mRadius;
    @SerializedName("zipcluster_id")
    private String mZipClusterId;

    public int compareTo(@NonNull Booking other)
    {
        boolean isComparingWithProxy = this.isProxy() || other.isProxy();
        if (!isComparingWithProxy && getProviderId().equals(NO_PROVIDER_ASSIGNED))
        {
            if (this.isRequested() && !other.isRequested())
            {
                return -1;
            }
            if (!this.isRequested() && other.isRequested())
            {
                return 1;
            }
        }
        return mStartDate.compareTo(other.mStartDate);
    }

    public boolean equals(Object o)
    {
        if (!(o instanceof Booking))
        {
            return false;
        }
        Booking b = (Booking) o;
        return b.mId.equals(mId);
    }

    public String getStatus()
    {
        return mStatus;
    }

    public List<BookingInstruction> getBookingInstructions()
    {
        return mBookingInstructions;
    }

    public List<BookingInstructionGroup> getBookingInstructionGroups()
    {
        return mBookingInstructionGroups;
    }

    public int getFrequency()
    {
        return mFrequency;
    }

    public String getPartner()
    {
        return mPartner;
    }

    public PaymentInfo getPaymentToProvider()
    {
        return mPaymentToProvider;
    }

    public PaymentInfo getBonusPaymentToProvider()
    {
        return mBonusPayment;
    }

    public boolean isRequested()
    {
        return mIsRequested;
    }

    public String getId()
    {
        return mId;
    }

    public boolean isStarted()
    {
        boolean isStarted = false;
        Date currentTime = Calendar.getInstance().getTime();
        if (getStartDate().compareTo(currentTime) < 0)
        {
            isStarted = true;
        }
        return isStarted;
    }

    public boolean isEnded()
    {
        boolean isEnded = false;
        Date currentTime = Calendar.getInstance().getTime();
        if (getEndDate().compareTo(currentTime) < 0)
        {
            isEnded = true;
        }
        return isEnded;
    }

    public boolean isRecurring()
    {
        return mFrequency > 0;
    }

    public String getService()
    {
        return mService;
    }

    public Date getStartDate()
    {
        return mStartDate;
    }

    public Date getEndDate()
    {
        return mEndDate;
    }

    public Address getAddress()
    {
        return mAddress;
    }

    public String getProviderId()
    {
        return mProviderId;
    }

    public ArrayList<ExtraInfoWrapper> getExtrasInfo()
    {
        return mExtrasInfo;
    }

    public ServiceInfo getServiceInfo()
    {
        return mServiceInfo;
    }

    public User getUser()
    {
        return mUser;
    }

    public String getBookingPhone()
    {
        return mBookingPhone;
    }

    public List<ExtraInfoWrapper> getExtrasInfoByMachineName(final String machineName)
    {
        ArrayList<Booking.ExtraInfoWrapper> extrasInfo = getExtrasInfo();
        if (extrasInfo != null)
        {
            return new ArrayList<>(Collections2.filter(extrasInfo, new Predicate<ExtraInfoWrapper>()
            {
                @Override
                public boolean apply(Booking.ExtraInfoWrapper input)
                {
                    return machineName.equals(input.getExtraInfo().getMachineName());
                }
            }));
        }
        return Collections.emptyList();
    }

    public boolean isUK()
    {
        return "GB".equalsIgnoreCase(mCountry);
    }

    public CheckInSummary getCheckInSummary()
    {
        return mCheckInSummary;
    }

    public Integer getProviderMinutesLate()
    {
        return mProviderMinutesLate;
    }

    //mProviderId = 0, no one assigned can claim, otherwise is already claimed
    public static final String NO_PROVIDER_ASSIGNED = "0";

    public String getFormattedDistance()
    {
        return mFormattedDistance;
    }

    public String getDescription()
    {
        return mDescription;
    }

    public BookingType getType()
    {
        return BookingType.valueOf(mType.toUpperCase());
    }

    public String getLocationName()
    {
        return mLocationName;
    }

    public boolean isClaimedByMe()
    {
        return mClaimedByMe;
    }

    public boolean isProxy()
    {
        return getType() == BookingType.BOOKING_PROXY;
    }

    public Coordinates getMidpoint()
    {
        return mMidpoint;
    }

    public float getRadius()
    {
        return mRadius;
    }

    public Date getRevealDate()
    {
        return mRevealDate;
    }

    public String getZipClusterId() { return mZipClusterId; }

    //Basic booking statuses inferrable from mProviderId
    public enum BookingStatus
    {
        AVAILABLE,
        CLAIMED,
        UNAVAILABLE,
    }

    public enum ArrivalTimeOption //TODO: better system to enforce values in sync with server?
    {
        /* KEEP IN SYNC WITH SERVER VALUES */
        EARLY_30_MINUTES(R.string.arrival_time_early_30, "-30"),
        EARLY_15_MINUTES(R.string.arrival_time_early_15, "-15"),
        LATE_10_MINUTES(R.string.arrival_time_late_10, "10"),
        LATE_15_MINUTES(R.string.arrival_time_late_15, "15"),
        LATE_30_MINUTES(R.string.arrival_time_late_30, "30"),;

        private String mValue;
        private int mStringId;

        ArrivalTimeOption(int stringId, String value)
        {
            this.mStringId = stringId;
            this.mValue = value;
        }

        public static List<ArrivalTimeOption> lateValues()
        {
            return Lists.newArrayList(LATE_10_MINUTES, LATE_15_MINUTES, LATE_30_MINUTES);
        }

        public static List<ArrivalTimeOption> earlyValues()
        {
            return Lists.newArrayList(EARLY_15_MINUTES, EARLY_30_MINUTES);
        }

        public String getValue()
        {
            return mValue;
        }

        public int getStringId()
        {
            return mStringId;
        }
    }

    public String getFormattedLocation(BookingStatus bookingStatus)
    {
        if (this.isProxy())
        {
            return getLocationName();
        }
        else if (bookingStatus == BookingStatus.CLAIMED)
        {
            return getAddress().getStreetAddress() + "\n" + getAddress().getZip();
        }
        else
        {
            return getAddress().getShortRegion() + "\n" + getAddress().getZip();
        }
    }

    //TODO: I don't like having all this business logic in the client, we should get authoritative statuses from the server
    public BookingStatus inferBookingStatus(String userId)
    {
        if (this.isProxy())
        {
            return isClaimedByMe() ? BookingStatus.CLAIMED : BookingStatus.AVAILABLE;
        }

        String assignedProvider = getProviderId();
        boolean bookingIsStarted = isStarted();

        if (assignedProvider.equals(NO_PROVIDER_ASSIGNED))
        {
            //Can't claim bookings that have already started
            if (bookingIsStarted)
            {
                return BookingStatus.UNAVAILABLE;
            }
            else
            {
                return BookingStatus.AVAILABLE;
            }
        }
        else if (getProviderId().equals(userId))
        {
            return BookingStatus.CLAIMED;
        }
        else
        {
            return BookingStatus.UNAVAILABLE;
        }
    }

    public List<Action> getAllowedActions()
    {
        if (mActionList != null)
        {
            return mActionList;
        }
        else
        {
            return Collections.emptyList();
        }
    }

    public static class Action implements Serializable
    {
        // KEEP IN SYNC WITH SERVER VALUES
        public static final String ACTION_CLAIM = "claim";
        public static final String ACTION_REMOVE = "remove";
        public static final String ACTION_ON_MY_WAY = "on_my_way";
        public static final String ACTION_CHECK_IN = "check_in";
        public static final String ACTION_CHECK_OUT = "check_out";
        public static final String ACTION_CONTACT_PHONE = "contact_phone";
        public static final String ACTION_CONTACT_TEXT = "contact_text";

        public static final String ACTION_HELP = "need_help";

        public static final String ACTION_REPORT_NO_SHOW = "report_no_show";
        public static final String ACTION_NOTIFY_EARLY = "notify_early";
        public static final String ACTION_NOTIFY_LATE = "notify_late";
        public static final String ACTION_ISSUE_UNSAFE = "unsafe_conditions";
        public static final String ACTION_ISSUE_HOURS = "change_hours";
        public static final String ACTION_ISSUE_OTHER = "other_issue";

        public static final String ACTION_RETRACT_NO_SHOW = "retract_no_show";

        @SerializedName("action_name")
        private String mActionName;
        @SerializedName("helper_text")
        private String mHelperText; //Displayed in text field below button / button section
        @SerializedName("warning_text")
        private String mWarningText; //Indicates a popup should be shown with this message to confirm action
        @SerializedName("enabled")
        private boolean mEnabled;
        @SerializedName("deep_link_data")
        private String mDeepLinkData;

        public String getActionName()
        {
            return mActionName;
        }

        public String getHelperText()
        {
            return mHelperText;
        }

        public String getWarningText()
        {
            return mWarningText;
        }

        public boolean isEnabled()
        {
            return mEnabled;
        }

        public String getDeepLinkData()
        {
            return mDeepLinkData;
        }
    }

    public static class User implements Serializable
    {
        @SerializedName("email")
        private String mEmail;
        @SerializedName("first_name")
        private String mFirstName;
        @SerializedName("last_name")
        private String mLastName;

        public String getEmail()
        {
            return mEmail;
        }

        public String getFirstName()
        {
            return mFirstName;
        }

        public String getLastName()
        {
            return mLastName;
        }

        public String getAbbreviatedName()
        {
            return mFirstName + (mLastName.isEmpty() ? "" : " " + mLastName.charAt(0) + ".");
        }

        public String getFullName()
        {
            return mFirstName + " " + mLastName;
        }
    }

    public static class BookingInstruction implements Serializable
    {
        @SerializedName("description")
        private String mDescription;
        @SerializedName("machine_name")
        private String mMachineName;

        public String getDescription()
        {
            return mDescription;
        }

        public String getMachineName()
        {
            return mMachineName;
        }
    }

    public static class BookingInstructionGroup implements Serializable
    {
        public static String GROUP_ENTRY_METHOD = "entry_method";
        public static String GROUP_LINENS_LAUNDRY = "linens_laundry";
        public static String GROUP_REFRIGERATOR = "refrigerator";
        public static String GROUP_TRASH = "trash";
        public static String GROUP_NOTE_TO_PRO = "note_to_pro";
        public static String OTHER = "other";

        @SerializedName("group")
        private String mGroup;
        @SerializedName("label")
        private String mLabel;
        @SerializedName("items")
        private List<String> mItems;

        public String getGroup()
        {
            return mGroup;
        }

        public String getLabel()
        {
            return mLabel;
        }

        public List<String> getItems()
        {
            return mItems;
        }
    }

    public static class CheckInSummary implements Serializable
    {
        @SerializedName("is_checked_in")
        private boolean mIsCheckedIn; //false if checked out or on my way

        @SerializedName("time")
        private Date mCheckInTime;

        public Date getCheckInTime()
        {
            return mCheckInTime;
        }

        public boolean isCheckedIn()
        {
            return mIsCheckedIn;
        }
    }

    public static class ServiceInfo implements Serializable
    {
        private static final String MACHINE_NAME_CLEANING = "home_cleaning";

        @SerializedName("machine_name")
        private String mMachineName;
        @SerializedName("name")
        private String mDisplayName;

        public String getMachineName()
        {
            return mMachineName;
        }

        public String getDisplayName()
        {
            return mDisplayName;
        }

        public boolean isHomeCleaning()
        {
            return MACHINE_NAME_CLEANING.equalsIgnoreCase(mMachineName);
        }
    }

    public static class ExtraInfoWrapper implements Serializable
    {
        public ExtraInfo getExtraInfo()
        {
            return mExtraInfo;
        }

        @SerializedName("extra")
        private ExtraInfo mExtraInfo;
        @SerializedName("quantity")
        private int mQuantity;
    }

    public static class ExtraInfo implements Serializable
    {
        //cleaning supplies are in their own mCategory apart from all other extras
        public static final String TYPE_CLEANING_SUPPLIES = "cleaning_supplies";

        @SerializedName("category")
        private String mCategory;
        @SerializedName("fee")
        private String mFee;
        @SerializedName("hours")
        private String mHours;
        @SerializedName("id")
        private int mId;
        @SerializedName("machine_name")
        private String mMachineName;
        @SerializedName("name")
        private String mName;

        public String getCategory()
        {
            return mCategory;
        }

        public String getFee()
        {
            return mFee;
        }

        public String getHours()
        {
            return mHours;
        }

        public int getId()
        {
            return mId;
        }

        public String getMachineName()
        {
            return mMachineName;
        }

        public String getName()
        {
            return mName;
        }
    }

    public static class Coordinates implements Serializable
    {
        @SerializedName("latitude")
        private float mLatitude;
        @SerializedName("longitude")
        private float mLongitude;

        public float getLatitude()
        {
            return mLatitude;
        }

        public float getLongitude()
        {
            return mLongitude;
        }
    }

}

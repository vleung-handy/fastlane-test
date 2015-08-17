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
    @SerializedName("id")
    private String id;
    @SerializedName("service_name")
    private String service;
    @SerializedName("service")
    private ServiceInfo serviceInfo;
    @SerializedName("start_date")
    private Date startDate;
    @SerializedName("status")
    private String status;
    @SerializedName("end_date")
    private Date endDate;

    @SerializedName("check_in_summary")
    private CheckInSummary checkInSummary;
    @SerializedName("eta_lateness_minutes")
    private Integer providerMinutesLate;//value returned from server can be null

    @SerializedName("address")
    private Address address;

    @SerializedName("is_requested")
    private boolean isRequested;
    @SerializedName("payment_to_provider")
    private PaymentInfo paymentToProvider;
    @SerializedName("bonus")
    private PaymentInfo bonusPayment;
    @SerializedName("frequency")
    private int frequency;

    @SerializedName("provider_id")
    private String providerId;
    @SerializedName("partner")
    private String partner;
    @SerializedName("country")
    private String country;
    @SerializedName("user")
    private User user;
    @SerializedName("actions")
    private List<Action> actionList;
    @SerializedName("booking_phone")
    private String bookingPhone;

    @SerializedName("booking_instructions")
    private List<BookingInstruction> bookingInstructions;
    @SerializedName("booking_instruction_groups")
    private List<BookingInstructionGroup> bookingInstructionGroups;
    @SerializedName("booking_extras")
    private ArrayList<ExtraInfoWrapper> extrasInfo;
    @SerializedName("msg_to_pro")
    private String proNote;

    @SerializedName("distance")
    private String distance; // pre-formatted string used for relative searches (e.g. booking A is 5 miles away from booking B)

    public int compareTo(@NonNull Booking other)
    {
        if (getProviderId().equals(NO_PROVIDER_ASSIGNED))
        {
            if (this.getIsRequested() && !other.getIsRequested())
            {
                return -1;
            }
            if (!this.getIsRequested() && other.getIsRequested())
            {
                return 1;
            }
        }
        return startDate.compareTo(other.startDate);
    }

    public boolean equals(Object o)
    {
        if (!(o instanceof Booking))
        {
            return false;
        }
        Booking b = (Booking) o;
        return b.id.equals(id);
    }

    public String getStatus()
    {
        return status;
    }

    public List<BookingInstruction> getBookingInstructions()
    {
        return bookingInstructions;
    }

    public List<BookingInstructionGroup> getBookingInstructionGroups()
    {
        return bookingInstructionGroups;
    }

    public int getFrequency()
    {
        return frequency;
    }

    public String getPartner()
    {
        return partner;
    }

    public PaymentInfo getPaymentToProvider()
    {
        return paymentToProvider;
    }

    public PaymentInfo getBonusPaymentToProvider()
    {
        return bonusPayment;
    }

    public boolean getIsRequested()
    {
        return isRequested;
    }

    public String getId()
    {
        return id;
    }

    public void setId(final String id)
    {
        this.id = id;
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
        return frequency > 0;
    }

    public String getProNote()
    {
        return proNote;
    }

    public String getService()
    {
        return service;
    }

    public Date getStartDate()
    {
        return startDate;
    }

    public Date getEndDate()
    {
        return endDate;
    }

    public Address getAddress()
    {
        return address;
    }

    public String getProviderId()
    {
        return providerId;
    }

    public ArrayList<ExtraInfoWrapper> getExtrasInfo()
    {
        return extrasInfo;
    }

    public ServiceInfo getServiceInfo()
    {
        return serviceInfo;
    }

    public User getUser()
    {
        return user;
    }

    public String getBookingPhone()
    {
        return bookingPhone;
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
        return "GB".equalsIgnoreCase(country);
    }

    public CheckInSummary getCheckInSummary()
    {
        return checkInSummary;
    }

    public Integer getProviderMinutesLate()
    {
        return providerMinutesLate;
    }

    //providerId = 0, no one assigned can claim, otherwise is already claimed
    public static final String NO_PROVIDER_ASSIGNED = "0";

    public void setService(String service)
    {
        this.service = service;
    }

    public void setServiceInfo(ServiceInfo serviceInfo)
    {
        this.serviceInfo = serviceInfo;
    }

    public void setStartDate(Date startDate)
    {
        this.startDate = startDate;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public void setEndDate(Date endDate)
    {
        this.endDate = endDate;
    }

    public void setProviderId(String providerId)
    {
        this.providerId = providerId;
    }

    public void setPartner(String partner)
    {
        this.partner = partner;
    }

    public void setCountry(String country)
    {
        this.country = country;
    }

    public void setUser(User user)
    {
        this.user = user;
    }

    public String getDistance()
    {
        return distance;
    }

    public void setDistance(String distance)
    {
        this.distance = distance;
    }

    //Basic booking statuses inferrable from providerId
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

        private String value;
        private int stringId;

        ArrivalTimeOption(int stringId, String value)
        {
            this.stringId = stringId;
            this.value = value;
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
            return value;
        }

        public int getStringId()
        {
            return stringId;
        }
    }

    //TODO: I don't like having all this business logic in the client, we should get authoritative statuses from the server
    public BookingStatus inferBookingStatus(String userId)
    {
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
        if (actionList != null)
        {
            return actionList;
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
        private String actionName;
        @SerializedName("helper_text")
        private String helperText; //Displayed in text field below button / button section
        @SerializedName("warning_text")
        private String warningText; //Indicates a popup should be shown with this message to confirm action
        @SerializedName("enabled")
        private boolean enabled;
        @SerializedName("deep_link_data")
        private String deepLinkData;

        public String getActionName()
        {
            return actionName;
        }

        public String getHelperText()
        {
            return helperText;
        }

        public String getWarningText()
        {
            return warningText;
        }

        public boolean isEnabled()
        {
            return enabled;
        }

        public String getDeepLinkData()
        {
            return deepLinkData;
        }

        public void setActionName(String actionName)
        {
            this.actionName = actionName;
        }

        public void setHelperText(String helperText)
        {
            this.helperText = helperText;
        }

        public void setWarningText(String warningText)
        {
            this.warningText = warningText;
        }

        public void setEnabled(boolean enabled)
        {
            this.enabled = enabled;
        }

        public void setDeepLinkData(String deepLinkData)
        {
            this.deepLinkData = deepLinkData;
        }
    }

    public static class User implements Serializable
    {
        @SerializedName("email")
        private String email;
        @SerializedName("first_name")
        private String firstName;
        @SerializedName("last_name")
        private String lastName;

        public String getEmail()
        {
            return email;
        }

        public String getFirstName()
        {
            return firstName;
        }

        public String getLastName()
        {
            return lastName;
        }

        public String getAbbreviatedName()
        {
            return firstName + (lastName.isEmpty() ? "" : " " + lastName.charAt(0) + ".");
        }

        public String getFullName()
        {
            return firstName + " " + lastName;
        }

        public void setEmail(String email)
        {
            this.email = email;
        }

        public void setFirstName(String firstName)
        {
            this.firstName = firstName;
        }

        public void setLastName(String lastName)
        {
            this.lastName = lastName;
        }
    }

    public static class PaymentInfo implements Serializable
    {
        @SerializedName("amount")
        private int amount;
        @SerializedName("adjusted_amount")
        private int adjustedAmount;
        @SerializedName("code")
        private String currencyCode;
        @SerializedName("symbol")
        private String currencySymbol;
        @SerializedName("suffix")
        private String currencySuffix;

        public int getAmount()
        {
            return amount;
        }

        public int getAdjustedAmount()
        {
            return adjustedAmount;
        }

        public String getCurrencySymbol()
        {
            return currencySymbol;
        }

        public String getCurrencySuffix()
        {
            return currencySuffix;
        }

        public void setAmount(int amount)
        {
            this.amount = amount;
        }

        public void setAdjustedAmount(int adjustedAmount)
        {
            this.adjustedAmount = adjustedAmount;
        }

        public String getCurrencyCode()
        {
            return currencyCode;
        }

        public void setCurrencyCode(String currencyCode)
        {
            this.currencyCode = currencyCode;
        }

        public void setCurrencySymbol(String currencySymbol)
        {
            this.currencySymbol = currencySymbol;
        }

        public void setCurrencySuffix(String currencySuffix)
        {
            this.currencySuffix = currencySuffix;
        }
    }

    public static class BookingInstruction implements Serializable
    {
        @SerializedName("description")
        private String description;
        @SerializedName("machine_name")
        private String machineName;

        public String getDescription()
        {
            return description;
        }

        public String getMachineName()
        {
            return machineName;
        }

        public void setDescription(String description)
        {
            this.description = description;
        }

        public void setMachineName(String machineName)
        {
            this.machineName = machineName;
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
        private String group;
        @SerializedName("label")
        private String label;
        @SerializedName("items")
        private List<String> items;

        public String getGroup()
        {
            return group;
        }

        public String getLabel()
        {
            return label;
        }

        public List<String> getItems()
        {
            return items;
        }

        public void setGroup(String group)
        {
            this.group = group;
        }

        public void setLabel(String label)
        {
            this.label = label;
        }

        public void setItems(List<String> items)
        {
            this.items = items;
        }
    }

    public static class CheckInSummary implements Serializable
    {
        @SerializedName("is_checked_in")
        private boolean isCheckedIn; //false if checked out or on my way

        @SerializedName("time")
        private Date checkInTime;

        public Date getCheckInTime()
        {
            return checkInTime;
        }

        public boolean isCheckedIn()
        {
            return isCheckedIn;
        }

        public void setIsCheckedIn(boolean isCheckedIn)
        {
            this.isCheckedIn = isCheckedIn;
        }

        public void setCheckInTime(Date checkInTime)
        {
            this.checkInTime = checkInTime;
        }
    }

    public static class ServiceInfo implements Serializable
    {
        private static final String MACHINE_NAME_CLEANING = "home_cleaning";

        @SerializedName("machine_name")
        private String machineName;
        @SerializedName("name")
        private String displayName;

        public String getMachineName()
        {
            return machineName;
        }

        public String getDisplayName()
        {
            return displayName;
        }

        public boolean isHomeCleaning()
        {
            return MACHINE_NAME_CLEANING.equalsIgnoreCase(machineName);
        }

        public void setMachineName(String machineName)
        {
            this.machineName = machineName;
        }

        public void setDisplayName(String displayName)
        {
            this.displayName = displayName;
        }
    }

    public static class Address implements Serializable
    {
        @SerializedName("address1")
        private String address1;
        @SerializedName("address2")
        private String address2;
        @SerializedName("city")
        private String city;
        @SerializedName("state")
        private String state;
        @SerializedName("country")
        private String country;
        @SerializedName("zipcode")
        private String zip;
        @SerializedName("latitude")
        private float latitude;
        @SerializedName("longitude")
        private float longitude;
        @SerializedName("short_region")
        private String shortRegion;
        @SerializedName("region_id")
        private int regionId;

        public float getLatitude()
        {
            return latitude;
        }

        public float getLongitude()
        {
            return longitude;
        }

        public String getShortRegion()
        {
            return shortRegion;
        }

        public String getAddress1()
        {
            return address1;
        }

        public String getAddress2()
        {
            return address2;
        }

        public String getCity()
        {
            return city;
        }

        public String getState()
        {
            return state;
        }

        public String getZip()
        {
            return zip;
        }

        public String getStreetAddress()
        {
            return (getAddress1() + (getAddress2() != null ? " " + getAddress2() : ""));
        }

        public void setAddress1(String address1)
        {
            this.address1 = address1;
        }

        public void setAddress2(String address2)
        {
            this.address2 = address2;
        }

        public void setCity(String city)
        {
            this.city = city;
        }

        public void setState(String state)
        {
            this.state = state;
        }

        public String getCountry()
        {
            return country;
        }

        public void setCountry(String country)
        {
            this.country = country;
        }

        public void setZip(String zip)
        {
            this.zip = zip;
        }

        public void setLatitude(float latitude)
        {
            this.latitude = latitude;
        }

        public void setLongitude(float longitude)
        {
            this.longitude = longitude;
        }

        public void setShortRegion(String shortRegion)
        {
            this.shortRegion = shortRegion;
        }

        public int getRegionId()
        {
            return regionId;
        }

        public void setRegionId(int regionId)
        {
            this.regionId = regionId;
        }
    }

    public static class ExtraInfoWrapper implements Serializable
    {
        public ExtraInfo getExtraInfo()
        {
            return extraInfo;
        }

        @SerializedName("extra")
        private ExtraInfo extraInfo;
        @SerializedName("quantity")
        private int quantity;

        public void setExtraInfo(ExtraInfo extraInfo)
        {
            this.extraInfo = extraInfo;
        }

        public void setQuantity(int quantity)
        {
            this.quantity = quantity;
        }
    }

    public static class ExtraInfo implements Serializable
    {
        //cleaning supplies are in their own category apart from all other extras
        public static final String TYPE_CLEANING_SUPPLIES = "cleaning_supplies";

        @SerializedName("category")
        private String category;
        @SerializedName("fee")
        private String fee;
        @SerializedName("hours")
        private String hours;
        @SerializedName("id")
        private int id;
        @SerializedName("machine_name")
        private String machineName;
        @SerializedName("name")
        private String name;

        public String getCategory()
        {
            return category;
        }

        public String getFee()
        {
            return fee;
        }

        public String getHours()
        {
            return hours;
        }

        public int getId()
        {
            return id;
        }

        public String getMachineName()
        {
            return machineName;
        }

        public String getName()
        {
            return name;
        }

        public void setCategory(String category)
        {
            this.category = category;
        }

        public void setFee(String fee)
        {
            this.fee = fee;
        }

        public void setHours(String hours)
        {
            this.hours = hours;
        }

        public void setId(int id)
        {
            this.id = id;
        }

        public void setMachineName(String machineName)
        {
            this.machineName = machineName;
        }

        public void setName(String name)
        {
            this.name = name;
        }
    }
}

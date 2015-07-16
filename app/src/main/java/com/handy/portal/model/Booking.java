package com.handy.portal.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import com.handy.portal.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class Booking implements Parcelable, Comparable<Booking>
{
    @SerializedName("id") private String id;
    @SerializedName("service_name") private String service;
    @SerializedName("service") private ServiceInfo serviceInfo;
    @SerializedName("start_date") private Date startDate;
    @SerializedName("status") private String status;
    @SerializedName("end_date") private Date endDate;
    @SerializedName("hours") private float hours;
    @SerializedName("price") private float price;

    @SerializedName("laundry_status") private LaundryStatus laundryStatus;
    @SerializedName("address") private Address address;
    @SerializedName("billed_status") private String billedStatus;
    @SerializedName("payment_hash") private ArrayList<LineItem> paymentInfo;

    @SerializedName("is_requested") private boolean isRequested;
    @SerializedName("payment_to_provider") private PaymentInfo paymentToProvider;
    @SerializedName("bonus") private PaymentInfo bonusPayment;
    @SerializedName("frequency") private int frequency;


    @SerializedName("provider_id") private String providerId;
    @SerializedName("partner") private String partner;
    @SerializedName("country") private String country;
    @SerializedName("user") private User user;
    @SerializedName("actions") private List<Action> actionList;
    @SerializedName("booking_phone") private String bookingPhone;

    @SerializedName("booking_instructions") private List<BookingInstruction> bookingInstructions; //Customer Details
    @SerializedName("booking_instruction_groups") private List<BookingInstructionGroup> bookingInstructionGroups; //Customer Details
    @SerializedName("booking_extras") private ArrayList<ExtraInfoWrapper> extrasInfo; //Extras
    @SerializedName("description") private String description; //Customer Request
    @SerializedName("msg_to_pro") private String proNote;       //Customer Request


    public int compareTo(Booking other)
    {
        if (this.getIsRequested() && !other.getIsRequested())
        {
            return -1;
        }
        if (!this.getIsRequested() && other.getIsRequested())
        {
            return 1;
        }
        return startDate.compareTo(other.startDate);
    }

    public boolean equals(Object o)
    {
        if (!(o instanceof Booking))
            return false;
        Booking b = (Booking) o;
        return b.id.equals(id);
    }

    public String getStatus(){return status;}
    public List<BookingInstruction> getBookingInstructions() { return bookingInstructions;}
    public List<BookingInstructionGroup> getBookingInstructionGroups() { return bookingInstructionGroups;}

    public int getFrequency() { return frequency; }

    public String getPartner() { return partner; }

    public PaymentInfo getPaymentToProvider() { return paymentToProvider; }
    public PaymentInfo getBonusPaymentToProvider() { return bonusPayment; }

    public String getDescription() { return description; }

    public boolean getIsRequested() { return isRequested;}

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public boolean isStarted()
    {
        boolean isStarted = false;
        Date currentTime = Calendar.getInstance().getTime();
        if(getStartDate().compareTo(currentTime) < 0)
        {
            isStarted = true;
        }
        return isStarted;
    }

    public boolean isEnded()
    {
        boolean isEnded = false;
        Date currentTime = Calendar.getInstance().getTime();
        if(getEndDate().compareTo(currentTime) < 0)
        {
            isEnded = true;
        }
        return isEnded;
    }

    public boolean isRecurring() {
        return frequency > 0;
    }

    public String getProNote() {
        return proNote;
    }

    public String getService() {
        return service;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public float getPrice() {
        return price;
    }

    public Address getAddress() {
        return address;
    }

    public String getProviderId() {
        return providerId;
    }

    public LaundryStatus getLaundryStatus() {
        return laundryStatus;
    }

    public String getBilledStatus() {
        return billedStatus;
    }

    public ArrayList<LineItem> getPaymentInfo() {
        return paymentInfo;
    }

    public ArrayList<ExtraInfoWrapper> getExtrasInfo() {
        return extrasInfo;
    }

    public User getUser()
    {
        return user;
    }

    public String getBookingPhone() {return bookingPhone;}

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

    //providerId = 0, no one assigned can claim, otherwise is already claimed
    public static final String NO_PROVIDER_ASSIGNED = "0";

    //Basic booking statuses inferrable from providerId
    public enum BookingStatus
    {
        AVAILABLE,
        CLAIMED,
        UNAVAILABLE,
    }

    public enum ArrivalTimeOption
    {
//** KEEP IN SYNC WITH SERVER VALUES **//
        EARLY_30_MINUTES(R.string.arrival_time_early_30, "-30"),
        EARLY_15_MINUTES(R.string.arrival_time_early_15, "-15"),
        LATE_10_MINUTES(R.string.arrival_time_late_10, "10"),
        LATE_15_MINUTES(R.string.arrival_time_late_15, "15"),
        LATE_30_MINUTES(R.string.arrival_time_late_30, "30"),
        ;
//** KEEP IN SYNC WITH SERVER VALUES **//

        private String value;
        private int stringId;

        ArrivalTimeOption(int stringId, String value)
        {
            this.stringId = stringId;
            this.value = value;
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

        if(assignedProvider.equals(NO_PROVIDER_ASSIGNED))
        {
            //Can't claim bookings that have already started
            if(bookingIsStarted)
            {
                return BookingStatus.UNAVAILABLE;
            }
            else
            {
                return BookingStatus.AVAILABLE;
            }
        }
        else if(getProviderId().equals(userId))
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

    public static class Action
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
        private String deepLink;

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
        public String getDeepLink()
        {
            return deepLink;
        }
    }

    public BookingStatus inferBookingStatus()
    {
        return inferBookingStatus("-1notavalidid");
    }

    private Booking(final Parcel in) {
        final String[] stringData = new String[8];
        in.readStringArray(stringData);
        id = stringData[0];
        service = stringData[1];

        try { laundryStatus = LaundryStatus.valueOf(stringData[2]); }
        catch (IllegalArgumentException x) { laundryStatus = null; }

        proNote = stringData[3];
        billedStatus = stringData[4];

        //final int[] intData = new int[1];
        //in.readIntArray(intData);


        final float[] floatData = new float[2];
        in.readFloatArray(floatData);
        hours = floatData[0];
        price = floatData[1];

        startDate = new Date(in.readLong());
        address = in.readParcelable(Address.class.getClassLoader());

        paymentInfo = new ArrayList<LineItem>();
        in.readTypedList(paymentInfo, LineItem.CREATOR);

//        extrasInfo = new ArrayList<ExtraInfo>();
//        in.readTypedList(extrasInfo, ExtraInfo.CREATOR);
    }

    public static Booking fromJson(final String json) {
        return new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create()
                .fromJson(json, Booking.class);
    }

    @Override
    public void writeToParcel(final Parcel out, final int flags) {
        out.writeStringArray(new String[]{id, service, laundryStatus != null
                ? laundryStatus.name() : "", proNote,
                billedStatus});

        out.writeIntArray(new int[]{});
        out.writeFloatArray(new float[]{hours, price});
        out.writeLong(startDate.getTime());
        out.writeParcelable(address, 0);
        out.writeTypedList(paymentInfo);
        //out.writeTypedList(extrasInfo);
    }

    @Override
    public int describeContents(){
        return 0;
    }

    public static final Creator CREATOR = new Creator() {
        public Booking createFromParcel(final Parcel in) {
            return new Booking(in);
        }
        public Booking[] newArray(final int size) {
            return new Booking[size];
        }
    };

    public static class User
    {
        @SerializedName("email")
        private String email;
        @SerializedName("first_name")
        private String firstName;
        @SerializedName("last_name")
        private String lastName;
        //TODO: We are currently receiving the real phone number which we don't want to expose, we should make sure we are getting twillo or nothing
        //@SerializedName("phone_str")
        //private String phoneNumberString;

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

        /*public String getPhoneNumberString()
        {
            return phoneNumberString;
        }*/

        public String getAbbreviatedName()
        {
            return firstName + (lastName.isEmpty() ? "" : " " + lastName.charAt(0) +".");
        }

        public String getFullName()
        {
            return firstName + " " +  lastName;
        }

    }

    public static class PaymentInfo
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

        public int getAmount() { return amount; }
        public int getAdjustedAmount() { return adjustedAmount; }
        public String getCurrencySymbol() { return currencySymbol; }
        public String getCurrencySuffix() { return currencySuffix; }
    }

    public static class BookingInstruction
    {
        @SerializedName("description")
        private String description;
        @SerializedName("machine_name")
        private String machineName;

        public String getDescription() { return description; }
        public String getMachineName() { return machineName; }

        //filter out based on machine name

    }

    public static class BookingInstructionGroup
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
    }

    public static class ServiceInfo implements Parcelable {
        @SerializedName("id")
        private String id;
        @SerializedName("machine_name")
        private String machineName;
        @SerializedName("name")
        private String displayName;

        @Override
        public void writeToParcel(final Parcel out, final int flags) {
            out.writeStringArray(new String[]{ id, machineName, displayName});
        }

        @Override
        public int describeContents(){
            return 0;
        }
    }

    public static class Address implements Parcelable {
        @SerializedName("address1") private String address1;
        @SerializedName("address2") private String address2;
        @SerializedName("city") private String city;
        @SerializedName("state") private String state;
        @SerializedName("country") private String country;
        @SerializedName("zipcode") private String zip;
        @SerializedName("latitude") private float latitude;
        @SerializedName("longitude") private float longitude;
        @SerializedName("short_region") private String shortRegion;
        @SerializedName("region_id") private int regionId;

        public float getLatitude() { return latitude;}
        public float getLongitude() { return longitude;}

        public String getShortRegion() { return shortRegion; }

        public String getAddress1() {
            return address1;
        }

        public String getAddress2() {
            return address2;
        }

        public String getCity() {
            return city;
        }

        public String getState() {
            return state;
        }

        public String getZip() {
            return zip;
        }

        public String getStreetAddress()
        {
            return (getAddress1() + (getAddress2() != null ? " " + getAddress2() : ""));
        }

        private Address(final Parcel in) {
            final String[] stringData = new String[5];
            in.readStringArray(stringData);
            address1 = stringData[0];
            address2 = stringData[1];
            city = stringData[2];
            state = stringData[3];
            zip = stringData[4];
        }

        @Override
        public void writeToParcel(final Parcel out, final int flags) {
            out.writeStringArray(new String[]{ address1, address2, city, state, zip });
        }

        @Override
        public int describeContents(){
            return 0;
        }

        public static final Creator CREATOR = new Creator() {
            public Address createFromParcel(final Parcel in) {
                return new Address(in);
            }
            public Address[] newArray(final int size) {
                return new Address[size];
            }
        };
    }

    public static class Provider implements Parcelable {
        @SerializedName("status") private int status;
        @SerializedName("first_name") private String firstName;
        @SerializedName("last_name") private String lastName;
        @SerializedName("phone") private String phone;

        public int getStatus() {
            return status;
        }

        final void setStatus(final int status) {
            this.status = status;
        }

        public String getFirstName() {
            return firstName;
        }

        final void setFirstName(final String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        final void setLastName(final String lastName) {
            this.lastName = lastName;
        }

        public String getPhone() {
            return phone;
        }

        final void setPhone(final String phone) {
            this.phone = phone;
        }

        private Provider(final Parcel in) {
            final int[] intData = new int[1];
            in.readIntArray(intData);
            status = intData[0];

            final String[] stringData = new String[3];
            in.readStringArray(stringData);
            firstName = stringData[0];
            lastName = stringData[1];
            phone = stringData[2];
        }

        @Override
        public void writeToParcel(final Parcel out, final int flags) {
            out.writeIntArray(new int[]{ status });
            out.writeStringArray(new String[]{ firstName, lastName, phone });
        }

        @Override
        public int describeContents(){
            return 0;
        }

        public static final Creator CREATOR = new Creator() {
            public Provider createFromParcel(final Parcel in) {
                return new Provider(in);
            }
            public Provider[] newArray(final int size) {
                return new Provider[size];
            }
        };
    }

    public static class LineItem implements Parcelable {
        @SerializedName("order") private int order;
        @SerializedName("label") private String label;
        @SerializedName("amount") private String amount;

        public int getOrder() {
            return order;
        }

        public String getLabel() {
            return label;
        }

        public String getAmount() {
            return amount;
        }

        private LineItem(final Parcel in) {
            final int[] intData = new int[1];
            in.readIntArray(intData);
            order = intData[0];

            final String[] stringData = new String[2];
            in.readStringArray(stringData);
            label = stringData[0];
            amount = stringData[1];
        }

        @Override
        public void writeToParcel(final Parcel out, final int flags) {
            out.writeIntArray(new int[]{order});
            out.writeStringArray(new String[]{label, amount});
        }

        @Override
        public int describeContents(){
            return 0;
        }

        public static final Creator CREATOR = new Creator() {
            public LineItem createFromParcel(final Parcel in) {
                return new LineItem(in);
            }
            public LineItem[] newArray(final int size) {
                return new LineItem[size];
            }
        };
    }

    public static class ExtraInfoWrapper
    {
        public ExtraInfo getExtraInfo()
        {
            return extraInfo;
        }

        @SerializedName("extra") private ExtraInfo extraInfo;
        @SerializedName("quantity") private int quantity;


    }

    public static class ExtraInfo
    {
        @SerializedName("category") private String category;
        @SerializedName("fee") private String fee;
        @SerializedName("hrs") private String hrs;
        @SerializedName("id") private int id;
        @SerializedName("machine_name") private String machineName;
        @SerializedName("name") private String name;

        public String getCategory()
        {
            return category;
        }

        public String getFee()
        {
            return fee;
        }

        public String getHrs()
        {
            return hrs;
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

        //cleaning supplies are in their own category apart from all other extras
        public static final String TYPE_CLEANING_SUPPLIES = "cleaning_supplies";
    }

    public enum LaundryStatus {
        @SerializedName("ready_for_pickup") READY_FOR_PICKUP,
        @SerializedName("in_progress") IN_PROGRESS,
        @SerializedName("out_for_delivery") OUT_FOR_DELIVERY,
        @SerializedName("delivered") DELIVERED,
        @SerializedName("skipped") SKIPPED
    }
}

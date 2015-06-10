package com.handy.portal.core.booking;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public final class Booking implements Parcelable, Comparable<Booking>
{
    @SerializedName("id") private String id;
    @SerializedName("booking_status") private int isPast;
    @SerializedName("service_name") private String service;
    @SerializedName("service") private ServiceInfo serviceInfo;
    @SerializedName("start_date") private Date startDate;
    @SerializedName("status") private String status;
    @SerializedName("end_date") private Date endDate;
    @SerializedName("hours") private float hours;
    @SerializedName("price") private float price;
    @SerializedName("msg_to_pro") private String proNote;
    @SerializedName("laundry_status") private LaundryStatus laundryStatus;
    @SerializedName("address") private Address address;
    @SerializedName("billed_status") private String billedStatus;
    @SerializedName("payment_hash") private ArrayList<LineItem> paymentInfo;
    @SerializedName("booking_extras") private ArrayList<ExtraInfoWrapper> extrasInfo;
    @SerializedName("is_requested") private boolean isRequested;
    @SerializedName("payment_to_provider") private PaymentInfo paymentToProvider;
    @SerializedName("bonus") private PaymentInfo bonusPayment;
    @SerializedName("frequency") private int frequency;
    @SerializedName("booking_instructions") private List<BookingInstruction> bookingInstructions;
    @SerializedName("description") private String description;
    @SerializedName("provider_id") private String providerId;
    @SerializedName("partner") private String partner;

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

    public final String getStatus(){return status;}
    public final List<BookingInstruction> getBookingInstructions() { return bookingInstructions;}

    public final int getFrequency() { return frequency; }

    public final String getPartner() { return partner; }

    public final PaymentInfo getPaymentToProvider() { return paymentToProvider; }
    public final PaymentInfo getBonusPaymentToProvider() { return bonusPayment; }

    public final String getDescription() { return description; }

    public final boolean getIsRequested() { return isRequested;}

    public final String getId() {
        return id;
    }

    public final void setId(final String id) {
        this.id = id;
    }

    public final boolean isPast() {
        return isPast == 1;
    }

    final void setIsPast(final boolean isPast) {
        if (isPast) this.isPast = 1;
        else this.isPast = 0;
    }

    public final boolean isRecurring() {
        return frequency > 0;
    }

    public final String getProNote() {
        return proNote;
    }

    public final String getService() {
        return service;
    }

    final void setService(final String service) {
        this.service = service;
    }

    public final Date getStartDate() {
        return startDate;
    }

    public final Date getEndDate() {
        return endDate;
    }

    public final void setStartDate(final Date startDate) {
        this.startDate = startDate;
    }

    public final float getHours() {
        return hours;
    }

    final void setHours(float hours) {
        this.hours = hours;
    }

    public final float getPrice() {
        return price;
    }

    final void setPrice(float price) {
        this.price = price;
    }

    public final Address getAddress() {
        return address;
    }

    final void setAddress(final Address address) {
        this.address = address;
    }

    public final String getProviderId() {
        return providerId;
    }

    public final LaundryStatus getLaundryStatus() {
        return laundryStatus;
    }

    public final String getBilledStatus() {
        return billedStatus;
    }

    public final ArrayList<LineItem> getPaymentInfo() {
        return paymentInfo;
    }

    public final ArrayList<ExtraInfoWrapper> getExtrasInfo() {
        return extrasInfo;
    }

    public final List<ExtraInfoWrapper> getExtrasInfoByMachineName(final String machineName)
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


    private Booking(final Parcel in) {
        final String[] stringData = new String[8];
        in.readStringArray(stringData);
        id = stringData[0];
        service = stringData[1];

        try { laundryStatus = LaundryStatus.valueOf(stringData[2]); }
        catch (IllegalArgumentException x) { laundryStatus = null; }

        proNote = stringData[3];
        billedStatus = stringData[4];

        final int[] intData = new int[1];
        in.readIntArray(intData);
        isPast = intData[0];

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
    public final void writeToParcel(final Parcel out, final int flags) {
        out.writeStringArray(new String[]{id, service, laundryStatus != null
                ? laundryStatus.name() : "", proNote,
                billedStatus});

        out.writeIntArray(new int[]{isPast});
        out.writeFloatArray(new float[]{hours, price});
        out.writeLong(startDate.getTime());
        out.writeParcelable(address, 0);
        out.writeTypedList(paymentInfo);
        //out.writeTypedList(extrasInfo);
    }

    @Override
    public final int describeContents(){
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

    public static final class PaymentInfo
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

    public static final class BookingInstruction
    {
        @SerializedName("description")
        private String description;
        @SerializedName("machine_name")
        private String machineName;

        public String getDescription() { return description; }
        public String getMachineName() { return machineName; }

    }

    public static final class ServiceInfo implements Parcelable {
        @SerializedName("id")
        private String id;
        @SerializedName("machine_name")
        private String machineName;
        @SerializedName("name")
        private String displayName;

        @Override
        public final void writeToParcel(final Parcel out, final int flags) {
            out.writeStringArray(new String[]{ id, machineName, displayName});
        }

        @Override
        public final int describeContents(){
            return 0;
        }
    }

    public static final class Address implements Parcelable {
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

        public final float getLatitude() { return latitude;}
        public final float getLongitude() { return longitude;}

        public final String getShortRegion() { return shortRegion; }

        public final String getAddress1() {
            return address1;
        }

        final void setAddress1(final String address1) {
            this.address1 = address1;
        }

        public final String getAddress2() {
            return address2;
        }

        final void setAddress2(final String address2) {
            this.address2 = address2;
        }

        public final String getCity() {
            return city;
        }

        final void setCity(final String city) {
            this.city = city;
        }

        public final String getState() {
            return state;
        }

        final void setState(final String state) {
            this.state = state;
        }

        public final String getZip() {
            return zip;
        }

        final void setZip(final String zip) {
            this.zip = zip;
        }

        private static final int [] UK_REGION_IDS = {36, 39, 40, 41, 42, 43};

        public boolean isUKRegion()
        {
            for(int i : UK_REGION_IDS)
            {
                if(i == this.regionId)
                {
                    return true;
                }
            }
            return false;
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
        public final void writeToParcel(final Parcel out, final int flags) {
            out.writeStringArray(new String[]{ address1, address2, city, state, zip });
        }

        @Override
        public final int describeContents(){
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

    public static final class Provider implements Parcelable {
        @SerializedName("status") private int status;
        @SerializedName("first_name") private String firstName;
        @SerializedName("last_name") private String lastName;
        @SerializedName("phone") private String phone;

        public final int getStatus() {
            return status;
        }

        final void setStatus(final int status) {
            this.status = status;
        }

        public final String getFirstName() {
            return firstName;
        }

        final void setFirstName(final String firstName) {
            this.firstName = firstName;
        }

        public final String getLastName() {
            return lastName;
        }

        final void setLastName(final String lastName) {
            this.lastName = lastName;
        }

        public final String getPhone() {
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
        public final void writeToParcel(final Parcel out, final int flags) {
            out.writeIntArray(new int[]{ status });
            out.writeStringArray(new String[]{ firstName, lastName, phone });
        }

        @Override
        public final int describeContents(){
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

    public static final class LineItem implements Parcelable {
        @SerializedName("order") private int order;
        @SerializedName("label") private String label;
        @SerializedName("amount") private String amount;

        public final int getOrder() {
            return order;
        }

        public final String getLabel() {
            return label;
        }

        public final String getAmount() {
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
        public final void writeToParcel(final Parcel out, final int flags) {
            out.writeIntArray(new int[]{order});
            out.writeStringArray(new String[]{label, amount});
        }

        @Override
        public final int describeContents(){
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

    public static final class ExtraInfoWrapper
    {
        public ExtraInfo getExtraInfo()
        {
            return extraInfo;
        }

        @SerializedName("extra") private ExtraInfo extraInfo;
        @SerializedName("quantity") private int quantity;


    }

    public static final class ExtraInfo
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

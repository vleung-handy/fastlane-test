package com.handy.portal.core;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public final class LaundryDropInfo implements Parcelable {
    @SerializedName("type") private String type;
    @SerializedName("title") private String title;
    @SerializedName("subtitle") private String subtitle;
    @SerializedName("max_date") private Date maxDate;
    @SerializedName("min_date") private Date minDate;
    @SerializedName("times") private DropTimes dropTimes;

    private ArrayList<Date> dates;

    public final String getType() {
        return type;
    }

    public final String getTitle() {
        return title;
    }

    public final String getSubtitle() {
        return subtitle;
    }

    public final List<Date> getDates() {
        if (dates == null) {
            dates = new ArrayList<>();

            final Calendar start = Calendar.getInstance();
            start.setTime(minDate);

            final Calendar end = Calendar.getInstance();
            end.setTime(maxDate);

            while(!start.after(end)){
                final List<DropTime> times = getDropTimes(start.getTime());
                if (times != null && times.size() > 0) {
                    dates.add(start.getTime());
                }
                start.add(Calendar.DATE, 1);
            }
        }
        return dates;
    }

    public final List<DropTime> getDropTimes(final Date date) {
        final Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        final int day = cal.get(Calendar.DAY_OF_WEEK);

        switch (day) {
            case Calendar.MONDAY:
                return dropTimes.getMonday();

            case Calendar.TUESDAY:
                return dropTimes.getTuesday();

            case Calendar.WEDNESDAY:
                return dropTimes.getWednesday();

            case Calendar.THURSDAY:
                return dropTimes.getThursday();

            case Calendar.FRIDAY:
                return dropTimes.getFriday();

            case Calendar.SATURDAY:
                return dropTimes.getSaturday();

            case Calendar.SUNDAY:
                return dropTimes.getSunday();
        }
        return null;
    }

    private LaundryDropInfo(final Parcel in) {
        final String[] stringData = new String[3];
        in.readStringArray(stringData);
        type = stringData[0];
        title = stringData[1];
        subtitle = stringData[2];

        maxDate = new Date(in.readLong());
        minDate = new Date(in.readLong());

        dropTimes = in.readParcelable(DropTimes.class.getClassLoader());
    }

    @Override
    public final void writeToParcel(final Parcel out, final int flags) {
        out.writeStringArray(new String[]{type, title, subtitle});
        out.writeLong(maxDate.getTime());
        out.writeLong(minDate.getTime());
        out.writeParcelable(dropTimes, 0);
    }

    @Override
    public final int describeContents(){
        return 0;
    }

    public static final Creator CREATOR = new Creator() {
        public LaundryDropInfo createFromParcel(final Parcel in) {
            return new LaundryDropInfo(in);
        }
        public LaundryDropInfo[] newArray(final int size) {
            return new LaundryDropInfo[size];
        }
    };

    public static LaundryDropInfo fromJson(final String json) {
        return new GsonBuilder().setDateFormat("MM/dd/yyyy").create()
                .fromJson(json, LaundryDropInfo.class);
    }

    public static final class DropTimes implements Parcelable {
        @SerializedName("monday") private ArrayList<DropTime> monday;
        @SerializedName("tuesday") private ArrayList<DropTime> tuesday;
        @SerializedName("wednesday") private ArrayList<DropTime> wednesday;
        @SerializedName("thursday") private ArrayList<DropTime> thursday;
        @SerializedName("friday") private ArrayList<DropTime> friday;
        @SerializedName("saturday") private ArrayList<DropTime> saturday;
        @SerializedName("sunday") private ArrayList<DropTime> sunday;

        public final ArrayList<DropTime> getMonday() {
            return monday;
        }

        public final ArrayList<DropTime> getTuesday() {
            return tuesday;
        }

        public final ArrayList<DropTime> getWednesday() {
            return wednesday;
        }

        public final ArrayList<DropTime> getThursday() {
            return thursday;
        }

        public final ArrayList<DropTime> getFriday() {
            return friday;
        }

        public final ArrayList<DropTime> getSaturday() {
            return saturday;
        }

        public final ArrayList<DropTime> getSunday() {
            return sunday;
        }

        private DropTimes(final Parcel in) {
            monday = new ArrayList<DropTime>();
            tuesday = new ArrayList<DropTime>();
            wednesday = new ArrayList<DropTime>();
            thursday = new ArrayList<DropTime>();
            friday = new ArrayList<DropTime>();
            saturday = new ArrayList<DropTime>();
            sunday = new ArrayList<DropTime>();

            in.readTypedList(monday, DropTime.CREATOR);
            in.readTypedList(tuesday, DropTime.CREATOR);
            in.readTypedList(wednesday, DropTime.CREATOR);
            in.readTypedList(thursday, DropTime.CREATOR);
            in.readTypedList(friday, DropTime.CREATOR);
            in.readTypedList(saturday, DropTime.CREATOR);
            in.readTypedList(sunday, DropTime.CREATOR);
        }

        @Override
        public final void writeToParcel(final Parcel out, final int flags) {
            out.writeTypedList(monday);
            out.writeTypedList(tuesday);
            out.writeTypedList(wednesday);
            out.writeTypedList(thursday);
            out.writeTypedList(friday);
            out.writeTypedList(saturday);
            out.writeTypedList(sunday);
        }

        @Override
        public final int describeContents(){
            return 0;
        }

        public static final Creator CREATOR = new Creator() {
            public DropTimes createFromParcel(final Parcel in) {
                return new DropTimes(in);
            }
            public DropTimes[] newArray(final int size) {
                return new DropTimes[size];
            }
        };
    }

    public static final class DropTime implements Parcelable {
        @SerializedName("hour") private int hour;
        @SerializedName("minute") private int minute;
        @SerializedName("display_string") private String displayTime;

        public final int getHour() {
            return hour;
        }

        public final int getMinute() {
            return minute;
        }

        public final String getDisplayTime() {
            return displayTime;
        }

        private DropTime(final Parcel in) {
            final int[] intData = new int[2];
            in.readIntArray(intData);
            hour = intData[0];
            minute = intData[1];

            final String[] stringData = new String[1];
            in.readStringArray(stringData);
            displayTime = stringData[0];
        }

        @Override
        public final void writeToParcel(final Parcel out, final int flags) {
            out.writeIntArray(new int[]{hour, minute});
            out.writeStringArray(new String[]{displayTime});
        }

        @Override
        public final int describeContents(){
            return 0;
        }

        public static final Creator CREATOR = new Creator() {
            public DropTime createFromParcel(final Parcel in) {
                return new DropTime(in);
            }
            public DropTime[] newArray(final int size) {
                return new DropTime[size];
            }
        };
    }
}

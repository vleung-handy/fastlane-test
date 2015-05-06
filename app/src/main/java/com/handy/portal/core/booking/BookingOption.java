package com.handy.portal.core.booking;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public final class BookingOption implements Parcelable {
    @SerializedName("uniq") private String uniq;
    @SerializedName("type") private String type;
    @SerializedName("title") private String title;
    @SerializedName("info") private String info;
    @SerializedName("default_value") private String defaultValue;
    @SerializedName("options") private String[] options;
    @SerializedName("options_sub_text") private String[] optionsSubText;
    @SerializedName("options_left_text") private String[] optionsRightText;
    @SerializedName("options_images") private String[][] optionsImages;
    @SerializedName("hour_info") private float[] hoursInfo;
    @SerializedName("warnings") private String[][] warnings;
    @SerializedName("child_elements") private String[][] children;
    @SerializedName("page") private int page;
    @SerializedName("post") private int post;

    public BookingOption() {}

    public final String getUniq() {
        return uniq;
    }

    final void setUniq(final String uniq) {
        this.uniq = uniq;
    }

    public final String getType() {
        return type;
    }

    public final void setType(final String type) {
        this.type = type;
    }

    public final String getTitle() {
        return title;
    }

    final void setTitle(final String title) {
        this.title = title;
    }

    public final String getInfo() {
        return info;
    }

    final void setInfo(final String info) {
        this.info = info;
    }

    public final String getDefaultValue() {
        return defaultValue;
    }

    public final void setDefaultValue(final String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public final String[] getOptions() {
        return options;
    }

    public final void setOptions(final String[] options) {
        this.options = options;
    }

    public final String[] getOptionsSubText() {
        return optionsSubText;
    }

    public final void setOptionsSubText(final String[] optionsSubText) {
        this.optionsSubText = optionsSubText;
    }

    public final String[] getOptionsRightText() {
        return optionsRightText;
    }

    public final void setOptionsRightText(final String[] optionsRightText) {
        this.optionsRightText = optionsRightText;
    }

    final String[][] getOptionsImages() {
        return optionsImages;
    }

    final void setOptionsImages(final String[][] optionsImages) {
        this.optionsImages = optionsImages;
    }

    public final float[] getHoursInfo() {
        return hoursInfo;
    }

    final void setHoursInfo(final float[] hoursInfo) {
        this.hoursInfo = hoursInfo;
    }

    public final String[][] getWarnings() {
        return warnings;
    }

    final void setWarnings(final String[][] warnings) {
        this.warnings = warnings;
    }

    public final String[][] getChildren() {
        return children;
    }

    final void setChildren(final String[][] children) {
        this.children = children;
    }

    public final int getPage() {
        return page;
    }

    final void setPage(final int page) {
        this.page = page;
    }

    public final boolean isPost() {
        return post == 1;
    }

    final void setPost(final boolean post) {
        this.post = post ? 1 : 0;
    }

    private BookingOption(final Parcel in) {
        final String[] stringData = new String[5];
        in.readStringArray(stringData);
        uniq = stringData[0];
        type = stringData[1];
        title = stringData[2];
        defaultValue = stringData[3];
        info = stringData[4];

        final int[] intData = new int[2];
        in.readIntArray(intData);
        page = intData[0];
        post = intData[1];

        options = in.createStringArray();
        optionsSubText = in.createStringArray();
        optionsRightText = in.createStringArray();
        optionsImages = (String[][]) in.readSerializable();
        hoursInfo = in.createFloatArray();
        warnings = (String[][]) in.readSerializable();
        children = (String[][]) in.readSerializable();
    }

    @Override
    public final void writeToParcel(final Parcel out, final int flags) {
        out.writeStringArray(new String[]{ uniq, type, title, defaultValue, info });
        out.writeIntArray(new int[]{ page, post });
        out.writeStringArray(options);
        out.writeStringArray(optionsSubText);
        out.writeStringArray(optionsRightText);
        out.writeSerializable(optionsImages);
        out.writeFloatArray(hoursInfo);
        out.writeSerializable(warnings);
        out.writeSerializable(children);
    }

    @Override
    public final int describeContents(){
        return 0;
    }

    public static final Creator CREATOR = new Creator() {
        public BookingOption createFromParcel(final Parcel in) {
            return new BookingOption(in);
        }
        public BookingOption[] newArray(final int size) {
            return new BookingOption[size];
        }
    };
}

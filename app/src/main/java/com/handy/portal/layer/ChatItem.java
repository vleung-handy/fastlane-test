package com.handy.portal.layer;

import android.support.annotation.ColorInt;

public class ChatItem {

    private ChatMessage mMessage;
    private Type mType;
    private int mGravity;

    public ChatItem(final ChatMessage message, final Type type) {
        mMessage = message;
        mType = type;
    }

    @ColorInt
    private int mTextColor;

    @ColorInt
    private int mBgColor;

    public int getGravity() {
        return mGravity;
    }

    public void setGravity(final int gravity) {
        mGravity = gravity;
    }

    public int getTextColor() {
        return mTextColor;
    }

    public void setTextColor(final int textColor) {
        mTextColor = textColor;
    }

    public int getBgColor() {
        return mBgColor;
    }

    public void setBgColor(final int bgColor) {
        mBgColor = bgColor;
    }

    public ChatMessage getMessage() {
        return mMessage;
    }

    public void setMessage(final ChatMessage message) {
        mMessage = message;
    }

    public Type getType() {
        return mType;
    }

    public void setType(final Type type) {
        mType = type;
    }

    public enum Type {
        TITLE, MESSAGE, DATE_DIVIDER
    }
}
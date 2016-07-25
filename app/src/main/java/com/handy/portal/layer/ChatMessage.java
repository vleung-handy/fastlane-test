package com.handy.portal.layer;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ChatMessage implements Serializable
{

    private String mMessage;
    private String mSenderId;
    private Date mDate;
    private boolean isRead;
    private DateFormat mFormatter = new SimpleDateFormat("hh:mm a");

    public ChatMessage(final String message, final String senderId, final Date date, final boolean isRead)
    {
        mMessage = message;
        mSenderId = senderId;
        mDate = date;
        this.isRead = isRead;
    }

    public ChatMessage()
    {
    }

    public String getMessage()
    {
        return mMessage;
    }

    public void setMessage(final String message)
    {
        mMessage = message;
    }

    public String getSenderId()
    {
        return mSenderId;
    }

    public void setSenderId(final String senderId)
    {
        mSenderId = senderId;
    }

    public Date getDate()
    {
        return mDate;
    }

    public void setDate(final Date date)
    {
        mDate = date;
    }

    public boolean isRead()
    {
        return isRead;
    }

    public void setRead(final boolean read)
    {
        isRead = read;
    }

    public String getStatus()
    {
        if (mDate == null)
        {
            return "status unknown";
        }
        else
        {
            return mFormatter.format(mDate) + " - read";
        }
    }
}

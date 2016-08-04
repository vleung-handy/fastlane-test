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
    private boolean mRead;
    private DateFormat mFormatter = new SimpleDateFormat("hh:mm a");
    private String mId;

    public ChatMessage(final String messageId, final String message, final String senderId, final Date date, final boolean isRead)
    {
        mId = messageId;
        mMessage = message;
        mSenderId = senderId;
        mDate = date;
        mRead = isRead;
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
        return mRead;
    }

    public void setRead(final boolean read)
    {
        mRead = read;
    }

    public String getId()
    {
        return mId;
    }

    public void setId(final String id)
    {
        mId = id;
    }

    public String getStatus()
    {
        if (mDate == null)
        {
            return "status unknown";
        }
        else
        {
            return mFormatter.format(mDate) + " - sent";
        }
    }
}

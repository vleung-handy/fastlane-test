package com.handy.portal.ui.constructor;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.handy.portal.model.Booking;

public abstract class BookingDetailsViewConstructor extends ViewConstructor<Booking>
{
    private final Bundle arguments;

    public BookingDetailsViewConstructor(@NonNull Context context, Bundle arguments)
    {
        super(context);
        this.arguments = arguments;
    }

    public Bundle getArguments()
    {
        return arguments;
    }
}

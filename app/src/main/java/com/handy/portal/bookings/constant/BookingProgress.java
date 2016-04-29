package com.handy.portal.bookings.constant;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public interface BookingProgress
{
    int UNAVAILABLE = 1;
    int READY_FOR_CLAIM = 2;
    int READY_FOR_ON_MY_WAY = 3;
    int READY_FOR_CHECK_IN = 4;
    int READY_FOR_CHECK_OUT = 5;
    int FINISHED = 6;


    //Define the list of accepted constants
    @IntDef({UNAVAILABLE, READY_FOR_CLAIM, READY_FOR_ON_MY_WAY, READY_FOR_CHECK_IN, READY_FOR_CHECK_OUT, FINISHED})
    //Tell the compiler not to store annotation data in the .class file
    @Retention(RetentionPolicy.SOURCE)
    @interface Progress {}
}

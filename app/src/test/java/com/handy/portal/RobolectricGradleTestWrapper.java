package com.handy.portal;

import android.os.Build;

import com.squareup.otto.Bus;

import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

@Ignore
@RunWith(RobolectricGradleTestRunner.class)
@Config(sdk = Build.VERSION_CODES.JELLY_BEAN,
        constants = BuildConfig.class,
        packageName = "com.handy.portal")
public class RobolectricGradleTestWrapper
{
    protected  <T> T getBusCaptorValue(ArgumentCaptor<?> captor, Class<T> classType)
    {
        for (Object o : captor.getAllValues())
        {
            if (classType.isInstance(o))
            {
                return classType.cast(o);
            }
        }
        return null;
    }

    public static <T> T getFirstMatchingBusEvent(Bus bus, Class klass)
    {
        ArgumentCaptor<T> captor = ArgumentCaptor.forClass(klass);
        verify(bus, atLeastOnce()).post(captor.capture());
        for (Object event : captor.getAllValues())
        {
            if (klass.isInstance(event))
            {
                return (T) event;
            }
        }
        return null;
    }
}

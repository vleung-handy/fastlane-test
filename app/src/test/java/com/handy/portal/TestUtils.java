package com.handy.portal;

import com.squareup.otto.Bus;

import org.mockito.ArgumentCaptor;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

public class TestUtils
{
    public static <T> T getBusCaptorValue(ArgumentCaptor<?> captor, Class<T> classType)
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

package com.handy.portal;

import com.squareup.otto.Bus;

import org.mockito.ArgumentCaptor;

import java.util.List;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

public class TestUtils
{
    public static <T> T getBusCaptorValue(ArgumentCaptor<?> captor, Class<T> classType)
    {
        List<?> values = captor.getAllValues();
        // using a index for loop to search from latest to earliest
        for (int i = values.size() - 1; i >= 0; --i)
        {
            if (classType.isInstance(values.get(i)))
            {
                return classType.cast(values.get(i));
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

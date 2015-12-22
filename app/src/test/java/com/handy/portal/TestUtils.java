package com.handy.portal;

import org.mockito.ArgumentCaptor;

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
}

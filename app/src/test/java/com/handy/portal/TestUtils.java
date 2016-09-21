package com.handy.portal;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import org.greenrobot.eventbus.EventBus;
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

    public static <T> T getFirstMatchingBusEvent(EventBus bus, Class<T> klass)
    {
        ArgumentCaptor<T> captor = ArgumentCaptor.forClass(klass);
        verify(bus, atLeastOnce()).post(captor.capture());
        for (T event : captor.getAllValues())
        {
            if (klass.isInstance(event))
            {
                return event;
            }
        }
        return null;
    }

    public static Fragment getScreenFragment(FragmentManager fragmentManager)
    {
        List<Fragment> fragments = fragmentManager.getFragments();
        for (int i = fragments.size() - 1; i >= 0; --i)
        {
            if (fragments.get(i) != null)
            { return fragments.get(i); }
        }
        return null;
    }
}

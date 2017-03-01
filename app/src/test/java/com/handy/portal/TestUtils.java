package com.handy.portal;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import org.greenrobot.eventbus.EventBus;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

public class TestUtils {
    public static <T> T getBusCaptorValue(ArgumentCaptor<?> captor, Class<T> classType) {
        List<?> values = captor.getAllValues();
        // using a index for loop to search from latest to earliest
        for (int i = values.size() - 1; i >= 0; --i) {
            if (classType.isInstance(values.get(i))) {
                return classType.cast(values.get(i));
            }
        }
        return null;
    }

    public static <T> T getFirstMatchingBusEvent(EventBus bus, Class<T> klass) {
        ArgumentCaptor<T> captor = ArgumentCaptor.forClass(klass);
        verify(bus, atLeastOnce()).post(captor.capture());
        for (T event : captor.getAllValues()) {
            if (klass.isInstance(event)) {
                return event;
            }
        }
        return null;
    }

    public static Fragment getScreenFragment(FragmentManager fragmentManager) {
        List<Fragment> fragments = fragmentManager.getFragments();
        for (int i = fragments.size() - 1; i >= 0; --i) {
            if (fragments.get(i) != null) { return fragments.get(i); }
        }
        return null;
    }

    public static void testFragmentNavigation(final Fragment fragment, final int viewId, final Class<?> fragmentClass, final int stringId) {
        fragment.getView().findViewById(viewId).performClick();
        Fragment currentFragment = getScreenFragment(fragment.getFragmentManager());
        assertThat(currentFragment, instanceOf(fragmentClass));
        assertEquals(fragment.getString(stringId),
                ((AppCompatActivity) fragment.getActivity()).getSupportActionBar().getTitle());
    }

    public static void testFragmentNavigation(final AppCompatActivity activity, final int viewId, final Class<?> fragmentClass, final int stringId) {
        activity.findViewById(viewId).performClick();
        Fragment currentFragment = getScreenFragment(activity.getSupportFragmentManager());
        assertThat(currentFragment, instanceOf(fragmentClass));
        assertEquals(activity.getString(stringId), activity.getSupportActionBar().getTitle());
    }
}

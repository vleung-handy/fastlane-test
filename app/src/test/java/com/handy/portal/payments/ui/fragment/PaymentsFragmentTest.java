package com.handy.portal.payments.ui.fragment;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import com.handy.portal.R;
import com.handy.portal.RobolectricGradleTestWrapper;
import com.handy.portal.core.TestBaseApplication;
import com.handy.portal.ui.activity.MainActivity;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.robolectric.shadows.ShadowApplication;
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class PaymentsFragmentTest extends RobolectricGradleTestWrapper
{
    private PaymentsFragment mFragment;

    @Before
    @Override
    public void setUp() throws Exception
    {
        super.setUp();
        ((TestBaseApplication) ShadowApplication.getInstance().getApplicationContext()).inject(this);
        mFragment = new PaymentsFragment();
        SupportFragmentTestUtil.startFragment(mFragment, MainActivity.class);
    }

    @Test
    public void shouldHaveCorrectTitleOnActionBar() throws Exception
    {
        ActionBar actionBar = ((AppCompatActivity) mFragment.getActivity()).getSupportActionBar();
        assertNotNull(actionBar);
        assertEquals(mFragment.getString(R.string.payments), actionBar.getTitle());
    }

    @Test
    @Ignore
    public void shouldRedirectToHelpCenterWhenHelpIconIsClicked() throws Exception
    {
        //TODO: Implement this
    }

}

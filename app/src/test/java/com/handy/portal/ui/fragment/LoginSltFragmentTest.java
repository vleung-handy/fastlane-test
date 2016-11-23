package com.handy.portal.ui.fragment;

import com.handy.portal.R;
import com.handy.portal.RobolectricGradleTestWrapper;
import com.handy.portal.ui.activity.LoginActivity;

import org.junit.Before;
import org.junit.Test;
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil;

import static junit.framework.Assert.assertEquals;

public class LoginSltFragmentTest extends RobolectricGradleTestWrapper
{
    private LoginSltFragment mFragment;

    @Before
    public void setUp()
    {
        mFragment = new LoginSltFragment();
        SupportFragmentTestUtil.startFragment(mFragment, LoginActivity.class);
    }

    @Test
    public void testRequestSltSuccess()
    {
        String phone = "3479999999";
        mFragment.mPhoneNumberEditText.setText(phone);
        mFragment.mLoginButton.performClick();

        assertEquals(mFragment.getString(R.string.login_instructions_slt2, phone), mFragment.mInstructionsText.getText());
        assertEquals(mFragment.getString(R.string.request_slt_again), mFragment.mLoginButton.getText());
    }

}

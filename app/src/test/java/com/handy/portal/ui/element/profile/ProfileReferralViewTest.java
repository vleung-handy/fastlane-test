package com.handy.portal.ui.element.profile;

import android.app.Application;
import android.widget.TextView;

import com.handy.portal.BuildConfig;
import com.handy.portal.R;
import com.handy.portal.model.ReferralInfo;
import com.handy.portal.ui.constructor.ProfileReferralView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class,
        packageName = "com.handy.portal",
        sdk = 19)
public class ProfileReferralViewTest
{
    private static final Application APP = RuntimeEnvironment.application;
    private ProfileReferralView mProfileReferralView;
    private ReferralInfo mReferralInfo;
    private String mReferralCode;

    @Before
    public void setUp() throws Exception
    {
        mReferralCode = "Ref Code";
        mProfileReferralView = new ProfileReferralView(APP, buildReferralInfo());
    }

    @Test
    public void shouldShowReferralCode()
    {
        TextView referralCodeText = (TextView) mProfileReferralView.findViewById(R.id.referral_code_text);
        assertEquals(mReferralInfo.getReferralCode(), referralCodeText.getText());
    }

    private ReferralInfo buildReferralInfo()
    {
        mReferralInfo = mock(ReferralInfo.class);
        when(mReferralInfo.getReferralCode()).thenReturn(mReferralCode);

        return mReferralInfo;
    }
}
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
    private ProfileReferralView prView;
    private ReferralInfo referralInfo;
    private String referralCode;

    @Before
    public void setUp() throws Exception
    {
        referralCode = "Ref Code";
        prView = new ProfileReferralView(APP, buildReferralInfo());
    }

    @Test
    public void shouldShowReferralCode()
    {
        TextView referralCodeText = (TextView) prView.findViewById(R.id.referral_code_text);
        assertEquals(referralInfo.getReferralCode(), referralCodeText.getText());
    }

    private ReferralInfo buildReferralInfo()
    {
        referralInfo = mock(ReferralInfo.class);
        when(referralInfo.getReferralCode()).thenReturn(referralCode);

        return referralInfo;
    }
}
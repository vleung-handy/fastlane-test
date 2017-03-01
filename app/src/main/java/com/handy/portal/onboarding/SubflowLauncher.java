package com.handy.portal.onboarding;

import com.handy.portal.onboarding.model.subflow.SubflowType;

public interface SubflowLauncher {
    void launchSubflow(final SubflowType subflowType);
}

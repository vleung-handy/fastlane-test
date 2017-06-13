package com.handy.portal.core;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

public interface MainContentFragmentHolder {
    void replaceMainContentFragment(@NonNull Fragment replacementFragment, boolean addToBackStack);
}

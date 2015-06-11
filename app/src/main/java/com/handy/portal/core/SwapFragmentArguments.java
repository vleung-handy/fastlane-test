package com.handy.portal.core;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.handy.portal.consts.TransitionStyle;

/**
 * Created by cdavis on 6/2/15.
 */
public class SwapFragmentArguments
{
    public Class targetClassType;
    public Fragment overrideFragment;
    public Bundle argumentsBundle;
    public boolean addToBackStack;
    public TransitionStyle transitionStyle;
}

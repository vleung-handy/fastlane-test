package com.handy.portal.model;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.handy.portal.constant.TransitionStyle;

public class SwapFragmentArguments
{
    public Class targetClassType;
    public Fragment overrideFragment;
    public Bundle argumentsBundle;
    public TransitionStyle transitionStyle;
    public boolean addToBackStack;
    public boolean clearBackStack;
    public boolean popBackStack;
}

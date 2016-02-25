package com.handy.portal.ui.fragment;


import com.handy.portal.R;
import com.handy.portal.constant.MainViewTab;

public class RatingsAndFeedbackFragment extends ActionBarFragment
{

    @Override
    protected MainViewTab getTab()
    {
        return MainViewTab.RATINGS_AND_FEEDBACK;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        setActionBar(R.string.ratings_and_feedback, false);
    }
}

package com.handy.portal.ui.activity;

import android.os.Bundle;

import com.handy.portal.R;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.model.TermsDetails;
import com.handy.portal.model.TermsDetailsGroup;
import com.handy.portal.ui.fragment.TermsFragment;

import java.util.Iterator;

public class TermsActivity extends BaseActivity
{
    private Iterator<TermsDetails> mTermsIterator;
    private int mStepIdentifier;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);
        final TermsDetailsGroup termsGroup =
                (TermsDetailsGroup) getIntent().getSerializableExtra(BundleKeys.TERMS_GROUP);
        mTermsIterator = termsGroup.getTermsDetails().iterator();
        mStepIdentifier = getIntent().getIntExtra(BundleKeys.FLOW_STEP_ID, -1);
        proceed();
    }

    public void proceed()
    {
        if (mTermsIterator.hasNext())
        {
            final TermsDetails termsDetails = mTermsIterator.next();
            displayTerms(termsDetails);
        }
        else
        {
            bus.post(new HandyEvent.StepCompleted(mStepIdentifier));
            finish();
        }
    }

    private void displayTerms(final TermsDetails termsDetails)
    {
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(
                        R.anim.slide_in_right,
                        R.anim.slide_out_left,
                        R.anim.slide_in_left,
                        R.anim.slide_out_right
                )
                .replace(R.id.main_content, TermsFragment.newInstance(termsDetails))
                .commit();
    }

    @Override
    public void onBackPressed()
    {
        moveTaskToBack(true);
    }
}

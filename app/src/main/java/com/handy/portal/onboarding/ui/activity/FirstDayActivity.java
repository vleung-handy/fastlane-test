package com.handy.portal.onboarding.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.SpannableString;
import android.widget.ImageView;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.util.MyLeadingMarginSpan2;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Simple, hard-coded activity to show content of suggestions on what to do on the first day
 * of work.
 */
public class FirstDayActivity extends AppCompatActivity
{

    @BindView(R.id.first_day_first_job_message)
    TextView mFirstJobMessage;

    @BindView(R.id.prepare_message)
    TextView mPreparedMessage;

    @BindView(R.id.done_message)
    TextView mDoneMessage;


    @BindView(R.id.image_vacuum)
    ImageView mVacuum;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_day);
        ButterKnife.bind(this);

        mFirstJobMessage.setText(Html.fromHtml(getString(R.string.first_job_message_styled)));

        int leftMargin = ContextCompat.getDrawable(this, R.drawable.img_vacuum).getIntrinsicWidth() + 2;
        SpannableString ss = new SpannableString(getString(R.string.be_prepared_message));
        ss.setSpan(new MyLeadingMarginSpan2(3, leftMargin), 0, ss.length(), 0);

        mPreparedMessage.setText(ss);

        int rightMargin = ContextCompat.getDrawable(this, R.drawable.img_vacuum).getIntrinsicWidth();
        SpannableString ss2 = new SpannableString(getString(R.string.done_message));
        ss.setSpan(new MyLeadingMarginSpan2(3, rightMargin), 0, ss2.length(), 0);

    }

    @Override
    protected void attachBaseContext(Context newBase)
    {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}

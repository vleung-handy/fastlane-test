package com.handy.portal.ui.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Button;

import com.handy.portal.R;
import com.handy.portal.model.onboarding.Job;
import com.handy.portal.model.onboarding.JobGroup;
import com.handy.portal.ui.adapter.JobsRecyclerAdapter;
import com.handy.portal.ui.view.HandyJobGroupView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class GettingStartedActivity extends AppCompatActivity implements HandyJobGroupView.OnJobChangeListener
{

    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;

    @Bind(R.id.btn_next)
    Button mBtnNext;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    JobsRecyclerAdapter mAdapter;

    List<JobGroup> mJobs;

    String mNoThanks;

    Drawable mGreenDrawable;
    Drawable mGrayDrawable;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_getting_started);

        ButterKnife.bind(this);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        mNoThanks = getString(R.string.onboard_no_thanks);

        createJobs();
        mAdapter = new JobsRecyclerAdapter(mJobs, getString(R.string.onboard_getting_started_title), this);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        mGreenDrawable = ContextCompat.getDrawable(this, R.drawable.button_green);
        mGrayDrawable = ContextCompat.getDrawable(this, R.drawable.button_gray);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(getString(R.string.onboard_getting_started));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_x_white);
        updateButton();
    }

    //    TODO: JIA: delete this method that generates fake data
    private void createJobs()
    {
        ArrayList<JobGroup> jobs = new ArrayList<>();

        JobGroup group1 = new JobGroup("2016-04-19");
        group1.jobs.add(new Job("Upper East Manhattan", "1:00pm - 4:00pm", 45, true));
        group1.jobs.add(new Job("Upper East Manhattan", "1:00pm - 4:00pm", 45, true));

        JobGroup group2 = new JobGroup("2016-04-20");
        group2.jobs.add(new Job("Upper West Manhattan", "8:00am - 11:00am", 45, true));

        JobGroup group3 = new JobGroup("2016-04-21");
        group3.jobs.add(new Job("Midtown East, Murray Hill, Gramercy", "8:00am - 11:00am", 45, true));
        group3.jobs.add(new Job("Midtown East, Murray Hill, Gramercy", "1:00pm - 4:00pm", 45, true));

        jobs.add(group1);
        jobs.add(group2);
        jobs.add(group3);
        onJobLoaded(jobs);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void onJobLoaded(List<JobGroup> jobs)
    {
        mJobs = jobs;
        if (mJobs == null || mJobs.isEmpty())
        {
            //TODO: JIA: when there are no jobs, finish this activity and redirect somewhere else
        }
    }

    @Override
    public void onPriceChanged()
    {
        updateButton();
    }

    public void updateButton()
    {
        //one of the jobs changed price, re-calculate
        float sum = 0;
        String symbol = "";
        for (JobGroup jg : mJobs)
        {
            if (jg != null)
            {
                for (Job job : jg.jobs)
                {
                    if (job.selected)
                    {
                        sum += job.amount;
                    }
                }
            }
        }

        if (sum > 0)
        {
            symbol = mJobs.get(1).jobs.get(0).symbol;
            String formattedPrice = symbol + String.format("%.2f", sum);
            String text = String.format(getString(R.string.onboard_claim_and_earn_formatted), formattedPrice);
            mBtnNext.setText(text);
            mBtnNext.setBackground(mGreenDrawable);
        }
        else
        {
            mBtnNext.setText(mNoThanks);
            mBtnNext.setBackground(mGrayDrawable);
        }
    }

}

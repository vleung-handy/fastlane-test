package com.handy.portal.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.model.onboarding.JobGroup;
import com.handy.portal.ui.view.HandyJobGroupView;

import java.util.List;

/**
 * Created by jtse on 4/18/16.
 */
public class JobsRecyclerAdapter extends RecyclerView.Adapter<JobsRecyclerAdapter.RecyclerViewHolder>
{
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private final String TAG = JobsRecyclerAdapter.class.getName();

    List<JobGroup> mJobs;
    String mTitle;

    HandyJobGroupView.OnJobChangeListener mOnJobChangeListener;

    public JobsRecyclerAdapter(List<JobGroup> jobs, String title,
                               HandyJobGroupView.OnJobChangeListener mListener)
    {

        mOnJobChangeListener = mListener;
        mTitle = title;
        mJobs = jobs;

        //adding place holders for the header position
        mJobs.add(0, null);
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        Log.d(TAG, "onCreateViewHolder");

        View layoutView = null;
        switch (viewType)
        {
            case TYPE_HEADER:
                layoutView = LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.getting_started_header, parent, false);

                ((TextView) layoutView).setText(mTitle);

                break;
            case TYPE_ITEM:
                layoutView = new HandyJobGroupView(parent.getContext());
                ((HandyJobGroupView) layoutView).setOnJobChangeListener(mOnJobChangeListener);
                break;
        }

        RecyclerViewHolder rcv = new RecyclerViewHolder(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position)
    {
        if (holder.getItemViewType() == TYPE_ITEM)
        {
            Log.d(TAG, "onBindViewHolder:" + position);
            holder.mJobView.bind(mJobs.get(position));
        }
    }

    @Override
    public int getItemViewType(int position)
    {
        if (position == 0)
        {
            return TYPE_HEADER;
        }
        else
        {
            return TYPE_ITEM;
        }
    }

    @Override
    public int getItemCount()
    {
        return mJobs.size();
    }

    static class RecyclerViewHolder extends RecyclerView.ViewHolder
    {
        public HandyJobGroupView mJobView;

        public RecyclerViewHolder(View itemView)
        {
            super(itemView);
            if (itemView instanceof HandyJobGroupView)
            {
                mJobView = (HandyJobGroupView) itemView;
            }
        }
    }
}

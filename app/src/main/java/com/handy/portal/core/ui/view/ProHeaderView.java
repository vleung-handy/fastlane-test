package com.handy.portal.core.ui.view;


import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.core.model.ProviderProfile;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProHeaderView extends FrameLayout {
    @BindView(R.id.pro_header_image)
    CircleImageView mImage;
    @BindView(R.id.pro_header_name)
    TextView mNameText;
    @BindView(R.id.pro_header_job_rating_and_count)
    ViewGroup mJobRatingAandCountLayout;
    @BindView(R.id.pro_header_job_rating)
    TextView mJobRatingText;
    @BindView(R.id.pro_header_job_count)
    TextView mJobCountText;

    public ProHeaderView(final Context context) {
        super(context);
        init();
    }

    public ProHeaderView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ProHeaderView(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ProHeaderView(final Context context, final AttributeSet attrs, final int defStyleAttr, final int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_pro_header, this);
        ButterKnife.bind(this);
    }

    public void setDisplay(@Nullable ProviderProfile profile, @Nullable String imageUrl) {
        if (profile == null) { return; }

        if (profile.getProviderPersonalInfo() != null) {
            mNameText.setText(profile.getProviderPersonalInfo().getFullName());
        }

        if (profile.getPerformanceInfo() != null) {
            mJobRatingAandCountLayout.setVisibility(VISIBLE);
            mJobRatingText.setText(String.valueOf(profile.getPerformanceInfo().getTotalRating()));
            int jobs = profile.getPerformanceInfo().getTotalJobsCount();
            mJobCountText.setText(
                    getResources().getQuantityString(R.plurals.profile_number_jobs_formatted, jobs, jobs));
        }
        else {
            mJobRatingAandCountLayout.setVisibility(GONE);
        }

        Picasso.with(getContext())
                .load(imageUrl)
                .placeholder(R.drawable.img_pro_placeholder)
                .noFade()
                .into(mImage);
    }
}

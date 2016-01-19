package com.handy.portal.ui.element.profile;

import android.content.Context;
import android.text.Html;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.model.PerformanceInfo;
import com.handy.portal.model.ProviderPersonalInfo;
import com.handy.portal.model.ProviderProfile;
import com.handy.portal.util.DateTimeUtils;

import java.text.DecimalFormat;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ProfileHeaderView extends FrameLayout {

    private static final DecimalFormat RATING_FORMAT = new DecimalFormat("0.00");

    @Bind(R.id.provider_first_name_text)
    TextView providerFirstNameText;
    @Bind(R.id.provider_last_name_text)
    TextView providerLastNameText;
    @Bind(R.id.joined_handy_text)
    TextView joinedHandyText;
    @Bind(R.id.jobs_rating_text)
    TextView jobsRatingText;

    private ProviderProfile mProfileProfile;

    public ProfileHeaderView(Context context, ProviderProfile profile) {
        super(context);

        // Inflate view using ButterKnife
        inflate(getContext(), R.layout.element_profile_header, this);
        ButterKnife.bind(this);

        // Fetch provider info to populate into ProfileHeaderView
        ProviderPersonalInfo providerPersonalInfo = profile.getProviderPersonalInfo();
        PerformanceInfo performanceInfo = profile.getPerformanceInfo();
        String formattedTotalRating = RATING_FORMAT.format(performanceInfo.getTotalRating());

        // Set text of view elements
        providerFirstNameText.setText(providerPersonalInfo.getFirstName());
        providerLastNameText.setText(providerPersonalInfo.getLastName());
        jobsRatingText.setText(Html.fromHtml(getContext().getString(R.string.jobs_and_rating, performanceInfo.getTotalJobsCount(), formattedTotalRating)));

        // Bind activation date if applicable
        Date activationDate = providerPersonalInfo.getActivationDate();
        if (activationDate != null)
        {
            joinedHandyText.setText(Html.fromHtml(getContext().getString(R.string.joined_handy, DateTimeUtils.formatMonthDateYear(activationDate))));
        }
        else
        {
            joinedHandyText.setVisibility(View.GONE);
        }
    }
}

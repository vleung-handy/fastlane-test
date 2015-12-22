package com.handy.portal.ui.constructor;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.model.PerformanceInfo;
import com.handy.portal.model.ProviderPersonalInfo;
import com.handy.portal.model.ProviderProfile;
import com.handy.portal.util.DateTimeUtils;

import java.text.DecimalFormat;
import java.util.Date;

import butterknife.InjectView;

public class ProfileHeaderViewConstructor extends ViewConstructor<ProviderProfile>
{
    private static final DecimalFormat RATING_FORMAT = new DecimalFormat("0.00");

    @InjectView(R.id.provider_first_name_text)
    TextView providerFirstNameText;
    @InjectView(R.id.provider_last_name_text)
    TextView providerLastNameText;
    @InjectView(R.id.joined_handy_text)
    TextView joinedHandyText;
    @InjectView(R.id.jobs_rating_text)
    TextView jobsRatingText;

    public ProfileHeaderViewConstructor(@NonNull Context context)
    {
        super(context);
    }

    @Override
    protected int getLayoutResourceId()
    {
        return R.layout.element_profile_header;
    }

    @Override
    protected boolean constructView(ViewGroup container, ProviderProfile providerProfile)
    {
        ProviderPersonalInfo providerPersonalInfo = providerProfile.getProviderPersonalInfo();
        PerformanceInfo performanceInfo = providerProfile.getPerformanceInfo();

        providerFirstNameText.setText(providerPersonalInfo.getFirstName());
        providerLastNameText.setText(providerPersonalInfo.getLastName());

        String formattedTotalRating = RATING_FORMAT.format(performanceInfo.getTotalRating());
        jobsRatingText.setText(Html.fromHtml(getContext().getString(R.string.jobs_and_rating, performanceInfo.getTotalJobsCount(), formattedTotalRating)));

        Date activationDate = providerPersonalInfo.getActivationDate();
        if (activationDate != null)
        {
            joinedHandyText.setText(Html.fromHtml(getContext().getString(R.string.joined_handy, DateTimeUtils.formatMonthDateYear(activationDate))));
        }
        else
        {
            joinedHandyText.setVisibility(View.GONE);
        }

        return true;
    }
}

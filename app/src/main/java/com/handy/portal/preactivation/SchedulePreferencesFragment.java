package com.handy.portal.preactivation;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ListView;

import com.handy.portal.R;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.library.util.DateTimeUtils;
import com.handy.portal.model.onboarding.OnboardingSuppliesInfo;
import com.handy.portal.onboarding.ui.activity.ScheduleBuilderFragment;
import com.handy.portal.ui.adapter.CheckBoxListAdapter;
import com.handy.portal.library.ui.view.StaticFieldTableRow;
import com.handy.portal.ui.widget.TitleView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import butterknife.Bind;
import butterknife.OnClick;

public class SchedulePreferencesFragment extends PreActivationFlowFragment
{
    @Bind(R.id.date_field)
    StaticFieldTableRow mDateField;
    @Bind(R.id.location_field)
    StaticFieldTableRow mLocationField;

    private Date mSelectedStartDate;
    private ArrayList<Integer> mSelectedZipclusterIds;
    private CheckBoxListAdapter.CheckBoxListItem[] mLocationViewModels;
    private OnboardingSuppliesInfo mOnboardingSuppliesInfo;

    @OnClick(R.id.date_field)
    public void onDateFieldClicked()
    {
        final DatePickerDialog datePickerDialog =
                new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener()
                {
                    @Override
                    public void onDateSet(
                            final DatePicker view,
                            final int year,
                            final int monthOfYear,
                            final int dayOfMonth)
                    {
                        final Calendar c = Calendar.getInstance();
                        c.set(year, monthOfYear, dayOfMonth);
                        updateSelectedStartedDate(c.getTime());
                    }
                }, 2016, Calendar.JUNE, 17);

        // FIXME: Pull date range from server
        final DatePicker datePicker = datePickerDialog.getDatePicker();

        final Calendar c1 = Calendar.getInstance();
        c1.set(2016, Calendar.JUNE, 15);
        datePicker.setMinDate(c1.getTimeInMillis());

        final Calendar c2 = Calendar.getInstance();
        c2.set(2016, Calendar.JULY, 15);
        datePicker.setMaxDate(c2.getTimeInMillis());

        datePickerDialog.show();
    }

    @OnClick(R.id.location_field)
    public void onLocationFieldClicked()
    {
        final CheckBoxListAdapter adapter = new CheckBoxListAdapter(getActivity(),
                Arrays.copyOf(mLocationViewModels, mLocationViewModels.length));
        final ListView listView = new ListView(getActivity());
        listView.setDivider(null);
        listView.setAdapter(adapter);

        final AlertDialog.Builder dialogBuilder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            dialogBuilder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogStyle);
            final TitleView titleView = new TitleView(getActivity());
            titleView.setText(R.string.choose_locations);
            dialogBuilder.setCustomTitle(titleView);
        }
        else
        {
            dialogBuilder = new AlertDialog.Builder(getActivity());
            dialogBuilder.setTitle(R.string.choose_locations);
        }
        dialogBuilder.setPositiveButton(R.string.ok,
                new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(final DialogInterface dialog, final int which)
                    {
                        updateSelectedLocations(adapter.getItems());
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .setView(listView)
                .create()
                .show();
    }

    public static SchedulePreferencesFragment newInstance(
            final OnboardingSuppliesInfo onboardingSuppliesInfo)
    {
        final SchedulePreferencesFragment fragment = new SchedulePreferencesFragment();
        final Bundle arguments = new Bundle();
        arguments.putSerializable(BundleKeys.ONBOARDING_SUPPLIES, onboardingSuppliesInfo);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mSelectedZipclusterIds = new ArrayList<>();
        mOnboardingSuppliesInfo = (OnboardingSuppliesInfo) getArguments()
                .getSerializable(BundleKeys.ONBOARDING_SUPPLIES);

        // FIXME: Pull locations from server
        mLocationViewModels = new CheckBoxListAdapter.CheckBoxListItem[]{
                new CheckBoxListAdapter.CheckBoxListItem("Midtown", false),
                new CheckBoxListAdapter.CheckBoxListItem("Upper East Side", false),
                new CheckBoxListAdapter.CheckBoxListItem("Gramercy", false),
                new CheckBoxListAdapter.CheckBoxListItem("Flatiron", false),
                new CheckBoxListAdapter.CheckBoxListItem("Lower East Side", false),
                new CheckBoxListAdapter.CheckBoxListItem("Union Square", false),
                new CheckBoxListAdapter.CheckBoxListItem("Kips Bay", false),
                new CheckBoxListAdapter.CheckBoxListItem("Financial District", false),
                new CheckBoxListAdapter.CheckBoxListItem("Hell's Kitchen", false),
                new CheckBoxListAdapter.CheckBoxListItem("Yorkville", false),
                new CheckBoxListAdapter.CheckBoxListItem("Chinatown", false),
        };
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        mDateField.setLabel(R.string.start_date);
        mLocationField.setLabel(R.string.locations);
        displaySelectedStartDate();
        displaySelectedLocations();
    }

    @Override
    protected int getLayoutResId()
    {
        return R.layout.view_schedule_preferences;
    }

    @Override
    protected String getTitle()
    {
        // FIXME: Pull from server
        return "Claim Jobs";
    }

    @Nullable
    @Override
    protected String getHeaderText()
    {
        // FIXME: Pull from server
        return "Set your job preferences";
    }

    @Nullable
    @Override
    protected String getSubHeaderText()
    {
        // FIXME: Pull from server
        return "You can start as early as next week!";
    }

    @Override
    protected String getPrimaryButtonText()
    {
        return getString(R.string.continue_to_next_step);
    }

    @Override
    protected void onPrimaryButtonClicked()
    {
        if (!validate())
        {
            return;
        }

        next(ScheduleBuilderFragment.newInstance(mOnboardingSuppliesInfo, mSelectedStartDate,
                mSelectedZipclusterIds));
    }

    private boolean validate()
    {
        boolean allFieldsValid = true;
        if (mSelectedStartDate == null)
        {
            mDateField.setErrorState(true);
            allFieldsValid = false;
        }
        if (mSelectedZipclusterIds.isEmpty())
        {
            mLocationField.setErrorState(true);
            allFieldsValid = false;
        }
        return allFieldsValid;
    }

    public void updateSelectedStartedDate(final Date date)
    {
        mSelectedStartDate = date;
        displaySelectedStartDate();
    }

    private void displaySelectedStartDate()
    {
        if (mSelectedStartDate != null)
        {
            final String dateString = DateTimeUtils.DAY_OF_WEEK_MONTH_DATE_YEAR_FORMATTER
                    .format(mSelectedStartDate.getTime());
            mDateField.setValue(dateString);
        }
        else
        {
            mDateField.setValue(null).setHint(R.string.choose_start_date);
        }
    }

    public void updateSelectedLocations(final CheckBoxListAdapter.CheckBoxListItem[] items)
    {
        mSelectedZipclusterIds.clear();
        mLocationViewModels = items;
        for (CheckBoxListAdapter.CheckBoxListItem item : mLocationViewModels)
        {
            if (item.isChecked())
            {
                // FIXME: Actually set this properly
                mSelectedZipclusterIds.add(1);
            }
        }
        displaySelectedLocations();
    }

    private void displaySelectedLocations()
    {
        final int count = mSelectedZipclusterIds != null ? mSelectedZipclusterIds.size() : 0;
        if (count > 0)
        {
            mLocationField.setValue(getResources().getQuantityString(
                    R.plurals.locations_selected_count_formatted, count, count));
        }
        else
        {
            mLocationField.setValue(null).setHint(R.string.choose_locations);
        }
    }
}

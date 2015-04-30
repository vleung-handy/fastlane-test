package com.handy.portal.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.handy.portal.MainActivity;
import com.handy.portal.R;
import com.handy.portal.core.User;

import butterknife.ButterKnife;

public class SplashActivity extends BaseActivity {
    private static final String STATE_LAUNCHED_NEXT = "LAUNCHED_NEXT";

    private User user;
    private boolean launchedNext;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.inject(this);

        if (savedInstanceState != null)
        {
            launchedNext = savedInstanceState.getBoolean(STATE_LAUNCHED_NEXT, false);
        }

        if (!launchedNext)
        {
            user = userManager.getCurrentUser();

            final Intent intent = this.getIntent();
            final String action = intent.getAction();
            final Uri data = intent.getData();

//            if (!action.equals("android.intent.action.VIEW") || !data.getScheme().equals("handy")) {
//                openServiceCategoriesActivity();
//                return;
//            }

            //navigationManager.handleSplashScreenLaunch(this.getIntent(), this);
            launchedNext = true;
        }
        else
        {
            //openServiceCategoriesActivity();

            openMainActivity();
        }
    }

    @Override
    public void startActivity(final Intent intent) {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        super.startActivity(intent);

        launchedNext = true;
        finish();
    }

    @Override
    public void startActivityForResult(final Intent intent, final int resultCode) {
        super.startActivityForResult(intent, resultCode);
        launchedNext = true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == BookingDateActivity.RESULT_RESCHEDULE_NEW_DATE) {
//            openServiceCategoriesActivity();
//        }
    }

    @Override
    public final void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_LAUNCHED_NEXT, launchedNext);
    }

    private void openMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
    }

//    private void openServiceCategoriesActivity() {
//        //startActivity(new Intent(this, ServiceCategoriesActivity.class));
//    }

//    private void openRescheduleActivity(final String bookingId) {
//        dataManager.getBooking(bookingId, user != null ? user.getAuthToken() : null,
//                new DataManager.Callback<Booking>() {
//                    @Override
//                    public void onSuccess(final Booking booking) {
//                        if (!allowCallbacks) return;
//
//                        dataManager.getPreRescheduleInfo(bookingId, new DataManager.Callback<String>() {
//                            @Override
//                            public void onSuccess(final String notice) {
//                                if (!allowCallbacks) return;
//
//                                final Intent intent = new Intent(SplashActivity.this, BookingDateActivity.class);
//                                intent.putExtra(BookingDateActivity.EXTRA_RESCHEDULE_BOOKING, booking);
//                                intent.putExtra(BookingDateActivity.EXTRA_RESCHEDULE_NOTICE, notice);
//                                startActivityForResult(intent, BookingDateActivity.RESULT_RESCHEDULE_NEW_DATE);
//                            }
//
//                            @Override
//                            public void onError(final DataManager.DataManagerError error) {
//                                if (!allowCallbacks) return;
//                                dataManagerErrorHandler.handleError(SplashActivity.this, error);
//                                openServiceCategoriesActivity();
//                            }
//                        });
//                    }
//
//                    @Override
//                    public void onError(final DataManager.DataManagerError error) {
//                        if (!allowCallbacks) return;
//                        dataManagerErrorHandler.handleError(SplashActivity.this, error);
//                        openServiceCategoriesActivity();
//                    }
//                });
//    }
}

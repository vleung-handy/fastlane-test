package com.handy.portal.onboarding.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.core.event.ProfileEvent;
import com.handy.portal.library.util.Utils;
import com.plattysoft.leonids.ParticleSystem;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ActivationWelcomeActivity extends AppCompatActivity
{
    public static final int COLORS = 12;

    private List<Integer> mDrawables;

    @BindView(R.id.left_center_view)
    View mLeftCenterView;

    @BindView(R.id.right_center_view)
    View mRightCenterView;

    @BindView(R.id.middle_view)
    View mCenterView;

    @BindView(R.id.tv_title)
    TextView mTvTitle;

    @BindView(R.id.loading_overlay)
    RelativeLayout mLoadingOverlay;

    @Inject
    EventBus mBus;

    private boolean mAnchorViewRendered = false;
    private boolean mProfileLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activation_welcome);
        ButterKnife.bind(this);
        Utils.inject(this, this);

        mDrawables = new ArrayList<>(COLORS);

        mDrawables.add(R.drawable.confetti_1);
        mDrawables.add(R.drawable.confetti_2);
        mDrawables.add(R.drawable.confetti_3);
        mDrawables.add(R.drawable.confetti_4);
        mDrawables.add(R.drawable.confetti_5);
        mDrawables.add(R.drawable.confetti_6);
        mDrawables.add(R.drawable.confetti_7);
        mDrawables.add(R.drawable.confetti_8);
        mDrawables.add(R.drawable.confetti_9);
        mDrawables.add(R.drawable.confetti_10);
        mDrawables.add(R.drawable.confetti_11);
        mDrawables.add(R.drawable.confetti_12);

        mTvTitle.setText(getString(R.string.congratulations));

        //we only play the confetti after the anchors have been rendered, otherwise
        //the confetti will come out weird.
        ViewTreeObserver viewTreeObserver = mLeftCenterView.getViewTreeObserver();
        if (viewTreeObserver.isAlive())
        {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
            {
                @Override
                public void onGlobalLayout()
                {
                    mLeftCenterView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    mAnchorViewRendered = true;
                    shootingConfetti();
                }
            });
        }

    }

    @OnClick(R.id.btn_next)
    public void getStarted()
    {
        finish();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        mBus.register(this);
        mLoadingOverlay.setVisibility(View.VISIBLE);
        mBus.post(new ProfileEvent.RequestProviderProfile(true));
    }

    @Override
    protected void attachBaseContext(Context newBase)
    {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onPause()
    {
        mBus.unregister(this);
        super.onPause();
    }

    @Subscribe
    public void onReceiveProviderProfileSuccess(ProfileEvent.ReceiveProviderProfileSuccess event)
    {
        mLoadingOverlay.setVisibility(View.GONE);
        mProfileLoaded = true;

        if (event.providerProfile != null
                && event.providerProfile.getProviderPersonalInfo() != null
                && !TextUtils.isEmpty(event.providerProfile.getProviderPersonalInfo().getFirstName()))
        {

            mTvTitle.setText(String.format(getString(
                    R.string.congratulations_formatted),
                    event.providerProfile.getProviderPersonalInfo().getFirstName()
            ));
        }

        shootingConfetti();
    }

    @Subscribe
    public void onReceiveProviderProfileError(ProfileEvent.ReceiveProviderProfileError event)
    {
        //we couldn't load the user profile, so we'll just stick with the default message
        mLoadingOverlay.setVisibility(View.GONE);
        mProfileLoaded = true;
        shootingConfetti();
    }


    /**
     * We only start confetti when these conditions are true:
     * -Profile information have finished loading
     * -The anchor views where the confettis are started have rendered.
     * -confetti haven't been fired before.
     */
    public void shootingConfetti()
    {
        if (!mProfileLoaded || !mAnchorViewRendered) { return; }

        int partNumPerSecond = 2;
        int emitTime = 1000;
        int maxParticles = 100;
        int timeToLive = 10000;
        for (int i = 0; i < COLORS; i++)
        {
            new ParticleSystem(this, maxParticles, mDrawables.get(i), timeToLive)
                    .setSpeedModuleAndAngleRange(0.1f, 0.3f, 225, 315)
                    .setRotationSpeed(144)
                    .setAcceleration(0.000685f, 90)
                    .setScaleRange(0.3f, 0.5f)
                    .emit(mRightCenterView, partNumPerSecond, emitTime);

            new ParticleSystem(this, maxParticles, mDrawables.get(i), timeToLive)
                    .setSpeedModuleAndAngleRange(0.1f, 0.3f, 225, 315)
                    .setRotationSpeed(144)
                    .setAcceleration(0.000685f, 90)
                    .setScaleRange(0.3f, 0.5f)
                    .emit(mLeftCenterView, partNumPerSecond, emitTime);

            new ParticleSystem(this, maxParticles, mDrawables.get(i), timeToLive)
                    .setSpeedModuleAndAngleRange(0.1f, 0.3f, 225, 315)
                    .setRotationSpeed(144)
                    .setAcceleration(0.000685f, 90)
                    .setScaleRange(0.3f, 0.5f)
                    .emit(mCenterView, partNumPerSecond, emitTime);
        }
    }
}

package com.handy.portal.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.handy.portal.R;
import com.plattysoft.leonids.ParticleSystem;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

//TODO: JIA: maybe change the name of this class, per Xi's comment that this process may not be
//called "OnBoarding"
public class OnboardActivity extends AppCompatActivity
{
    public static final int COLORS = 12;

    @DrawableRes
    List<Integer> mDrawables;

    @Bind(R.id.left_center_view)
    View mLeftCenterView;

    @Bind(R.id.right_center_view)
    View mRightCenterView;

    @Bind(R.id.middle_view)
    View mCenterView;

    @Bind(R.id.tv_title)
    TextView mTvTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboard);
        ButterKnife.bind(this);

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

        //TODO: JIA: remove this hardcoded name with a real name passed in
        String userName = "Jaclyn";
        mTvTitle.setText(String.format(getString(R.string.onboard_congratulations_formatted), userName));

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
                    shootingConfetti();
                }
            });
        }

    }

    @OnClick(R.id.btn_next)
    public void getStarted()
    {
        startActivity(new Intent(this, GettingStartedActivity.class));
    }


    public void shootingConfetti()
    {
        int partNumPerSecond = 2;
        int emitTime = 1000;
        for (int i = 0; i < COLORS; i++)
        {
            new ParticleSystem(this, 5, mDrawables.get(i), 5000)
                    .setSpeedModuleAndAngleRange(0.1f, 0.3f, 225, 315)
                    .setRotationSpeed(144)
                    .setAcceleration(0.000685f, 90)
                    .setScaleRange(0.3f, 0.5f)
                    .emit(mRightCenterView, partNumPerSecond, emitTime);

            new ParticleSystem(this, 5, mDrawables.get(i), 5000)
                    .setSpeedModuleAndAngleRange(0.1f, 0.3f, 225, 315)
                    .setRotationSpeed(144)
                    .setAcceleration(0.000685f, 90)
                    .setScaleRange(0.3f, 0.5f)
                    .emit(mLeftCenterView, partNumPerSecond, emitTime);

            new ParticleSystem(this, 5, mDrawables.get(i), 5000)
                    .setSpeedModuleAndAngleRange(0.1f, 0.3f, 225, 315)
                    .setRotationSpeed(144)
                    .setAcceleration(0.000685f, 90)
                    .setScaleRange(0.3f, 0.5f)
                    .emit(mCenterView, partNumPerSecond, emitTime);
        }
    }
}

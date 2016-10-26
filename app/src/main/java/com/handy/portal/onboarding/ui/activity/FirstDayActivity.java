package com.handy.portal.onboarding.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.core.BaseApplication;
import com.handy.portal.library.util.Utils;
import com.handy.portal.logger.handylogger.LogEvent;
import com.handy.portal.logger.handylogger.model.NativeOnboardingLog;
import com.handy.portal.onboarding.model.status.LearningLinkDetails;
import com.handy.portal.onboarding.model.subflow.SubflowData;
import com.handy.portal.onboarding.ui.view.LearningLinksView;
import com.handy.portal.util.MyLeadingMarginSpan2;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

import butterknife.BindDimen;
import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Simple, hard-coded activity to show content of suggestions on what to do on the first day
 * of work.
 */
public class FirstDayActivity extends AppCompatActivity
{

    private static final String TAG = FirstDayActivity.class.getName();
    private static final String LOG_ARTICLE_NAME = "Your First Day";

    @BindView(R.id.first_day_first_job_message)
    TextView mFirstJobMessage;

    @BindView(R.id.prepare_message)
    TextView mPreparedMessage;

    @BindView(R.id.done_message)
    TextView mDoneMessage;

    @BindDrawable(R.drawable.img_vacuum)
    Drawable mVacuumDrawable;

    @BindDrawable(R.drawable.img_success)
    Drawable mSuccessDrawable;

    @BindDimen(R.dimen.default_margin)
    int mDefaultMargin;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.links_container)
    LearningLinksView mLinksContainer;

    @Inject
    protected EventBus mBus;

    private SubflowData mStatusData;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_day);
        ButterKnife.bind(this);

        mStatusData = (SubflowData) getIntent().getSerializableExtra(BundleKeys.SUBFLOW_DATA);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        ((BaseApplication) getApplication()).inject(this);
        mFirstJobMessage.setText(Html.fromHtml(getString(R.string.first_job_message_styled)));

        setupPreparedMessage();
        setupDoneMessage();
        initLearningLinksView();
    }

    private void initLearningLinksView()
    {
        if (mStatusData == null)
        {
            return;
        }

        final LearningLinkDetails learningLinkDetails = mStatusData.getLearningLinkDetails();
        if (learningLinkDetails != null
                && learningLinkDetails.getLearningLinks() != null
                && !learningLinkDetails.getLearningLinks().isEmpty())
        {
            mLinksContainer.setVisibility(View.VISIBLE);
            mLinksContainer.bindLearningLinks(learningLinkDetails.getLearningLinks(), new View.OnClickListener()
            {
                @Override
                public void onClick(final View v)
                {
                    String url = (String) v.getTag();
                    mBus.post(new LogEvent.AddLogEvent(new NativeOnboardingLog.HelpLinkSelected(url)));
                    final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    Utils.safeLaunchIntent(intent, FirstDayActivity.this);
                }
            });
        }
        else
        {
            mLinksContainer.setVisibility(View.GONE);
        }
    }

    /**
     * Sets up the card that displays "Be prepared". The trick here is that the image is left aligned
     * with the text flowing beneath it.
     */
    private void setupPreparedMessage()
    {
        int leftMargin = mVacuumDrawable.getIntrinsicWidth() + mDefaultMargin;
        SpannableString ss = new SpannableString(getString(R.string.be_prepared_message));
        int lines = getTextLines(mPreparedMessage, mVacuumDrawable) + 1;

        Log.d(TAG, "setupPreparedMessage: lines:" + lines);

        ss.setSpan(new MyLeadingMarginSpan2(lines, leftMargin), 0, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mPreparedMessage.setText(ss);
    }

    /**
     * Given a textview and a image, it will return the number of lines of text that can be in the
     * textview to reach the height of the image
     *
     * @return
     */
    private int getTextLines(TextView textView, Drawable drawable)
    {
        float textLineHeight = textView.getPaint().getTextSize() * textView.getLineSpacingMultiplier();
        //minus 1 at the end to account for the title line
        return (int) Math.floor(drawable.getIntrinsicHeight() / textLineHeight) - 1;
    }

    /**
     * Sets up the card that displays "Day one is done". The trick here is that the image is right aligned,
     * with the text flowing beneath it. The technique used here is different than where the image is
     * left aligned.
     */
    private void setupDoneMessage()
    {
        String text = getString(R.string.done_message);

        int lines = getTextLines(mDoneMessage, mSuccessDrawable);
        Log.d(TAG, "setupDoneMessage: should have " + lines + "lines");

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int width = displayMetrics.widthPixels;

        text = insertLineBreaks(text, width - 4 * mDefaultMargin - mSuccessDrawable.getIntrinsicWidth(), lines);
        mDoneMessage.setText(text);
    }


    /**
     * @param text         The text to be wrapped around the image.
     * @param maxTextWidth The maximum width of the line of text
     * @param lines        The numbers of lines of text that can fit to the left of the image
     * @return
     */
    private String insertLineBreaks(String text, int maxTextWidth, int lines)
    {

        int spaceIndex = 0;
        int breaksInserted = 0;

        //Loop this until the number of new lines have been reached.
        while (text.indexOf(" ", spaceIndex) > 0 && breaksInserted < lines)
        {

            String currentLine = "";
            String nextWord = getNextWord(text, spaceIndex);
            while (nextWord != null && getWordLength(currentLine + nextWord) < maxTextWidth)
            {
                currentLine += nextWord;
                spaceIndex = text.indexOf(" ", spaceIndex) + 1; //advances the index to the next word
                nextWord = getNextWord(text, spaceIndex);
            }

            text = text.substring(0, spaceIndex) + "\n" + text.substring(spaceIndex, text.length());
            breaksInserted++;
        }

        return text;
    }

    private int getWordLength(String word)
    {
        Rect bounds = new Rect();
        mDoneMessage.getPaint().getTextBounds(word, 0, word.length(), bounds);
        return bounds.width();
    }

    private String getNextWord(String text, int start)
    {
        int end = text.indexOf(" ", start);

        if (end > 0)
        {
            return text.substring(start, end);
        }
        else
        {
            return null;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) //default support for back button
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void attachBaseContext(Context newBase)
    {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        mBus.post(new LogEvent.AddLogEvent(new NativeOnboardingLog.HelpArticleOpened(LOG_ARTICLE_NAME)));
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        mBus.post(new LogEvent.AddLogEvent(new NativeOnboardingLog.HelpArticleClosed(LOG_ARTICLE_NAME)));
    }
}

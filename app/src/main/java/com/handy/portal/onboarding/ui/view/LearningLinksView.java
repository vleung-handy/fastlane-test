package com.handy.portal.onboarding.ui.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.onboarding.model.status.LearningLink;

import java.util.ArrayList;

import butterknife.ButterKnife;

/**
 * A vertical linear layout with links to help pages.
 */
public class LearningLinksView extends LinearLayout
{
    public LearningLinksView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
        inflate(getContext(), R.layout.learning_links_view, this);
        ButterKnife.bind(this);
    }

    public void bindLearningLinks(ArrayList<LearningLink> learningLinks, OnClickListener onClickListener)
    {
        if (learningLinks == null)
        {
            return;
        }

        //remove everything it has, except for the title
        while (getChildCount() > 1)
        {
            removeViewAt(getChildCount() - 1);
        }

        //add the links
        for (final LearningLink learningLink : learningLinks)
        {
            addLinkTextView(learningLink.getTitle(), learningLink.getUrl(), onClickListener);
        }
    }

    private void addLinkTextView(
            final String text,
            @NonNull final String url,
            final OnClickListener onClickListener
    )
    {
        final TextView view = (TextView) LayoutInflater.from(getContext())
                .inflate(R.layout.view_link_text, this, false);
        view.setText(text);
        view.setTag(url);
        view.setClickable(true);
        view.setOnClickListener(onClickListener);
        addView(view);
    }
}

package com.handy.portal.announcements.ui;


import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.handy.portal.R;
import com.handy.portal.announcements.model.Announcement;
import com.handy.portal.library.util.TextUtils;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AnnouncementView extends FrameLayout {

    @BindView(R.id.fragment_announcement_header_image)
    ImageView mHeaderImage;

    @BindView(R.id.fragment_announcement_action_button)
    Button mActionButton;

    @BindView(R.id.fragment_announcement_title_text)
    TextView mTitleText;

    @BindView(R.id.fragment_announcement_body_text)
    TextView mBodyText;

    public AnnouncementView(@NonNull final Context context) {
        super(context);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.layout_announcement, this);
        ButterKnife.bind(this);
    }

    /**
     *
     * @param announcement
     * @param announcementActionButtonClickedListener
     * @param actionButtonText this is currently not sent from the server so it is not part of the Announcement model
     */
    protected void update(@NonNull final Announcement announcement,
                          @Nullable final OnAnnouncementActionButtonClickedListener announcementActionButtonClickedListener,
                          @NonNull String actionButtonText) {
        if(TextUtils.isNullOrEmpty(announcement.getTitle()))
        {
            //the title text will not necessarily be sent back from the server
            mTitleText.setVisibility(GONE);
        }
        else
        {
            mTitleText.setVisibility(VISIBLE);
            mTitleText.setText(announcement.getTitle());
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mBodyText.setText(Html.fromHtml(announcement.getSubtitle(), Html.FROM_HTML_MODE_LEGACY));
        }
        else {
            mBodyText.setText(Html.fromHtml(announcement.getSubtitle()));
        }
        mBodyText.setMovementMethod(LinkMovementMethod.getInstance());

        mActionButton.setText(actionButtonText);
        loadHeaderImage(announcement.getImageUrl());

        if (announcementActionButtonClickedListener != null) {
            mActionButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(final View v) {
                    announcementActionButtonClickedListener
                            .onAnnouncementActionButtonClicked(announcement);
                }
            });
        }
    }

    private void loadHeaderImage(@NonNull final String imageUrl) {
        mHeaderImage.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        try //picasso doesn't catch all errors like empty URL!
                        {
                            if (mHeaderImage.getWidth() <= 0) {
                                //may have to make more than one layout pass before this gets measured
                                return;
                            }
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                mHeaderImage.getViewTreeObserver()
                                        .removeOnGlobalLayoutListener(this);
                            }
                            else {
                                mHeaderImage.getViewTreeObserver()
                                        .removeGlobalOnLayoutListener(this);
                            }
                            Picasso.with(getContext()).
                                    load(imageUrl).
                                    error(R.drawable.banner_image_load_failed).
                                    resize(mHeaderImage.getWidth(), 0).
                                    into(mHeaderImage);
                            /*
                            need to call resize() because ImageView
                            can have rounding errors when scaling
                            that causes a 1px margin around the image and
                            there's no known way of scaling it against one dimension
                            using the view params
                             */
                        }
                        catch (Exception e) {
                            Crashlytics.logException(e);
                        }
                    }
                });

    }

    protected interface OnAnnouncementActionButtonClickedListener {
        void onAnnouncementActionButtonClicked(Announcement announcement);
    }
}

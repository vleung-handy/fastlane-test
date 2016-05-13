package com.handy.portal.library.ui.view;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.handy.portal.R;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;

public class YoutubeImagePlaceholderView extends FrameLayout
{
    @Bind(R.id.video_image)
    ImageView mVideoImage;

    private String mId;
    private String mImageUrlFormatted = "http://img.youtube.com/vi/%s/maxresdefault.jpg";
    private String mSection;

    public YoutubeImagePlaceholderView(final Context context)
    {
        super(context);
        init();
    }

    public YoutubeImagePlaceholderView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public YoutubeImagePlaceholderView(final Context context, final AttributeSet attrs, final int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public YoutubeImagePlaceholderView(final Context context, final AttributeSet attrs, final int defStyleAttr, final int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public void setID(String id)
    {
        mId = id;
        mImageUrlFormatted = String.format(mImageUrlFormatted, id);
        Picasso.with(getContext())
                .load(mImageUrlFormatted)
                .into(mVideoImage);
    }

    public void setSection(String section)
    {
        mSection = section;
    }

    public String getID()
    {
        return mId;
    }

    public String getSection()
    {
        return mSection;
    }

    private void init()
    {
        inflate(getContext(), R.layout.element_youtube_image_view, this);
        ButterKnife.bind(this);

        Resources resources = getResources();
        int widthPX = (int) resources.getDimension(R.dimen.youtube_image_view_width);
        int heightPX = (int) resources.getDimension(R.dimen.youtube_image_view_height);
        int marginTopPX = (int) resources.getDimension(R.dimen.youtube_image_view_margin_top);
        int marginBottomPX = (int) resources.getDimension(R.dimen.youtube_image_view_margin_bottom);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(widthPX, heightPX);
        layoutParams.setMargins(0, marginTopPX, 0, marginBottomPX);
        setLayoutParams(layoutParams);
    }
}

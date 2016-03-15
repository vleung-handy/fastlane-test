package com.handy.portal.ui.view;


import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.util.TypedValue;
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
    private String mImageUrl = "http://img.youtube.com/vi/%s/maxresdefault.jpg";
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

    @TargetApi(21)
    public YoutubeImagePlaceholderView(final Context context, final AttributeSet attrs, final int defStyleAttr, final int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public void setID(String id)
    {
        this.mId = id;
        mImageUrl = String.format(mImageUrl, id);
        Picasso.with(getContext())
                .load(getImageUrl())
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

    public String getImageUrl() { return mImageUrl; }

    public String getSection()
    {
        return mSection;
    }

    private void init()
    {
        inflate(getContext(), R.layout.element_youtube_image_view, this);
        ButterKnife.bind(this);

        int width = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 343, getResources().getDisplayMetrics());
        int height = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 193, getResources().getDisplayMetrics());
        int marginTop = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());
        int marginBottom = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 28, getResources().getDisplayMetrics());

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, height);
        layoutParams.setMargins(0, marginTop, 0, marginBottom);
        setLayoutParams(layoutParams);
    }

    public static Bitmap overlay(Bitmap bmp1, Bitmap bmp2)
    {
        Bitmap bmOverlay = Bitmap.createBitmap(bmp1.getWidth(), bmp1.getHeight(), bmp1.getConfig());
        Canvas canvas = new Canvas(bmOverlay);
        canvas.drawBitmap(bmp1, new Matrix(), null);
        canvas.drawBitmap(bmp2, 0, 0, null);
        return bmOverlay;
    }
}

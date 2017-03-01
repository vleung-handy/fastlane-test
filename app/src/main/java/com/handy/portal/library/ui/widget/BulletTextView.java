package com.handy.portal.library.ui.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.handy.portal.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BulletTextView extends FrameLayout {
    @BindView(R.id.text_with_bullet)
    TextView mTextView;

    public BulletTextView(final Context context, CharSequence text) {
        super(context);
        init();
        setText(text);
    }

    public BulletTextView(final Context context) {
        super(context);
        init();
    }

    public BulletTextView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BulletTextView(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BulletTextView(final Context context, final AttributeSet attrs, final int defStyleAttr, final int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.text_view_bullet, this);
        ButterKnife.bind(this);
        mTextView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public void setText(CharSequence text) {
        mTextView.setText(text);
    }
}

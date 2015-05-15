package com.handy.portal.ui.widget;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;

import com.handy.portal.util.TextUtils;

public final class PhoneInputTextView extends InputTextField
{
    private static final int PHONE_NUMBER_LENGTH = 10;

    private String countryCode;

    public PhoneInputTextView(final Context context)
    {
        super(context);
        init();
    }

    public PhoneInputTextView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public PhoneInputTextView(final Context context, final AttributeSet attrs, final int defStyle)
    {
        super(context, attrs, defStyle);
        init();
    }

    void init()
    {
        super.init();
        this.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(final CharSequence charSequence, final int start,
                                          final int count, final int after)
            {
            }

            @Override
            public void onTextChanged(final CharSequence charSequence, final int start,
                                      final int before, final int count)
            {
            }

            @Override
            public void afterTextChanged(final Editable editable)
            {
                PhoneInputTextView.this.removeTextChangedListener(this);
                PhoneInputTextView.this.setText(TextUtils.formatPhone(editable.toString(),
                        getCountryCode()));

                PhoneInputTextView.this.setSelection(PhoneInputTextView.this.getText().length());
                PhoneInputTextView.this.addTextChangedListener(this);
            }
        });
    }

    final String getCountryCode()
    {
        return countryCode;
    }

    public final void setCountryCode(final String countryCode)
    {
        this.countryCode = countryCode;
    }

    public final boolean validate()
    {
        final String phone = this.getText().toString();
        if (phone.replaceAll("[^0-9]", "").length() != PHONE_NUMBER_LENGTH)
        {
            highlight();
            return false;
        } else
        {
            unHighlight();
            return true;
        }
    }

    public final String getPhoneNumber()
    {
        return (this.countryCode != null ? this.countryCode : "") + this.getText().toString().replaceAll("[^0-9]", "");
    }
}

package com.handy.portal.library.ui.fragment.dialog;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.handy.portal.R;

import butterknife.BindView;

/**
 * a dismissible info dialog that accepts a title and message
 */
public class InfoDialogFragment extends ConfirmActionSlideUpDialogFragment {
    public static final String FRAGMENT_TAG =
            InfoDialogFragment.class.getName();

    public static final String BUNDLE_KEY_TITLE_TEXT
            = "BUNDLE_KEY_TITLE_TEXT";
    public static final String BUNDLE_KEY_MESSAGE_TEXT
            = "BUNDLE_KEY_MESSAGE_TEXT";

    @BindView(R.id.fragment_dialog_info_title_text)
    TextView mTitleText;
    @BindView(R.id.fragment_dialog_info_message_text)
    TextView mMessageText;

    public static InfoDialogFragment newInstance(
            @NonNull String titleText,
            @NonNull String messageText
    ) {
        Bundle args = new Bundle();
        args.putString(BUNDLE_KEY_TITLE_TEXT, titleText);
        args.putString(BUNDLE_KEY_MESSAGE_TEXT, messageText);
        InfoDialogFragment fragment =
                new InfoDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected View inflateConfirmActionContentView(final LayoutInflater inflater, final ViewGroup container) {
        return inflater.inflate(R.layout.fragment_dialog_info, container, false);
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String titleText =
                getArguments().getString(BUNDLE_KEY_TITLE_TEXT);
        mTitleText.setText(titleText);
        String messageText =
                getArguments().getString(BUNDLE_KEY_MESSAGE_TEXT);
        mMessageText.setText(messageText);
    }

    @Override
    protected void onConfirmActionButtonClicked() {
        dismiss();
    }

    @Override
    protected int getConfirmButtonBackgroundResourceId() {
        return R.drawable.button_green_round;
    }

    @Override
    protected String getConfirmButtonText() {
        return getString(R.string.info_dialog_confirm_button);
    }
}

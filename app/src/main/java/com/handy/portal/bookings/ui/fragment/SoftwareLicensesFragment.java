package com.handy.portal.bookings.ui.fragment;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.google.common.io.CharStreams;
import com.handy.portal.R;
import com.handy.portal.core.constant.MainViewPage;
import com.handy.portal.core.event.NavigationEvent;
import com.handy.portal.core.ui.fragment.ActionBarFragment;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import butterknife.BindView;
import butterknife.ButterKnife;


public class SoftwareLicensesFragment extends ActionBarFragment {
    @BindView(R.id.software_licenses_text_layout)
    TextView mSoftwareLicensesTextLayout;

    private static final String HTML_FILE = "software_licenses.html";
    private static final String UTF_8 = "UTF-8";

    @Override
    protected MainViewPage getAppPage() {
        return MainViewPage.SOFTWARE_LICENSES;
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_software_licenses, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setActionBar(R.string.software_licenses, true);

        try {
            InputStream stream = getContext().getAssets().open(HTML_FILE);
            String template = CharStreams.toString(new InputStreamReader(stream, UTF_8));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                mSoftwareLicensesTextLayout.setText(Html.fromHtml(template,
                        Html.FROM_HTML_MODE_LEGACY));
            }
            else {
                mSoftwareLicensesTextLayout.setText(Html.fromHtml(template));
            }
            mSoftwareLicensesTextLayout.setMovementMethod(LinkMovementMethod.getInstance());
        }
        catch (IOException e) {
            Crashlytics.logException(e);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        bus.post(new NavigationEvent.SetNavigationTabVisibility(false));
    }

    @Override
    public void onStop() {
        super.onStop();
        bus.post(new NavigationEvent.SetNavigationTabVisibility(true));
    }
}

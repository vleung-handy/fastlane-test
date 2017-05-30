package com.handy.portal.core.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;

import com.handy.portal.R;
import com.handy.portal.core.MainContentFragmentHolder;

/**
 * use when you want to launch a fragment in its own activity (ex. update payment method)
 */
public class FragmentContainerActivity extends BaseActivity
        implements MainContentFragmentHolder {

    public static final String BUNDLE_KEY_CLASS_NAME = "BUNDLE_KEY_CLASS_NAME";
    public static final String BUNDLE_KEY_FRAGMENT_BUNDLE = "BUNDLE_KEY_FRAGMENT_BUNDLE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_container);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        Bundle bundleExtras = getIntent().getExtras();

        Bundle fragmentBundle = bundleExtras.getBundle(BUNDLE_KEY_FRAGMENT_BUNDLE);
        String className = bundleExtras.getString(BUNDLE_KEY_CLASS_NAME);
        Fragment fragment = Fragment.instantiate(this, className, fragmentBundle);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_container, fragment).commit();
    }

    public static Intent getIntent(
            @NonNull Context context,
            @NonNull Class fragmentClass,
            @Nullable Bundle fragmentBundle) {
        Intent intent = new Intent(context, FragmentContainerActivity.class);
        intent.putExtra(BUNDLE_KEY_CLASS_NAME, fragmentClass.getName());
        intent.putExtra(BUNDLE_KEY_FRAGMENT_BUNDLE, fragmentBundle);
        return intent;
    }

    @Override
    public void replaceMainContentFragment(@NonNull final Fragment replacementFragment, final boolean addToBackStack) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_container, replacementFragment);
        if(addToBackStack)
        {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }
}

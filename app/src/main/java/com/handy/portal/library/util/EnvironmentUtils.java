package com.handy.portal.library.util;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import com.handy.portal.R;
import com.handy.portal.core.EnvironmentModifier;

public class EnvironmentUtils {

    public static void showEnvironmentModifierDialog(
            final EnvironmentModifier environmentModifier,
            final Context context,
            @Nullable final EnvironmentModifier.OnEnvironmentChangedListener callback) {
        final String[] environmentNames = getEnvironmentNames(context, environmentModifier);
        UIUtils.createDialogBuilderWithTitle(context, R.string.select_environment)
                .setAdapter(new ArrayAdapter<>(context, R.layout.view_selection_text,
                                environmentNames),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialog, final int which) {
                                final EnvironmentModifier.Environment selectedEnvironment =
                                        EnvironmentModifier.Environment.values()[which];
                                switch (selectedEnvironment) {
                                    case NAMESAPCE:
                                    case LOCAL:
                                        showEnvironmentPrefixDialog(context, environmentModifier,
                                                selectedEnvironment, callback);
                                        break;
                                    default:
                                        environmentModifier.setEnvironment(selectedEnvironment,
                                                null, callback);
                                        break;
                                }
                            }
                        })
                .setNegativeButton(R.string.cancel, null)
                .create()
                .show();
    }

    private static void showEnvironmentPrefixDialog(
            final Context context,
            final EnvironmentModifier environmentModifier,
            final EnvironmentModifier.Environment environment,
            final EnvironmentModifier.OnEnvironmentChangedListener callback) {
        final EditText input = new EditText(context);
        input.setSingleLine();
        input.setTypeface(FontUtils.getFont(context, FontUtils.CIRCULAR_BOOK));
        input.setGravity(Gravity.CENTER);
        int titleTextResId;
        switch (environment) {
            case NAMESAPCE:
                titleTextResId = R.string.env_modifier_dialog_title_namespace;
                input.setHint(R.string.env_modifier_dialog_input_hint_namespace);
                break;
            case LOCAL:
                titleTextResId = R.string.env_modifier_dialog_title_domain;
                input.setHint(R.string.env_modifier_dialog_input_hint_domain);
                break;
            default:
                return;
        }
        UIUtils.createDialogBuilderWithTitle(context, titleTextResId)
                .setView(input)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, final int which) {
                        String value = input.getText().toString().trim().toLowerCase();
                        if (!TextUtils.isNullOrEmpty(value)) {
                            environmentModifier.setEnvironment(environment, value, callback);
                        }
                    }
                })
                .create()
                .show();
    }

    @NonNull
    private static String[] getEnvironmentNames(final Context context,
                                                final EnvironmentModifier environmentModifier) {
        final EnvironmentModifier.Environment currentEnvironment =
                environmentModifier.getEnvironment();
        final String currentEnvironmentPrefix = environmentModifier.getEnvironmentPrefix();
        final EnvironmentModifier.Environment[] environments =
                EnvironmentModifier.Environment.values();
        final String[] environmentNames = new String[environments.length];

        for (int i = 0; i < environments.length; i++) {
            final EnvironmentModifier.Environment environment = environments[i];
            environmentNames[i] = context.getString(environment.getDisplayNameResId());
            if (currentEnvironment == environment) {
                if (!TextUtils.isNullOrEmpty(currentEnvironmentPrefix)) {
                    environmentNames[i] += " - " + currentEnvironmentPrefix;
                }
                environmentNames[i] += " (current)";
            }
        }
        return environmentNames;
    }
}

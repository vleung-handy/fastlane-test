package com.handy.portal.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.constant.BookingActionButtonType;
import com.handy.portal.core.EnvironmentModifier;
import com.handy.portal.model.Booking;
import com.handy.portal.model.PaymentInfo;
import com.handy.portal.model.definitions.FieldDefinition;
import com.handy.portal.ui.activity.BaseActivity;

import java.text.DecimalFormat;

public final class UIUtils
{
    //TODO: move some of these functions into a separate util class
    public static void launchFragmentInMainActivityOnBackStack(FragmentActivity activity, Fragment fragment)
    {
        FragmentTransaction fragmentTransaction = activity.getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_container, fragment).addToBackStack(null).commit();

    }

    public static boolean validateField(TextView input, FieldDefinition fieldDefinition)
    {
        if (fieldDefinition.getCompiledPattern() != null && !fieldDefinition.getCompiledPattern().matcher(input.getText()).matches())
        {
            input.setError(fieldDefinition.getErrorMessage());
            return false;
        }
        return true;
    }

    public static void setInputFilterForInputType(TextView textView, FieldDefinition.InputType inputType)
    {
        if (inputType == null || textView == null) return;
        switch (inputType)
        {
            case NUMBER:
                textView.setInputType(InputType.TYPE_CLASS_NUMBER);
                break;
            case ALPHA_NUMERIC:
                textView.setFilters(new InputFilter[]{new InputFilter()
                {
                    @Override
                    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend)
                    {
                        for (int i = start; i < end; i++)
                        {
                            if (!Character.isLetterOrDigit(source.charAt(i)))
                            {
                                return "";
                            }
                        }
                        return null;
                    }
                }});
                break;
        }
    }


    public static void setFieldsFromDefinition(TextView label, TextView input, FieldDefinition fieldDefinition)
    {
        if (label != null)
        {
            label.setText(fieldDefinition.getDisplayName());
        }
        if (input != null)
        {
            input.setHint(fieldDefinition.getHintText());
        }
        setInputFilterForInputType(input, fieldDefinition.getInputType());
    }

    public static void dismissOnBackPressed(Activity activity)
    {
        UIUtils.dismissKeyboard(activity);
        activity.onBackPressed();
    }

    public static void dismissKeyboard(Activity activity)
    {
        View currentFocus = activity.getCurrentFocus();
        if (currentFocus != null)
        {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
        }
    }

    public static ViewGroup getParent(View view)
    {
        return (ViewGroup) view.getParent();
    }

    public static void removeView(View view)
    {
        ViewGroup parent = getParent(view);
        if (parent != null)
        {
            parent.removeView(view);
        }
    }

    public static void replaceView(View currentView, View newView)
    {
        ViewGroup parent = getParent(currentView);
        if (parent == null)
        {
            return;
        }
        final int index = parent.indexOfChild(currentView);
        removeView(currentView);
        removeView(newView);
        parent.addView(newView, index);
    }

    public static void replaceViewWithFragment(Context context, ViewGroup view, Fragment fragment)
    {
        FragmentManager fragmentManager = ((BaseActivity) context).getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(view.getId(), fragment);
        fragmentTransaction.commit();
        fragmentManager.executePendingTransactions();
    }

    public static void setPaymentInfo(TextView dollarTextView, TextView centsTextView, PaymentInfo paymentInfo, String format)
    {
        if (paymentInfo != null && paymentInfo.getAmount() > 0)
        {
            int amount = paymentInfo.getAmount();
            double centsAmount = (amount % 100) * 0.01;
            int dollarAmount = amount / 100;

            if (centsTextView != null)
            {
                if (centsAmount > 0)
                {
                    centsTextView.setText(new DecimalFormat(".00").format(centsAmount));
                    centsTextView.setVisibility(View.VISIBLE);
                }
                else
                {
                    centsTextView.setVisibility(View.GONE);
                }
            }

            String paymentString = CurrencyUtils.formatPrice(dollarAmount, paymentInfo.getCurrencySymbol());
            dollarTextView.setText(String.format(format, paymentString));
            dollarTextView.setVisibility(View.VISIBLE);
        }
        else
        {
            dollarTextView.setVisibility(View.INVISIBLE);
            if (centsTextView != null)
            {
                centsTextView.setVisibility(View.GONE);
            }
        }
    }

    public static String getFrequencyInfo(Booking booking, Context parentContext)
    {
        //Frequency
        //Valid values : 1,2,4 every X weeks, 0 = non-recurring
        int frequency = booking.getFrequency();
        String bookingFrequencyFormat = getFrequencyFormatString(booking, parentContext);
        return String.format(bookingFrequencyFormat, frequency);
    }

    public static void setFrequencyInfo(Booking booking, TextView textView, Context parentContext)
    {
        String bookingFrequency = getFrequencyInfo(booking, parentContext);
        textView.setText(bookingFrequency);
    }

    private static String getFrequencyFormatString(Booking booking, Context parentContext)
    {
        int frequency = booking.getFrequency();
        String bookingFrequencyFormat;

        if (frequency == 0)
        {
            bookingFrequencyFormat = parentContext.getString(R.string.booking_frequency_non_recurring);
        }
        else if (frequency == 1)
        {
            bookingFrequencyFormat = parentContext.getString(R.string.booking_frequency_every_week);
        }
        else
        {
            bookingFrequencyFormat = parentContext.getString(R.string.booking_frequency);
        }
        return bookingFrequencyFormat;
    }

    //Map action button data to a booking action button type
    public static BookingActionButtonType getAssociatedActionType(Booking.Action data)
    {
        String actionName = data.getActionName();
        for (BookingActionButtonType bat : BookingActionButtonType.values())
        {
            if (actionName.equals(bat.getActionName()))
            {
                return bat;
            }
        }
        return null;
    }

    public static AlertDialog createEnvironmentModifierDialog(final EnvironmentModifier environmentModifier, final Context context, final EnvironmentModifier.OnEnvironmentChangedListener callback)
    {
        final EnvironmentModifier.Environment[] environments = EnvironmentModifier.Environment.values();
        String[] environmentNames = new String[environments.length];
        String currentEnvironmentPrefix = environmentModifier.getEnvironmentPrefix();
        for (int i = 0; i < environments.length; i++)
        {
            EnvironmentModifier.Environment environment = environments[i];
            environmentNames[i] = environment + (currentEnvironmentPrefix.equals(environment.getPrefix()) ? " (selected)" : "");
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Pick an environment")
                .setItems(environmentNames, new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        String selectedEnvironmentPrefix = environments[which].getPrefix();
                        environmentModifier.setEnvironmentPrefix(selectedEnvironmentPrefix, callback);
                    }
                });
        return builder.create();
    }
}

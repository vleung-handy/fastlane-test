package com.handy.portal.library.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.handy.portal.R;
import com.handy.portal.bookings.constant.BookingActionButtonType;
import com.handy.portal.bookings.model.Booking;
import com.handy.portal.library.ui.view.DateFormFieldTableRow;
import com.handy.portal.library.ui.view.Errorable;
import com.handy.portal.library.ui.view.FormFieldTableRow;
import com.handy.portal.model.definitions.FieldDefinition;
import com.handy.portal.payments.model.PaymentInfo;
import com.handy.portal.ui.activity.BaseActivity;
import com.handy.portal.ui.widget.TitleView;

import java.text.DecimalFormat;
import java.util.regex.Pattern;

public final class UIUtils
{
    public static final ViewGroup.LayoutParams MATCH_PARENT_PARAMS = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    public static final ViewGroup.LayoutParams MATCH_WIDTH_WRAP_HEIGHT_PARAMS = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    private static final int SNACK_BAR_DURATION_WITH_ICON = 4000;


    public static boolean validateField(FormFieldTableRow field, FieldDefinition fieldDefinition)
    {
        CharSequence value = field.getValue().getText();
        Pattern pattern = fieldDefinition.getCompiledPattern();
        boolean isValid = pattern == null || pattern.matcher(value).matches();

        if (!isValid)
        {
            field.setErrorState(true);
        }

        return isValid;
    }

    public static boolean validateField(DateFormFieldTableRow field, FieldDefinition monthFieldDefinition, FieldDefinition yearFieldDefinition)
    {
        CharSequence monthValue = field.getMonthValue().getText();
        CharSequence yearValue = field.getYearValue().getText();
        Pattern monthPattern = monthFieldDefinition.getCompiledPattern();
        Pattern yearPattern = yearFieldDefinition.getCompiledPattern();
        boolean isValid = (monthPattern == null || monthPattern.matcher(monthValue).matches()) && (yearPattern == null || yearPattern.matcher(yearValue).matches());

        if (!isValid)
        {
            field.setErrorState(true);
        }

        return isValid;
    }

    public static void setInputFilterForInputType(TextView textView, FieldDefinition.InputType inputType)
    {
        if (inputType == null || textView == null) { return; }
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

    public static void setFieldsFromDefinition(FormFieldTableRow field, FieldDefinition fieldDefinition)
    {
        field.getLabel().setText(fieldDefinition.getDisplayName());
        field.getValue().setHint(fieldDefinition.getHintText());
        setInputFilterForInputType(field.getValue(), fieldDefinition.getInputType());
    }

    public static void setFieldsFromDefinition(DateFormFieldTableRow field, FieldDefinition dateFieldDefinition, FieldDefinition monthFieldDefinition, FieldDefinition yearFieldDefinition)
    {
        field.getLabel().setText(dateFieldDefinition.getDisplayName());
        field.getMonthValue().setHint(monthFieldDefinition.getHintText());
        field.getYearValue().setHint(yearFieldDefinition.getHintText());
        setInputFilterForInputType(field.getMonthValue(), monthFieldDefinition.getInputType());
        setInputFilterForInputType(field.getYearValue(), yearFieldDefinition.getInputType());
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

    public static void setSnackbarImage(Snackbar snackbar, @DrawableRes int imageRes, int padding)
    {
        //make the snackbar display an icon if there exists one.
        View snackbarLayout = snackbar.getView();

        //ignore the lint error: Must be one of: Snackbar.LENGTH_SHORT, Snackbar.LENGTH_LONG.
        //will work wtih design library 23.0+, which is what we have.
        snackbar.setDuration(SNACK_BAR_DURATION_WITH_ICON);

        TextView textView = (TextView) snackbarLayout.findViewById(android.support.design.R.id.snackbar_text);
        if (textView != null)
        {
            textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
            textView.setCompoundDrawablesWithIntrinsicBounds(imageRes, 0, 0, 0);
            textView.setCompoundDrawablePadding(padding);
            textView.setMaxLines(3);    //otherwise for some devices, it won't fit some of the messages we have.
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

    public static void setService(final TextView serviceTextView, final Booking booking)
    {
        Booking.ServiceInfo serviceInfo = booking.getServiceInfo();
        if (serviceInfo.isHomeCleaning())
        {
            final Context context = serviceTextView.getContext();
            String frequencyInfo = UIUtils.getFrequencyInfo(booking, context);
            if (booking.isUK() &&
                    booking.getExtrasInfoByMachineName(Booking.ExtraInfo.TYPE_CLEANING_SUPPLIES)
                            .size() > 0)
            {
                frequencyInfo += " \u22C5 " + context.getString(R.string.supplies);
            }
            serviceTextView.setText(frequencyInfo);
        }
        else
        {
            serviceTextView.setText(serviceInfo.getDisplayName());
        }
        appendTimeWindow(serviceTextView, booking.getMinimumHours(), booking.getHours());
    }

    private static void appendTimeWindow(
            final TextView timeWindowTextView,
            final float minimumHours,
            final float hours
    )
    {
        if (minimumHours > 0 && minimumHours < hours)
        {
            final String minimumHoursFormatted = TextUtils.formatHours(minimumHours);
            final String hoursFormatted = TextUtils.formatHours(hours);
            final Context context = timeWindowTextView.getContext();
            timeWindowTextView.append(" " + context.getString(R.string.time_window_formatted,
                    minimumHoursFormatted, hoursFormatted));
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

    public static int calculateDpToPx(Context context, int dp)
    {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + .5f); // round up if the decimal is greater than .5
    }

    @NonNull
    public static android.app.AlertDialog.Builder createDialogBuilderWithTitle(
            final Context context, @StringRes final int titleResId)
    {
        final android.app.AlertDialog.Builder dialogBuilder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            dialogBuilder = new android.app.AlertDialog.Builder(context, R.style.AlertDialogStyle);
            final TitleView titleView = new TitleView(context);
            titleView.setText(titleResId);
            dialogBuilder.setCustomTitle(titleView);
        }
        else
        {
            dialogBuilder = new android.app.AlertDialog.Builder(context);
            dialogBuilder.setTitle(titleResId);
        }
        return dialogBuilder;
    }

    public static class FormFieldErrorStateRemover implements TextWatcher
    {
        private Errorable errorable;

        public FormFieldErrorStateRemover(Errorable errorable)
        {
            this.errorable = errorable;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) { }

        @Override
        public void afterTextChanged(Editable s)
        {
            errorable.setErrorState(false);
        }
    }

    //Return index of child that is checked, -1 otherwise
    public static int indexOfCheckedRadioButton(RadioGroup radioGroup)
    {
        if (radioGroup != null)
        {
            View v = radioGroup.findViewById(radioGroup.getCheckedRadioButtonId());
            if (v != null)
            {
                return radioGroup.indexOfChild(v);
            }
        }
        return -1;
    }

    public static void showToast(Context context, String message, int length)
    {
        Toast toast = Toast.makeText(context, message, length);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    // Convert a view to bitmap
    public static Bitmap createDrawableFromView(Context context, View view)
    {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return bitmap;
    }

    public static float getPercentViewVisibleInScrollView(View view, ScrollView scrollView)
    {
        float percentViewVisible = 0f;

        Rect viewHitRect = new Rect();
        view.getHitRect(viewHitRect);

        Rect scrollBounds = new Rect(scrollView.getScrollX(),
                scrollView.getScrollY(),
                scrollView.getScrollX() + scrollView.getWidth(),
                scrollView.getScrollY() + scrollView.getHeight());

        //Is this at all visible?
        if (Rect.intersects(viewHitRect, scrollBounds))
        {
            //We have at least some overlap, calc the percent
            percentViewVisible = UIUtils.rectangleOverlapPercent(viewHitRect, scrollBounds);
        }

        return percentViewVisible;
    }

    public static float rectangleOverlapPercent(Rect r1, Rect r2)
    {
        float xOverlap = Math.max(0, Math.min(r1.right, r2.right) - Math.max(r1.left, r2.left));
        float yOverlap = Math.max(0, Math.min(r1.bottom, r2.bottom) - Math.max(r1.top, r2.top));
        float overlapArea = xOverlap * yOverlap;

        return overlapArea / (r1.width() * r1.height());
    }

    public static LinearLayout createLinearLayout(Context context, int orientation)
    {
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(orientation);
        linearLayout.setLayoutParams(MATCH_PARENT_PARAMS);
        return linearLayout;
    }

    @Nullable
    public static RadioButton getCheckedRadioButton(RadioGroup radioGroup)
    {
        for (int i = 0; i < radioGroup.getChildCount(); ++i)
        {
            RadioButton radioButton = (RadioButton) radioGroup.getChildAt(i);
            if (radioButton.isChecked())
            {
                return radioButton;
            }
        }
        return null;
    }
}

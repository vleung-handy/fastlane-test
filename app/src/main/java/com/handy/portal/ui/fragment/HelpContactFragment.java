package com.handy.portal.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.crashlytics.android.Crashlytics;
import com.handy.portal.R;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.constant.MainViewTab;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.model.HelpNode;
import com.handy.portal.model.Provider;
import com.handy.portal.ui.view.HelpBannerView;
import com.handy.portal.ui.view.HelpContactView;
import com.squareup.otto.Subscribe;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.mime.TypedByteArray;
import retrofit.mime.TypedInput;

public final class HelpContactFragment extends InjectedFragment
{
    private static final String HELP_CONTACT_FORM_DISPOSITION = "help-contact-form-disposition";
    private static final String HELP_CONTACT_FORM_NAME = "name";
    private static final String HELP_CONTACT_FORM_EMAIL = "email";
    private static final String HELP_CONTACT_FORM_DESCRIPTION = "description";
    private static final String HELP_CONTACT_FORM_PATH = "path";
    private static final String HELP_CONTACT_FORM_BOOKING_ID = "booking_id";

    private static final String SALESFORCE_DATA_WRAPPER_KEY = "salesforce_data";

    @InjectView(R.id.help_contact_view)
    HelpContactView helpContactView;

    @InjectView(R.id.help_banner_view)
    HelpBannerView helpBannerView;

    private HelpNode associatedNode;
    private String path;
    private String bookingId;

    @Override
    protected List<String> requiredArguments()
    {
        List<String> requiredArguments = new ArrayList<>();
        requiredArguments.add(BundleKeys.HELP_NODE);
        requiredArguments.add(BundleKeys.PATH);
        return requiredArguments;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if(!MainActivityFragment.clearingBackStack)
        {
            bus.post(new HandyEvent.RequestProviderInfo());
        }
    }

    @Override
    public final View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                                   final Bundle savedInstanceState)
    {
        final View view = getActivity().getLayoutInflater()
                .inflate(R.layout.fragment_help_contact, container, false);

        ButterKnife.inject(this, view);

        //should have passed along an associated node and a path
        if (!validateRequiredArguments())
        {
            Crashlytics.log("Can not construct Help Contact Form, missing requirements");
            return view;
        }

        //required arguments
        this.associatedNode = getArguments().getParcelable(BundleKeys.HELP_NODE);
        this.path = getArguments().getString(BundleKeys.PATH);

        //optional argument booking id
        if (getArguments() != null && getArguments().containsKey(BundleKeys.BOOKING_ID))
        {
            this.bookingId = getArguments().getString(BundleKeys.BOOKING_ID);
        }
        else
        {
            this.bookingId = "";
        }

        helpContactView.updateDisplay(this.associatedNode);

        helpBannerView.updateDisplay(); //TODO: can we call this inside updateDisplay(HelpNode) instead?

        assignClickListeners(view);

        return view;
    }

    private void assignClickListeners(View view)
    {
        helpContactView.sendMessageButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                onSendMessageButtonClick();
            }
        });

        final Activity activity = this.getActivity();
        helpBannerView.backImage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                dismissKeyboard();
                activity.onBackPressed();
            }
        });
    }

    private void onSendMessageButtonClick()
    {
        Boolean allValid = true;

        allValid &= helpContactView.nameText.validate();
        allValid &= helpContactView.emailText.validate();
        allValid &= helpContactView.commentText.validate();

        if (allValid)
        {
            dismissKeyboard();
            sendContactFormData(helpContactView.nameText.getString(), helpContactView.emailText.getString(), helpContactView.commentText.getString(), this.associatedNode);
        }
        else
        {
            showToast(R.string.ensure_fields_valid);
        }
    }

    private void dismissKeyboard()
    {
        View currentFocus = getActivity().getCurrentFocus();
        if (currentFocus != null)
        {
            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
        }
    }

    private void sendContactFormData(String name, String email, String comment, HelpNode associatedNode)
    {
        HashMap<String, String> contactFormInfo = extractDispositions(associatedNode);

        //add contact form information
        contactFormInfo.put(HELP_CONTACT_FORM_NAME, name);
        contactFormInfo.put(HELP_CONTACT_FORM_EMAIL, email);
        contactFormInfo.put(HELP_CONTACT_FORM_DESCRIPTION, comment);
        contactFormInfo.put(HELP_CONTACT_FORM_PATH, path);
        contactFormInfo.put(HELP_CONTACT_FORM_BOOKING_ID, bookingId);

        JSONObject salesforceWrapper = new JSONObject();
        try
        {
            salesforceWrapper.put(SALESFORCE_DATA_WRAPPER_KEY, new JSONObject(contactFormInfo));
        } catch (Exception e)
        {
        }

        TypedInput body;
        try
        {
            body = new TypedByteArray("application/json", salesforceWrapper.toString().getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e)
        {
            body = null;
        }

        bus.post(new HandyEvent.SetLoadingOverlayVisibility(true));

        bus.post(new HandyEvent.RequestNotifyHelpContact(body));
    }

    private HashMap<String, String> extractDispositions(HelpNode node)
    {
        HashMap<String, String> params = new HashMap<>();
        for (HelpNode childNode : node.getChildren())
        {
            if(childNode == null || childNode.getType() == null)
            {
                continue;
            }

            if (childNode.getType().equals(HELP_CONTACT_FORM_DISPOSITION))
            {
                params.put(childNode.getLabel(), childNode.getContent());
            }
        }
        return params;
    }

    private void returnToJobsScreen()
    {
        bus.post(new HandyEvent.NavigateToTab(MainViewTab.AVAILABLE_JOBS));
    }

    private void returnToBookingDetails(String bookingId)
    {
        Bundle arguments = new Bundle();
        arguments.putString(BundleKeys.BOOKING_ID, bookingId);
        HandyEvent.NavigateToTab event = new HandyEvent.NavigateToTab(MainViewTab.DETAILS, arguments);
        bus.post(event);
    }

    //Event Listeners
    @Subscribe
    public void onReceiveNotifyHelpContactSuccess(HandyEvent.ReceiveNotifyHelpContactSuccess event)
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        if (bookingId == null || bookingId.isEmpty())
        {
            returnToJobsScreen();
        }
        else
        {
            returnToBookingDetails(bookingId);
        }

        showToast(getString(R.string.contact_received));
    }

    @Subscribe
    public void onReceiveNotifyHelpContactError(HandyEvent.ReceiveNotifyHelpContactError event)
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        showToast(getString(R.string.an_error_has_occurred));
    }

    @Subscribe
    public void onReceiveProviderInfoSuccess(HandyEvent.ReceiveProviderInfoSuccess event)
    {
        Provider provider = event.provider;
        helpContactView.prepopulateProviderData(provider);

    }
    @Subscribe
    public void onReceiveProviderInfoFailure(HandyEvent.ReceiveProviderInfoError event)
    {
        helpContactView.prepopulateProviderData(null);
    }
}
package com.handy.portal.helpcenter.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.crashlytics.android.Crashlytics;
import com.google.common.collect.Lists;
import com.handy.portal.R;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.constant.MainViewTab;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.event.LogEvent;
import com.handy.portal.helpcenter.model.HelpNode;
import com.handy.portal.helpcenter.ui.view.HelpContactView;
import com.handy.portal.model.Provider;
import com.handy.portal.ui.fragment.ActionBarFragment;
import com.handy.portal.ui.fragment.MainActivityFragment;
import com.handy.portal.util.UIUtils;
import com.squareup.otto.Subscribe;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.mime.TypedByteArray;
import retrofit.mime.TypedInput;

public final class HelpContactFragment extends ActionBarFragment
{
    private static final String HELP_CONTACT_FORM_DISPOSITION = "help-contact-form-disposition";
    private static final String HELP_CONTACT_FORM_NAME = "name";
    private static final String HELP_CONTACT_FORM_EMAIL = "email";
    private static final String HELP_CONTACT_FORM_DESCRIPTION = "description";
    private static final String HELP_CONTACT_FORM_PATH = "path";
    private static final String HELP_CONTACT_FORM_BOOKING_ID = "booking_id";

    private static final String SALESFORCE_DATA_WRAPPER_KEY = "salesforce_data";

    @Bind(R.id.help_contact_view)
    HelpContactView helpContactView;

    private HelpNode associatedNode;
    private String path;
    private String bookingId;
    private String bookingType;

    @Override
    protected List<String> requiredArguments()
    {
        return Lists.newArrayList(BundleKeys.HELP_NODE, BundleKeys.PATH);
    }

    @Override
    protected MainViewTab getTab()
    {
        return MainViewTab.HELP;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        setActionBar(R.string.contact_us, true);
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

        ButterKnife.bind(this, view);

        //should have passed along an associated node and a path
        if (!validateRequiredArguments())
        {
            Crashlytics.logException(new RuntimeException("Cannot construct Help Contact Form, missing requirements"));
            return view;
        }

        //required arguments
        Bundle arguments = getArguments();
        this.associatedNode = arguments.getParcelable(BundleKeys.HELP_NODE);
        this.path = arguments.getString(BundleKeys.PATH);

        //optional argument booking id
        if (arguments != null && arguments.containsKey(BundleKeys.BOOKING_ID))
        {
            this.bookingId = arguments.getString(BundleKeys.BOOKING_ID);
            this.bookingType = arguments.getString(BundleKeys.BOOKING_TYPE);
        }
        else
        {
            this.bookingId = "";
        }

        helpContactView.updateDisplay(this.associatedNode);

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
    }

    private void onSendMessageButtonClick()
    {
        Boolean allValid = true;

        allValid &= helpContactView.nameText.validate();
        allValid &= helpContactView.emailText.validate();
        allValid &= helpContactView.commentText.validate();

        if (allValid)
        {
            UIUtils.dismissKeyboard(getActivity());
            sendContactFormData(helpContactView.nameText.getString(), helpContactView.emailText.getString(), helpContactView.commentText.getString(), this.associatedNode);
        }
        else
        {
            showToast(R.string.ensure_fields_valid);
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

    private void returnToBookingDetails(String bookingId, String bookingType)
    {
        Bundle arguments = new Bundle();
        arguments.putString(BundleKeys.BOOKING_ID, bookingId);
        arguments.putString(BundleKeys.BOOKING_TYPE, bookingType);
        HandyEvent.NavigateToTab event = new HandyEvent.NavigateToTab(MainViewTab.DETAILS, arguments);
        bus.post(event);
    }

    //Event Listeners
    @Subscribe
    public void onReceiveNotifyHelpContactSuccess(HandyEvent.ReceiveNotifyHelpContactSuccess event)
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        bus.post(new LogEvent.AddLogEvent(mEventLogFactory.createHelpContactFormSubmittedLog(
                path, associatedNode.getId(), associatedNode.getLabel())));
        if (bookingId == null || bookingId.isEmpty())
        {
            returnToJobsScreen();
        }
        else
        {
            returnToBookingDetails(bookingId, bookingType);
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

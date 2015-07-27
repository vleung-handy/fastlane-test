package com.handy.portal.help;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.handy.portal.R;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.constant.MainViewTab;
import com.handy.portal.data.DataManager;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.manager.LoginManager;
import com.handy.portal.ui.fragment.InjectedFragment;
import com.handy.portal.ui.widget.BasicInputTextView;
import com.handy.portal.ui.widget.EmailInputTextView;
import com.handy.portal.ui.widget.FirstNameInputTextView;
import com.squareup.otto.Subscribe;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.mime.TypedByteArray;
import retrofit.mime.TypedInput;

public final class HelpContactFragment extends InjectedFragment
{
    public static final String EXTRA_HELP_NODE = "com.handy.handy.EXTRA_HELP_NODE";
    public static final String EXTRA_HELP_PATH = "com.handy.handy.EXTRA_HELP_PATH";
    private static final String HELP_CONTACT_FORM_DISPOSITION = "help-contact-form-disposition";
    private static final String HELP_CONTACT_FORM_NAME = "name";
    private static final String HELP_CONTACT_FORM_EMAIL = "email";
    private static final String HELP_CONTACT_FORM_DESCRIPTION = "description";
    private static final String HELP_CONTACT_FORM_PATH = "path";
    private static final String SALESFORCE_DATA_WRAPPER_KEY = "salesforce_data";

    @InjectView(R.id.contact_page_content)
    RelativeLayout contactPageContent;

    @InjectView(R.id.nav_content)
    RelativeLayout navContent;

    private HelpNode associatedNode;
    private String path;

    private View associatedView;

    private HelpContactView contactView;
    private HelpNodeNavView navView;


    @Inject
    LoginManager loginManager;

    @Inject
    DataManager dataManager;

    public static HelpContactFragment newInstance(final HelpNode node, final String path)
    {
        final HelpContactFragment fragment = new HelpContactFragment();
        final Bundle args = new Bundle();
        args.putParcelable(EXTRA_HELP_NODE, node);
        args.putString(EXTRA_HELP_PATH, path);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public final void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        associatedNode = getArguments().getParcelable(EXTRA_HELP_NODE);
        path = getArguments().getString(EXTRA_HELP_PATH);

    }


    @Override
    protected List<String> requiredArguments()
    {
        List<String> requiredArguments = new ArrayList<>();
        requiredArguments.add(BundleKeys.HELP_NODE);
        requiredArguments.add(BundleKeys.PATH);
        return requiredArguments;
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
            System.err.println("Can not construct Help Contact Form");
            return view;
        }

        this.associatedNode = getArguments().getParcelable(BundleKeys.HELP_NODE);
        this.path = getArguments().getString(BundleKeys.PATH);

        contactView = new HelpContactView();
        navView = new HelpNodeNavView();

        constructViews(this.associatedNode);

        assignClickListeners(view);

        associatedView = view;

        return view;
    }

    private void assignClickListeners(View view)
    {
        ImageView backImage = (ImageView) view.findViewById(R.id.back_img);
        Button sendMessageButton = (Button) view.findViewById(R.id.send_message_button);

        sendMessageButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                onSendMessageButtonClick();
            }
        });

        final Activity activity = this.getActivity();
        backImage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                activity.onBackPressed();
            }
        });
    }



    //construct the views
    private void constructViews(HelpNode node)
    {
        contactPageContent.removeAllViews();
        navContent.removeAllViews();

        contactView.constructView(node, contactPageContent, getActivity(), this);
        navView.constructView(node, navContent, getActivity());
    }

    private void onSendMessageButtonClick()
    {
        Boolean allValid = true;

        FirstNameInputTextView nameText = (FirstNameInputTextView) associatedView.findViewById(R.id.help_contact_user_name_text);
        EmailInputTextView emailText = (EmailInputTextView) associatedView.findViewById(R.id.help_contact_email_text);
        BasicInputTextView commentText = (BasicInputTextView) associatedView.findViewById(R.id.help_contact_comment_text);

        allValid &= nameText.validate();
        allValid &= emailText.validate();
        allValid &= commentText.validate();

        if (allValid)
        {
            sendContactFormData(nameText.getString(), emailText.getString(), commentText.getString(), this.associatedNode);
        }
    }

    private void sendContactFormData(String name, String email, String comment, HelpNode associatedNode)
    {
        HashMap<String, String> contactFormInfo = parseHelpNode(associatedNode);

        //add contact form information
        contactFormInfo.put(HELP_CONTACT_FORM_NAME, name);
        contactFormInfo.put(HELP_CONTACT_FORM_EMAIL, email);
        contactFormInfo.put(HELP_CONTACT_FORM_DESCRIPTION, comment);
        contactFormInfo.put(HELP_CONTACT_FORM_PATH, path);

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

    private HashMap<String, String> parseHelpNode(HelpNode node)
    {
        HashMap<String, String> params = new HashMap<String, String>();
        for (HelpNode childNode : node.getChildren())
        {
            if (childNode.getType().equals(HELP_CONTACT_FORM_DISPOSITION))
            {
                params.put(childNode.getLabel(), childNode.getContent());
            }
        }
        return params;
    }

    private void returnToHomeScreen()
    {
        bus.post(new HandyEvent.NavigateToTab(MainViewTab.JOBS));
    }

    @Subscribe
    public void onReceiveNotifyHelpContactSuccess(HandyEvent.ReceiveNotifyHelpContactSuccess event)
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        returnToHomeScreen();
        showToast(getString(R.string.contact_received));
    }

    @Subscribe
    public void onReceiveNotifyHelpContactError(HandyEvent.ReceiveNotifyHelpContactError event)
    {
        //TODO: Get them to resubmit?
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        showToast(getString(R.string.an_error_has_occurred));
    }

}

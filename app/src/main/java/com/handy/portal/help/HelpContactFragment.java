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
import com.handy.portal.analytics.Mixpanel;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.data.DataManager;
import com.handy.portal.manager.LoginManager;
import com.handy.portal.ui.fragment.InjectedFragment;
import com.handy.portal.ui.widget.BasicInputTextView;
import com.handy.portal.ui.widget.EmailInputTextView;
import com.handy.portal.ui.widget.FirstNameInputTextView;

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


    @Inject
    Mixpanel mixpanel;


    @InjectView(R.id.contact_page_content)
    RelativeLayout contactPageContent;

    @InjectView(R.id.nav_content)
    RelativeLayout navContent;


//    @InjectView(R.id.send_message_button) Button sendMessageButton;
//    @InjectView(R.id.user_name_text) FirstNameInputTextView nameText;
//    @InjectView(R.id.email_text) EmailInputTextView emailText;
//    @InjectView(R.id.comment_text) BasicInputTextView commentText;
//    @InjectView(R.id.close_img) ImageView closeImage;
//    @InjectView(R.id.back_img) ImageView backImage;
//
//    @InjectView(R.id.name_layout) ViewGroup nameLayout;
//    @InjectView(R.id.email_layout) ViewGroup emailLayout;
    ;
    private HelpNode associatedNode;
    private String path;

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
            return view;
        }

        this.associatedNode = getArguments().getParcelable(BundleKeys.HELP_NODE);
        this.path = getArguments().getString(BundleKeys.PATH);

        constructViews(this.associatedNode);

//Link up onclicks
        ImageView closeImage = (ImageView) view.findViewById(R.id.close_img);
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

        closeImage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                returnToHomeScreen();
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

        return view;
    }


    //construct the views
    private void constructViews(HelpNode node)
    {
        contactPageContent.removeAllViews();
        navContent.removeAllViews();

        HelpNodeContactViewConstructor constructor = new HelpNodeContactViewConstructor();
        constructor.constructView(node, contactPageContent, getActivity(), this);

        HelpNodeNavViewConstructor navViewConstructor = new HelpNodeNavViewConstructor();
        navViewConstructor.constructView(node, navContent, getActivity());
    }

    private void prepopulateUserData()
    {
//        if(this.userManager.getCurrentUser() != null)
//        {
//            this.nameText.setText(this.userManager.getCurrentUser().getFullName());
//            this.emailText.setText(this.userManager.getCurrentUser().getEmail());
//
//            //Hide the name and email fields if they are prepopulated so the user can not alter them
//                //Validating them off prepopulated data, not hiding if the prepop data would prevent validation
//            if(nameText.validate()) {
//                this.nameLayout.setVisibility(View.GONE);
//            }
//            if(emailText.validate()) {
//                this.emailLayout.setVisibility(View.GONE);
//            }
//        }
    }

    private void onSendMessageButtonClick()
    {
        Boolean allValid = true;
//        allValid &= nameText.validate();
//        allValid &= emailText.validate();
//        allValid &= commentText.validate();


        FirstNameInputTextView nameText = (FirstNameInputTextView) getActivity().findViewById(R.id.help_contact_user_name_text);
        EmailInputTextView emailText = (EmailInputTextView) getActivity().findViewById(R.id.help_contact_email_text);
        BasicInputTextView commentText = (BasicInputTextView) getActivity().findViewById(R.id.help_contact_comment_text);

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

        dataManager.createHelpCase(body, createCaseCallback);
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
//        final Intent toHomeScreenIntent = new Intent(getActivity(), ServiceCategoriesActivity.class);
//        toHomeScreenIntent.addFlags((Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
//        startActivity(toHomeScreenIntent);
    }

    private DataManager.Callback<Void> createCaseCallback = new DataManager.Callback<Void>()
    {
        @Override
        public void onSuccess(final Void v)
        {
            if (!allowCallbacks) return;
            //progressDialog.dismiss();

            returnToHomeScreen();

            toast.setText(getString(R.string.contact_received));
            toast.show();

//            mixpanel.trackEventHelpCenterSubmitTicket(Integer
//                    .toString(associatedNode.getId()), associatedNode.getLabel());
        }

        @Override
        public void onError(final DataManager.DataManagerError error)
        {
            if (!allowCallbacks) return;
//            progressDialog.dismiss();
//            dataManagerErrorHandler.handleError(getActivity(), error);
        }
    };
}

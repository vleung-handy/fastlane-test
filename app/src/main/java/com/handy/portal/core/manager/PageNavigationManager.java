package com.handy.portal.core.manager;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;

import com.crashlytics.android.Crashlytics;
import com.handy.portal.R;
import com.handy.portal.core.constant.BundleKeys;
import com.handy.portal.core.constant.MainViewPage;
import com.handy.portal.core.constant.TransitionStyle;
import com.handy.portal.core.event.HandyEvent;
import com.handy.portal.core.event.NavigationEvent;
import com.handy.portal.deeplink.DeeplinkMapper;
import com.handy.portal.deeplink.DeeplinkUtils;
import com.handy.portal.library.ui.fragment.dialog.TransientOverlayDialogFragment;
import com.handy.portal.logger.handylogger.model.DeeplinkLog;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

public class PageNavigationManager {
    private final EventBus mBus;

    @Inject
    public PageNavigationManager(final EventBus bus) {
        mBus = bus;
    }

    /**
     * NOTE: as the name suggests, this method is only to be used when the given deeplink
     * data bundle is NOT derived from a Uri. reason is that we want to keep the logging logic
     * <p>
     * making this a direct call because we specifically only want THIS manager
     * to handle "deeplinks" which are specific to the logic in this manager
     * and are not traditional Android deeplinks
     * <p>
     * NOTE: cannot cleanly consolidate with the handling
     * of the deeplink data bundle in handleDeeplinkUrl
     * because of logging requirements and the way the log classes are currently structured
     */
    public void handleNonUriDerivedDeeplinkDataBundle(
            @NonNull final FragmentManager fragmentManager,
            @Nullable final Bundle deeplinkDataBundle,
            @DeeplinkLog.Source.DeeplinkSource final String deeplinkSource) {
        if (deeplinkDataBundle != null) {
            final String deeplink = deeplinkDataBundle.getString(BundleKeys.DEEPLINK);
            if (!TextUtils.isEmpty(deeplink)) {
                final MainViewPage page = DeeplinkMapper.getPageForDeeplink(deeplink);
                if (page != null) {
                    mBus.post(new DeeplinkLog.Processed(
                            deeplinkSource,
                            deeplinkDataBundle
                    ));
                    navigateToPage(fragmentManager, page, deeplinkDataBundle, null, false);
                }
                else {
                    mBus.post(new DeeplinkLog.Ignored(
                            deeplinkSource,
                            DeeplinkLog.Ignored.Reason.UNRECOGNIZED,
                            deeplinkDataBundle
                    ));
                }
            }
        }
    }

    /**
     * see notes on {@link #handleNonUriDerivedDeeplinkDataBundle(Bundle, String)}
     *
     * @param deeplinkSource
     * @param deeplinkUrl
     */
    public void handleDeeplinkUrl(
            @Nullable FragmentManager fragmentManager,
            @DeeplinkLog.Source.DeeplinkSource String deeplinkSource,
            @NonNull String deeplinkUrl) {
        final Uri deeplinkUri = Uri.parse(deeplinkUrl);
        final Bundle deeplinkDataBundle = DeeplinkUtils.createDeeplinkBundleFromUri(deeplinkUri);
        /*
        not consolidating this part with handleNonUriDerivedDeeplinkDataBundle because logging is different
         */
        if (deeplinkDataBundle != null) {
            /*
            TODO don't know why the Opened log event is being triggered here instead of on the actual click
             */
            mBus.post(new DeeplinkLog.Opened(deeplinkSource, deeplinkUri));
            final String deeplink = deeplinkDataBundle.getString(BundleKeys.DEEPLINK);
            if (!TextUtils.isEmpty(deeplink)) {
                final MainViewPage page = DeeplinkMapper.getPageForDeeplink(deeplink);
                if (page != null) {

                    mBus.post(new DeeplinkLog.Processed(
                            deeplinkSource,
                            deeplinkUri
                    ));
                    navigateToPage(fragmentManager, page, deeplinkDataBundle, null, !page.isTopLevel());
                    //TODO PortalWebViewClient didn't use !page.isTopLevel() to determine whether to add to back stack. check if OK
                }
                else {
                    mBus.post(new DeeplinkLog.Ignored(
                            deeplinkSource,
                            DeeplinkLog.Ignored.Reason.UNRECOGNIZED,
                            deeplinkUri
                    ));
                }
            }
        }
        else if (deeplinkUri != null) {
            mBus.post(new DeeplinkLog.Ignored(
                    deeplinkSource,
                    DeeplinkLog.Ignored.Reason.UNRECOGNIZED,
                    deeplinkUri
            ));
        }
    }

    public void navigateToPage(
            @Nullable FragmentManager fragmentManager,
            @NonNull MainViewPage newPage,
            @Nullable Bundle arguments,
            @Nullable TransitionStyle transitionStyle,
            boolean addToBackStack
    ) {
        if (arguments == null) {
            arguments = new Bundle();
        }

        if (transitionStyle == null) {
            transitionStyle = TransitionStyle.NATIVE_TO_NATIVE;
        }

        Fragment newFragment;
        try {
            newFragment = (Fragment) newPage.getClassType().newInstance();
        }
        catch (Exception e) {
            Crashlytics.logException(new RuntimeException("Error instantiating fragment class", e));
            return;
        }
        newFragment.setArguments(arguments);

        mBus.post(new HandyEvent.Navigation(newPage.toString().toLowerCase()));

        switchFragment(fragmentManager, newFragment, transitionStyle, addToBackStack);

        mBus.post(new NavigationEvent.SelectPage(newPage));
    }

    public void switchFragment(
            @Nullable FragmentManager fragmentManager,
            Fragment newFragment,
            TransitionStyle transitionStyle,
            boolean addToBackStack
    ) {
        if (fragmentManager == null) { return; }
        if (!addToBackStack) {
            fragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }

        FragmentTransaction transaction = fragmentManager.beginTransaction();

        //Animate the transition, animations must come before the .replace call
        if (transitionStyle != null) {
            transaction.setCustomAnimations(
                    transitionStyle.getIncomingAnimId(),
                    transitionStyle.getOutgoingAnimId(),
                    transitionStyle.getPopIncomingAnimId(),
                    transitionStyle.getPopOutgoingAnimId());

            //Runs async, covers the transition
            if (transitionStyle.shouldShowOverlay()) {
                TransientOverlayDialogFragment overlayDialogFragment =
                        TransientOverlayDialogFragment.newInstance(
                                R.anim.overlay_fade_in_then_out,
                                R.drawable.ic_success_circle,
                                transitionStyle.getOverlayStringId());
                overlayDialogFragment.show(fragmentManager, "overlay dialog fragment");
            }
        }

        transaction.replace(R.id.main_container, newFragment);
        if (addToBackStack) {
            transaction.addToBackStack(null);
        }
        else {
            transaction.disallowAddToBackStack();
        }
        transaction.commit();
    }
}

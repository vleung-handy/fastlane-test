package com.handy.portal.clients;

import android.app.Application;

import com.handy.portal.clients.ui.adapter.RequestedJobsRecyclerViewAdapter;
import com.handy.portal.clients.ui.fragment.ClientConversationsFragment;
import com.handy.portal.clients.ui.fragment.ClientsFragment;
import com.handy.portal.clients.ui.fragment.ProRequestedJobsFragment;
import com.handy.portal.clients.ui.fragment.dialog.RequestDismissalReasonsDialogFragment;
import com.handy.portal.clients.ui.fragment.dialog.SwapBookingClaimDialogFragment;
import com.handybook.shared.LayerHelper;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.RestAdapter;

@Module(
        library = true,
        complete = false,
        injects = {
                ClientsFragment.class,
                ProRequestedJobsFragment.class,
                RequestDismissalReasonsDialogFragment.class,
                RequestedJobsRecyclerViewAdapter.class,
                SwapBookingClaimDialogFragment.class,
                ClientConversationsFragment.class,
        })
public final class ClientsModule
{
    @Singleton
    @Provides
    final LayerHelper provideLayerHelper(final Application application,
                                         final RestAdapter restAdapter)
    {
        return null;
        // FIXME: Uncomment the following line when feature is ready
        // return HandyLayer.init(restAdapter, application);
    }
}

package com.handy.portal.clients;

import com.handy.portal.clients.ui.adapter.RequestedJobsRecyclerViewAdapter;
import com.handy.portal.clients.ui.fragment.ClientConversationsFragment;
import com.handy.portal.clients.ui.fragment.ClientsFragment;
import com.handy.portal.clients.ui.fragment.ProRequestedJobsFragment;
import com.handy.portal.clients.ui.fragment.dialog.RequestDismissalReasonsDialogFragment;
import com.handy.portal.clients.ui.fragment.dialog.SwapBookingClaimDialogFragment;
import com.handybook.shared.core.HandyLibrary;
import com.handybook.shared.layer.LayerHelper;

import dagger.Module;
import dagger.Provides;

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
    @Provides
    final LayerHelper provideLayerHelper()
    {
        return HandyLibrary.getInstance().getLayerHelper();
    }
}

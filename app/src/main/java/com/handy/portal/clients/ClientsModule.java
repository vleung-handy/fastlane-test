package com.handy.portal.clients;

import com.handy.portal.clients.ui.adapter.RequestedJobsRecyclerViewAdapter;
import com.handy.portal.clients.ui.fragment.dialog.SwapBookingClaimDialogFragment;
import com.handy.portal.clients.ui.fragment.ProRequestedJobsFragment;
import com.handy.portal.clients.ui.fragment.dialog.RequestDismissalReasonsDialogFragment;

import dagger.Module;

@Module(
        library = true,
        complete = false,
        injects = {
                ProRequestedJobsFragment.class,
                RequestDismissalReasonsDialogFragment.class,
                RequestedJobsRecyclerViewAdapter.class,
                SwapBookingClaimDialogFragment.class,
        })
public final class ClientsModule
{
}

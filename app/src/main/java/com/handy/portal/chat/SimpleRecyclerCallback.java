package com.handy.portal.chat;

import android.util.Log;

import com.layer.sdk.query.RecyclerViewController;

/**
 * Created by jtse on 9/28/16.
 */
public abstract class SimpleRecyclerCallback implements RecyclerViewController.Callback
{

    private static final String TAG = "SimpleRecyclerCallback";

    @Override
    public void onQueryDataSetChanged(final RecyclerViewController recyclerViewController)
    {
        Log.d(
                TAG,
                "onQueryDataSetChanged() called with: recyclerViewController = [" + recyclerViewController + "]"
        );
    }

    @Override
    public void onQueryItemChanged(final RecyclerViewController recyclerViewController, final int i)
    {
        Log.d(
                TAG,
                "onQueryItemChanged() called with: recyclerViewController = [" + recyclerViewController + "], i = [" + i + "]"
        );
    }

    @Override
    public void onQueryItemRangeChanged(
            final RecyclerViewController recyclerViewController,
            final int i,
            final int i1
    )
    {
        Log.d(
                TAG,
                "onQueryItemRangeChanged() called with: recyclerViewController = [" + recyclerViewController + "], i = [" + i + "], i1 = [" + i1 + "]"
        );
    }

    @Override
    public void onQueryItemInserted(
            final RecyclerViewController recyclerViewController,
            final int i
    )
    {
        Log.d(
                TAG,
                "onQueryItemInserted() called with: recyclerViewController = [" + recyclerViewController + "], i = [" + i + "]"
        );
    }

    @Override
    public void onQueryItemRangeInserted(
            final RecyclerViewController recyclerViewController,
            final int i,
            final int i1
    )
    {
        Log.d(
                TAG,
                "onQueryItemRangeInserted() called with: recyclerViewController = [" + recyclerViewController + "], i = [" + i + "], i1 = [" + i1 + "]"
        );
    }

    @Override
    public void onQueryItemRemoved(final RecyclerViewController recyclerViewController, final int i)
    {
        Log.d(
                TAG,
                "onQueryItemRemoved() called with: recyclerViewController = [" + recyclerViewController + "], i = [" + i + "]"
        );
    }

    @Override
    public void onQueryItemRangeRemoved(
            final RecyclerViewController recyclerViewController,
            final int i,
            final int i1
    )
    {
        Log.d(
                TAG,
                "onQueryItemRangeRemoved() called with: recyclerViewController = [" + recyclerViewController + "], i = [" + i + "], i1 = [" + i1 + "]"
        );
    }

    @Override
    public void onQueryItemMoved(
            final RecyclerViewController recyclerViewController,
            final int i,
            final int i1
    )
    {
        Log.d(
                TAG,
                "onQueryItemMoved() called with: recyclerViewController = [" + recyclerViewController + "], i = [" + i + "], i1 = [" + i1 + "]"
        );
    }
}

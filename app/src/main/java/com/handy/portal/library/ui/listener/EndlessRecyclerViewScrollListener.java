package com.handy.portal.library.ui.listener;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

public abstract class EndlessRecyclerViewScrollListener extends RecyclerView.OnScrollListener {
    // Default minimum amount of items to have below your current scroll position
    private static final int VISIBLE_THRESHOLD_PER_SPAN_COUNT = 5;
    // Sets the starting page index
    private static final int STARTING_PAGE_INDEX = 0;

    // The minimum amount of items to have below your current scroll position
    // before loading more.
    private int mVisibleThreshold;
    // The current offset index of data you have loaded
    private int mCurrentPage = 0;
    // The total number of items in the dataset after the last load
    private int mPreviousTotalItemCount = 0;
    // True if we are still waiting for the last set of data to load.
    private boolean mLoading = true;

    RecyclerView.LayoutManager mLayoutManager;

    public EndlessRecyclerViewScrollListener(LinearLayoutManager layoutManager) {
        mLayoutManager = layoutManager;
        mVisibleThreshold = VISIBLE_THRESHOLD_PER_SPAN_COUNT;
    }

    public EndlessRecyclerViewScrollListener(GridLayoutManager layoutManager) {
        mLayoutManager = layoutManager;
        mVisibleThreshold = VISIBLE_THRESHOLD_PER_SPAN_COUNT * layoutManager.getSpanCount();
    }

    public EndlessRecyclerViewScrollListener(StaggeredGridLayoutManager layoutManager) {
        mLayoutManager = layoutManager;
        mVisibleThreshold = VISIBLE_THRESHOLD_PER_SPAN_COUNT * layoutManager.getSpanCount();
    }

    public int getLastVisibleItem(int[] lastVisibleItemPositions) {
        int maxSize = 0;
        for (int i = 0; i < lastVisibleItemPositions.length; i++) {
            if (i == 0) {
                maxSize = lastVisibleItemPositions[i];
            }
            else if (lastVisibleItemPositions[i] > maxSize) {
                maxSize = lastVisibleItemPositions[i];
            }
        }
        return maxSize;
    }

    // This happens many times a second during a scroll, so be wary of the code you place here.
    // We are given a few useful parameters to help us work out if we need to load some more data,
    // but first we check if we are waiting for the previous load to finish.
    @Override
    public void onScrolled(RecyclerView view, int dx, int dy) {
        int lastVisibleItemPosition = 0;
        int totalItemCount = mLayoutManager.getItemCount();

        if (mLayoutManager instanceof StaggeredGridLayoutManager) {
            int[] lastVisibleItemPositions = ((StaggeredGridLayoutManager) mLayoutManager).findLastVisibleItemPositions(null);
            // get maximum element within the list
            lastVisibleItemPosition = getLastVisibleItem(lastVisibleItemPositions);
        }
        else if (mLayoutManager instanceof LinearLayoutManager) {
            lastVisibleItemPosition = ((LinearLayoutManager) mLayoutManager).findLastVisibleItemPosition();
        }
        else if (mLayoutManager instanceof GridLayoutManager) {
            lastVisibleItemPosition = ((GridLayoutManager) mLayoutManager).findLastVisibleItemPosition();
        }

        // If the total item count is zero and the previous isn't, assume the
        // list is invalidated and should be reset back to initial state
        if (totalItemCount < mPreviousTotalItemCount) {
            mCurrentPage = STARTING_PAGE_INDEX;
            mPreviousTotalItemCount = totalItemCount;
            if (totalItemCount == 0) {
                mLoading = true;
            }
        }
        // If it’s still loading, we check to see if the dataset count has
        // changed, if so we conclude it has finished loading and update the current page
        // number and total item count.
        if (mLoading && (totalItemCount > mPreviousTotalItemCount)) {
            mLoading = false;
            mPreviousTotalItemCount = totalItemCount;
        }

        // If it isn’t currently loading, we check to see if we have breached
        // the mVisibleThreshold and need to reload more data.
        // If we do need to reload some more data, we execute onLoadMore to fetch the data.
        // threshold should reflect how many total columns there are too
        if (!mLoading && (lastVisibleItemPosition + mVisibleThreshold) > totalItemCount) {
            mCurrentPage++;
            onLoadMore(mCurrentPage, totalItemCount);
            mLoading = true;
        }
    }

    // Defines the process for actually loading more data based on page
    public abstract void onLoadMore(int page, int totalItemsCount);
}

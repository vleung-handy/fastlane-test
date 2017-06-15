package com.handy.portal.library.ui.listener;

import android.support.v4.view.ViewCompat;
import android.widget.AbsListView;

/**
 * fixme need to test. don't know if this works
 */
public abstract class OnScrollToListViewBottomListener implements AbsListView.OnScrollListener {

    private int mCurrentFirstVisibleItemIndex = -1;
    private int mCurrentVisibleItemCount = 0;
    private int mTotalItemCount = 0;

    @Override
    public void onScrollStateChanged(final AbsListView view, final int scrollState) {
        if (scrollState == SCROLL_STATE_IDLE) {
            //done scrolling. check if at bottom
            //mCurrentVisibleItemCount is never 0
            int lastVisibleItemIndex = mCurrentFirstVisibleItemIndex + mCurrentVisibleItemCount - 1;
            if (lastVisibleItemIndex == mTotalItemCount - 1
                    && !ViewCompat.canScrollVertically(view, ViewCompat.SCROLL_AXIS_VERTICAL)) {
                onScrollToBottom();
            }
        }
    }

    @Override
    public void onScroll(final AbsListView view, final int firstVisibleItem, final int visibleItemCount, final int totalItemCount) {
        mCurrentFirstVisibleItemIndex = firstVisibleItem;
        mCurrentVisibleItemCount = visibleItemCount;
        mTotalItemCount = totalItemCount;
    }

    public abstract void onScrollToBottom();
}

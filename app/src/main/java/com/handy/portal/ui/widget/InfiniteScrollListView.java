package com.handy.portal.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AbsListView;
import android.widget.ListView;

public class InfiniteScrollListView extends ListView implements AbsListView.OnScrollListener //TODO: WIP. refine
{
    private OnScrollToBottomListener onScrollToBottomListener;
    private int previousLastItem = -1; //prevent "on scrolled to bottom function" from being called more than once for the current list

    public InfiniteScrollListView(final Context context)
    {
        super(context);
    }

    public InfiniteScrollListView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
    }

    public InfiniteScrollListView(final Context context, final AttributeSet attrs, final int defStyle)
    {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onFinishInflate()
    {
        super.onFinishInflate();
        setOnScrollListener(this);
    }

    public interface OnScrollToBottomListener{

        void onScrollToBottom();
    }

    public void resetInfiniteScroll()
    {
        previousLastItem = -1;
        // should reset when data items <= previousLastItem are modified (so not including appended items)
        // i.e. if list is reset to data with same amount of items w/o change in scroll position, so previousLastItem = lastItem, it is possible scroll to bottom won't be detected???
    }

    public void setOnScrollToBottomListener(OnScrollToBottomListener onScrollToBottomListener)
    {
        this.onScrollToBottomListener = onScrollToBottomListener;
    }

    private void notifyListenersOnScrollToBottom()
    {
        if(onScrollToBottomListener!=null)
        {
            onScrollToBottomListener.onScrollToBottom();
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState)
    {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
    {
        if(totalItemCount == 0 || visibleItemCount == 0) return; //don't do anything if there are no items in the list

        int lastItem = firstVisibleItem + visibleItemCount;
        if (lastItem == totalItemCount && lastItem!=previousLastItem) //scrolled to bottom!
        {
            previousLastItem = lastItem;
            notifyListenersOnScrollToBottom();
        }
    }
}

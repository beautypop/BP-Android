package com.beautypop.listener;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

public abstract class EndlessScrollListener extends RecyclerView.OnScrollListener {
    public static String TAG = EndlessScrollListener.class.getName();

    private int previousTotal = 0; // The total number of items in the dataset after the last load
    private boolean loading = true; // True if we are still waiting for the last set of data to load.
    private int visibleThreshold = 5; // The minimum amount of items to have below your current scroll position before loading more.
    int firstVisibleItem, visibleItemCount, totalItemCount;

    private boolean isOffsetItemTag = true;

    private int currentPage = 0;

    private static final int HIDE_THRESHOLD = 20;

    private static boolean scrollReset = false;

    private int mScrolledDistance = 0;
    private boolean mControlsVisible = true;

    private LinearLayoutManager mLinearLayoutManager;

    public EndlessScrollListener(LinearLayoutManager linearLayoutManager) {
        this(linearLayoutManager, true);
    }

    public EndlessScrollListener(LinearLayoutManager linearLayoutManager, boolean isOffsetItemTag) {
        this.mLinearLayoutManager = linearLayoutManager;
        this.isOffsetItemTag = isOffsetItemTag;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        visibleItemCount = recyclerView.getChildCount();
        totalItemCount = mLinearLayoutManager.getItemCount();
        firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition();
        //int totalItems = totalItemCount - previousTotal;
        if (loading) {
            if (totalItemCount > previousTotal) {
                loading = false;
                previousTotal = totalItemCount;
            }
        }
        if (!loading &&
                (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
            // End has been reached

            // Do something

            // Need to check for accuracy.
            if (recyclerView.getChildAt(visibleItemCount - 1) == null) {
                return;
            }

            currentPage++;
            Long offset = isOffsetItemTag? (Long) recyclerView.getChildAt(visibleItemCount - 1).getTag() : currentPage;
            if (offset != null) {
                onLoadMore(offset);
            }

            loading = true;
        }

        // show / hide controls trigger
        int threshold = HIDE_THRESHOLD;
        if (firstVisibleItem == 0) {
            threshold = HIDE_THRESHOLD * 5;
        }

        if (scrollReset) {
            scrollReset();
        }

        //Log.d(TAG, "mControlsVisible="+mControlsVisible+" mScrolledDistance="+mScrolledDistance);

        if (mScrolledDistance > threshold && mControlsVisible) {
            //Log.d(TAG, "onScrollDown()");
            onScrollDown();
            mControlsVisible = false;
            mScrolledDistance = 0;
        } else if (mScrolledDistance < -threshold && !mControlsVisible) {
            //Log.d(TAG, "onScrollUp()");
            onScrollUp();
            mControlsVisible = true;
            mScrolledDistance = 0;
        }

        /*
        if (firstVisibleItem == 0) {
            if (!mControlsVisible) {
                onScrollUp();
                mControlsVisible = true;
            }
        } else {
            if (mScrolledDistance > threshold && mControlsVisible) {
                onScrollDown();
                mControlsVisible = false;
                mScrolledDistance = 0;
            } else if (mScrolledDistance < -threshold && !mControlsVisible) {
                onScrollUp();
                mControlsVisible = true;
                mScrolledDistance = 0;
            }
        }
        */

        if ((mControlsVisible && dy > 0) || (!mControlsVisible && dy < 0)) {
            mScrolledDistance += dy;
        }
    }

    public static void setScrollReset() {
        scrollReset = true;
    }

    public void scrollReset() {
        //Log.d(TAG, "scrollReset()");
        mControlsVisible = true;
        mScrolledDistance = 0;
        scrollReset = false;
        currentPage = 0;
    }

    public abstract void onLoadMore(Long offset);
    public abstract void onScrollUp();
    public abstract void onScrollDown();
}
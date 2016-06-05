package com.beautypop.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.beautypop.R;

public class SellerFollowingFeedViewFragment extends FeedViewFragment {

    private static final String TAG = SellerFollowingFeedViewFragment.class.getName();

    private Button startFollowingButton;

    private static boolean refresh = false;

    private static SellerFollowingFeedViewFragment mInstance;

    public static void setRefreshFeed() {
        refresh = true;
    }

    public static boolean isRefreshFeed() {
        return refresh;
    }

    public static void refreshFeed() {
        if (mInstance != null) {
            mInstance.reloadFeed();
        }
        refresh = false;
    }

    public static synchronized SellerFollowingFeedViewFragment getInstance() {
        return mInstance;
    }

    @Override
    public boolean showSeller() {
        return true;
    }

    @Override
    protected FeedViewItemsLayout getFeedViewItemsLayout() {
        return FeedViewItemsLayout.TWO_COLUMNS;
    }

    @Override
    protected View getHeaderView(LayoutInflater inflater) {
        if (headerView == null) {
            headerView = inflater.inflate(R.layout.seller_following_feed_view_header, null);
        }
        return headerView;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        this.mInstance = this;

        startFollowingButton = (Button) headerView.findViewById(R.id.startFollowingButton);
        startFollowingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SellerMainFragment.selectRecommendedSellersTab();
            }
        });

        return view;
    }

    @Override
    protected void onScrollUp() {
        //MainActivity.getInstance().showToolbar(true);
    }

    @Override
    protected void onScrollDown() {
        //MainActivity.getInstance().showToolbar(false);
    }
}


package com.beautypop.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.beautypop.R;
import com.beautypop.util.SharedPreferencesUtil;

public class SellerFollowingFeedViewFragment extends FeedViewFragment {

    private static final String TAG = SellerFollowingFeedViewFragment.class.getName();

    private Button startFollowingButton;

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

        startFollowingButton = (Button) headerView.findViewById(R.id.startFollowingButton);
        startFollowingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SellerMainFragment.getInstance().getViewPager().setCurrentItem(1);
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


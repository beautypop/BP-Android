package com.beautypop.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import com.beautypop.R;
import com.beautypop.activity.MainActivity;
import com.beautypop.adapter.SellerListAdapter;
import com.beautypop.app.AppController;
import com.beautypop.app.TrackedFragment;
import com.beautypop.listener.InfiniteScrollListener;
import com.beautypop.util.DefaultValues;
import com.beautypop.util.ViewUtil;
import com.beautypop.viewmodel.SellerVM;
import com.yalantis.phoenix.PullToRefreshView;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SellerFeedFragment extends TrackedFragment {

    private static final String TAG = SellerFeedFragment.class.getName();

    protected ListView listView;

    protected ImageView backImage;
    protected PullToRefreshView pullListView;

    protected List<SellerVM> items;
    protected SellerListAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.seller_feed_fragment, container, false);

        listView = (ListView) view.findViewById(R.id.sellerList);

        items = new ArrayList<>();
        adapter = new SellerListAdapter(getActivity(), items);
        listView.setAdapter(adapter);

        // pull refresh
        pullListView = (PullToRefreshView) view.findViewById(R.id.pull_to_refresh);
        if (pullListView != null) {
            pullListView.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    pullListView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            pullListView.setRefreshing(false);
                            getSellers(0L);
                        }
                    }, DefaultValues.PULL_TO_REFRESH_DELAY);
                }
            });
        }

        attachEndlessScrollListener();

        getSellers(0L);

        return view;
    }

    protected void attachEndlessScrollListener() {
        int visibleThreshold = 5;
        listView.setOnScrollListener(new InfiniteScrollListener(visibleThreshold) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                getSellers(items.get(items.size() - 1).offset);
            }

            @Override
            public void onScrollUp() {
                //MainActivity.getInstance().showToolbar(false);
            }

            @Override
            public void onScrollDown() {
                //MainActivity.getInstance().showToolbar(true);
            }
        });
    }

    protected void getSellers(final long offset) {
        Log.d(TAG, "getSellers() offset="+offset);

        ViewUtil.showSpinner(getActivity());
        AppController.getApiService().getRecommendedSellersFeed(offset, new Callback<List<SellerVM>>() {
            @Override
            public void success(List<SellerVM> sellers, Response response) {
                Log.d(TAG, "getSellers: success size="+sellers.size());

                if (offset == 0L) {
                    items.clear();
                    adapter.notifyDataSetChanged();
                }

                items.addAll(sellers);
                adapter.notifyDataSetChanged();
                ViewUtil.stopSpinner(getActivity());
            }

            @Override
            public void failure(RetrofitError error) {
                ViewUtil.stopSpinner(getActivity());
                Log.e(TAG, "getSellers: failure", error);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (getChildFragmentManager() != null && getChildFragmentManager().getFragments() != null) {
            for (Fragment fragment : getChildFragmentManager().getFragments()) {
                if (fragment != null)
                    fragment.onActivityResult(requestCode, resultCode, data);
            }
        }
    }
}
package com.beautypop.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.beautypop.R;
import com.beautypop.app.AppController;
import com.beautypop.util.FeedFilter;
import com.beautypop.util.ViewUtil;
import com.beautypop.viewmodel.PostVMLite;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class FeedViewFragment extends AbstractFeedViewFragment {

    private static final String TAG = FeedViewFragment.class.getName();

    protected Callback<List<PostVMLite>> feedCallback;

    @Override
    protected FeedViewItemsLayout getFeedViewItemsLayout() {
        return FeedViewItemsLayout.THREE_COLUMNS;
    }

    @Override
    protected void initFeedFilter() {
        FeedFilter.FeedType feedType = getFeedType(
                getArguments().getString(ViewUtil.BUNDLE_KEY_FEED_TYPE));
        FeedFilter.ConditionType conditionType = getFeedFilterConditionType(
                getArguments().getString(ViewUtil.BUNDLE_KEY_FEED_FILTER_CONDITION_TYPE));
        Long objId = getArguments().getLong(ViewUtil.BUNDLE_KEY_ID, -1);
        setFeedFilter(new FeedFilter(feedType, conditionType, objId));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        return view;
    }

    protected void initCallback() {
        feedCallback = new Callback<List<PostVMLite>>() {
            @Override
            public void success(final List<PostVMLite> posts, Response response) {
                loadFeedItemsToList(posts);
                ViewUtil.stopSpinner(getActivity());
            }

            @Override
            public void failure(RetrofitError error) {
                //setFooterText(R.string.list_loading_error);

				error.printStackTrace();

                String msg = getActivity().getString(R.string.list_loading_error);
                if (AppController.isUserAdmin()) {
                    msg += "\n[FeedFilter]\n"+
                            getFeedFilter().toString()+"\n[Error]\n"+
                            ViewUtil.getResponseBody(error.getResponse());
                }
                ViewUtil.alert(getActivity(), msg);
                Log.e(TAG, "getFeed: failure feedFilter=\n"+getFeedFilter().toString(), error);
                ViewUtil.stopSpinner(getActivity());
            }
        };
    }

    protected void loadFeed(Long offset, FeedFilter feedFilter) {
        if (feedFilter == null || feedFilter.feedType == null) {
            Log.w(TAG, "loadFeed: offset=" + offset + " with null key!!");
            return;
        }

        if (feedCallback == null) {
            initCallback();
        }

        Log.d(TAG, "loadFeed: offset=" + offset + " with key=" + feedFilter.feedType.name());
        switch (feedFilter.feedType) {
            case HOME_EXPLORE:
                AppController.getApiService().getHomeExploreFeed(
                        Long.valueOf(offset),
                        feedCallback);
                break;
            case HOME_FOLLOWING:
                AppController.getApiService().getHomeFollowingFeed(
                        Long.valueOf(offset),
                        feedCallback);
                break;
            case CATEGORY_POPULAR:
                AppController.getApiService().getCategoryPopularFeed(
                        Long.valueOf(offset),
                        feedFilter.objId,
                        feedFilter.conditionType.name(),
                        feedCallback);
                break;
            case CATEGORY_NEWEST:
                AppController.getApiService().getCategoryNewestFeed(
                        Long.valueOf(offset),
                        feedFilter.objId,
                        feedFilter.conditionType.name(),
                        feedCallback);
                break;
            case CATEGORY_PRICE_LOW_HIGH:
                AppController.getApiService().getCategoryPriceLowHighFeed(
                        Long.valueOf(offset),
                        feedFilter.objId,
                        feedFilter.conditionType.name(),
                        feedCallback);
                break;
            case CATEGORY_PRICE_HIGH_LOW:
                AppController.getApiService().getCategoryPriceHighLowFeed(
                        Long.valueOf(offset),
                        feedFilter.objId,
                        feedFilter.conditionType.name(),
                        feedCallback);
                break;
            case USER_POSTED:
                AppController.getApiService().getUserPostedFeed(
                        Long.valueOf(offset),
                        feedFilter.objId,
                        feedCallback);
                break;
            case USER_LIKED:
                AppController.getApiService().getUserLikedFeed(
                        Long.valueOf(offset),
                        feedFilter.objId,
                        feedCallback);
                break;
            default:
                Log.w(TAG, "loadFeed: unknown default case with key - " + feedFilter.feedType.name());
        }
    }

    @Override
    protected void onScrollUp() {
        // implemented in subclass
    }

    @Override
    protected void onScrollDown() {
        // implemented in subclass
    }
}
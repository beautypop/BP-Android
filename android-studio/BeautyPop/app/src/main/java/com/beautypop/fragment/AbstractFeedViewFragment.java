package com.beautypop.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.beautypop.R;
import com.beautypop.adapter.FeedViewAdapter;
import com.beautypop.app.NotificationCounter;
import com.beautypop.app.TrackedFragment;
import com.beautypop.listener.EndlessScrollListener;
import com.beautypop.util.DefaultValues;
import com.beautypop.util.FeedFilter;
import com.beautypop.util.ViewUtil;
import com.beautypop.viewmodel.PostVMLite;
import com.yalantis.phoenix.PullToRefreshView;

import org.parceler.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractFeedViewFragment extends TrackedFragment {

    private static final String TAG = AbstractFeedViewFragment.class.getName();

    private int RECYCLER_VIEW_COLUMN_SIZE = 3;

    private int TOP_MARGIN = ViewUtil.getRealDimension(DefaultValues.FEED_LAYOUT_3COL_TOP_MARGIN);
    private int BOTTOM_MARGIN = ViewUtil.getRealDimension(DefaultValues.FEED_LAYOUT_3COL_BOTTOM_MARGIN);
    private int SIDE_MARGIN = ViewUtil.getRealDimension(DefaultValues.FEED_LAYOUT_3COL_SIDE_MARGIN);
    private int LEFT_SIDE_MARGIN = ViewUtil.getRealDimension(DefaultValues.FEED_LAYOUT_3COL_LEFT_SIDE_MARGIN);
    private int RIGHT_SIDE_MARGIN = ViewUtil.getRealDimension(DefaultValues.FEED_LAYOUT_3COL_RIGHT_SIDE_MARGIN);

    protected RecyclerView feedView;
    protected FeedViewAdapter feedAdapter;
    protected GridLayoutManager layoutManager;

    protected FeedFilter feedFilter;
    protected List<PostVMLite> items;

    protected View headerView;
    protected View footerView;
    protected View noItemView;

    protected PullToRefreshView pullListView;

    protected boolean reload = false;

    public enum FeedViewItemsLayout {
        ONE_COLUMNS,
		TWO_COLUMNS,
        THREE_COLUMNS
    }

    public enum ItemChangedState {
        ITEM_UPDATED,
        ITEM_ADDED,
        ITEM_REMOVED
    }

    abstract protected FeedViewItemsLayout getFeedViewItemsLayout();

    abstract protected void loadFeed(Long offset, FeedFilter feedFilter);

    abstract protected void onScrollUp();

    abstract protected void onScrollDown();

    abstract protected void initFeedFilter();

    public boolean showSeller() {
        return false;
    }

    public boolean hasHeader() {
        return headerView != null;
    }

    public RecyclerView getFeedView() {
        return feedView;
    }

    protected FeedFilter getFeedFilter() {
        return feedFilter;
    }

    protected void setFeedFilter(FeedFilter feedFilter) {
        Log.d(TAG, "setFeedFilter: feedFilter\n" + feedFilter.toString());
        this.feedFilter = feedFilter;
    }

    protected View getHeaderView(LayoutInflater inflater) {
        return null;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.feed_view_fragment, container, false);

        pullListView = (PullToRefreshView) view.findViewById(R.id.pull_to_refresh);
        footerView = inflater.inflate(R.layout.list_loading_footer, null);

        items = new ArrayList<>();

        feedView = (RecyclerView) view.findViewById(R.id.feedView);
        //feedView.setHasFixedSize(true);

        // default is 3 columns layout
        if (FeedViewItemsLayout.TWO_COLUMNS.equals(getFeedViewItemsLayout())) {
            RECYCLER_VIEW_COLUMN_SIZE = 2;
            TOP_MARGIN = ViewUtil.getRealDimension(DefaultValues.FEED_LAYOUT_2COL_TOP_MARGIN);
            BOTTOM_MARGIN = ViewUtil.getRealDimension(DefaultValues.FEED_LAYOUT_2COL_BOTTOM_MARGIN);
            SIDE_MARGIN = ViewUtil.getRealDimension(DefaultValues.FEED_LAYOUT_2COL_SIDE_MARGIN);
            LEFT_SIDE_MARGIN = ViewUtil.getRealDimension(DefaultValues.FEED_LAYOUT_2COL_LEFT_SIDE_MARGIN);
            RIGHT_SIDE_MARGIN = ViewUtil.getRealDimension(DefaultValues.FEED_LAYOUT_2COL_RIGHT_SIDE_MARGIN);
        }

        feedView.addItemDecoration(
                new RecyclerView.ItemDecoration() {
                    @Override
                    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                        //int pos = parent.getChildAdapterPosition(view);
                        //int margin = getActivity().getResources().getDimensionPixelSize(R.dimen.feed_item_margin);

                        ViewUtil.FeedItemPosition feedItemPosition =
                                ViewUtil.getFeedItemPosition(view, RECYCLER_VIEW_COLUMN_SIZE, hasHeader());
                        if (feedItemPosition == ViewUtil.FeedItemPosition.HEADER) {
                            outRect.set(0, 0, 0, 0);
                        } else if (feedItemPosition == ViewUtil.FeedItemPosition.LEFT_COLUMN) {
                            outRect.set(LEFT_SIDE_MARGIN, TOP_MARGIN, SIDE_MARGIN, BOTTOM_MARGIN);
                        } else if (feedItemPosition == ViewUtil.FeedItemPosition.RIGHT_COLUMN) {
                            outRect.set(SIDE_MARGIN, TOP_MARGIN, RIGHT_SIDE_MARGIN, BOTTOM_MARGIN);
                        } else if (feedItemPosition == ViewUtil.FeedItemPosition.MIDDLE_COLUMN) {
                            outRect.set(SIDE_MARGIN, TOP_MARGIN, SIDE_MARGIN, BOTTOM_MARGIN);
                        }
                    }
                });

        // header
        headerView = getHeaderView(inflater);
        if (headerView != null) {
            noItemView = headerView.findViewById(R.id.noItemView);
        }

        // adapter
        feedAdapter = new FeedViewAdapter(getActivity(), items, getFeedViewItemsLayout(), headerView, showSeller());
        feedView.setAdapter(feedAdapter);

        // layout manager
        layoutManager = new GridLayoutManager(getActivity(), RECYCLER_VIEW_COLUMN_SIZE);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return feedAdapter.isHeader(position) ? layoutManager.getSpanCount() : 1;
            }
        });
        feedView.setLayoutManager(layoutManager);

        // pull refresh
        pullListView.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pullListView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pullListView.setRefreshing(false);
                        reloadFeed();
                        onRefreshView();
                    }
                }, DefaultValues.PULL_TO_REFRESH_DELAY);
            }
        });

        initFeedFilter();

        reloadFeed();

        return view;
    }

    public void reloadFeed() {
        reloadFeed(feedFilter);
    }

    protected void reloadFeed(FeedFilter feedFilter) {
        // reload already fired
        if (this.feedFilter != null &&
                this.feedFilter.equals(feedFilter) &&
                reload) {
            Log.w(TAG, "reloadFeed: reload already fired for filter\n"+feedFilter.toString());
            return;
        }

        if (feedFilter.feedType != null) {
            ViewUtil.showSpinner(getActivity());
            setFeedFilter(feedFilter);
            loadFeed(0L, feedFilter);
            attachEndlessScrollListener();
            reload = true;
        }
    }

    protected FeedFilter.FeedType getFeedType(String feedType) {
        if (StringUtils.isEmpty(feedType)) {
            Log.w(TAG, "getFeedType: null feedType!!");
            return null;
        }

        try {
            return FeedFilter.FeedType.valueOf(feedType);
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "getFeedType: Invalid feedType="+feedType, e);
        }
        return null;
    }

    protected FeedFilter.ConditionType getFeedFilterConditionType(String conditionType) {
        if (StringUtils.isEmpty(conditionType)) {
            return FeedFilter.ConditionType.ALL;
        }

        try {
            return FeedFilter.ConditionType.valueOf(conditionType);
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "getConditionType: Invalid conditionType="+conditionType, e);
        }
        return FeedFilter.ConditionType.ALL;
    }

    protected void attachEndlessScrollListener() {
        feedView.setOnScrollListener(new EndlessScrollListener(layoutManager) {
            @Override
            public void onLoadMore(Long offset) {
                loadFeed(offset, getFeedFilter());
            }

            @Override
            public void onScrollUp() {
                AbstractFeedViewFragment.this.onScrollUp();
            }

            @Override
            public void onScrollDown() {
                AbstractFeedViewFragment.this.onScrollDown();
            }
        });
    }

    protected void loadFeedItemsToList(List<PostVMLite> posts) {
        Log.d(TAG, "loadFeedItemsToList: size=" + posts.size()+"\n"+getFeedFilter().toString());

        if (reload) {
            clearFeedItems();
            ViewUtil.stopSpinner(getActivity());
            reload = false;
        }

        items.addAll(posts);
        feedAdapter.notifyDataSetChanged();

        if (noItemView != null) {
            if (feedAdapter.isEmpty()) {
                noItemView.setVisibility(View.VISIBLE);
            } else {
                noItemView.setVisibility(View.GONE);
            }
        }

        showFooter(false);
        if (posts == null || posts.size() == 0) {
            setFooterText(R.string.list_loaded_all);
        } else {
            setFooterText(R.string.list_loading);
        }
    }

    protected void clearFeedItems() {
        items.clear();
        feedAdapter.notifyDataSetChanged();
    }

    protected void onRefreshView() {
        NotificationCounter.refresh();
    }

    protected void setFooterText(int text) {
        //showFooter(true);
        //footerText.setText(text);
    }

    protected void showFooter(boolean show) {
        footerView.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: requestCode:" + requestCode + " resultCode:" + resultCode + " data:" + data);

        if (requestCode == ViewUtil.START_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK &&
                data != null && feedAdapter != null) {

            // changed state
            ItemChangedState itemChangedState = null;
            try {
                itemChangedState = Enum.valueOf(ItemChangedState.class, data.getStringExtra(ViewUtil.INTENT_RESULT_ITEM_CHANGED_STATE));
            } catch (Exception e) {
            }

            // item
            PostVMLite feedPost = null;
            Serializable obj = data.getSerializableExtra(ViewUtil.INTENT_RESULT_OBJECT);
            if (obj != null) {
                try {
                    feedPost = (PostVMLite) obj;
                } catch (ClassCastException e) {
                }
            }

            if (itemChangedState == ItemChangedState.ITEM_UPDATED) {
                int position = feedAdapter.getClickedPosition();
                if (position == -1 || feedAdapter.isEmpty()) {
                    return;
                }

                PostVMLite item = feedAdapter.getItem(position);
                if (feedPost != null) {
                    Log.d(TAG, "onActivityResult: feedAdapter ITEM_UPDATED=" + position + " post=" + feedPost.id);
                    item.title = feedPost.title;
                    item.price = feedPost.price;
                    item.sold = feedPost.sold;
                    item.isLiked = feedPost.isLiked;
                    item.numLikes = feedPost.numLikes;
                    feedAdapter.notifyItemChanged(position);
                }
            } else if (itemChangedState == ItemChangedState.ITEM_REMOVED) {
                int position = feedAdapter.getClickedPosition();
                if (position == -1 || feedAdapter.isEmpty()) {
                    return;
                }

                Log.d(TAG, "onActivityResult: feedAdapter ITEM_REMOVED="+position);
                feedAdapter.removeItem(position);
                feedAdapter.notifyItemRemoved(position);
            }  else if (itemChangedState == ItemChangedState.ITEM_ADDED) {
                if (feedPost != null) {
                    Log.d(TAG, "onActivityResult: feedAdapter ITEM_ADDED=0 post=" + feedPost.id);
                    items.add(0, feedPost);
                    feedAdapter.notifyItemInserted(0);
                }
            }
        }
    }
}

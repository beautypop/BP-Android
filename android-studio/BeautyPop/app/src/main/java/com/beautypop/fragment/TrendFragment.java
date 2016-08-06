package com.beautypop.fragment;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.beautypop.R;
import com.beautypop.adapter.TrendAdapter;
import com.beautypop.app.CategoryCache;
import com.beautypop.app.TrackedFragment;
import com.beautypop.util.DefaultValues;
import com.beautypop.util.ViewUtil;
import com.beautypop.viewmodel.CategoryVM;
import com.yalantis.phoenix.PullToRefreshView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class TrendFragment extends TrackedFragment {
    private static final String TAG = TrendFragment.class.getName();
	private int RECYCLER_VIEW_COLUMN_SIZE = 3;

	private int TOP_MARGIN = ViewUtil.getRealDimension(DefaultValues.FEED_LAYOUT_3COL_TOP_MARGIN);
	private int BOTTOM_MARGIN = ViewUtil.getRealDimension(DefaultValues.FEED_LAYOUT_3COL_BOTTOM_MARGIN);
	private int SIDE_MARGIN = ViewUtil.getRealDimension(DefaultValues.FEED_LAYOUT_3COL_SIDE_MARGIN);
	private int LEFT_SIDE_MARGIN = ViewUtil.getRealDimension(DefaultValues.FEED_LAYOUT_3COL_LEFT_SIDE_MARGIN);
	private int RIGHT_SIDE_MARGIN = ViewUtil.getRealDimension(DefaultValues.FEED_LAYOUT_3COL_RIGHT_SIDE_MARGIN);

	protected RecyclerView feedView;
	protected TrendAdapter feedAdapter;
	protected GridLayoutManager layoutManager;

	protected View headerView;

	protected PullToRefreshView pullListView;

	protected View getHeaderView(LayoutInflater inflater) {
		return null;
	}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_trend, container, false);

		pullListView = (PullToRefreshView) view.findViewById(R.id.pull_to_refresh);

		//items = new ArrayList<>();

		feedView = (RecyclerView) view.findViewById(R.id.feedView);
		//feedView.setHasFixedSize(true);

		// default is 3 columns layout
	//	if (AbstractFeedViewFragment.FeedViewItemsLayout.TWO_COLUMNS.equals(1)) {
			RECYCLER_VIEW_COLUMN_SIZE = 1;
			TOP_MARGIN = ViewUtil.getRealDimension(DefaultValues.FEED_LAYOUT_2COL_TOP_MARGIN);
			BOTTOM_MARGIN = ViewUtil.getRealDimension(DefaultValues.FEED_LAYOUT_2COL_BOTTOM_MARGIN);
			SIDE_MARGIN = ViewUtil.getRealDimension(DefaultValues.FEED_LAYOUT_2COL_SIDE_MARGIN);
			LEFT_SIDE_MARGIN = ViewUtil.getRealDimension(DefaultValues.FEED_LAYOUT_2COL_LEFT_SIDE_MARGIN);
			RIGHT_SIDE_MARGIN = ViewUtil.getRealDimension(DefaultValues.FEED_LAYOUT_2COL_RIGHT_SIDE_MARGIN);
	//	}

		feedView.addItemDecoration(
				new RecyclerView.ItemDecoration() {
					@Override
					public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
						//int pos = parent.getChildAdapterPosition(view);
						//int margin = getActivity().getResources().getDimensionPixelSize(R.dimen.feed_item_margin);

						ViewUtil.FeedItemPosition feedItemPosition =
								ViewUtil.getFeedItemPosition(view, RECYCLER_VIEW_COLUMN_SIZE, true);
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

		pullListView.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
			@Override
			public void onRefresh() {
				pullListView.postDelayed(new Runnable() {
					@Override
					public void run() {
						pullListView.setRefreshing(false);
						reloadFeed();
					}
				}, DefaultValues.PULL_TO_REFRESH_DELAY);
			}
		});

		// header
		headerView = getHeaderView(inflater);
		if (headerView != null) {
			//noItemView = headerView.findViewById(R.id.noItemView);
		}

		reloadFeed();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

    }

	public void reloadFeed(){
		List<CategoryVM> items = new ArrayList<>();

		for(CategoryVM vm : CategoryCache.getTrendCategories()){
			items.add(vm);
		}
		// adapter
		feedAdapter = new TrendAdapter(getActivity(), items, AbstractFeedViewFragment.FeedViewItemsLayout.ONE_COLUMNS, headerView, false);
		feedView.setAdapter(feedAdapter);

		layoutManager = new GridLayoutManager(getActivity(), RECYCLER_VIEW_COLUMN_SIZE);
		layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
			@Override
			public int getSpanSize(int position) {
				return feedAdapter.isHeader(position) ? layoutManager.getSpanCount() : 1;
			}
		});
		feedView.setLayoutManager(layoutManager);
	}

    @Override
    public void onDetach() {
        super.onDetach();

        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);

        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }
}

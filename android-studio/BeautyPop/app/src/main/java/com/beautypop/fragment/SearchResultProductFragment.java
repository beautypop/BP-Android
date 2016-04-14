package com.beautypop.fragment;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.beautypop.R;
import com.beautypop.adapter.FeedViewAdapter;
import com.beautypop.app.AppController;
import com.beautypop.app.TrackedFragment;
import com.beautypop.listener.EndlessScrollListener;
import com.beautypop.util.DefaultValues;
import com.beautypop.util.ViewUtil;
import com.beautypop.viewmodel.PostVMLite;
import com.yalantis.phoenix.PullToRefreshView;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class SearchResultProductFragment extends TrackedFragment {


	public static int RECYCLER_VIEW_COLUMN_SIZE = 2;

	private static final int TOP_MARGIN = ViewUtil.getRealDimension(DefaultValues.FEEDVIEW_ITEM_TOP_MARGIN);
	private static final int BOTTOM_MARGIN = ViewUtil.getRealDimension(DefaultValues.FEEDVIEW_ITEM_BOTTOM_MARGIN);
	private static final int SIDE_MARGIN = ViewUtil.getRealDimension(DefaultValues.FEEDVIEW_ITEM_SIDE_MARGIN);
	private static final int LEFT_SIDE_MARGIN = (SIDE_MARGIN * 2) + ViewUtil.getRealDimension(2);
	private static final int RIGHT_SIDE_MARGIN = (SIDE_MARGIN * 2) + ViewUtil.getRealDimension(2);

	protected TextView noDataText;
	protected RecyclerView feedView;
	protected FeedViewAdapter feedAdapter;
	protected GridLayoutManager layoutManager;
	protected List<PostVMLite> items;
	protected Long cnt = 1L;

	protected PullToRefreshView pullListView;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.search_result_product_fragment, container, false);

		pullListView = (PullToRefreshView) view.findViewById(R.id.pull_to_refresh);
		noDataText = (TextView) view.findViewById(R.id.noDataText);

		items = new ArrayList<>();

		feedView = (RecyclerView) view.findViewById(R.id.feedView);
		feedView.addItemDecoration(
				new RecyclerView.ItemDecoration() {
					@Override
					public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

						ViewUtil.FeedItemPosition feedItemPosition =
								ViewUtil.getFeedItemPosition(view, RECYCLER_VIEW_COLUMN_SIZE, false);
						if (feedItemPosition == ViewUtil.FeedItemPosition.HEADER) {
							outRect.set(0, 0, 0, 0);
						} else if (feedItemPosition == ViewUtil.FeedItemPosition.LEFT_COLUMN) {
							outRect.set(LEFT_SIDE_MARGIN, TOP_MARGIN, SIDE_MARGIN, BOTTOM_MARGIN);
						} else if (feedItemPosition == ViewUtil.FeedItemPosition.RIGHT_COLUMN) {
							outRect.set(SIDE_MARGIN, TOP_MARGIN, RIGHT_SIDE_MARGIN, BOTTOM_MARGIN);
						}
					}
				});

		feedAdapter = new FeedViewAdapter(getActivity(), items, null, false);
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
					}
				}, DefaultValues.PULL_TO_REFRESH_DELAY);
			}
		});

		loadFeed(0L);
		attachEndlessScrollListener();

		return view;
	}

	protected void loadFeedItemsToList(List<PostVMLite> posts) {

		items.addAll(posts);
		feedAdapter.notifyDataSetChanged();
		ViewUtil.stopSpinner(getActivity());

	}

	protected void loadFeed(Long offset) {
		ViewUtil.showSpinner(getActivity());
		AppController.getApiService().searchProduct(getArguments().getString("searchText"),getArguments().getLong("catId"),offset,new Callback<List<PostVMLite>>() {
			@Override
			public void success(List<PostVMLite> postVMLites, Response response) {
				if(postVMLites.size() == 0){
					noDataText.setVisibility(View.VISIBLE);
					feedView.setVisibility(View.GONE);
				}
				loadFeedItemsToList(postVMLites);

			}

			@Override
			public void failure(RetrofitError error) {
				noDataText.setVisibility(View.VISIBLE);
				feedView.setVisibility(View.GONE);
				error.printStackTrace();
				ViewUtil.stopSpinner(getActivity());

			}
		});

	}

	protected void attachEndlessScrollListener() {

		feedView.setOnScrollListener(new EndlessScrollListener(layoutManager) {
			@Override
			public void onLoadMore(Long offset) {
				loadFeed(cnt);
				cnt ++;

			}

			@Override
			public void onScrollUp() {
			}

			@Override
			public void onScrollDown() {
			}
		});
	}

}

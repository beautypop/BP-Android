package com.beautypop.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.beautypop.R;
import com.beautypop.activity.ThemeActivity;
import com.beautypop.app.AppController;
import com.beautypop.fragment.AbstractFeedViewFragment.FeedViewItemsLayout;
import com.beautypop.util.FeedFilter;
import com.beautypop.util.ImageUtil;
import com.beautypop.util.ViewUtil;
import com.beautypop.viewmodel.CategoryVM;
import com.beautypop.viewmodel.PostVMLite;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * http://blog.sqisland.com/2014/12/recyclerview-grid-with-header.html
 * https://github.com/chiuki/android-recyclerview
 */
public class TrendAdapter extends RecyclerView.Adapter<TrendAdapter.FeedViewHolder> {

	private static final int ITEM_VIEW_TYPE_HEADER = 0;
	private static final int ITEM_VIEW_TYPE_ITEM = 1;

	private FeedViewItemsLayout feedViewItemsLayout;

	private View headerView;

	private Activity activity;

	private List<CategoryVM> items;

	private boolean showSeller = false;

	private int clickedPosition = -1;

	private boolean pending = false;

	int imageWidth,margin;

	public TrendAdapter(Activity activity, List<CategoryVM> items,
						   FeedViewItemsLayout feedViewItemsLayout, View header) {
		this(activity, items, feedViewItemsLayout, header, false);
	}

	public TrendAdapter(Activity activity, List<CategoryVM> items,
						   FeedViewItemsLayout feedViewItemsLayout, View header, boolean showSeller) {
		this.activity = activity;
		this.items = items;
		this.feedViewItemsLayout = feedViewItemsLayout;
		this.headerView = header;
		this.showSeller = showSeller;
	}

	public int getClickedPosition() {
		return clickedPosition;
	}

	public boolean hasHeader() {
		return headerView != null;
	}

	public boolean isHeader(int position) {
		if (hasHeader()) {
			return position == 0;
		}
		return false;
	}

	public CategoryVM getItem(int position) {
		if (hasHeader()) {
			return items.get(position - 1);
		}
		return items.get(position);
	}

	public CategoryVM removeItem(int position) {
		if (hasHeader()) {
			return items.remove(position - 1);
		}
		return items.remove(position);
	}

	public boolean isEmpty() {
		return items.size() == 0;
	}

	public CategoryVM getLastItem() {
		return getItem(getItemCount() - 1);
	}

	@Override
	public int getItemCount() {
		if (hasHeader()) {
			return items.size() + 1;
		}
		return items.size();
	}

	@Override
	public FeedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		if (viewType == ITEM_VIEW_TYPE_HEADER) {
			return new FeedViewHolder(headerView);  // Dummy!! This will not be binded!!
		}

		View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.trend_list_item, parent, false);
		FeedViewHolder holder = new FeedViewHolder(layoutView);
		return holder;
	}

	@Override
	public void onBindViewHolder(final FeedViewHolder holder, final int position) {
		if (isHeader(position)) {
			return;
		}

		final CategoryVM item = getItem(position);

		 imageWidth = (int) ((double) ViewUtil.getDisplayDimensions(activity).width() / 4);  // fit around 4 items
		 margin = 10;

		holder.trendTitleText.setText(item.getName());
		ImageUtil.displayImage(item.getIcon(), holder.trendImageView);

		holder.trendImageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				Intent intent = new Intent(activity, ThemeActivity.class);
				intent.putExtra(ViewUtil.BUNDLE_KEY_CATEGORY_ID, item.getId());
				activity.startActivity(intent);

			}
		});

		getPopularProducts(item, holder.moreProductsImagesLayout);

	}

	@Override
	public int getItemViewType(int position) {
		return isHeader(position) ? ITEM_VIEW_TYPE_HEADER : ITEM_VIEW_TYPE_ITEM;
	}

	class FeedViewHolder extends RecyclerView.ViewHolder {
		ImageView trendImageView;
		TextView trendTitleText;
		LinearLayout moreProductsImagesLayout;


		public FeedViewHolder(View holder) {
			super(holder);

			trendImageView = (ImageView) holder.findViewById(R.id.trendImageView);
			trendTitleText = (TextView) holder.findViewById(R.id.trendTitleText);
			moreProductsImagesLayout = (LinearLayout) holder.findViewById(R.id.moreProductsImagesLayout);
		}

		void setTag(long score) {
			//itemLayout.setTag(score);
		}
	}

	public void getPopularProducts(CategoryVM categoryVM, final LinearLayout linearLayout){

		AppController.getApiService().getCategoryPopularFeed(0L,categoryVM.getId(), FeedFilter.ConditionType.ALL.name(),new Callback<List<PostVMLite>>() {
			@Override
			public void success(List<PostVMLite> postVMLites, Response response) {

				for (final PostVMLite vm : postVMLites) {

					FrameLayout layout = new FrameLayout(activity);
					layout.setLayoutParams(new ViewGroup.LayoutParams(imageWidth + margin, imageWidth + margin));
					//layout.setGravity(Gravity.CENTER);
					ImageView imageView = new ImageView(activity);
					imageView.setLayoutParams(new ViewGroup.LayoutParams(imageWidth, imageWidth));
					imageView.setScaleType(ImageView.ScaleType.FIT_XY);

					if (vm.getImages() != null)
						System.out.println("vm ::::: "+vm.images.length);

					if (vm.getImages() != null && vm.getImages().length != 0) {
						loadImage(vm.getImages()[0], imageView);
					}

					layout.addView(imageView);

					TextView textView = new TextView(activity);
					textView.setText(vm.getTitle());
					textView.setGravity(Gravity.CENTER);
					textView.setTextColor(Color.WHITE);
					layout.addView(textView);

					layout.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							ViewUtil.startProductActivity(activity, vm.getId());
						}
					});
					linearLayout.addView(layout);
				}
			}

			@Override
			public void failure(RetrofitError error) {

			}
		});

	}

	private void loadImage(final Long imageId, final ImageView imageView) {
		imageView.setAdjustViewBounds(true);
		imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
		imageView.setPadding(0, 0, 0, 0);
		ImageUtil.displayPostImage(imageId, imageView);

        /*
        imageView.setImageResource(ImageUtil.getImageLoadingResId(imageId));
        new Handler().postDelayed(new Runnable() {
            public void run() {
                ImageUtil.displayPostImage(imageId, imageView);
            }
        }, 0);
        */
	}
}
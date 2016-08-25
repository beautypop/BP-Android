package com.beautypop.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
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
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.HashMap;
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
	private HashMap<Long,TrendAdapter.FeedViewHolder> hashMap = new HashMap<>();

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

		imageWidth = (int) ((double) ViewUtil.getDisplayDimensions(activity).width() / 3.5);  // fit around 3.5 items

        // padding = 10;

		holder.trendTitleText.setText(item.getName());
		holder.categoryId = item.id;
        Glide
            .with(activity)
            .load(item.getIcon())
            .diskCacheStrategy(DiskCacheStrategy.SOURCE)
            .dontAnimate()
            .placeholder(R.drawable.ic_image_load)
            .into(holder.trendImageView);

		holder.trendImageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				clickedPosition = position;
				Intent intent = new Intent(activity, ThemeActivity.class);
				intent.putExtra(ViewUtil.BUNDLE_KEY_CATEGORY_ID, item.getId());
				activity.startActivity(intent);
			}
		});

        if (item.featured) {
            holder.triangleIcon.setVisibility(View.VISIBLE);
            holder.horizontalView.setVisibility(View.VISIBLE);

			if(hashMap.get(item.getId()) == null) {
				hashMap.put(item.getId(),holder);
				getPopularProducts(item, holder);
			}

        } else {
            holder.triangleIcon.setVisibility(View.GONE);
            holder.horizontalView.setVisibility(View.GONE);
        }
	}

	@Override
	public int getItemViewType(int position) {
		return isHeader(position) ? ITEM_VIEW_TYPE_HEADER : ITEM_VIEW_TYPE_ITEM;
	}

	class FeedViewHolder extends RecyclerView.ViewHolder {
		Long categoryId = 0L;
		ImageView trendImageView;
		TextView trendTitleText;
        ImageView triangleIcon;
        HorizontalScrollView horizontalView;
		LinearLayout moreProductsImagesLayout;

		public FeedViewHolder(View holder) {
			super(holder);

			trendImageView = (ImageView) holder.findViewById(R.id.trendImageView);
			trendTitleText = (TextView) holder.findViewById(R.id.trendTitleText);
            triangleIcon = (ImageView) holder.findViewById(R.id.triangleIcon);
            horizontalView = (HorizontalScrollView) holder.findViewById(R.id.horizontalView);
			moreProductsImagesLayout = (LinearLayout) holder.findViewById(R.id.moreProductsImagesLayout);
		}

		void setTag(long score) {
			//itemLayout.setTag(score);
		}
	}

	public void getPopularProducts(CategoryVM categoryVM,  final FeedViewHolder viewHolder){
		viewHolder.moreProductsImagesLayout.removeAllViews();
		ViewUtil.showSpinner(activity);
		AppController.getApiService().getCategoryPopularFeed(0L,categoryVM.getId(), FeedFilter.ConditionType.ALL.name(),new Callback<List<PostVMLite>>() {
			@Override
			public void success(List<PostVMLite> postVMLites, Response response) {
				for (final PostVMLite vm : postVMLites) {

					LinearLayout layout = new LinearLayout(activity);
					layout.setOrientation(LinearLayout.VERTICAL);
					LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
					params.setMargins(5,0,5,0);
					layout.setLayoutParams(params);
					ImageView imageView = new ImageView(activity);
					imageView.setLayoutParams(new ViewGroup.LayoutParams(imageWidth, imageWidth));
					imageView.setScaleType(ImageView.ScaleType.FIT_XY);

					if (vm.getImages() != null && vm.getImages().length != 0) {
						loadImage(vm.getImages()[0], imageView);
					}
					layout.addView(imageView);

					/*TextView titleText = new TextView(activity);
					titleText.setSingleLine(false);
					titleText.setLines(2);
					titleText.setTextSize(12);
					titleText.setEllipsize(TextUtils.TruncateAt.END);
					titleText.setText(vm.getTitle());
					titleText.setGravity(Gravity.CENTER_HORIZONTAL);
					titleText.setTextColor(activity.getResources().getColor(R.color.dark_gray));
					layout.addView(titleText);*/

					LinearLayout layout1 = new LinearLayout(activity);
					layout1.setOrientation(LinearLayout.HORIZONTAL);

					TextView originalPrice = new TextView(activity);
					originalPrice.setText(ViewUtil.formatPrice(vm.getOriginalPrice()));
					originalPrice.setGravity(Gravity.CENTER_HORIZONTAL);
					originalPrice.setTextSize(13);
					originalPrice.setTypeface(null, Typeface.BOLD);
					originalPrice.setTextColor(activity.getResources().getColor(R.color.light_gray));
					layout1.addView(originalPrice);

					LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
					layoutParams.setMargins(5,0,0,0);

					TextView priceText = new TextView(activity);
					priceText.setText(ViewUtil.formatPrice(vm.getPrice()));
					priceText.setLayoutParams(layoutParams);
					priceText.setTextSize(13);
					priceText.setTypeface(null, Typeface.BOLD);
					priceText.setGravity(Gravity.CENTER_HORIZONTAL);
					priceText.setTextColor(activity.getResources().getColor(R.color.green));
					layout1.addView(priceText);

					if (vm.getOriginalPrice() > 0) {
						originalPrice.setVisibility(View.VISIBLE);
						originalPrice.setText(ViewUtil.formatPrice(vm.getOriginalPrice()));
						ViewUtil.strikeText(originalPrice);
					} else {
						originalPrice.setVisibility(View.GONE);
						priceText.setGravity(Gravity.CENTER_HORIZONTAL);
					}

					layout.addView(layout1);

					layout.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							ViewUtil.startProductActivity(activity, vm.getId());
						}
					});

					if (viewHolder == hashMap.get(vm.getTrendId()) &&
							viewHolder.categoryId == vm.getTrendId()) {
						viewHolder.moreProductsImagesLayout.addView(layout);
					} else {

					}
					//hashMap.get(vm.getTrendId()).moreProductsImagesLayout.addView(layout);
					ViewUtil.stopSpinner(activity);
					//TrendAdapter.this.notifyDataSetChanged();
				}
			}

			@Override
			public void failure(RetrofitError error) {
				ViewUtil.stopSpinner(activity);
			}
		});

	}

	private void loadImage(final Long imageId, final ImageView imageView) {
		imageView.setAdjustViewBounds(true);
		imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
		imageView.setPadding(0, 0, 0, 0);
		ImageUtil.displayPostImage(imageId, imageView);
	}
}
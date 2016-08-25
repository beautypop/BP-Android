package com.beautypop.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.beautypop.R;
import com.beautypop.activity.ThemeActivity;
import com.beautypop.app.AppController;
import com.beautypop.app.CategoryCache;
import com.beautypop.app.TrackedFragment;
import com.beautypop.util.FeedFilter;
import com.beautypop.util.ImageUtil;
import com.beautypop.util.ViewUtil;
import com.beautypop.viewmodel.CategoryVM;
import com.beautypop.viewmodel.PostVMLite;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ThemeFragment extends TrackedFragment {
	private LinearLayout themeImagesLayout;
	private LinearLayout placeHolder;
	int imageWidth,margin;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		View view = inflater.inflate(R.layout.theme_fragment, container, false);
		themeImagesLayout = (LinearLayout)view. findViewById(R.id.themeImagesLayout);
		placeHolder = (LinearLayout)view. findViewById(R.id.placeHolder);

		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
		params.addRule(RelativeLayout.BELOW,R.id.themeLabel);

		imageWidth = (int) ((double) ViewUtil.getDisplayDimensions(getActivity()).width() / 3.5);  // fit around 3.5 items

		showThemes();
		showTrends();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

	public void showThemes(){

		themeImagesLayout.removeAllViews();

		int imageWidth = (int) ((double) ViewUtil.getDisplayDimensions(getActivity()).width() / 3.5);  // fit around 3.5 items
		int padding = 10;
		for (final CategoryVM vm : CategoryCache.getThemeCategories()) {

			FrameLayout layout = new FrameLayout(getActivity());
            layout.setPadding(padding,padding,padding,padding);
			layout.setLayoutParams(new ViewGroup.LayoutParams(imageWidth, imageWidth));

			ImageView imageView = new ImageView(getActivity());
			imageView.setLayoutParams(new ViewGroup.LayoutParams(imageWidth, imageWidth));
			imageView.setScaleType(ImageView.ScaleType.FIT_XY);
			ImageUtil.displayImage(vm.getThumbnail(), imageView);
			layout.addView(imageView);

			TextView textView = new TextView(getActivity());
			textView.setText(vm.getName());
			textView.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
            textView.setPadding(0,0,0,10);
			textView.setTextColor(Color.WHITE);
            textView.setTextSize(13);
			textView.setBackground(getActivity().getResources().getDrawable(R.drawable.gradient_bottom));
			layout.addView(textView);

			layout.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(getActivity(), ThemeActivity.class);
					intent.putExtra(ViewUtil.BUNDLE_KEY_CATEGORY_ID, vm.getId());
					startActivity(intent);
				}
			});
			themeImagesLayout.addView(layout);
		}
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
	public void onResume() {
		super.onResume();
	}


	public void showTrends(){
		List<CategoryVM> items = new ArrayList<>();

		for(final CategoryVM vm : CategoryCache.getTrendCategories()){

			LayoutInflater vi = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View v = vi.inflate(R.layout.trend_list_item, null);

			ImageView trendImageView = (ImageView) v.findViewById(R.id.trendImageView);
			TextView trendTitleText = (TextView) v.findViewById(R.id.trendTitleText);
			ImageView triangleIcon = (ImageView) v.findViewById(R.id.triangleIcon);
			HorizontalScrollView horizontalView = (HorizontalScrollView) v.findViewById(R.id.horizontalView);

			LinearLayout moreProductsImagesLayout = (LinearLayout) v.findViewById(R.id.moreProductsImagesLayout);

			Glide
					.with(getActivity())
					.load(vm.getIcon())
					.diskCacheStrategy(DiskCacheStrategy.SOURCE)
					.dontAnimate()
					.placeholder(R.drawable.ic_image_load)
					.into(trendImageView);

			trendTitleText.setText(vm.getName());
			placeHolder.addView(v);
			if (vm.featured) {
				triangleIcon.setVisibility(View.VISIBLE);
				horizontalView.setVisibility(View.VISIBLE);
				getPopularProducts(vm, moreProductsImagesLayout, v);
			} else {
				triangleIcon.setVisibility(View.GONE);
				horizontalView.setVisibility(View.GONE);
			}

		trendImageView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(getActivity(), ThemeActivity.class);
					intent.putExtra(ViewUtil.BUNDLE_KEY_CATEGORY_ID, vm.getId());
					getActivity().startActivity(intent);
				}
			});

		}
	}


	public void getPopularProducts(CategoryVM categoryVM, final LinearLayout moreProductsImagesLayout, final View v){
		ViewUtil.showSpinner(getActivity());
		AppController.getApiService().getCategoryPopularFeed(0L,categoryVM.getId(), FeedFilter.ConditionType.ALL.name(),new Callback<List<PostVMLite>>() {
			@Override
			public void success(List<PostVMLite> postVMLites, Response response) {
				for (final PostVMLite vm : postVMLites) {

					LinearLayout layout = new LinearLayout(getActivity());
					layout.setOrientation(LinearLayout.VERTICAL);
					LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
					params.setMargins(5,0,5,0);
					layout.setLayoutParams(params);
					ImageView imageView = new ImageView(getActivity());
					imageView.setLayoutParams(new ViewGroup.LayoutParams(imageWidth, imageWidth));
					imageView.setScaleType(ImageView.ScaleType.FIT_XY);

					if (vm.getImages() != null && vm.getImages().length != 0) {
						loadImage(vm.getImages()[0], imageView);
					}
					layout.addView(imageView);

					LinearLayout layout1 = new LinearLayout(getActivity());
					layout1.setOrientation(LinearLayout.HORIZONTAL);

					TextView originalPrice = new TextView(getActivity());
					LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
					layoutParams.gravity = Gravity.CENTER;
					layoutParams.setMargins(5,0,0,0);

					layout1.setGravity(Gravity.CENTER_HORIZONTAL);

					TextView priceText = new TextView(getActivity());

					if (vm.getOriginalPrice() > 0) {

						originalPrice.setText(ViewUtil.formatPrice(vm.getOriginalPrice()));
						originalPrice.setGravity(Gravity.CENTER);
						originalPrice.setTextSize(13);
						originalPrice.setTypeface(null, Typeface.BOLD);
						originalPrice.setTextColor(getActivity().getResources().getColor(R.color.light_gray));
						layout1.addView(originalPrice);
						originalPrice.setVisibility(View.VISIBLE);
						originalPrice.setText(ViewUtil.formatPrice(vm.getOriginalPrice()));
						ViewUtil.strikeText(originalPrice);
					} else {

						priceText.setText(ViewUtil.formatPrice(vm.getPrice()));
						priceText.setLayoutParams(layoutParams);
						priceText.setTextSize(13);
						priceText.setTypeface(null, Typeface.BOLD);
						priceText.setGravity(Gravity.CENTER);
						priceText.setTextColor(getActivity().getResources().getColor(R.color.green));
						layout1.addView(priceText);

						originalPrice.setVisibility(View.GONE);
						priceText.setGravity(Gravity.CENTER_HORIZONTAL);
					}

					layout.addView(layout1);

					layout.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							ViewUtil.startProductActivity(getActivity(), vm.getId());
						}
					});

					moreProductsImagesLayout.addView(layout);
					ViewUtil.stopSpinner(getActivity());
				}
			}

			@Override
			public void failure(RetrofitError error) {
				ViewUtil.stopSpinner(getActivity());
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

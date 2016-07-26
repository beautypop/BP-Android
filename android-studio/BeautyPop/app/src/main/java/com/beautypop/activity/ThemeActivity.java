package com.beautypop.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.beautypop.R;
import com.beautypop.app.CategoryCache;
import com.beautypop.fragment.PopularFeedViewFragment;
import com.beautypop.fragment.ThemeImagePagerFragment;
import com.beautypop.util.DefaultValues;
import com.beautypop.util.ViewUtil;
import com.beautypop.viewmodel.CategoryVM;

public class ThemeActivity extends ActionBarActivity {


	private ImageView backImage,image;
	private TextView titleText,infoText;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_theme);

		infoText = (TextView) findViewById(R.id.infoText);
		titleText = (TextView) findViewById(R.id.titleText);
		backImage = (ImageView) findViewById(R.id.backImage);
		image = (ImageView) findViewById(R.id.image);

		//ViewUtil.addDots(ProductActivity.this, imagePagerAdapter.getCount(), dotsLayout, dots, imagePager);
		CategoryVM categoryVM = CategoryCache.getCategory(getIntent().getLongExtra(ViewUtil.BUNDLE_KEY_CATEGORY_ID, 0L));

		Bundle bundle = new Bundle();
		bundle.putString(ViewUtil.BUNDLE_KEY_FEED_TYPE, DefaultValues.DEFAULT_CATEGORY_FEED_TYPE.name());
		bundle.putString(ViewUtil.BUNDLE_KEY_FEED_FILTER_CONDITION_TYPE, DefaultValues.DEFAULT_FEED_FILTER_CONDITION_TYPE.name());
		bundle.putLong(ViewUtil.BUNDLE_KEY_CATEGORY_ID, categoryVM.getId());

		PopularFeedViewFragment fragment = new PopularFeedViewFragment();
		FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
		fragment.setArguments(bundle);
		fragmentTransaction.replace(R.id.childLayout, fragment).commit();

		backImage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		System.out.println("Theme activity :::: "+categoryVM.getDescription());


	}

	@Override
	public void onBackPressed()
	{
		super.onBackPressed();
	}
}

class ThemeImagePagerAdapter extends FragmentStatePagerAdapter {
	private static final String TAG = ProductImagePagerAdapter.class.getName();

	private int[] images;

	public ThemeImagePagerAdapter(FragmentManager fm, int[] images) {
		super(fm);
		this.images = images;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return "";
	}

	@Override
	public int getCount() {
		return images == null? 0 : images.length;
	}

	@Override
	public Fragment getItem(int position) {
		Log.d(TAG, "getItem: item - " + position);
		switch (position) {
			default: {
				ThemeImagePagerFragment fragment = new ThemeImagePagerFragment();
				fragment.setImageId(images[position]);
				return fragment;
			}
		}
	}

	@Override
	public int getItemPosition(Object object) {
		return POSITION_NONE;
	}
}



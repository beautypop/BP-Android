package com.beautypop.activity;

import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.astuetz.PagerSlidingTabStrip;
import com.beautypop.R;
import com.beautypop.app.TrackedFragment;
import com.beautypop.fragment.SearchProductFragment;
import com.beautypop.fragment.SearchResultProductFragment;
import com.beautypop.fragment.SearchUserFragment;
import com.beautypop.util.ViewUtil;

public class SearchResultActivity extends FragmentActivity {

	private static final String TAG = SearchResultActivity.class.getName();

	 ViewPager viewPager;
	private SearchResultPagerAdapter adapter;

	private PagerSlidingTabStrip tabs;
	private ImageView backImage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_result);

		tabs = (PagerSlidingTabStrip) findViewById(R.id.searchResultTabs);
		viewPager = (ViewPager) findViewById(R.id.searchResultPager);
		backImage = (ImageView) findViewById(R.id.backImage);

		adapter = new SearchResultPagerAdapter(getSupportFragmentManager());

		backImage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				onBackPressed();
			}
		});

		int pageMargin = ViewUtil.getRealDimension(0);
		viewPager.setPageMargin(pageMargin);
		viewPager.setAdapter(adapter);

		tabs.setViewPager(viewPager);

	}

	class SearchResultPagerAdapter extends FragmentStatePagerAdapter {

		public SearchResultPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return ViewUtil.SEARCH_MAIN_TITLES[position];
		}

		@Override
		public int getCount() {
			return ViewUtil.SEARCH_MAIN_TITLES.length;
		}

		@Override
		public Fragment getItem(int position) {
			Log.d(this.getClass().getSimpleName(), "getItem: item position=" + position);

			Bundle bundle = new Bundle();
			TrackedFragment fragment = null;
			switch (position) {
				// Product
				case 0: {
					fragment = new SearchResultProductFragment();
					bundle.putString("searchText",getIntent().getStringExtra("searchText"));
					bundle.putLong("catId",getIntent().getLongExtra("catId",0L));
					break;
				}
				// USER
				case 1: {
					fragment = new SearchUserFragment();
					bundle.putString("searchText",getIntent().getStringExtra("searchText"));
					break;
				}
				default: {
					Log.e(this.getClass().getSimpleName(), "getItem: Unknown item position=" + position);
					break;
				}
			}

			if (fragment != null) {
				fragment.setArguments(bundle);
				fragment.setTrackedOnce();
			}
			return fragment;
		}
	}

}
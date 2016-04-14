package com.beautypop.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;

import com.astuetz.PagerSlidingTabStrip;
import com.beautypop.R;
import com.beautypop.app.TrackedFragment;
import com.beautypop.fragment.SearchProductFragment;
import com.beautypop.fragment.SearchUserFragment;
import com.beautypop.util.ViewUtil;

public class SearchActivity extends FragmentActivity {

	private static final String TAG = SearchActivity.class.getName();

	private SearchView searchView;
	private ViewPager viewPager;
	private SearchResultPagerAdapter adapter;
	private String searchKey;
	private ImageView backImage;

	private PagerSlidingTabStrip tabs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);

		tabs = (PagerSlidingTabStrip) findViewById(R.id.searchResultTabs);
		viewPager = (ViewPager) findViewById(R.id.searchResultPager);
		searchView = (SearchView) findViewById(R.id.searchView);
		backImage = (ImageView) findViewById(R.id.backImage);

		searchView.setIconified(false);
		searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String s) {
				return false;
			}

			@Override
			public boolean onQueryTextChange(String s) {
				searchKey = s;

				adapter = new SearchResultPagerAdapter(getSupportFragmentManager());
				viewPager.setAdapter(adapter);
				tabs.setViewPager(viewPager);

				return false;
			}
		});

		backImage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				onBackPressed();
			}
		});

		adapter = new SearchResultPagerAdapter(getSupportFragmentManager());
		viewPager.setAdapter(adapter);
		tabs.setViewPager(viewPager);


		int pageMargin = ViewUtil.getRealDimension(0);
		viewPager.setPageMargin(pageMargin);


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
					bundle.putString("searchText",searchKey);
					fragment = new SearchProductFragment();
					break;
				}
				// USER
				case 1: {
					fragment = new SearchUserFragment();
					bundle.putString("searchText",searchKey);
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

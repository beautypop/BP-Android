package com.beautypop.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;

import com.beautypop.R;
import com.beautypop.app.TrackedFragment;
import com.beautypop.fragment.SearchResultProductFragment;
import com.beautypop.fragment.SearchResultUserFragment;
import com.beautypop.util.ViewUtil;

public class SearchResultActivity extends FragmentActivity {

	private static final String TAG = SearchResultActivity.class.getName();

	private ImageView backImage;
	public SearchView searchView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_result_activity);

		backImage = (ImageView) findViewById(R.id.backImage);
		searchView = (SearchView) findViewById(R.id.searchView);

		searchView.setVisibility(View.GONE);

		backImage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				onBackPressed();
			}
		});

		Bundle bundle = new Bundle();
		TrackedFragment fragment = null;
		if(getIntent().getStringExtra("flag").equals("user")){
			fragment = new SearchResultUserFragment();
			bundle.putString("searchText", getIntent().getStringExtra("searchText"));
			fragment.setArguments(bundle);
			fragment.setTrackedOnce();
			FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
			fragmentTransaction.replace(R.id.placeHolder, fragment).commit();
		} else {
			fragment = new SearchResultProductFragment();
			bundle.putString("searchText", getIntent().getStringExtra("searchText"));
			bundle.putLong("catId",getIntent().getLongExtra("catId", -1L));
			fragment.setArguments(bundle);
			fragment.setTrackedOnce();
			FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
			fragmentTransaction.replace(R.id.placeHolder, fragment).commit();
		}
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
					bundle.putLong("catId",getIntent().getLongExtra("catId", -1L));
					break;
				}
				// USER
				case 1: {
					fragment = new SearchResultUserFragment();
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

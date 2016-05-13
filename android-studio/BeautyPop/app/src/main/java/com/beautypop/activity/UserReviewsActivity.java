package com.beautypop.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;

import com.astuetz.PagerSlidingTabStrip;
import com.beautypop.R;
import com.beautypop.app.TrackedFragment;
import com.beautypop.app.TrackedFragmentActivity;
import com.beautypop.fragment.PurchasedReviewFragment;
import com.beautypop.fragment.SoldReviewFragment;
import com.beautypop.util.ViewUtil;

public class UserReviewsActivity extends TrackedFragmentActivity {

	private ViewPager viewPager;
	private ReviewPagerAdapter adapter;
	private PagerSlidingTabStrip tabs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.user_reviews_activity);

        setToolbarTitle(getString(R.string.reviews));

		tabs = (PagerSlidingTabStrip) findViewById(R.id.sellerTabs);
		viewPager = (ViewPager) findViewById(R.id.sellerPager);
		adapter = new ReviewPagerAdapter(getSupportFragmentManager());

		int pageMargin = ViewUtil.getRealDimension(0);
		viewPager.setPageMargin(pageMargin);
		viewPager.setAdapter(adapter);

		tabs.setViewPager(viewPager);
	}
}

class ReviewPagerAdapter extends FragmentStatePagerAdapter {

	public ReviewPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return ViewUtil.REVIEW_MAIN_TITLES[position];
	}

	@Override
	public int getCount() {
		return ViewUtil.REVIEW_MAIN_TITLES.length;
	}

	@Override
	public Fragment getItem(int position) {
		Log.d(this.getClass().getSimpleName(), "getItem: item position=" + position);

		Bundle bundle = new Bundle();
		TrackedFragment fragment = null;
		switch (position) {
			case 0: {
				fragment = new SoldReviewFragment();
				break;
			}
			case 1: {
				fragment = new PurchasedReviewFragment();
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

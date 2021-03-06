package com.beautypop.app;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.beautypop.R;
import com.beautypop.activity.MainActivity;
import com.beautypop.util.DefaultValues;
import com.beautypop.util.ViewUtil;
import com.google.analytics.tracking.android.EasyTracker;

public abstract class TrackedFragmentActivity extends FragmentActivity {

    protected EasyTracker tracker;

    protected boolean tracked = true;

    protected EasyTracker getTracker() {
        if (tracker == null)
            tracker = EasyTracker.getInstance(this);
        return tracker;
    }

    public void setTracked(boolean tracked) {
        this.tracked = tracked;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ViewUtil.setLocale(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        ViewUtil.setLocale(this);
    }

    @Override
    public void onStart() {
        super.onStart();

        Log.d(this.getClass().getSimpleName(), "[DEBUG] activityStart");
        if (tracked) {
            getTracker().activityStart(this);
        }
        if (MainActivity.getInstance() != null) {
            MainActivity.getInstance().resetControls();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(this.getClass().getSimpleName(), "[DEBUG] activityStop");
        if (tracked) {
            getTracker().activityStop(this);
        }
    }

    //
    // UI helper
    //

    protected void setToolbarTitle(String title) {
        TextView titleText = (TextView) findViewById(R.id.toolbarTitleText);
        if (titleText != null) {
            titleText.setText(ViewUtil.shortenString(title, DefaultValues.DEFAULT_TITLE_COUNT));
        }

        /*
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            View actionBarView = actionBar.getCustomView();
            TextView titleText = (TextView) actionBarView.findViewById(R.id.toolbarTitleText);
            if (titleText != null) {
                titleText.setText(title);
            }
        }
        */
    }

    protected void showToolbarTitle(boolean show) {
        TextView titleText = (TextView) findViewById(R.id.toolbarTitleText);
        if (titleText != null) {
            titleText.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    protected Toolbar getToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        return toolbar;
    }

    protected void showToolbarOffsetLayout(boolean show) {
        if (findViewById(R.id.toolbarOffsetLayout) != null) {
            RelativeLayout toolbarOffsetLayout = (RelativeLayout) findViewById(R.id.toolbarOffsetLayout);
            toolbarOffsetLayout.setVisibility(show? View.VISIBLE : View.GONE);
        }
    }
}

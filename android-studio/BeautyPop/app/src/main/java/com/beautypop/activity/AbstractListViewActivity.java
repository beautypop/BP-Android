package com.beautypop.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.beautypop.R;
import com.beautypop.app.TrackedFragmentActivity;
import com.beautypop.listener.InfiniteScrollListener;
import com.beautypop.util.DefaultValues;
import com.beautypop.util.ViewUtil;

public abstract class AbstractListViewActivity extends TrackedFragmentActivity {

    protected ImageView backImage;
    protected TextView titleText;
    protected TextView noItemText;
    protected ListView listView;
    protected BaseAdapter listAdapter;

    abstract protected String getTitleText();

    abstract protected String getNoItemText();

    abstract protected BaseAdapter getListAdapter();

    abstract protected void loadListItems(Long objId, Long offset);

    protected void showNoItemText() {
        noItemText.setVisibility(View.VISIBLE);
        ViewUtil.stopSpinner(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.child_list_view);

        setActionBarTitle(getTitleText());

        noItemText = (TextView) findViewById(R.id.noItemText);
        listView = (ListView) findViewById(R.id.listView);

        noItemText.setText(getNoItemText());

        backImage = (ImageView) this.findViewById(R.id.backImage);
        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // list
        listAdapter = getListAdapter();
        listView.setAdapter(listAdapter);

        final Long objId = getIntent().getLongExtra(ViewUtil.BUNDLE_KEY_ID, 0L);
        listView.setOnScrollListener(new InfiniteScrollListener(
                DefaultValues.DEFAULT_INFINITE_SCROLL_VISIBLE_THRESHOLD, false, false) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                loadListItems(objId, page - 1L);
            }

            @Override
            public void onScrollUp() {
            }

            @Override
            public void onScrollDown() {
            }
        });

        loadListItems(objId, 0L);
    }
}

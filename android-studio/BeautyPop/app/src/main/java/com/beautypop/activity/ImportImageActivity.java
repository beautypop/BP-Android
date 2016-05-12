package com.beautypop.activity;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.beautypop.R;
import com.beautypop.app.AppController;
import com.beautypop.instagram.ImageAdapter;
import com.beautypop.instagram.InstagramApp;
import com.beautypop.instagram.InstagramVM;
import com.beautypop.util.ViewUtil;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ImportImageActivity extends Activity {

	public static int RECYCLER_VIEW_COLUMN_SIZE = 2;

	static final int TOP_MARGIN = 5;
	private static final int BOTTOM_MARGIN = 3;
	private static final int SIDE_MARGIN = 1;
	private static final int LEFT_SIDE_MARGIN = 10;
	private static final int RIGHT_SIDE_MARGIN = 10;

	protected RecyclerView feedView;
	protected ImageAdapter feedAdapter;
	public TextView titleText,importButton;
	protected GridLayoutManager layoutManager;
    protected ImageView backImage;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_import_image);

		feedView = (RecyclerView) findViewById(R.id.feedView);
		titleText = (TextView) findViewById(R.id.selectCountText);
		importButton = (TextView) findViewById(R.id.importButton);
        backImage = (ImageView) findViewById(R.id.backImage);

		titleText.setText("Select Images");

		feedView.addItemDecoration(
                new RecyclerView.ItemDecoration() {
                    @Override
                    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

                        ViewUtil.FeedItemPosition feedItemPosition =
                                ViewUtil.getFeedItemPosition(view, RECYCLER_VIEW_COLUMN_SIZE, false);
                        if (feedItemPosition == ViewUtil.FeedItemPosition.HEADER) {
                            outRect.set(0, 0, 0, 0);
                        } else if (feedItemPosition == ViewUtil.FeedItemPosition.LEFT_COLUMN) {
                            outRect.set(LEFT_SIDE_MARGIN, TOP_MARGIN, SIDE_MARGIN, BOTTOM_MARGIN);
                        } else if (feedItemPosition == ViewUtil.FeedItemPosition.RIGHT_COLUMN) {
                            outRect.set(SIDE_MARGIN, TOP_MARGIN, RIGHT_SIDE_MARGIN, BOTTOM_MARGIN);
                        }
                    }
                });

		importButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<InstagramVM> vms = new ArrayList<InstagramVM>();
                final SparseBooleanArray selected = feedAdapter.getSelectedIds();
                for (int i = 0; i < selected.size(); i++) {
                    if (selected.valueAt(i)) {
                        InstagramVM item = feedAdapter.getItem(selected.keyAt(i));
                        vms.add(item);
                        titleText.setText("Select Images");
                    }
                }

                if(vms.size() == 0){
                    Toast.makeText(ImportImageActivity.this,"Please select atleast one image." , Toast.LENGTH_SHORT).show();
                    return;
                }
                if(vms.size() > 20){
                    Toast.makeText(ImportImageActivity.this,"Selected images rage must ne within 1 to 20." , Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(ImportImageActivity.this, EditImportImageActivity.class);
                intent.putParcelableArrayListExtra("items", vms);
                startActivity(intent);

            }
        });

        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        AppController.getApiService().getInstagramImportdetPost(new Callback<List<String>>() {
            @Override
            public void success(List<String> importedIds, Response response) {
                for (InstagramVM instagramVM : InstagramApp.mediaList) {
                    instagramVM.setIsImported(importedIds.contains(instagramVM.getMediaId()));
                }

                feedAdapter = new ImageAdapter(ImportImageActivity.this, InstagramApp.mediaList);
                feedView.setAdapter(feedAdapter);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(ImportImageActivity.class.getName(), "doPost: failure", error);
            }
        });
		// adapter

		// layout manager
		layoutManager = new GridLayoutManager(ImportImageActivity.this, RECYCLER_VIEW_COLUMN_SIZE);
		layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return feedAdapter.isHeader(position) ? layoutManager.getSpanCount() : 1;
            }
        });
		feedView.setLayoutManager(layoutManager);
	}
}

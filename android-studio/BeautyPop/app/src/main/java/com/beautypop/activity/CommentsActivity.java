package com.beautypop.activity;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Toast;

import com.beautypop.R;
import com.beautypop.adapter.CommentListAdapter;
import com.beautypop.app.AppController;
import com.beautypop.util.ViewUtil;
import com.beautypop.viewmodel.CommentVM;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class CommentsActivity extends AbstractListViewActivity {
    private static final String TAG = CommentsActivity.class.getName();

    private List<CommentVM> items;

    @Override
    protected String getTitleText() {
        return getString(R.string.comments);
    }

    @Override
    protected String getNoItemText() {
        return getString(R.string.no_product_comments);
    }

    @Override
    protected BaseAdapter getListAdapter() {
        if (items == null) {
            items = new ArrayList<>();
        }
        return new CommentListAdapter(CommentsActivity.this, items);
    }

    @Override
    protected void loadListItems(final Long objId, final Long offset) {
        ViewUtil.showSpinner(this);

        AppController.getApiService().getComments(offset, objId, new Callback<List<CommentVM>>() {
            @Override
            public void success(List<CommentVM> comments, Response response) {
                Log.d(TAG, "loadListItems.getComments: offset=" + offset +
                        " size=" + (comments == null ? 0 : comments.size()));

                if (offset == 0 && (comments == null || comments.size() == 0)) {
                    showNoItemText();
                    return;
                }

                if (comments != null && comments.size() > 0) {
                    items.addAll(comments);
                    listAdapter.notifyDataSetChanged();
                }

                ViewUtil.stopSpinner(CommentsActivity.this);
            }

            @Override
            public void failure(RetrofitError error) {
                ViewUtil.stopSpinner(CommentsActivity.this);
                Log.e(TAG, "loadListItems.getComments: failure", error);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                final CommentVM item = items.get(i);
                if (ViewUtil.copyToClipboard(item.getBody())) {
                    Toast.makeText(CommentsActivity.this, CommentsActivity.this.getString(R.string.comment_copy_success), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(CommentsActivity.this, CommentsActivity.this.getString(R.string.comment_copy_failed), Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });
    }
}

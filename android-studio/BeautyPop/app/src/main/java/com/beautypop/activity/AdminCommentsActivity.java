package com.beautypop.activity;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Toast;

import com.beautypop.R;
import com.beautypop.adapter.AdminCommentListAdapter;
import com.beautypop.adapter.CommentListAdapter;
import com.beautypop.app.AppController;
import com.beautypop.util.ViewUtil;
import com.beautypop.viewmodel.AdminCommentVM;
import com.beautypop.viewmodel.CommentVM;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class AdminCommentsActivity extends AbstractListViewActivity {
    private static final String TAG = AdminCommentsActivity.class.getName();

    private List<AdminCommentVM> items;

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
        return new AdminCommentListAdapter(AdminCommentsActivity.this, items);
    }

    @Override
    protected void loadListItems(Long objId, final Long offset) {
        ViewUtil.showSpinner(this);

        AppController.getApiService().getLatestComments(offset, new Callback<List<AdminCommentVM>>() {
            @Override
            public void success(List<AdminCommentVM> comments, Response response) {
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

                ViewUtil.stopSpinner(AdminCommentsActivity.this);
            }

            @Override
            public void failure(RetrofitError error) {
                ViewUtil.stopSpinner(AdminCommentsActivity.this);
                Log.e(TAG, "loadListItems.getComments: failure", error);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                final AdminCommentVM item = items.get(i);
                if (ViewUtil.copyToClipboard(item.body)) {
                    Toast.makeText(AdminCommentsActivity.this, AdminCommentsActivity.this.getString(R.string.comment_copy_success), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AdminCommentsActivity.this, AdminCommentsActivity.this.getString(R.string.comment_copy_failed), Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });
    }
}

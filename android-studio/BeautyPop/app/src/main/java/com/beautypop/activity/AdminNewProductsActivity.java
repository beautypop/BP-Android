package com.beautypop.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.BaseAdapter;

import com.beautypop.R;
import com.beautypop.adapter.AdminProductListAdapter;
import com.beautypop.app.AppController;
import com.beautypop.util.ViewUtil;
import com.beautypop.viewmodel.PostVMLite;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class AdminNewProductsActivity extends AbstractListViewActivity {
    private static final String TAG = AdminNewProductsActivity.class.getName();

    private List<PostVMLite> items;

    @Override
    protected String getTitleText() {
        return getString(R.string.admin_new_products);
    }

    @Override
    protected String getNoItemText() {
        return getString(R.string.admin_new_products);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTracked(false);
    }

    @Override
    protected BaseAdapter getListAdapter() {
        if (items == null) {
            items = new ArrayList<>();
        }
        return new AdminProductListAdapter(this, items);
    }

    @Override
    protected void loadListItems(final Long objId, final Long offset) {
        ViewUtil.showSpinner(this);

        AppController.getApiService().getNewProducts(offset, new Callback<List<PostVMLite>>() {
            @Override
            public void success(List<PostVMLite> posts, Response response) {
                Log.d(TAG, "loadListItems.getNewProducts: offset=" + offset +
                        " size=" + (posts == null ? 0 : posts.size()));

                if (offset == 0 && (posts == null || posts.size() == 0)) {
                    showNoItemText();
                    return;
                }

                if (posts != null && posts.size() > 0) {
                    items.addAll(posts);
                    listAdapter.notifyDataSetChanged();
                }

                ViewUtil.stopSpinner(AdminNewProductsActivity.this);
            }

            @Override
            public void failure(RetrofitError error) {
                ViewUtil.stopSpinner(AdminNewProductsActivity.this);
                Log.e(TAG, "loadListItems.getNewProducts: failure", error);
            }
        });
    }
}

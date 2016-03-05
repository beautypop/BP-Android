package com.beautypop.activity;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.beautypop.R;
import com.beautypop.app.AppController;
import com.beautypop.app.CategoryCache;
import com.beautypop.util.DefaultValues;
import com.beautypop.util.ViewUtil;
import com.beautypop.viewmodel.CategoryVM;
import com.beautypop.viewmodel.NewPostVM;
import com.beautypop.viewmodel.PostVM;
import com.beautypop.viewmodel.ResponseStatusVM;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class EditPostActivity extends NewPostActivity {

    private Long postId;

    @Override
    protected String getActionTypeText() {
        return getString(R.string.edit_post_action);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setActionBarTitle(getString(R.string.edit_post_actionbar_title));

        // disable images edit
        imagesLayout.setVisibility(View.GONE);

        postId = getIntent().getLongExtra(ViewUtil.BUNDLE_KEY_ID, -1L);
        getPost(postId);
    }

    protected void getPost(final Long postId) {
        ViewUtil.showSpinner(this);

        AppController.getApiService().getPost(postId, new Callback<PostVM>() {
            @Override
            public void success(final PostVM post, Response response) {
                titleEdit.setText(post.getTitle());
                descEdit.setText(post.getBody());
                priceEdit.setText((int) post.getPrice() + "");
                setConditionTypeSpinner(ViewUtil.parsePostConditionType(post.getConditionType()));

                // category, subcategory
                setCategory(CategoryCache.getCategory(post.categoryId));
                setSubCategory(CategoryCache.getCategory(post.subCategoryId));

                // seller
                if (post.getOriginalPrice() > 0) {
                    originalPriceEdit.setText((int) post.getOriginalPrice() + "");
                }
                freeDeliveryCheckBox.setChecked(post.isFreeDelivery());
                setCountrySpinner(post.countryCode);

                ViewUtil.stopSpinner(EditPostActivity.this);
            }

            @Override
            public void failure(RetrofitError error) {
                if (RetrofitError.Kind.NETWORK.equals(error.getKind()) ||
                        RetrofitError.Kind.HTTP.equals(error.getKind())) {
                    Toast.makeText(EditPostActivity.this, EditPostActivity.this.getString(R.string.post_not_found), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(EditPostActivity.this, EditPostActivity.this.getString(R.string.connection_timeout_message), Toast.LENGTH_SHORT).show();
                }

                ViewUtil.stopSpinner(EditPostActivity.this);

                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        finish();
                    }
                }, DefaultValues.DEFAULT_HANDLER_DELAY);

                Log.e(ProductActivity.class.getSimpleName(), "getPost: failure", error);
            }
        });
    }

    @Override
    protected void doPost() {
        NewPostVM newPost = getNewPost();
        if (newPost == null) {
            return;
        }

        ViewUtil.showSpinner(this);

        // set postId
        newPost.id = postId;

        postAction.setEnabled(false);
        AppController.getApiService().editPost(newPost, new Callback<ResponseStatusVM>() {
            @Override
            public void success(ResponseStatusVM responseStatus, Response response) {
                complete();
            }

            @Override
            public void failure(RetrofitError error) {
                reset();
                Toast.makeText(EditPostActivity.this, String.format(EditPostActivity.this.getString(R.string.post_failed), getActionTypeText()), Toast.LENGTH_SHORT).show();
                Log.e(EditPostActivity.class.getSimpleName(), "doPost: failure", error);
            }
        });
    }
}

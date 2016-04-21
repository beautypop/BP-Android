package com.beautypop.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.beautypop.R;
import com.beautypop.app.AppController;
import com.beautypop.app.CategoryCache;
import com.beautypop.app.UserInfoCache;
import com.beautypop.fragment.AbstractFeedViewFragment;
import com.beautypop.util.DefaultValues;
import com.beautypop.util.ViewUtil;
import com.beautypop.viewmodel.NewPostVM;
import com.beautypop.viewmodel.PostVM;
import com.beautypop.viewmodel.PostVMLite;
import com.beautypop.viewmodel.ResponseStatusVM;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class EditPostActivity extends NewPostActivity {
    private static final String TAG = EditPostActivity.class.getName();

    private RelativeLayout deleteLayout;

    private Long postId;

    @Override
    protected String getActionTypeText() {
        return getString(R.string.edit_post_action);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setToolbarTitle(getString(R.string.edit_post_actionbar_title));

        // disable images edit
        imagesLayout.setVisibility(View.GONE);

        deleteLayout = (RelativeLayout) findViewById(R.id.deleteLayout);
        deleteLayout.setVisibility(View.VISIBLE);

        deleteLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(EditPostActivity.this);
                alertDialogBuilder.setMessage(getString(R.string.post_delete_confirm));
                alertDialogBuilder.setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deletePost(postId);
                    }
                });
                alertDialogBuilder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });

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

                Log.e(TAG, "getPost: failure", error);
            }
        });
    }

    @Override
    protected void doPost() {
        if (pending) {
            return;
        }

        NewPostVM newPost = getNewPost();
        if (newPost == null) {
            return;
        }

        ViewUtil.showSpinner(this);

        // set postId
        newPost.id = postId;

        postAction.setEnabled(false);
        pending = true;
        AppController.getApiService().editPost(newPost, new Callback<ResponseStatusVM>() {
            @Override
            public void success(ResponseStatusVM responseStatus, Response response) {
                complete();
                pending = false;
            }

            @Override
            public void failure(RetrofitError error) {
                reset();
                Toast.makeText(EditPostActivity.this, String.format(EditPostActivity.this.getString(R.string.post_failed), getActionTypeText()), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "doPost: failure", error);
                pending = false;
            }
        });
    }

    protected void deletePost(Long id) {
        if (pending) {
            return;
        }

        pending = true;
        AppController.getApiService().deletePost(id, new Callback<Response>() {
            @Override
            public void success(Response responseObject, Response response) {
                Toast.makeText(EditPostActivity.this, getString(R.string.post_delete_success), Toast.LENGTH_SHORT).show();

                UserInfoCache.decrementNumProducts();

                // pass back to feed view to handle
                setActivityResult(AbstractFeedViewFragment.ItemChangedState.ITEM_REMOVED, null);
                pending = false;
                finish();
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(EditPostActivity.this, getString(R.string.post_delete_failed), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "deletePost: failure", error);
                pending = false;
            }
        });
    }

    protected void setActivityResult(AbstractFeedViewFragment.ItemChangedState itemChangedState, PostVMLite post) {
        Intent intent = new Intent();
        intent.putExtra(ViewUtil.INTENT_RESULT_ITEM_CHANGED_STATE, itemChangedState.name());
        intent.putExtra(ViewUtil.INTENT_RESULT_OBJECT, post);
        setResult(RESULT_OK, intent);
    }
}

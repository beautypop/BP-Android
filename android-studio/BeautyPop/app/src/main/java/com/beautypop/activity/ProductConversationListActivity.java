package com.beautypop.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.beautypop.R;
import com.beautypop.adapter.ConversationListAdapter;
import com.beautypop.app.AppController;
import com.beautypop.app.ConversationCache;
import com.beautypop.util.ImageUtil;
import com.beautypop.util.ViewUtil;
import com.beautypop.viewmodel.ConversationVM;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ProductConversationListActivity extends ConversationListActivity {

    private static final String TAG = ProductConversationListActivity.class.getName();

    private RelativeLayout postLayout;
    private TextView postTitleText, postPriceText;
    private ImageView postImage;

    private List<ConversationVM> conversations = new ArrayList<>();

    @Override
    protected int getContentViewResource() {
        return R.layout.product_conversation_list_activity;
    }

    @Override
    protected String getActionBarTitle() {
        return getString(R.string.pm_actionbar_title);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        postLayout = (RelativeLayout) findViewById(R.id.postLayout);
        postImage = (ImageView) findViewById(R.id.postImage);
        postTitleText = (TextView) findViewById(R.id.postTitleText);
        postPriceText = (TextView) findViewById(R.id.postPriceText);
    }

    @Override
    protected void attachEndlessScrollListener() {
        // no infinite scroll
    }

    private void updateConversations(ConversationVM conversation) {
        boolean deleted = conversations.remove(conversation);
        if (deleted) {
            conversations.add(conversation);
        }
        ConversationCache.sortConversations(conversations);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ViewUtil.START_ACTIVITY_REQUEST_CODE &&
                resultCode == Activity.RESULT_OK &&
                data != null && adapter != null) {

            Long conversationId = data.getLongExtra(ViewUtil.INTENT_RESULT_ID, -1L);

            Log.d(this.getClass().getSimpleName(), "onActivityResult: conversationId=" + conversationId);
            if (conversationId != -1L) {
                // new message sent for conversation
                AppController.getApiService().getConversation(conversationId, new Callback<ConversationVM>() {
                    @Override
                    public void success(ConversationVM conversation, Response response) {
                        updateConversations(conversation);
                        ViewUtil.stopSpinner(ProductConversationListActivity.this);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        ViewUtil.stopSpinner(ProductConversationListActivity.this);
                        Log.e(ProductConversationListActivity.class.getSimpleName(), "onActivityResult: failure", error);
                    }
                });
            }
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void getConversations(final long offset) {
        Long postId = getIntent().getLongExtra(ViewUtil.BUNDLE_KEY_ID, -1L);
        if (postId == -1) {
            Log.w(ProductConversationListActivity.class.getSimpleName(), "getConversations: postId == -1");
            return;
        }

        ViewUtil.showSpinner(this);
        AppController.getApiService().getPostConversations(postId, new Callback<List<ConversationVM>>() {
            @Override
            public void success(List<ConversationVM> vms, Response response) {
                Log.d(ProductConversationListActivity.class.getSimpleName(), "getConversations: success");

                conversations = vms;
                if (conversations.size() == 0) {
                    postLayout.setVisibility(View.GONE);
                    tipText.setVisibility(View.VISIBLE);
                } else {
                    adapter = new ConversationListAdapter(ProductConversationListActivity.this, conversations, false);
                    listView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

                    // init post details
                    postLayout.setVisibility(View.VISIBLE);
                    ConversationVM conversation = conversations.get(0);
                    postTitleText.setText(conversation.getPostTitle());
                    postPriceText.setText(ViewUtil.formatPrice(conversation.getPostPrice()));
                    ImageUtil.displayPostImage(conversation.getPostImage(), postImage);
                    ViewUtil.setConversationImageTag(ProductConversationListActivity.this, conversations.get(0));
                }

                ViewUtil.stopSpinner(ProductConversationListActivity.this);
            }

            @Override
            public void failure(RetrofitError error) {
                ViewUtil.stopSpinner(ProductConversationListActivity.this);
                Log.e(ProductConversationListActivity.class.getSimpleName(), "getConversations: failure", error);
            }
        });
    }
}
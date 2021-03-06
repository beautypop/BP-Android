package com.beautypop.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.beautypop.R;
import com.beautypop.adapter.CommentListAdapter;
import com.beautypop.app.AppController;
import com.beautypop.app.ConversationCache;
import com.beautypop.app.CountryCache;
import com.beautypop.app.TrackedFragmentActivity;
import com.beautypop.app.UserInfoCache;
import com.beautypop.fragment.AbstractFeedViewFragment;
import com.beautypop.fragment.AbstractFeedViewFragment.ItemChangedState;
import com.beautypop.fragment.ProductImagePagerFragment;
import com.beautypop.util.DateTimeUtil;
import com.beautypop.util.DefaultValues;
import com.beautypop.util.ImageUtil;
import com.beautypop.util.SharingUtil;
import com.beautypop.util.UrlUtil;
import com.beautypop.util.ViewUtil;
import com.beautypop.view.AdaptiveViewPager;
import com.beautypop.viewmodel.CommentVM;
import com.beautypop.viewmodel.ConversationVM;
import com.beautypop.viewmodel.NewCommentVM;
import com.beautypop.viewmodel.PostVM;
import com.beautypop.viewmodel.PostVMLite;
import com.beautypop.viewmodel.ResponseStatusVM;

import org.joda.time.DateTime;
import org.parceler.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ProductActivity extends TrackedFragmentActivity {
    private static final String TAG = ProductActivity.class.getName();

    private FrameLayout mainLayout;
    private ImageView backImage, facebookAction, whatsappAction, copyLinkAction, moreAction;
    private TextView editPostAction;

    private AdaptiveViewPager imagePager;
    private ProductImagePagerAdapter imagePagerAdapter;
    private LinearLayout dotsLayout;
    private List<ImageView> dots = new ArrayList<>();

    private TextView titleText, descText, priceText, originalPriceText, soldText, conditionText;
    private Button chatButton, buyButton, viewChatsButton, soldButton, soldViewChatsButton;
    private LinearLayout actionsLayout, likeLayout, buyerButtonsLayout, sellerButtonsLayout, buyerSoldButtonsLayout, sellerSoldButtonsLayout, adminLayout;
    private ImageView likeImage, freeDeliveryImage, countryImage;
    private TextView likeText, numLikesText;

    private LinearLayout sellerLayout;
    private ImageView sellerImage;
    private TextView sellerNameText, sellerLastActiveText, sellerProductsText, sellerFollowersText;

    private RelativeLayout moreCommentsLayout;
    private ImageView moreCommentsImage;

    private TextView commentText, commentSendButton, catNameText, subCatNameText, timeText, numViewsText, numCommentsText, deleteText;
    private EditText commentEditText;
    private Button sendButton;

    private ListView commentList;
    private LinearLayout moreProductsLayout, moreProductsImagesLayout;

    private PopupWindow commentPopup;
    private Button followButton;

    private PostVM post;
    private long postId;
    private long ownerId;
    private boolean isFollowing;
    private CommentListAdapter commentListAdapter;

    private boolean pending = false;

    private PopupWindow menuPopup;    // where u want show on view click event popupwindow.showAsDropDown(view, x, y);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.product_activity);

        mainLayout = (FrameLayout) findViewById(R.id.mainLayout);
        backImage = (ImageView) findViewById(R.id.backImage);
        facebookAction = (ImageView)findViewById(R.id.facebookAction);
        whatsappAction = (ImageView) findViewById(R.id.whatsappAction);
        copyLinkAction = (ImageView) findViewById(R.id.copyLinkAction);
        editPostAction = (TextView) findViewById(R.id.editPostAction);
        moreAction = (ImageView) findViewById(R.id.moreAction);

        menuPopup = initMenuPopup();

        imagePager = (AdaptiveViewPager) findViewById(R.id.imagePager);
        dotsLayout = (LinearLayout) findViewById(R.id.dotsLayout);

        titleText = (TextView) findViewById(R.id.titleText);
        descText = (TextView) findViewById(R.id.descText);
        priceText = (TextView) findViewById(R.id.priceText);
        originalPriceText = (TextView) findViewById(R.id.originalPriceText);

        buyerButtonsLayout = (LinearLayout) findViewById(R.id.buyerButtonsLayout);
        chatButton = (Button) findViewById(R.id.chatButton);
        buyButton = (Button) findViewById(R.id.buyButton);

        sellerButtonsLayout = (LinearLayout) findViewById(R.id.sellerButtonsLayout);
        viewChatsButton = (Button) findViewById(R.id.viewChatsButton);
        soldButton = (Button) findViewById(R.id.soldButton);

        buyerSoldButtonsLayout = (LinearLayout) findViewById(R.id.buyerSoldButtonsLayout);
        soldText = (TextView) findViewById(R.id.soldText);

        sellerSoldButtonsLayout = (LinearLayout) findViewById(R.id.sellerSoldButtonsLayout);
        soldViewChatsButton = (Button) findViewById(R.id.soldViewChatsButton);

        conditionText = (TextView) findViewById(R.id.conditionText);

        actionsLayout = (LinearLayout) findViewById(R.id.actionsLayout);
        likeLayout = (LinearLayout) findViewById(R.id.likeLayout);
        likeImage = (ImageView) findViewById(R.id.likeImage);
        likeText = (TextView) findViewById(R.id.likeText);
        numLikesText = (TextView) findViewById(R.id.numLikesText);

        freeDeliveryImage = (ImageView) findViewById(R.id.freeDeliveryImage);
        countryImage = (ImageView) findViewById(R.id.countryImage);

        adminLayout = (LinearLayout) findViewById(R.id.adminLayout);

        followButton = (Button) findViewById(R.id.followButton);

        sellerLayout = (LinearLayout) findViewById(R.id.sellerLayout);
        sellerImage = (ImageView) findViewById(R.id.sellerImage);
        sellerNameText = (TextView) findViewById(R.id.sellerNameText);
        sellerLastActiveText = (TextView) findViewById(R.id.sellerLastActiveText);
        sellerProductsText = (TextView) findViewById(R.id.sellerProductsText);
        sellerFollowersText = (TextView) findViewById(R.id.sellerFollowersText);

        commentText = (TextView) findViewById(R.id.commentText);
        sendButton = (Button) findViewById(R.id.sendButton);
        catNameText = (TextView) findViewById(R.id.catNameText);
        subCatNameText = (TextView) findViewById(R.id.subCatNameText);
        timeText = (TextView) findViewById(R.id.timeText);
        numViewsText = (TextView) findViewById(R.id.numViewsText);
        numCommentsText = (TextView) findViewById(R.id.numCommentsText);
        deleteText = (TextView) findViewById(R.id.deleteText);

        moreCommentsLayout = (RelativeLayout) findViewById(R.id.moreCommentsLayout);
        moreCommentsImage = (ImageView) findViewById(R.id.moreCommentsImage);

        commentList = (ListView) findViewById(R.id.commentList);
        moreProductsLayout = (LinearLayout) findViewById(R.id.moreProductsLayout);
        moreProductsImagesLayout = (LinearLayout) findViewById(R.id.moreProductsImagesLayout);

        commentText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initCommentPopup();
            }
        });

        followButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFollowing) {
                    unfollow(ownerId);
                } else {
                    follow(ownerId);
                }
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initCommentPopup();
            }
        });

        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        postId = getIntent().getLongExtra(ViewUtil.BUNDLE_KEY_ID, -1L);
        if (postId <= 0L) {
            postId = ViewUtil.getIntentFilterLastPathSegment(getIntent());
        }

        getProduct(postId);
        getMoreProducts(postId);
    }

    private void setPost(PostVM post) {
        this.post = post;
    }

    private void getMoreProducts(final Long postId) {
        moreProductsLayout.setVisibility(View.GONE);

        AppController.getApiService().getSuggestedProducts(postId, new Callback<List<PostVMLite>>() {
            @Override
            public void success(List<PostVMLite> posts, Response response) {
                if (posts != null && posts.size() > 0) {
                    Log.d(TAG, "getMoreProducts.success: postId=" + postId + " size=" + posts.size());

                    int imageWidth = (int) ((double) ViewUtil.getDisplayDimensions(ProductActivity.this).width() / 4);  // fit around 4 items
                    int margin = 10;

                    moreProductsLayout.setVisibility(View.VISIBLE);

                    for (final PostVMLite post : posts) {
                        LinearLayout layout = new LinearLayout(getApplicationContext());
                        layout.setLayoutParams(new ViewGroup.LayoutParams(imageWidth + margin, imageWidth + margin));
                        layout.setGravity(Gravity.CENTER);

                        ImageView imageView = new ImageView(getApplicationContext());
                        imageView.setLayoutParams(new ViewGroup.LayoutParams(imageWidth, imageWidth));
                        imageView.setScaleType(ImageView.ScaleType.FIT_XY);

						if(post.getImages() != null)
	                        ImageUtil.displayPostImage(post.getImages()[0], imageView);

                        layout.addView(imageView);

                        layout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ViewUtil.startProductActivity(ProductActivity.this, post.id);
                            }
                        });

                        moreProductsImagesLayout.addView(layout);
                    }
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(TAG, "getMoreProducts.failure", error);
            }
        });
    }

    private void getProduct(final Long postId) {
        ViewUtil.showSpinner(this);

        AppController.getApiService().getPost(postId, new Callback<PostVM>() {
            @Override
            public void success(final PostVM post, Response response) {
                setPost(post);

                ownerId = post.getOwnerId();

                if (UserInfoCache.getUser().getId() == ownerId) {
                    followButton.setVisibility(View.GONE);
                }

                isFollowing = post.isFollowingOwner();
                if (isFollowing) {
                    ViewUtil.selectFollowButtonStyleLite(followButton);
                } else {
                    ViewUtil.unselectFollowButtonStyle(followButton);
                }

                // Image slider

                if (post.images != null) {
                    imagePagerAdapter = new ProductImagePagerAdapter(getSupportFragmentManager(), post.images, post.sold);
                    imagePager.setAdapter(imagePagerAdapter);
                    imagePager.setCurrentItem(0);
                    ViewUtil.addDots(ProductActivity.this, imagePagerAdapter.getCount(), dotsLayout, dots, imagePager);
                }

                // details

                //setToolbarTitle(post.getTitle());
                showToolbarTitle(false);

                //ViewUtil.setHtmlText(post.getTitle(), titleText, ProductActivity.this, true);
                //ViewUtil.setHtmlText(post.getBody(), descText, ProductActivity.this, true, true);
                titleText.setText(post.title);
                descText.setText(post.body);
                catNameText.setText(post.getCategoryName());
                subCatNameText.setText(post.getSubCategoryName());
                priceText.setText(ViewUtil.formatPrice(post.getPrice()));
                conditionText.setText(ViewUtil.getPostConditionTypeValue(
                        ViewUtil.parsePostConditionType(post.getConditionType())));
                timeText.setText(DateTimeUtil.getTimeAgo(post.getCreatedDate()));
                numCommentsText.setText(post.getNumComments() + " " + getString(R.string.comments));

                catNameText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ViewUtil.startCategoryActivity(ProductActivity.this, post.getCategoryId());
                    }
                });

                subCatNameText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ViewUtil.startCategoryActivity(ProductActivity.this, post.getSubCategoryId());
                    }
                });

                titleText.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if (ViewUtil.copyToClipboard(titleText.getText().toString())) {
                            Toast.makeText(ProductActivity.this, ProductActivity.this.getString(R.string.text_copy_success), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ProductActivity.this, ProductActivity.this.getString(R.string.text_copy_failed), Toast.LENGTH_SHORT).show();
                        }
                        return true;
                    }
                });

                descText.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if (ViewUtil.copyToClipboard(descText.getText().toString())) {
                            Toast.makeText(ProductActivity.this, ProductActivity.this.getString(R.string.text_copy_success), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ProductActivity.this, ProductActivity.this.getString(R.string.text_copy_failed), Toast.LENGTH_SHORT).show();
                        }
                        return true;
                    }
                });

                // like

                if (post.isLiked()) {
                    ViewUtil.selectLikeButtonStyle(likeImage, likeText, post.getNumLikes());
                } else {
                    ViewUtil.unselectLikeButtonStyle(likeImage, likeText, post.getNumLikes());
                }

                likeLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (post.isLiked) {
                            unlike(post);
                        } else {
                            like(post);
                        }
                    }
                });

                numLikesText.setText(post.numLikes + "");

                // seller

                if (post.getOriginalPrice() > 0) {
                    originalPriceText.setVisibility(View.VISIBLE);
                    originalPriceText.setText(ViewUtil.formatPrice(post.getOriginalPrice()));
                    ViewUtil.strikeText(originalPriceText);
                } else {
                    originalPriceText.setVisibility(View.GONE);
                }

                freeDeliveryImage.setVisibility(post.isFreeDelivery() ? View.VISIBLE : View.GONE);

                if (!StringUtils.isEmpty(post.countryCode) &&
                        !post.countryCode.equalsIgnoreCase(CountryCache.COUNTRY_CODE_NA)) {
                    ImageUtil.displayImage(post.countryIcon, countryImage);
                    countryImage.setVisibility(View.VISIBLE);
                } else {
                    countryImage.setVisibility(View.GONE);
                }

                // delete

                if (AppController.isUserAdmin()) {
                    deleteText.setVisibility(View.VISIBLE);

                    deleteText.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ProductActivity.this);
                            alertDialogBuilder.setMessage(getString(R.string.post_delete_confirm));
                            alertDialogBuilder.setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    deletePost(post.getId());
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
                } else {
                    deleteText.setVisibility(View.GONE);
                }

                // action buttons

                initActionsLayout();

                // buyer actions

                chatButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        openConversation(post.id);
                    }
                });

                buyButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final long price = (long) post.price;

                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ProductActivity.this);
                        final EditText priceEdit = ViewUtil.initOfferPriceAlertLayout(ProductActivity.this, price, alertDialogBuilder);
                        alertDialogBuilder.setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                long offeredPrice = ViewUtil.validateAndGetPriceFromInput(ProductActivity.this, priceEdit);
                                if (offeredPrice != -1L) {
                                    openConversation(post.id, true, offeredPrice);
                                }
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

                // seller actions

                viewChatsButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ViewUtil.startProductConversationListActivity(ProductActivity.this, post.id);
                    }
                });
                soldButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ProductActivity.this);
                        alertDialogBuilder.setMessage(getString(R.string.post_sold_confirm));
                        alertDialogBuilder.setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                sold(post);
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

                // sold actions

                soldText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ViewUtil.alert(ProductActivity.this, getString(R.string.sold_text));
                    }
                });
                soldViewChatsButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ViewUtil.startProductConversationListActivity(ProductActivity.this, post.id);
                    }
                });

                // seller

                ImageUtil.displayThumbnailProfileImage(post.getOwnerId(), sellerImage);
                sellerNameText.setText(post.getOwnerName());
                sellerLastActiveText.setText(ViewUtil.formatSellerLastActive(post.getOwnerLastLogin()));
                sellerProductsText.setText(ViewUtil.formatUserProducts(post.getOwnerNumProducts()));
                sellerFollowersText.setText(ViewUtil.formatUserFollowers(post.getOwnerNumFollowers()));

                sellerLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ViewUtil.startUserProfileActivity(ProductActivity.this, post.getOwnerId());
                    }
                });

                // comments

                List<CommentVM> comments = post.getLatestComments();
                commentListAdapter = new CommentListAdapter(ProductActivity.this, comments);
                commentList.setAdapter(commentListAdapter);
                ViewUtil.setHeightBasedOnChildren(ProductActivity.this, commentList);

                moreCommentsImage.setVisibility(View.GONE);
                if (post.getNumComments() > comments.size()) {
                    moreCommentsImage.setVisibility(View.VISIBLE);
                    moreCommentsLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ViewUtil.startCommentsActivity(ProductActivity.this, postId);
                        }
                    });
                }

                commentList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                        final CommentVM item = commentListAdapter.getItem(i);
                        if (ViewUtil.copyToClipboard(item.getBody())) {
                            Toast.makeText(ProductActivity.this, ProductActivity.this.getString(R.string.comment_copy_success), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ProductActivity.this, ProductActivity.this.getString(R.string.comment_copy_failed), Toast.LENGTH_SHORT).show();
                        }
                        return true;
                    }
                });

                commentList.setVisibility(View.VISIBLE);

                // actionbar

                facebookAction.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        SharingUtil.shareToFacebook(post, ProductActivity.this);
                    }
                });

                whatsappAction.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SharingUtil.shareToWhatsapp(post, ProductActivity.this);
                    }
                });

                copyLinkAction.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (ViewUtil.copyToClipboard(UrlUtil.createProductUrl(post))) {
                            Toast.makeText(ProductActivity.this, ProductActivity.this.getString(R.string.url_copy_success), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ProductActivity.this, ProductActivity.this.getString(R.string.url_copy_failed), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                if (post.isOwner()) {
                    editPostAction.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ViewUtil.startEditPostActivity(ProductActivity.this, post.id, post.categoryId);
                        }
                    });
                    editPostAction.setVisibility(View.VISIBLE);
                } else {
                    editPostAction.setVisibility(View.GONE);
                }

                if (post.isOwner()) {
                    moreAction.setVisibility(View.GONE);
                } else {
                    moreAction.setVisibility(View.VISIBLE);
                    moreAction.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            menuPopup.showAsDropDown(moreAction, -40, 15);
                        }
                    });
                }

                // admin

                adminLayout.setVisibility(AppController.isUserAdmin() ? View.VISIBLE : View.GONE);
                if (AppController.isUserAdmin()) {
                    TextView idText = (TextView) findViewById(R.id.idText);
                    TextView numViewsText = (TextView) findViewById(R.id.numViewsText);
                    TextView scoreText = (TextView) findViewById(R.id.scoreText);
                    ImageView upImage = (ImageView) findViewById(R.id.upImage);
                    ImageView downImage = (ImageView) findViewById(R.id.downImage);
                    ImageView upImage2 = (ImageView) findViewById(R.id.upImage2);
                    ImageView downImage2 = (ImageView) findViewById(R.id.downImage2);
                    ImageView resetImage = (ImageView) findViewById(R.id.resetImage);

                    idText.setText(post.id + "");
                    numViewsText.setText(post.getNumViews() + "");
                    scoreText.setText(ViewUtil.formatScore(post.timeScore, post.baseScoreAdjust, DefaultValues.DEFAULT_DOUBLE_SCALE));

                    idText.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            adminLayout.setVisibility(View.GONE);
                        }
                    });

                    upImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AppController.getApiService().adjustUpPostScore(post.id, DefaultValues.POST_SCORE_POINTS_ADJUST, new Callback<Response>() {
                                @Override
                                public void success(Response responseObject, Response response) {
                                    ViewUtil.alert(ProductActivity.this,
                                            getString(R.string.score_adjust_success, ViewUtil.getResponseBody(responseObject)));
                                }

                                @Override
                                public void failure(RetrofitError error) {
                                    ViewUtil.alert(ProductActivity.this,
                                            String.format(getString(R.string.score_adjust_failure), error.getLocalizedMessage()));
                                    Log.e(TAG, "adjustUpPostScore: failure", error);
                                }
                            });
                        }
                    });

                    downImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AppController.getApiService().adjustDownPostScore(post.id, DefaultValues.POST_SCORE_POINTS_ADJUST, new Callback<Response>() {
                                @Override
                                public void success(Response responseObject, Response response) {
                                    ViewUtil.alert(ProductActivity.this,
                                            getString(R.string.score_adjust_success, ViewUtil.getResponseBody(responseObject)));
                                }

                                @Override
                                public void failure(RetrofitError error) {
                                    ViewUtil.alert(ProductActivity.this,
                                            String.format(getString(R.string.score_adjust_failure), error.getLocalizedMessage()));
                                    Log.e(TAG, "adjustDownPostScore: failure", error);
                                }
                            });
                        }
                    });

                    upImage2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AppController.getApiService().adjustUpPostScore(post.id, DefaultValues.POST_SCORE_POINTS_ADJUST2, new Callback<Response>() {
                                @Override
                                public void success(Response responseObject, Response response) {
                                    ViewUtil.alert(ProductActivity.this,
                                            getString(R.string.score_adjust_success, ViewUtil.getResponseBody(responseObject)));
                                }

                                @Override
                                public void failure(RetrofitError error) {
                                    ViewUtil.alert(ProductActivity.this,
                                            String.format(getString(R.string.score_adjust_failure), error.getLocalizedMessage()));
                                    Log.e(TAG, "adjustUpPostScore: failure", error);
                                }
                            });
                        }
                    });

                    downImage2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AppController.getApiService().adjustDownPostScore(post.id, DefaultValues.POST_SCORE_POINTS_ADJUST2, new Callback<Response>() {
                                @Override
                                public void success(Response responseObject, Response response) {
                                    ViewUtil.alert(ProductActivity.this,
                                            getString(R.string.score_adjust_success, ViewUtil.getResponseBody(responseObject)));
                                }

                                @Override
                                public void failure(RetrofitError error) {
                                    ViewUtil.alert(ProductActivity.this,
                                            String.format(getString(R.string.score_adjust_failure), error.getLocalizedMessage()));
                                    Log.e(TAG, "adjustDownPostScore: failure", error);
                                }
                            });
                        }
                    });

                    resetImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AppController.getApiService().resetAdjustPostScore(post.id, new Callback<Response>() {
                                @Override
                                public void success(Response responseObject, Response response) {
                                    ViewUtil.alert(ProductActivity.this,
                                            getString(R.string.score_adjust_reset_success, ViewUtil.getResponseBody(responseObject)));
                                }

                                @Override
                                public void failure(RetrofitError error) {
                                    ViewUtil.alert(ProductActivity.this,
                                            String.format(getString(R.string.score_adjust_failure), error.getLocalizedMessage()));
                                    Log.e(TAG, "resetAdjustPostScore: failure", error);
                                }
                            });
                        }
                    });
                }

                ViewUtil.stopSpinner(ProductActivity.this);
            }

            @Override
            public void failure(RetrofitError error) {
                if (RetrofitError.Kind.NETWORK.equals(error.getKind()) ||
                        RetrofitError.Kind.HTTP.equals(error.getKind())) {
                    Toast.makeText(ProductActivity.this, ProductActivity.this.getString(R.string.post_not_found), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ProductActivity.this, ProductActivity.this.getString(R.string.connection_timeout_message), Toast.LENGTH_SHORT).show();
                }

                ViewUtil.stopSpinner(ProductActivity.this);

                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        finish();
                    }
                }, DefaultValues.DEFAULT_HANDLER_DELAY);

                Log.e(TAG, "getPost: failure", error);
            }
        });
    }

    private void initActionsLayout() {
        actionsLayout.setVisibility(View.VISIBLE);
        if (post.isOwner()) {
            if (post.isSold()) {
                buyerButtonsLayout.setVisibility(View.GONE);
                sellerButtonsLayout.setVisibility(View.GONE);
                buyerSoldButtonsLayout.setVisibility(View.GONE);
                sellerSoldButtonsLayout.setVisibility(View.VISIBLE);
            } else {
                buyerButtonsLayout.setVisibility(View.GONE);
                sellerButtonsLayout.setVisibility(View.VISIBLE);
                buyerSoldButtonsLayout.setVisibility(View.GONE);
                sellerSoldButtonsLayout.setVisibility(View.GONE);
            }
        } else {
            if (post.isSold()) {
                buyerButtonsLayout.setVisibility(View.GONE);
                sellerButtonsLayout.setVisibility(View.GONE);
                buyerSoldButtonsLayout.setVisibility(View.VISIBLE);
                sellerSoldButtonsLayout.setVisibility(View.GONE);
            } else {
                buyerButtonsLayout.setVisibility(View.VISIBLE);
                sellerButtonsLayout.setVisibility(View.GONE);
                buyerSoldButtonsLayout.setVisibility(View.GONE);
                sellerSoldButtonsLayout.setVisibility(View.GONE);
            }
        }
    }

    private void setActivityResult(ItemChangedState itemChangedState, PostVMLite post) {
        Intent intent = new Intent();
        intent.putExtra(ViewUtil.INTENT_RESULT_ITEM_CHANGED_STATE, itemChangedState.name());
        intent.putExtra(ViewUtil.INTENT_RESULT_OBJECT, post);
        setResult(RESULT_OK, intent);
    }

    private void openConversation(final Long postId) {
        openConversation(postId, false, -1L);
    }

    private void openConversation(final Long postId, final boolean buy, final long offeredPrice) {
        if (pending) {
            return;
        }

        pending = true;
        ConversationCache.open(postId, new Callback<ConversationVM>() {
            @Override
            public void success(ConversationVM vm, Response response) {
                if (vm != null) {
                    ViewUtil.startMessageListActivity(ProductActivity.this, vm, buy, offeredPrice);
                } else {
                    Toast.makeText(ProductActivity.this, getString(R.string.pm_start_failed), Toast.LENGTH_SHORT).show();
                }
                pending = false;
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(ProductActivity.this, getString(R.string.pm_start_failed), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "openConversation: failure", error);
                pending = false;
            }
        });
    }

    private void sold(final PostVM post) {
        if (pending) {
            return;
        }

        ViewUtil.showSpinner(ProductActivity.this);

        pending = true;
        AppController.getApiService().soldPost(post.id, new Callback<Response>() {
            @Override
            public void success(Response responseObject, Response response) {
                post.sold = true;
                initActionsLayout();

                // pass back to feed view to handle
                setActivityResult(ItemChangedState.ITEM_UPDATED, post);

                pending = false;
                ViewUtil.stopSpinner(ProductActivity.this);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(TAG, "sold: failure", error);
                pending = false;
                ViewUtil.stopSpinner(ProductActivity.this);
            }
        });
    }

    private void like(final PostVM post) {
        if (pending) {
            return;
        }

        post.isLiked = true;
        post.numLikes++;
        ViewUtil.selectLikeButtonStyle(likeImage, likeText, post.getNumLikes());

        pending = true;
        AppController.getApiService().likePost(post.id, new Callback<Response>() {
            @Override
            public void success(Response responseObject, Response response) {
                UserInfoCache.incrementNumLikes();

                // pass back to feed view to handle
                setActivityResult(ItemChangedState.ITEM_UPDATED, post);
                pending = false;
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(TAG, "like: failure", error);
                pending = false;
            }
        });
    }

    private void unlike(final PostVM post) {
        if (pending) {
            return;
        }

        if (post.numLikes <= 0) {
            return;
        }

        post.isLiked = false;
        post.numLikes--;
        ViewUtil.unselectLikeButtonStyle(likeImage, likeText, post.getNumLikes());

        pending = true;
        AppController.getApiService().unlikePost(post.id, new Callback<Response>() {
            @Override
            public void success(Response responseObject, Response response) {
                UserInfoCache.decrementNumLikes();

                // pass back to feed view to handle
                setActivityResult(ItemChangedState.ITEM_UPDATED, post);
                pending = false;
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(TAG, "unlike: failure", error);
                pending = false;
            }
        });
    }

    public PopupWindow initMenuPopup() {
        final PopupWindow popupWindow = new PopupWindow(ProductActivity.this);
        LayoutInflater inflater = (LayoutInflater) ProductActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.actionbar_menu_popup_window, null);
        TextView reportText = (TextView) view.findViewById(R.id.reportText);
        reportText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent reportActivity = new Intent(ProductActivity.this, ReportPostActivity.class);
                reportActivity.putExtra(ViewUtil.BUNDLE_KEY_ID, postId);
                startActivity(reportActivity);
            }
        });

        //popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setContentView(view);
        return popupWindow;
    }

    private void initCommentPopup() {
        //mainLayout.getForeground().setAlpha(20);
        //mainLayout.getForeground().setColorFilter(R.color.light_gray, PorterDuff.Mode.OVERLAY);

        try {
            LayoutInflater inflater = (LayoutInflater) ProductActivity.this
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            final View layout = inflater.inflate(R.layout.message_popup_window,
                    (ViewGroup) findViewById(R.id.popupElement));

            if (commentPopup == null) {
                commentPopup = new PopupWindow(
                        layout,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT, //activityUtil.getRealDimension(DefaultValues.COMMENT_POPUP_HEIGHT),
                        true);

                commentPopup.setOutsideTouchable(false);
                commentPopup.setFocusable(true);
                commentPopup.setBackgroundDrawable(new BitmapDrawable(getResources(), ""));
                commentPopup.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

                commentPopup.setTouchInterceptor(new View.OnTouchListener() {
                    public boolean onTouch(View view, MotionEvent event) {
                        return false;
                    }
                });

                commentEditText = (EditText) layout.findViewById(R.id.commentEditText);
                commentEditText.setLongClickable(true);

                commentSendButton = (TextView) layout.findViewById(R.id.commentSendButton);
                commentSendButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        doComment();
                    }
                });

                ImageView commentCancelButton = (ImageView) layout.findViewById(R.id.commentCancelButton);
                commentCancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        commentPopup.dismiss();
                        commentPopup = null;
                    }
                });

                ImageView commentBrowseImage = (ImageView) layout.findViewById(R.id.browseImage);
                commentBrowseImage.setVisibility(View.GONE);
            }

            commentPopup.showAtLocation(layout, Gravity.BOTTOM, 0, 0);
            ViewUtil.popupInputMethodWindow(this, commentEditText);
        } catch (Exception e) {
            Log.e(TAG, "initCommentPopup: failure", e);
        }
    }

    private void doComment() {
        if (pending) {
            return;
        }

        String comment = commentEditText.getText().toString().trim();
        if (StringUtils.isEmpty(comment)) {
            Toast.makeText(ProductActivity.this, ProductActivity.this.getString(R.string.invalid_comment_body_empty), Toast.LENGTH_SHORT).show();
            return;
        }

        ViewUtil.showSpinner(this);

        Log.d(TAG, "doComment: postId=" + postId + " comment=" + comment.substring(0, Math.min(5, comment.length())));

        pending = true;
        final NewCommentVM newComment = new NewCommentVM(postId, comment);
        AppController.getApiService().newComment(newComment, new Callback<ResponseStatusVM>() {
            @Override
            public void success(ResponseStatusVM responseStatus, Response response) {
                numCommentsText.setText((post.getNumComments() + 1) + " " + getString(R.string.comments));
                updateCommentList(responseStatus.objId, newComment.body);
                Toast.makeText(ProductActivity.this, ProductActivity.this.getString(R.string.comment_success), Toast.LENGTH_LONG).show();
                reset();
                pending = false;
                ViewUtil.stopSpinner(ProductActivity.this);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(TAG, "doComment.api.newComment: failed with error", error);
                Toast.makeText(ProductActivity.this, ProductActivity.this.getString(R.string.comment_failed), Toast.LENGTH_SHORT).show();
                reset();
                pending = false;
                ViewUtil.stopSpinner(ProductActivity.this);
            }
        });
    }

    private void updateCommentList(Long commentId, String body) {
        CommentVM comment = new CommentVM();
        comment.id = commentId;
        comment.createdDate = new DateTime().getMillis();
        comment.ownerId = UserInfoCache.getUser().id;
        comment.ownerName = UserInfoCache.getUser().displayName;
        comment.body = body;
        comment.deviceType = AppController.DeviceType.ANDROID.name();

        if (post.latestComments == null) {
            post.latestComments = new ArrayList<>();
        }
        post.latestComments.add(comment);
        commentListAdapter.notifyDataSetChanged();
        ViewUtil.setHeightBasedOnChildren(ProductActivity.this, commentList);
    }

    private void deletePost(Long id) {
        if (pending) {
            return;
        }

        ViewUtil.showSpinner(this);

        pending = true;
        AppController.getApiService().deletePost(id, new Callback<Response>() {
            @Override
            public void success(Response responseObject, Response response) {
                Toast.makeText(ProductActivity.this, getString(R.string.post_delete_success), Toast.LENGTH_SHORT).show();

                UserInfoCache.decrementNumProducts();

                // pass back to feed view to handle
                setActivityResult(ItemChangedState.ITEM_REMOVED, null);

                pending = false;
                ViewUtil.stopSpinner(ProductActivity.this);

                finish();
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(ProductActivity.this, getString(R.string.post_delete_failed), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "deletePost: failure", error);
                pending = false;
                ViewUtil.stopSpinner(ProductActivity.this);
            }
        });
    }

    private void reset() {
        if (commentPopup != null) {
            commentEditText.setText("");
            commentPopup.dismiss();
            commentPopup = null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.report:
                Log.d(TAG, "onOptionsItemSelected: "+item.getItemId());
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        reset();
    }

    public void follow(Long id){
        if (pending) {
            return;
        }

        pending = true;
        AppController.getApiService().followUser(id, new Callback<Response>() {
            @Override
            public void success(Response responseObject, Response response) {
                ViewUtil.selectFollowButtonStyleLite(followButton);
                isFollowing = true;
                pending = false;
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(TAG, "follow: failure", error);
                pending = false;
            }
        });
    }

    public void unfollow(Long id){
        if (pending) {
            return;
        }

        pending = true;
        AppController.getApiService().unfollowUser(id, new Callback<Response>() {
            @Override
            public void success(Response responseObject, Response response) {
                ViewUtil.unselectFollowButtonStyle(followButton);
                isFollowing = false;
                pending = false;
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(TAG, "unfollow: failure", error);
                pending = false;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: requestCode:" + requestCode + " resultCode:" + resultCode + " data:" + data);

        if (requestCode == ViewUtil.START_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            boolean refresh = data.getBooleanExtra(ViewUtil.INTENT_RESULT_REFRESH, false);
            if (refresh) {
                getProduct(postId);
            }

            // changed state
            ItemChangedState itemChangedState = null;
            try {
                itemChangedState = Enum.valueOf(ItemChangedState.class, data.getStringExtra(ViewUtil.INTENT_RESULT_ITEM_CHANGED_STATE));
            } catch (Exception e) {
            }

            if (itemChangedState == ItemChangedState.ITEM_REMOVED) {
                Log.d(this.getClass().getSimpleName(), "onActivityResult: feedAdapter ITEM_REMOVED");
                // pass back to feed view to handle
                setActivityResult(AbstractFeedViewFragment.ItemChangedState.ITEM_REMOVED, null);
                finish();
            }
        }
    }
}

class ProductImagePagerAdapter extends FragmentStatePagerAdapter {
    private static final String TAG = ProductImagePagerAdapter.class.getName();

    private Long[] images;
    private boolean sold;

    public ProductImagePagerAdapter(FragmentManager fm, Long[] images, boolean sold) {
        super(fm);
        this.images = images;
        this.sold = sold;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "";
    }

    @Override
    public int getCount() {
        return images == null? 0 : images.length;
    }

    @Override
    public Fragment getItem(int position) {
        Log.d(TAG, "getItem: item - " + position);
        switch (position) {
            default: {
                ProductImagePagerFragment fragment = new ProductImagePagerFragment();
                fragment.setImageId(images[position]);
                fragment.setSold(sold);
                return fragment;
            }
        }
    }

    /**
     * HACK... returns POSITION_NONE will refresh pager more frequent than needed... but works in this case
     * http://stackoverflow.com/questions/12510404/reorder-pages-in-fragmentstatepageradapter-using-getitempositionobject-object
     *
     * @param object
     * @return
     */
    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}

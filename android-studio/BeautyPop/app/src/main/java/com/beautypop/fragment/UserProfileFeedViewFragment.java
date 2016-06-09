package com.beautypop.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.beautypop.R;
import com.beautypop.activity.LeaveReviewActivity;
import com.beautypop.activity.MainActivity;
import com.beautypop.activity.UserReviewsActivity;
import com.beautypop.app.AppController;
import com.beautypop.app.UserInfoCache;
import com.beautypop.util.FeedFilter;
import com.beautypop.util.ImageUtil;
import com.beautypop.util.UrlUtil;
import com.beautypop.util.ViewUtil;
import com.beautypop.viewmodel.UserVM;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import org.parceler.apache.commons.lang.StringUtils;

import java.lang.reflect.Field;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class UserProfileFeedViewFragment extends FeedViewFragment {

    private static final String TAG = UserProfileFeedViewFragment.class.getName();

    protected ImageView coverImage, profileImage, editCoverImage, editProfileImage, settingsIcon, fbImage;
	protected ImageView star1,star2,star3,star4,star5;
    protected TextView userNameText, followersText, followingsText, userInfoText, userDescText, sellerUrlText,totalReviews;
    protected LinearLayout userInfoLayout, ratingsLayout;
    protected Button loginAsButton, editButton, followButton, productsButton, likesButton;

    protected FrameLayout tipsLayout;
    protected ImageView dismissTipsButton;

    protected boolean following;

    protected Long userId;
    protected FeedFilter.FeedType feedType;

    @Override
    protected FeedViewItemsLayout getFeedViewItemsLayout() {
        return FeedViewItemsLayout.TWO_COLUMNS;
    }

    @Override
    protected View getHeaderView(LayoutInflater inflater) {
        if (headerView == null) {
            headerView = inflater.inflate(R.layout.user_profile_feed_view_header, null);
        }
        return headerView;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        RelativeLayout toolbarOffsetLayout = (RelativeLayout) headerView.findViewById(R.id.toolbarOffsetLayout);
        toolbarOffsetLayout.setVisibility(View.GONE);

        userNameText = (TextView) headerView.findViewById(R.id.userNameText);
		totalReviews = (TextView) headerView.findViewById(R.id.totalReviews);
        coverImage = (ImageView) headerView.findViewById(R.id.coverImage);
        profileImage = (ImageView) headerView.findViewById(R.id.userImage);
        editCoverImage = (ImageView) headerView.findViewById(R.id.editCoverImage);
        editProfileImage = (ImageView) headerView.findViewById(R.id.editProfileImage);

		star1 = (ImageView) headerView.findViewById(R.id.star1);
		star2 = (ImageView) headerView.findViewById(R.id.star2);
		star3 = (ImageView) headerView.findViewById(R.id.star3);
		star4 = (ImageView) headerView.findViewById(R.id.star4);
		star5 = (ImageView) headerView.findViewById(R.id.star5);

        settingsIcon = (ImageView) headerView.findViewById(R.id.settingsIcon);

        followersText = (TextView) headerView.findViewById(R.id.followersText);
        followingsText = (TextView) headerView.findViewById(R.id.followingsText);

        followButton = (Button) headerView.findViewById(R.id.followButton);
        editButton = (Button) headerView.findViewById(R.id.editButton);

        productsButton = (Button) headerView.findViewById(R.id.productsButton);
        likesButton = (Button) headerView.findViewById(R.id.likesButton);

        tipsLayout = (FrameLayout) headerView.findViewById(R.id.tipsLayout);
        dismissTipsButton = (ImageView) headerView.findViewById(R.id.dismissTipsButton);

        userInfoLayout = (LinearLayout) headerView.findViewById(R.id.userInfoLayout);
        ratingsLayout = (LinearLayout) headerView.findViewById(R.id.ratingsLayout);
        userInfoText = (TextView) headerView.findViewById(R.id.userInfoText);
        fbImage = (ImageView) headerView.findViewById(R.id.fbImage);
        loginAsButton = (Button) headerView.findViewById(R.id.loginAsButton);
        userDescText = (TextView) headerView.findViewById(R.id.userDescText);
        sellerUrlText = (TextView) headerView.findViewById(R.id.sellerUrlText);

        setUserId(getArguments().getLong(ViewUtil.BUNDLE_KEY_ID));

        // init
        initLayout();
        initUserProfile();

        selectFeedFilter(FeedFilter.FeedType.USER_POSTED);

        return view;
    }

    /**
     * Subclass to override
     */
    protected void initLayout() {
        // hide
        editCoverImage.setVisibility(View.GONE);
        editProfileImage.setVisibility(View.GONE);
        editButton.setVisibility(View.GONE);
        settingsIcon.setVisibility(View.GONE);
        tipsLayout.setVisibility(View.GONE);
        userInfoLayout.setVisibility(View.GONE);

        // show
        ratingsLayout.setVisibility(View.VISIBLE);

        if (userId.equals(UserInfoCache.getUser().id)) {
            followButton.setVisibility(View.GONE);
        } else {
            followButton.setVisibility(View.VISIBLE);
        }

        // actions
        ratingsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), UserReviewsActivity.class);
                intent.putExtra(ViewUtil.BUNDLE_KEY_ID, userId);
                startActivity(intent);
            }
        });

        productsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectFeedFilter(FeedFilter.FeedType.USER_POSTED);
            }
        });

        likesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectFeedFilter(FeedFilter.FeedType.USER_LIKED);
            }
        });
    }

    protected void initUserInfoLayout(final UserVM user) {
        userNameText.setVisibility(StringUtils.isEmpty(user.name)? View.GONE : View.VISIBLE);
        userNameText.setText(user.name);
        totalReviews.setText("(" + user.getNumReviews() + ")");

		Log.d("Review score=",user.getAverageReviewScore()+"");

		if(user.getAverageReviewScore() >= 1.0 && user.getAverageReviewScore() <= 1.25){
            star1.setImageResource(R.drawable.star_selected);
		}else if(user.getAverageReviewScore() > 1.25 && user.getAverageReviewScore() <= 1.75){
			star1.setImageResource(R.drawable.star_selected);
			star2.setImageResource(R.drawable.star_half_selected);
		}else if(user.getAverageReviewScore() > 1.75 && user.getAverageReviewScore() <= 2.25){
			star1.setImageResource(R.drawable.star_selected);
			star2.setImageResource(R.drawable.star_selected);
		}else if(user.getAverageReviewScore() > 2.25 && user.getAverageReviewScore() <= 2.75){
			star1.setImageResource(R.drawable.star_selected);
			star2.setImageResource(R.drawable.star_selected);
			star3.setImageResource(R.drawable.star_half_selected);
		}else if(user.getAverageReviewScore() > 2.75 && user.getAverageReviewScore() <= 3.25){
			star1.setImageResource(R.drawable.star_selected);
			star2.setImageResource(R.drawable.star_selected);
			star3.setImageResource(R.drawable.star_selected);
		}else if(user.getAverageReviewScore() > 3.25 && user.getAverageReviewScore() <= 3.75){
			star1.setImageResource(R.drawable.star_selected);
			star2.setImageResource(R.drawable.star_selected);
			star3.setImageResource(R.drawable.star_selected);
			star4.setImageResource(R.drawable.star_half_selected);
		}else if(user.getAverageReviewScore() > 3.75 && user.getAverageReviewScore() <= 4.25){
			star1.setImageResource(R.drawable.star_selected);
			star2.setImageResource(R.drawable.star_selected);
			star3.setImageResource(R.drawable.star_selected);
			star4.setImageResource(R.drawable.star_selected);
		}else if(user.getAverageReviewScore() > 4.25 && user.getAverageReviewScore() <= 4.75){
			star1.setImageResource(R.drawable.star_selected);
			star2.setImageResource(R.drawable.star_selected);
			star3.setImageResource(R.drawable.star_selected);
			star4.setImageResource(R.drawable.star_selected);
			star5.setImageResource(R.drawable.star_half_selected);
		}else if(user.getAverageReviewScore() > 4.75 && user.getAverageReviewScore() <= 5.0){
			star1.setImageResource(R.drawable.star_selected);
			star2.setImageResource(R.drawable.star_selected);
			star3.setImageResource(R.drawable.star_selected);
			star4.setImageResource(R.drawable.star_selected);
			star5.setImageResource(R.drawable.star_selected);
		}

        if (!StringUtils.isEmpty(user.aboutMe)) {
            userDescText.setVisibility(View.VISIBLE);
            userDescText.setText(user.aboutMe);
        } else {
            userDescText.setVisibility(View.GONE);
        }

        sellerUrlText.setText(UrlUtil.createShortSellerUrl(user));
        sellerUrlText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ViewUtil.copyToClipboard(UrlUtil.createSellerUrl(user))) {
                    Toast.makeText(getActivity(), getString(R.string.url_copy_success), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), getString(R.string.url_copy_failed), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Subclass to override
     */
    protected void initUserProfile() {
        ViewUtil.showSpinner(getActivity());

        AppController.getApiService().getUser(userId, new Callback<UserVM>() {
            @Override
            public void success(final UserVM user, retrofit.client.Response response) {
                setActionBarTitle(user.displayName);

                initUserInfoLayout(user);

                ImageUtil.displayProfileImage(userId, profileImage, new RequestListener<String, GlideBitmapDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideBitmapDrawable> target, boolean isFirstResource) {
                        ViewUtil.stopSpinner(getActivity());
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideBitmapDrawable resource, String model, Target<GlideBitmapDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        ViewUtil.stopSpinner(getActivity());
                        return false;
                    }
                });

                followersText.setText(ViewUtil.formatFollowers(user.numFollowers));

                followersText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ViewUtil.startFollowersActivity(getActivity(), userId);
                    }
                });

                followingsText.setText(ViewUtil.formatFollowings(user.numFollowings));
                followingsText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ViewUtil.startFollowingsActivity(getActivity(), userId);
                    }
                });

                following = user.isFollowing;
                if (following) {
                    ViewUtil.selectFollowButtonStyle(followButton);
                } else {
                    ViewUtil.unselectFollowButtonStyle(followButton);
                }

                followButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (following) {
                            unFollow(user.id);
                        } else {
                            follow(user.id);
                        }
                    }
                });

                productsButton.setText(ViewUtil.formatProductsTab(user.numProducts));
                likesButton.setText(ViewUtil.formatLikesTab(user.numLikes));

                // admin only
                if (AppController.isUserAdmin()) {
                    userInfoText.setText(user.toString());
                    userInfoLayout.setVisibility(View.VISIBLE);

                    // fb login
                    fbImage.setVisibility(user.isFbLogin? View.VISIBLE : View.GONE);

                    // login as
                    loginAsButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setMessage(R.string.logout_message)
                                    .setCancelable(false)
                                    .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            AppController.getInstance().logout(user.email);
                                        }
                                    })
                                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });
                            AlertDialog alert = builder.create();
                            alert.show();
                        }
                    });
                } else {
                    userInfoLayout.setVisibility(View.GONE);
                }
                userInfoLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        userInfoLayout.setVisibility(View.GONE);
                    }
                });
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(TAG, "getUserProfile: failure", error);
                ViewUtil.stopSpinner(getActivity());
            }
        });
    }

    protected void selectFeedFilter(FeedFilter.FeedType feedType) {
        selectFeedFilter(feedType, true);
    }

    protected void selectFeedFilter(FeedFilter.FeedType feedType, boolean loadFeed) {
        if (FeedFilter.FeedType.USER_POSTED.equals(feedType)) {
            ViewUtil.selectProfileFeedButtonStyle(productsButton);
        } else {
            ViewUtil.unselectProfileFeedButtonStyle(productsButton);
        }

        if (FeedFilter.FeedType.USER_LIKED.equals(feedType)) {
            ViewUtil.selectProfileFeedButtonStyle(likesButton);
        } else {
            ViewUtil.unselectProfileFeedButtonStyle(likesButton);
        }

        this.feedType = feedType;

        if (loadFeed) {
            reloadFeed(new FeedFilter(
                    feedType,
                    userId));
        }
    }

    protected void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    protected void onRefreshView() {
        super.onRefreshView();
        initUserProfile();
    }

    @Override
    public void onDetach() {
        super.onDetach();

        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void follow(Long id){
        AppController.getApiService().followUser(id, new Callback<Response>() {
            @Override
            public void success(Response responseObject, Response response) {
                ViewUtil.selectFollowButtonStyle(followButton);
                following = true;
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
            }
        });
    }

    public void unFollow(Long id){
        AppController.getApiService().unfollowUser(id, new Callback<Response>() {
            @Override
            public void success(Response responseObject, Response response) {
                ViewUtil.unselectFollowButtonStyle(followButton);
                following = false;
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
            }
        });
    }

    @Override
    protected void onScrollUp() {
        if (MainActivity.getInstance() != null) {
            //MainActivity.getInstance().showToolbar(true);
        }
    }

    @Override
    protected void onScrollDown() {
        if (MainActivity.getInstance() != null) {
            //MainActivity.getInstance().showToolbar(false);
        }
    }
}
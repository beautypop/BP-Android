package com.beautypop.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.beautypop.R;
import com.beautypop.app.AppController;
import com.beautypop.app.NotificationCounter;
import com.beautypop.app.TrackedFragment;
import com.beautypop.app.TrackedFragmentActivity;
import com.beautypop.app.UserInfoCache;
import com.beautypop.fragment.ActivityMainFragment;
import com.beautypop.fragment.HomeMainFragment;
import com.beautypop.fragment.ProfileMainFragment;
import com.beautypop.fragment.SellerMainFragment;
import com.beautypop.fragment.ThemeFragment;
import com.beautypop.listener.EndlessScrollListener;
import com.beautypop.util.DefaultValues;
import com.beautypop.util.SharedPreferencesUtil;
import com.beautypop.util.ViewUtil;
import com.beautypop.viewmodel.NotificationCounterVM;

public class MainActivity extends TrackedFragmentActivity {
    private static final String TAG = MainActivity.class.getName();

    private ImageView gameBadgeImage;

    private ViewGroup chatLayout, newPostLayout;
    private TextView chatCountText;
    private LinearLayout bottomBarLayout;

    private LinearLayout homeLayout,themeLayout;
    private ImageView homeImage,themeImage;
    private TextView homeText,themeText;

    private LinearLayout sellerLayout;
    private ImageView sellerImage;
    private TextView sellerText;

    private LinearLayout activityLayout;
    private ImageView activityImage;
    private TextView activityText;
    private TextView activityCountText;

    private LinearLayout profileLayout;
    private ImageView profileImage,searchImage;
    private TextView profileText;
    private View toolBar;
    private boolean homeClicked = false, sellerClicked = false, activityClicked = false, profileClicked = false, themeClicked = false;

    private boolean animatingToolbar = false;
    private boolean showBottomMenuBar = true;

    private TrackedFragment selectedFragment;

    private static MainActivity mInstance;

    public static synchronized MainActivity getInstance() {
        return mInstance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTracked(false);

        setContentView(R.layout.main_activity);

        mInstance = this;

        gameBadgeImage = (ImageView) findViewById(R.id.gameBadgeImage);

		searchImage = (ImageView) findViewById(R.id.searchImage);

        chatCountText = (TextView) findViewById(R.id.chatCountText);
        chatLayout = (ViewGroup) findViewById(R.id.chatLayout);
        newPostLayout = (ViewGroup) findViewById(R.id.newPostLayout);

        /*
        gameBadgeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewUtil.startGameBadgesActivity(MainActivity.this, UserInfoCache.getUser().getId());
            }
        });
        */

        chatLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewUtil.startConversationListActivity(MainActivity.this);
            }
        });

        newPostLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewUtil.startNewPostActivity(MainActivity.this);
            }
        });

        // bottom menu bar
        bottomBarLayout = (LinearLayout) findViewById(R.id.bottomBarLayout);

        homeLayout = (LinearLayout) findViewById(R.id.homeLayout);
		themeLayout = (LinearLayout) findViewById(R.id.themeLayout);
        homeImage = (ImageView) findViewById(R.id.homeImage);
        themeImage = (ImageView) findViewById(R.id.themeImage);
        homeText = (TextView) findViewById(R.id.homeText);
        themeText = (TextView) findViewById(R.id.themeText);

        sellerLayout = (LinearLayout) findViewById(R.id.sellerLayout);
        sellerImage = (ImageView) findViewById(R.id.sellerImage);
        sellerText = (TextView) findViewById(R.id.sellerText);

        activityLayout = (LinearLayout) findViewById(R.id.activityLayout);
        activityImage = (ImageView) findViewById(R.id.activityImage);
        activityText = (TextView) findViewById(R.id.activityText);
        activityCountText = (TextView) findViewById(R.id.activityCountText);

        profileLayout = (LinearLayout) findViewById(R.id.profileLayout);
        profileImage = (ImageView) findViewById(R.id.profileImage);
        profileText = (TextView) findViewById(R.id.profileText);

        homeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Home tab clicked");
                pressHomeTab();
            }
        });

		themeLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d(TAG, "onClick: Theme tab clicked");
				pressThemeTab();
			}
		});

        sellerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Seller tab clicked");
                pressSellerTab();
            }
        });

        activityLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Activity tab clicked");
                pressActivityTab();
            }
        });

        profileLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Profile tab clicked");
                pressProfileTab();
            }
        });

		searchImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewUtil.startSearchActivity(MainActivity.this);
            }
        });

        // tour
        if (!SharedPreferencesUtil.getInstance().isScreenViewed(SharedPreferencesUtil.Screen.TOUR)) {
            ViewUtil.startTourActivity(this);
        }

        pressHomeTab();

        checkAndroidUpgrade();
    }

    @Override
    public void onResume() {
        super.onResume();

        // Refresh fom Home and My profile page
        //NotificationCounter.refresh();

        // handle gcm
        if (ViewUtil.isGcmLaunchTarget(getIntent())) {
            pressActivityTab();
            getIntent().removeExtra(ViewUtil.GCM_LAUNCH_TARGET);
        }
    }

	public void pressThemeTab(){
		if (themeClicked) {
			return;
		}

		FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
		selectedFragment = new ThemeFragment();
		fragmentTransaction.replace(R.id.placeHolder, selectedFragment).commit();

		showToolbar(true, false);
		showToolbarTitle(false);

		setMenuButton(themeImage, themeText, R.drawable.ic_view_grid, R.color.sharp_pink);
		themeClicked = true;

		setMenuButton(sellerImage, sellerText, R.drawable.mn_seller, R.color.dark_gray_2);
		sellerClicked = false;

		setMenuButton(activityImage, activityText, R.drawable.mn_notif, R.color.dark_gray_2);
		activityClicked = false;

		setMenuButton(profileImage, profileText, R.drawable.mn_profile, R.color.dark_gray_2);
		profileClicked = false;

		setMenuButton(homeImage, homeText, R.drawable.mn_home, R.color.dark_gray_2);
		homeClicked = false;

	}

    public void pressHomeTab() {
        if (homeClicked) {
            return;
        }

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        selectedFragment = new HomeMainFragment();
        fragmentTransaction.replace(R.id.placeHolder, selectedFragment).commit();

        showToolbar(true, false);
        showToolbarTitle(false);

        setMenuButton(homeImage, homeText, R.drawable.mn_home_sel, R.color.sharp_pink);
        homeClicked = true;

		setMenuButton(themeImage, themeText, R.drawable.ic_view_grid, R.color.sharp_pink);
		themeClicked = false;

        setMenuButton(sellerImage, sellerText, R.drawable.mn_seller, R.color.dark_gray_2);
        sellerClicked = false;

        setMenuButton(activityImage, activityText, R.drawable.mn_notif, R.color.dark_gray_2);
        activityClicked = false;

        setMenuButton(profileImage, profileText, R.drawable.mn_profile, R.color.dark_gray_2);
        profileClicked = false;
    }

    public void pressSellerTab() {
        pressSellerTab(0);
    }

    public void pressSellerTab(int tab) {
        if (sellerClicked) {
            SellerMainFragment.selectTab(tab);
            return;
        }

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        selectedFragment = new SellerMainFragment();
        fragmentTransaction.replace(R.id.placeHolder, selectedFragment).commit();

        showToolbar(true, false);
        showToolbarTitle(false);

        setMenuButton(homeImage, homeText, R.drawable.mn_home, R.color.dark_gray_2);
        homeClicked = false;

		setMenuButton(themeImage, themeText, R.drawable.ic_view_grid, R.color.dark_gray_2);
		themeClicked = false;

        setMenuButton(sellerImage, sellerText, R.drawable.mn_seller_sel, R.color.sharp_pink);
        sellerClicked = true;

        setMenuButton(activityImage, activityText, R.drawable.mn_notif, R.color.dark_gray_2);
        activityClicked = false;

        setMenuButton(profileImage, profileText, R.drawable.mn_profile, R.color.dark_gray_2);
        profileClicked = false;

        SellerMainFragment.selectTab(tab);
    }

    public void pressActivityTab() {
        if (activityClicked) {
            return;
        }

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        selectedFragment = new ActivityMainFragment();
        fragmentTransaction.replace(R.id.placeHolder, selectedFragment).commit();

        showToolbar(true, false);
        showToolbarTitle(false);

        // read... clear gcm notifs
        SharedPreferencesUtil.getInstance().clear(SharedPreferencesUtil.GCM_COMMENT_NOTIFS);
        SharedPreferencesUtil.getInstance().clear(SharedPreferencesUtil.GCM_FOLLOW_NOTIFS);

        setMenuButton(homeImage, homeText, R.drawable.mn_home, R.color.dark_gray_2);
        homeClicked = false;

		setMenuButton(themeImage, themeText, R.drawable.ic_view_grid, R.color.dark_gray_2);
		themeClicked = false;

        setMenuButton(sellerImage, sellerText, R.drawable.mn_seller, R.color.dark_gray_2);
        sellerClicked = false;

        setMenuButton(activityImage, activityText, R.drawable.mn_notif_sel, R.color.sharp_pink);
        activityClicked = true;

        setMenuButton(profileImage, profileText, R.drawable.mn_profile, R.color.dark_gray_2);
        profileClicked = false;
    }

    public void pressProfileTab() {
        pressProfileTab(false);
    }

    public void pressProfileTab(boolean refresh) {
        if (profileClicked && !refresh) {
            return;
        }

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        selectedFragment = new ProfileMainFragment();
        selectedFragment.setTrackedOnce();
        fragmentTransaction.replace(R.id.placeHolder, selectedFragment).commit();

        showToolbar(true, false);
        showToolbarTitle(true);

        setMenuButton(homeImage, homeText, R.drawable.mn_home, R.color.dark_gray_2);
        homeClicked = false;

		setMenuButton(themeImage, themeText, R.drawable.ic_view_grid, R.color.dark_gray_2);
		themeClicked = false;

        setMenuButton(sellerImage, sellerText, R.drawable.mn_seller, R.color.dark_gray_2);
        sellerClicked = false;

        setMenuButton(activityImage, activityText, R.drawable.mn_notif, R.color.dark_gray_2);
        activityClicked = false;

        setMenuButton(profileImage, profileText, R.drawable.mn_profile_sel, R.color.sharp_pink);
        profileClicked = true;
    }

    private void setMenuButton(ImageView imageView, TextView textView, int image, int textColor) {
        imageView.setImageDrawable(this.getResources().getDrawable(image));
        textView.setTextColor(this.getResources().getColor(textColor));
    }

    @Override
    public void onBackPressed() {
        if (selectedFragment != null && !selectedFragment.allowBackPressed()) {
            return;
        }

        if (isTaskRoot()) {
            if (!showBottomMenuBar) {
                resetControls();
                return;
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.exit_app)
                    .setCancelable(false)
                    .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            AppController.getInstance().clearUserCaches();
                            MainActivity.super.onBackPressed();
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.d(TAG, "onDestroy: clear all");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: requestCode:" + requestCode + " resultCode:" + resultCode + " data:" + data);

        if (requestCode == ViewUtil.START_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            boolean refresh = data.getBooleanExtra(ViewUtil.INTENT_RESULT_REFRESH, false);
            if (refresh) {
                Log.d(TAG, "onActivityResult: pressProfileTab");
                pressProfileTab(true);
                return;     // handled... dont trickle down to fragments
            }
        }

        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            if (fragment != null) {
                Log.d(TAG, "onActivityResult: propagate to fragment=" + fragment.getClass().getSimpleName());
                fragment.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    public void refreshNotifications() {
        NotificationCounterVM counter = NotificationCounter.getCounter();
        if (counter == null) {
            return;
        }

        Log.d(TAG, "refreshNotifications: activitiesCount=" + counter.activitiesCount + " conversationsCount=" + counter.conversationsCount);

        if (counter.activitiesCount == 0) {
            activityCountText.setVisibility(View.INVISIBLE);
        } else {
            activityCountText.setVisibility(View.VISIBLE);
            activityCountText.setText(counter.activitiesCount+"");
        }

        if (counter.conversationsCount == 0) {
            chatCountText.setVisibility(View.INVISIBLE);
        } else {
            chatCountText.setVisibility(View.VISIBLE);
            chatCountText.setText(counter.conversationsCount + "");
        }
    }

    public void checkAndroidUpgrade() {
        if (UserInfoCache.requestAndroidUpgrade()) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage(getString(R.string.request_upgrade));
            alertDialogBuilder.setPositiveButton(getString(R.string.request_upgrade_confirm), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ViewUtil.openPlayStoreForUpgrade(MainActivity.this);
                }
            });
            alertDialogBuilder.setNegativeButton(getString(R.string.request_upgrade_cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    UserInfoCache.skipAndroidUpgrade();
                }
            });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
    }

    public void showToolbar(boolean show) {
        showToolbar(show, true);
    }

    public void showToolbar(boolean show, boolean animate) {
        final Toolbar toolbar = getToolbar();
        if (toolbar == null || animatingToolbar) {
            return;
        }

        animatingToolbar = true;
        if (show) {
            ViewPropertyAnimator animator = toolbar.animate().translationY(0).setListener(
                    new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            animatingToolbar = false;
                        }
                    });
            if (animate) {
                animator.setDuration(300).setInterpolator(new DecelerateInterpolator(2)).start();
            } else {
                animator.setDuration(0).start();
            }
        } else {
            ViewPropertyAnimator animator = toolbar.animate().translationY(DefaultValues.MAIN_TOOLBAR_MIN_HEIGHT - toolbar.getHeight()).setListener(
                    new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            animatingToolbar = false;
                        }
                    });
            if (animate) {
                animator.setDuration(300).setInterpolator(new AccelerateInterpolator(2)).start();
            } else {
                animator.setDuration(0).start();
            }
        }
    }

    public void showBottomMenuBar(boolean show) {
        if (bottomBarLayout == null) {
            return;
        }

        if (show) {
            bottomBarLayout.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
        } else {
            bottomBarLayout.animate().translationY(bottomBarLayout.getHeight()).setInterpolator(new AccelerateInterpolator(2)).start();
        }
        showBottomMenuBar = show;
    }

    public void resetControls() {
        if (!showBottomMenuBar) {
            showBottomMenuBar(true);
            EndlessScrollListener.setScrollReset();
        }
    }
}
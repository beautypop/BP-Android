package com.beautypop.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.beautypop.R;
import com.beautypop.app.TrackedFragmentActivity;
import com.beautypop.fragment.ImagePagerFragment;
import com.beautypop.util.ViewUtil;
import com.beautypop.view.AdaptiveViewPager;

import java.util.ArrayList;
import java.util.List;

public class WelcomeActivity extends TrackedFragmentActivity {
    private static final String TAG = WelcomeActivity.class.getName();

    private AdaptiveViewPager imagePager;
    private WelcomeImagePagerAdapter imagePagerAdapter;
    private LinearLayout dotsLayout;
    private List<ImageView> dots = new ArrayList<>();

    private TextView loginText, signupText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.welcome_activity);

        imagePager = (AdaptiveViewPager) findViewById(R.id.imagePager);
        dotsLayout = (LinearLayout) findViewById(R.id.dotsLayout);

        loginText = (TextView) findViewById(R.id.loginText);
        signupText = (TextView) findViewById(R.id.signupText);

        imagePagerAdapter = new WelcomeImagePagerAdapter(getSupportFragmentManager());
        imagePager.setAdapter(imagePagerAdapter);
        imagePager.setCurrentItem(0);
        ViewUtil.addDots(WelcomeActivity.this, imagePagerAdapter.getCount(), dotsLayout, dots, imagePager);

        loginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewUtil.startLoginActivity(WelcomeActivity.this);
            }
        });

        signupText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewUtil.startSignupActivity(WelcomeActivity.this);
            }
        });
    }
}

class WelcomeImagePagerAdapter extends FragmentStatePagerAdapter {

    private int[] images = {
            R.drawable.welcome_1,
            R.drawable.welcome_2,
            R.drawable.welcome_3
    };

    public WelcomeImagePagerAdapter(FragmentManager fm) {
        super(fm);
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
        Log.d(this.getClass().getSimpleName(), "getItem: item - " + position);
        switch (position) {
            default: {
                if (position < images.length) {
                    ImagePagerFragment fragment = new ImagePagerFragment();
                    fragment.setImageSource(images[position]);
                    return fragment;
                }
                return null;
            }
        }
    }
}
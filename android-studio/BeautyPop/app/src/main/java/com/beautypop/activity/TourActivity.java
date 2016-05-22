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
import com.beautypop.util.DefaultValues;
import com.beautypop.util.SharedPreferencesUtil;
import com.beautypop.util.ViewUtil;
import com.beautypop.view.AdaptiveViewPager;

import java.util.ArrayList;
import java.util.List;

public class TourActivity extends TrackedFragmentActivity {
    private static final String TAG = TourActivity.class.getName();

    private AdaptiveViewPager imagePager;
    private TourImagePagerAdapter imagePagerAdapter;
    private LinearLayout dotsLayout;
    private List<ImageView> dots = new ArrayList<>();

    private TextView doneText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.tour_activity);

        imagePager = (AdaptiveViewPager) findViewById(R.id.imagePager);
        dotsLayout = (LinearLayout) findViewById(R.id.dotsLayout);
        doneText = (TextView) findViewById(R.id.doneText);

        imagePagerAdapter = new TourImagePagerAdapter(getSupportFragmentManager(), this);
        imagePager.setAdapter(imagePagerAdapter);
        imagePager.setCurrentItem(0);
        ViewUtil.addDots(TourActivity.this, imagePagerAdapter.getCount(), dotsLayout, dots, imagePager);

        doneText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferencesUtil.getInstance().setScreenViewed(SharedPreferencesUtil.Screen.TOUR);
                finish();
            }
        });

        doneText.setVisibility(View.GONE);
    }

    public void showDoneAction(boolean show) {
        doneText.setVisibility(show? View.VISIBLE : View.GONE);
    }
}

class TourImagePagerAdapter extends FragmentStatePagerAdapter {

    private int[] en_images = {
            R.drawable.tour_1_en,
            R.drawable.tour_2_en,
            R.drawable.tour_3_en,
            R.drawable.tour_4_en,
            R.drawable.tour_5_en,
            R.drawable.tour_6_en,
            R.drawable.tour_7_en,
            R.drawable.tour_8_en
    };

    private int[] zh_images = {
            R.drawable.tour_1_zh,
            R.drawable.tour_2_zh,
            R.drawable.tour_3_zh,
            R.drawable.tour_4_zh,
            R.drawable.tour_5_zh,
            R.drawable.tour_6_zh,
            R.drawable.tour_7_zh,
            R.drawable.tour_8_zh
    };

    private int[] images;

    private TourActivity activity;

    public TourImagePagerAdapter(FragmentManager fm, TourActivity activity) {
        super(fm);

        this.activity = activity;

        String lang = SharedPreferencesUtil.getInstance().getLang();
        if (DefaultValues.LANG_ZH.equalsIgnoreCase(lang)) {
            images = zh_images;
        } else if (DefaultValues.LANG_EN.equalsIgnoreCase(lang)) {
            images = en_images;
        } else {
            if (DefaultValues.DEFAULT_LANG.equalsIgnoreCase(DefaultValues.LANG_ZH)) {
                images = zh_images;
            } else {
                images = en_images;
            }
        }
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

        // done action
        this.activity.showDoneAction(position == images.length - 1);

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
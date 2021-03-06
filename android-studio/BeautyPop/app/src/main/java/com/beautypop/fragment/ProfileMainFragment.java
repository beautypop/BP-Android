package com.beautypop.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.beautypop.app.UserInfoCache;
import com.beautypop.util.DefaultValues;
import com.beautypop.util.ViewUtil;

import java.lang.reflect.Field;

import com.beautypop.R;
import com.beautypop.app.TrackedFragment;

public class ProfileMainFragment extends TrackedFragment {
    private static final String TAG = ProfileMainFragment.class.getName();
	private ImageView searchImage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.child_layout_view, container, false);
		searchImage = (ImageView) getActivity().findViewById(R.id.searchImage);

		searchImage.setVisibility(View.GONE);


        Bundle bundle = new Bundle();
        bundle.putString(ViewUtil.BUNDLE_KEY_FEED_TYPE, DefaultValues.DEFAULT_USER_FEED_TYPE.name());
        bundle.putString(ViewUtil.BUNDLE_KEY_FEED_FILTER_CONDITION_TYPE, DefaultValues.DEFAULT_FEED_FILTER_CONDITION_TYPE.name());
        bundle.putLong(ViewUtil.BUNDLE_KEY_ID, UserInfoCache.getUser().getId());

        TrackedFragment fragment = new MyProfileFeedViewFragment();
        fragment.setArguments(bundle);
        fragment.setTrackedOnce();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.childLayout, fragment, "profile").commit();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        // TODO: add back notification code
        /*
        NotificationCache.refresh(new Callback<NotificationsParentVM>() {
            @Override
            public void success(NotificationsParentVM notificationsParentVM, Response response) {
                // update notification count
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
            }
        });
        */
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (getChildFragmentManager() != null && getChildFragmentManager().getFragments() != null) {
            for (Fragment fragment : getChildFragmentManager().getFragments()) {
                if (fragment != null) {
                    Log.d(TAG, "onActivityResult: propagate to fragment=" + fragment.getClass().getSimpleName());
                    fragment.onActivityResult(requestCode, resultCode, data);
                }
            }
        }
    }
}

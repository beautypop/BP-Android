package com.beautypop.fragment;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.beautypop.R;
import com.beautypop.adapter.UserReviewAdapter;
import com.beautypop.app.AppController;
import com.beautypop.app.TrackedFragment;
import com.beautypop.viewmodel.ReviewVM;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SoldReviewsFragment extends TrackedFragment {
	private UserReviewAdapter adapter;
	private ListView listView;
	private TextView noItemText;

    private Long userId;

    public void setUserId(Long userId) {
        this.userId = userId;
    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		View rootView = inflater.inflate(R.layout.review_list_fragment, container, false);
		listView = (ListView) rootView.findViewById(R.id.feedList);
        noItemText = (TextView) rootView.findViewById(R.id.noItemText);

		AppController.getApiService().getBuyerReviewsFor(userId, new Callback<List<ReviewVM>>() {
            @Override
            public void success(List<ReviewVM> reviewVMs, Response response) {
                if (reviewVMs.size() == 0) {
                    noItemText.setVisibility(View.VISIBLE);
                    ((ViewPager)getActivity().findViewById(R.id.sellerPager)).setCurrentItem(1);
                }
                adapter = new UserReviewAdapter(getActivity(), reviewVMs);
                listView.setAdapter(adapter);
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
            }
        });

		return rootView;
	}
}

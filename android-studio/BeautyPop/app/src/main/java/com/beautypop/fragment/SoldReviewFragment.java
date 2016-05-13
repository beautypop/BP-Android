package com.beautypop.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.beautypop.R;
import com.beautypop.adapter.SoldReviewAdapter;
import com.beautypop.app.AppController;
import com.beautypop.app.TrackedFragment;
import com.beautypop.app.UserInfoCache;
import com.beautypop.viewmodel.ReviewVM;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SoldReviewFragment extends TrackedFragment {
	private SoldReviewAdapter soldReviewAdapter;
	private ListView listView;
	private TextView noItemText;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		View rootView = inflater.inflate(R.layout.review_list_fragment, container, false);
		listView = (ListView) rootView.findViewById(R.id.feedList);
        noItemText = (TextView) rootView.findViewById(R.id.noItemText);

		AppController.getApiService().getReviewsAsSeller(UserInfoCache.getUser().id, new Callback<List<ReviewVM>>() {
			@Override
			public void success(List<ReviewVM> reviewVMs, Response response) {

				if (reviewVMs.size() == 0) {
                    noItemText.setVisibility(View.VISIBLE);
				}
				soldReviewAdapter = new SoldReviewAdapter(getActivity(), reviewVMs);
				listView.setAdapter(soldReviewAdapter);
			}

			@Override
			public void failure(RetrofitError error) {
				error.printStackTrace();
			}
		});

		return rootView;
	}
}

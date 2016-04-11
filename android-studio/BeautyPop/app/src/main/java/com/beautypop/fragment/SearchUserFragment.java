package com.beautypop.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.beautypop.R;
import com.beautypop.adapter.UserListAdapter;
import com.beautypop.app.AppController;
import com.beautypop.app.TrackedFragment;
import com.beautypop.util.ViewUtil;
import com.beautypop.viewmodel.UserVM;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class SearchUserFragment extends TrackedFragment {

	private UserListAdapter userListAdapter;
	private ListView userListView;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {

		View view =  inflater.inflate(R.layout.search_user_fragment, container, false);
		userListView = (ListView) view.findViewById(R.id.userListView);


		AppController.getApiService().searchUser(getArguments().getString("searchText"),0,new Callback<List<UserVM>>() {
			@Override
			public void success(List<UserVM> userVMs, Response response) {
				userListAdapter = new UserListAdapter(getActivity(),userVMs);
				userListView.setAdapter(userListAdapter);
				ViewUtil.stopSpinner(getActivity());

			}
			@Override
			public void failure(RetrofitError error) {
				ViewUtil.stopSpinner(getActivity());
				error.printStackTrace();

			}
		});
		return view;
	}
}

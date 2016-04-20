package com.beautypop.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.beautypop.R;
import com.beautypop.activity.SearchResultActivity;
import com.beautypop.adapter.UserListAdapter;
import com.beautypop.app.AppController;
import com.beautypop.app.TrackedFragment;
import com.beautypop.listener.InfiniteScrollListener;
import com.beautypop.util.ViewUtil;
import com.beautypop.viewmodel.PostVMLite;
import com.beautypop.viewmodel.UserVM;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class SearchResultUserFragment extends TrackedFragment {

	private TextView noDataText;
	private UserListAdapter userListAdapter;
	private ListView userListView;
	private List<UserVM> items;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {

		View view =  inflater.inflate(R.layout.search_result_user_fragment, container, false);
		userListView = (ListView) view.findViewById(R.id.userListView);
		noDataText = (TextView) view.findViewById(R.id.noDataText);
		items = new ArrayList<>();

		ViewUtil.showSpinner(getActivity());

		userListAdapter = new UserListAdapter(getActivity(),items);
		userListView.setAdapter(userListAdapter);

		getUsers(0);
		attachEndlessScrollListener();

		return view;
	}

	protected void loadUsersToList(List<UserVM> vms) {

		Log.d("ResultUserFragment ::: ",vms.size()+"");
		items.addAll(vms);
		userListAdapter.notifyDataSetChanged();
		ViewUtil.stopSpinner(getActivity());

	}

	protected void getUsers(int offset){
		AppController.getApiService().searchUsers(getArguments().getString("searchText"),offset,new Callback<List<UserVM>>() {
			@Override
			public void success(List<UserVM> userVMs, Response response) {

				Log.d("userVMs sizw ::: ", userVMs.size() + "");

				if(userVMs.size() == 0){
					noDataText.setVisibility(View.VISIBLE);
					userListView.setVisibility(View.GONE);
				}else {
					loadUsersToList(userVMs);
				}

			}
			@Override
			public void failure(RetrofitError error) {

				if(items.size() == 0) {
					noDataText.setVisibility(View.VISIBLE);
					userListView.setVisibility(View.GONE);
				}

				error.printStackTrace();
				ViewUtil.stopSpinner(getActivity());

			}
		});
	}

	protected void attachEndlessScrollListener() {
		userListView.setOnScrollListener(new InfiniteScrollListener() {
			@Override
			public void onLoadMore(int page, int totalItemsCount) {
				Log.d("attachEndlessScrollListener ::: ", page + "");
				getUsers(page - 1);
			}

			@Override
			public void onScrollUp() {
				//MainActivity.getInstance().showToolbar(true);
			}

			@Override
			public void onScrollDown() {
				//MainActivity.getInstance().showToolbar(false);
			}
		});
	}
}

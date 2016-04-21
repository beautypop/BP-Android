package com.beautypop.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.beautypop.R;
import com.beautypop.adapter.SellerListAdapter;
import com.beautypop.app.AppController;
import com.beautypop.app.TrackedFragment;
import com.beautypop.listener.InfiniteScrollListener;
import com.beautypop.util.ViewUtil;
import com.beautypop.viewmodel.SellerVM;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SearchResultUserFragment extends TrackedFragment {
    private static final String TAG = SearchResultProductFragment.class.getName();

	private TextView noDataText;
	private SellerListAdapter adapter;
	private ListView userListView;
	private List<SellerVM> items;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view =  inflater.inflate(R.layout.search_result_user_fragment, container, false);
		userListView = (ListView) view.findViewById(R.id.userListView);
		noDataText = (TextView) view.findViewById(R.id.noDataText);
		items = new ArrayList<>();

        adapter = new SellerListAdapter(getActivity(),items);
		userListView.setAdapter(adapter);

		getUsers(0);
		attachEndlessScrollListener();

		return view;
	}

	protected void loadUsersToList(List<SellerVM> vms) {
		items.addAll(vms);
        adapter.notifyDataSetChanged();
		ViewUtil.stopSpinner(getActivity());
	}

	protected void getUsers(final int offset) {
        ViewUtil.showSpinner(getActivity());

        String searchKey = getArguments().getString(ViewUtil.BUNDLE_KEY_NAME);

        Log.d(TAG, "loadFeed: searchKey="+searchKey+" offset="+offset);
        AppController.getApiService().searchUsers(searchKey, offset, new Callback<List<SellerVM>>() {
            @Override
            public void success(List<SellerVM> users, Response response) {
                Log.d(TAG, "loadFeed.success: users.size=" + users.size());
                ViewUtil.stopSpinner(getActivity());
                if (offset == 0 && users.size() == 0) {
                    noDataText.setVisibility(View.VISIBLE);
                    userListView.setVisibility(View.GONE);
                } else if (users.size() > 0){
                    loadUsersToList(users);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                ViewUtil.stopSpinner(getActivity());
                Log.e(TAG, "loadFeed.failure", error);
            }
        });
	}

	protected void attachEndlessScrollListener() {
		userListView.setOnScrollListener(new InfiniteScrollListener() {
			@Override
			public void onLoadMore(int page, int totalItemsCount) {
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

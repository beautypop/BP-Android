package com.beautypop.fragment;

import android.content.Intent;
import android.os.Bundle;
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
import com.beautypop.util.ViewUtil;
import com.beautypop.viewmodel.UserVM;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class SearchUserFragment extends TrackedFragment {
	private RelativeLayout searchLayout;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {

		View view =  inflater.inflate(R.layout.search_user_fragment, container, false);
		searchLayout = (RelativeLayout) view.findViewById(R.id.searchLayout);

		ViewUtil.popupInputMethodWindow(getActivity());

		searchLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				if(getArguments().getString("searchText") != null) {
					Intent intent = new Intent(getActivity(), SearchResultActivity.class);
					intent.putExtra("searchText", getArguments().getString("searchText"));
					intent.putExtra("flag", "user");
					startActivity(intent);
				}else{
					Toast.makeText(getActivity(), "Enter search Text", Toast.LENGTH_LONG).show();
				}
			}
		});

		return view;
	}
}

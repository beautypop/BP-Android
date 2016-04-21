package com.beautypop.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.Toast;

import com.beautypop.R;
import com.beautypop.app.TrackedFragment;
import com.beautypop.util.ViewUtil;

public class SearchUserFragment extends TrackedFragment {
    private static final String TAG = SearchUserFragment.class.getName();

	private RelativeLayout searchLayout;
	private SearchView searchView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view =  inflater.inflate(R.layout.search_user_fragment, container, false);

		searchLayout = (RelativeLayout) view.findViewById(R.id.searchLayout);
		searchView = (SearchView) getActivity().findViewById(R.id.searchView);

		//ViewUtil.popupInputMethodWindow(getActivity());

		searchLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (searchView.getQuery().toString() != null && !searchView.getQuery().toString().equals("")) {
                    ViewUtil.startSearchResultActivity(getActivity(), "user", searchView.getQuery().toString());
				} else {
					Toast.makeText(getActivity(), "Enter search Text", Toast.LENGTH_LONG).show();
				}
			}
		});

		return view;
	}
}

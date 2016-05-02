package com.beautypop.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.beautypop.R;
import com.beautypop.util.ImageUtil;
import com.beautypop.viewmodel.ReviewVM;

import java.util.List;

/**
 * Created by JAY GANESH on 2/22/2016.
 */
public class SoldReviewAdapter extends BaseAdapter {
	private Activity activity;
	private LayoutInflater inflater;

	private List<ReviewVM> reviewVMs;

	public SoldReviewAdapter(Activity activity, List<ReviewVM> reviewVMs) {
		this.activity = activity;
		this.reviewVMs = reviewVMs;
	}

	@Override
	public int getCount() {
		return reviewVMs.size();
	}

	@Override
	public ReviewVM getItem(int i) {
		return reviewVMs.get(i);
	}

	@Override
	public long getItemId(int i) {
		return i;
	}

	@Override
	public View getView(int i, View view, ViewGroup viewGroup) {

		if (inflater == null)
			inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		if (view == null)
			view = inflater.inflate(R.layout.sold_review_item, null);

		ReviewVM reviewVM = reviewVMs.get(i);

		TextView userNameText = (TextView) view.findViewById(R.id.userNameText);
		TextView reviewText = (TextView) view.findViewById(R.id.reviewText);
		ImageView postImage = (ImageView) view.findViewById(R.id.postImage);
		ImageView userImage = (ImageView) view.findViewById(R.id.userImage);

		ImageView star1 = (ImageView) view.findViewById(R.id.star1);
		ImageView star2 = (ImageView) view.findViewById(R.id.star2);
		ImageView star3 = (ImageView) view.findViewById(R.id.star3);
		ImageView star4 = (ImageView) view.findViewById(R.id.star4);
		ImageView star5 = (ImageView) view.findViewById(R.id.star5);
		ImageUtil.displayPostImage(reviewVM.getPostId(), postImage);
		ImageUtil.displayProfileImage(reviewVM.getUserId(), userImage);

		userNameText.setText(reviewVM.getUserName());
		reviewText.setText(reviewVM.getReview());

		if(reviewVM.getScore() > 0.5 && reviewVM.getScore() <= 1.5){
			star1.setImageResource(R.drawable.star_selected);
		}else if(reviewVM.getScore() > 1.5 && reviewVM.getScore() <= 2.5){
			star1.setImageResource(R.drawable.star_selected);
			star2.setImageResource(R.drawable.star_selected);
		}else if(reviewVM.getScore() > 2.5 && reviewVM.getScore() <= 3.5){
			star1.setImageResource(R.drawable.star_selected);
			star2.setImageResource(R.drawable.star_selected);
			star3.setImageResource(R.drawable.star_selected);
		}else if(reviewVM.getScore() > 3.5 && reviewVM.getScore() <= 4.5){
			star1.setImageResource(R.drawable.star_selected);
			star2.setImageResource(R.drawable.star_selected);
			star3.setImageResource(R.drawable.star_selected);
			star4.setImageResource(R.drawable.star_selected);
		}else if(reviewVM.getScore() > 4.5){
			star1.setImageResource(R.drawable.star_selected);
			star2.setImageResource(R.drawable.star_selected);
			star3.setImageResource(R.drawable.star_selected);
			star4.setImageResource(R.drawable.star_selected);
			star5.setImageResource(R.drawable.star_selected);
		}

		return view;
	}
}

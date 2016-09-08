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
import com.beautypop.util.DateTimeUtil;
import com.beautypop.util.ImageUtil;
import com.beautypop.util.ViewUtil;
import com.beautypop.viewmodel.ReviewVM;

import java.util.List;

/**
 * Created by JAY GANESH on 2/22/2016.
 */
public class UserReviewAdapter extends BaseAdapter {
	private Activity activity;
	private LayoutInflater inflater;

	private List<ReviewVM> reviewVMs;

	public UserReviewAdapter(Activity activity, List<ReviewVM> reviewVMs) {
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
			view = inflater.inflate(R.layout.user_review_item, null);

		final ReviewVM reviewVM = reviewVMs.get(i);

		TextView userNameText = (TextView) view.findViewById(R.id.userNameText);
		TextView reviewText = (TextView) view.findViewById(R.id.reviewText);
		TextView timeText = (TextView) view.findViewById(R.id.timeText);
		ImageView postImage = (ImageView) view.findViewById(R.id.postImage);
		ImageView userImage = (ImageView) view.findViewById(R.id.userImage);

		ImageView star1 = (ImageView) view.findViewById(R.id.star1);
		ImageView star2 = (ImageView) view.findViewById(R.id.star2);
		ImageView star3 = (ImageView) view.findViewById(R.id.star3);
		ImageView star4 = (ImageView) view.findViewById(R.id.star4);
		ImageView star5 = (ImageView) view.findViewById(R.id.star5);
		ImageUtil.displayProfileImage(reviewVM.getUserId(), userImage);
        ImageUtil.displayPostImage(reviewVM.getPostImageId(), postImage);

		userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewUtil.startUserProfileActivity(activity, reviewVM.getUserId());
            }
        });

		postImage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				ViewUtil.startProductActivity(activity, reviewVM.getPostId());
			}
		});

		userNameText.setText(reviewVM.getUserName());
		reviewText.setText(reviewVM.getReview());
        timeText.setText(DateTimeUtil.getTimeAgo(reviewVM.getReviewDate()));

        // reset
        star1.setImageResource(R.drawable.star_unselected);
        star2.setImageResource(R.drawable.star_unselected);
        star3.setImageResource(R.drawable.star_unselected);
        star4.setImageResource(R.drawable.star_unselected);
        star5.setImageResource(R.drawable.star_unselected);

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

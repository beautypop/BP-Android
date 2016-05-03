package com.beautypop.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.beautypop.R;
import com.beautypop.app.AppController;
import com.beautypop.util.ViewUtil;
import com.beautypop.viewmodel.PostFeedbackVM;
import com.beautypop.viewmodel.ReviewVM;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class FeedbackActivity extends Activity {

	private EditText feedBackText;
	private TextView countText;
	private ImageView submitImage,star1,star2,star3,star4,star5,backImage;
	private Double score = 0.0;
	private Boolean starSelected1=false,starSelected2=false,starSelected3=false,starSelected4=false,starSelected5=false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feedback);

		feedBackText = (EditText) findViewById(R.id.feedBackText);
		countText = (TextView) findViewById(R.id.countText);
		submitImage = (ImageView) findViewById(R.id.submitImage);
		backImage = (ImageView) findViewById(R.id.backImage);
		star1 = (ImageView) findViewById(R.id.star1);
		star2 = (ImageView) findViewById(R.id.star2);
		star3 = (ImageView) findViewById(R.id.star3);
		star4 = (ImageView) findViewById(R.id.star4);
		star5 = (ImageView) findViewById(R.id.star5);


		backImage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				onBackPressed();
			}
		});

		star1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if(starSelected1) {
					star1.setImageResource(R.drawable.star_unselected);
					star2.setImageResource(R.drawable.star_unselected);
					star3.setImageResource(R.drawable.star_unselected);
					star4.setImageResource(R.drawable.star_unselected);
					star5.setImageResource(R.drawable.star_unselected);
					score = 0.0;
					starSelected1 = false;
				}else{
					star1.setImageResource(R.drawable.star_selected);
					score = 1.0;
					starSelected1 = true;
				}
			}
		});

		star2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if(starSelected2) {
					star2.setImageResource(R.drawable.star_unselected);
					star3.setImageResource(R.drawable.star_unselected);
					star4.setImageResource(R.drawable.star_unselected);
					star5.setImageResource(R.drawable.star_unselected);
					score = 1.0;
					starSelected2 = false;
				}else{
					star1.setImageResource(R.drawable.star_selected);
					star2.setImageResource(R.drawable.star_selected);
					score = 2.0;
					starSelected2 = true;
				}
			}
		});

		star3.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if(starSelected3) {
					star3.setImageResource(R.drawable.star_unselected);
					star4.setImageResource(R.drawable.star_unselected);
					star5.setImageResource(R.drawable.star_unselected);
					score = 2.0;
					starSelected3 = false;
				}else{
					star1.setImageResource(R.drawable.star_selected);
					star2.setImageResource(R.drawable.star_selected);
					star3.setImageResource(R.drawable.star_selected);
					score = 3.0;
					starSelected3 = true;
				}
			}
		});

		star4.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if(starSelected4) {
					star4.setImageResource(R.drawable.star_unselected);
					star5.setImageResource(R.drawable.star_unselected);
					score = 3.0;
					starSelected4 = false;
				}else{
					star1.setImageResource(R.drawable.star_selected);
					star2.setImageResource(R.drawable.star_selected);
					star3.setImageResource(R.drawable.star_selected);
					star4.setImageResource(R.drawable.star_selected);
					score = 4.0;
					starSelected4 = true;
				}
			}
		});

		star5.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if(starSelected5) {
					star5.setImageResource(R.drawable.star_unselected);
					score = 4.0;
					starSelected5 = false;
				}else{
					star1.setImageResource(R.drawable.star_selected);
					star2.setImageResource(R.drawable.star_selected);
					star3.setImageResource(R.drawable.star_selected);
					star4.setImageResource(R.drawable.star_selected);
					star5.setImageResource(R.drawable.star_selected);
					score = 5.0;
					starSelected5 = true;
				}
			}
		});

		ViewUtil.showSpinner(this);
		AppController.getApiService().getFeedback(getIntent().getLongExtra("cId",0L),new Callback<ReviewVM>() {
			@Override
			public void success(ReviewVM reviewVM, Response response) {
				ViewUtil.stopSpinner(FeedbackActivity.this);

				feedBackText.setText(reviewVM.getReview());

				if(reviewVM.getScore() >= 0.5 && reviewVM.getScore() <= 1.5){
					star1.setImageResource(R.drawable.star_selected);
				}else if(reviewVM.getScore() > 1.5 && reviewVM.getScore() <= 2.5){
					star1.setImageResource(R.drawable.star_selected);
					star2.setImageResource(R.drawable.star_selected);
				}else if(reviewVM.getScore() >2.5 && reviewVM.getScore() <= 3.5){
					star1.setImageResource(R.drawable.star_selected);
					star2.setImageResource(R.drawable.star_selected);
					star3.setImageResource(R.drawable.star_selected);
				}else if(reviewVM.getScore() >3.5 && reviewVM.getScore() <= 4.5){
					star1.setImageResource(R.drawable.star_selected);
					star2.setImageResource(R.drawable.star_selected);
					star3.setImageResource(R.drawable.star_selected);
					star4.setImageResource(R.drawable.star_selected);
				}else if(reviewVM.getScore() >4.5 && reviewVM.getScore() <= 5.0){
					star1.setImageResource(R.drawable.star_selected);
					star2.setImageResource(R.drawable.star_selected);
					star3.setImageResource(R.drawable.star_selected);
					star4.setImageResource(R.drawable.star_selected);
					star5.setImageResource(R.drawable.star_selected);
				}


			}

			@Override
			public void failure(RetrofitError error) {
				error.printStackTrace();
				ViewUtil.stopSpinner(FeedbackActivity.this);
			}
		});





		TextWatcher mTextEditorWatcher = new TextWatcher() {
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			public void onTextChanged(CharSequence s, int start, int before, int count) {
				countText.setText(String.valueOf(s.length()));
			}

			public void afterTextChanged(Editable s) {
			}
		};

		feedBackText.addTextChangedListener(mTextEditorWatcher);

		submitImage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if(!feedBackText.getText().toString().equals("")) {
					showDialog();
				}else{
					Toast.makeText(getApplicationContext(),"Please add your experience ",Toast.LENGTH_LONG).show();
				}
			}
		});


	}

	public void showDialog(){
		final AlertDialog.Builder alertDialog = new AlertDialog.Builder(FeedbackActivity.this);

		alertDialog.setTitle("Ready to submit feedback");

		alertDialog.setMessage("You are about to leave feedback for fellow member. A notification will be sent. All set ?");

		alertDialog.setPositiveButton("MAKE CHANGES ", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int which) {
				dialog.dismiss();
				//Toast.makeText(getApplicationContext(), "You clicked on YES", Toast.LENGTH_SHORT).show();
			}
		});

		alertDialog.setNegativeButton("SUBMIT", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				PostFeedbackVM postFeedbackVM = new PostFeedbackVM();
				postFeedbackVM.setConversationId(getIntent().getLongExtra("cId",0L));
				postFeedbackVM.setForSeller(getIntent().getBooleanExtra("isSeller",false));
				postFeedbackVM.setScore(score);
				postFeedbackVM.setReview(feedBackText.getText().toString());

				ViewUtil.showSpinner(FeedbackActivity.this);

				AppController.getApiService().postFeedback(postFeedbackVM,new Callback<Response>() {
					@Override
					public void success(Response response, Response response2) {
						ViewUtil.stopSpinner(FeedbackActivity.this);
						Toast.makeText(getApplicationContext(), "Review submitted successfully !", Toast.LENGTH_SHORT).show();
						finish();
					}

					@Override
					public void failure(RetrofitError error) {
						ViewUtil.stopSpinner(FeedbackActivity.this);
						Toast.makeText(getApplicationContext(), "Failed to submit review. Please try again later.", Toast.LENGTH_SHORT).show();
						error.printStackTrace();
					}
				});
			}
		});

		alertDialog.show();
	}
}

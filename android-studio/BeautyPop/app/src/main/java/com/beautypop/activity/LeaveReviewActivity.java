package com.beautypop.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.beautypop.R;
import com.beautypop.app.AppController;
import com.beautypop.util.ViewUtil;
import com.beautypop.viewmodel.NewReviewVM;
import com.beautypop.viewmodel.ReviewVM;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class LeaveReviewActivity extends Activity {
    private static final String TAG = ProductActivity.class.getName();

	private EditText reviewText;
	private TextView title1Text,countText;
	private ImageView submitImage,star1,star2,star3,star4,star5,backImage;
	private Double score = 0.0;
	private Boolean starSelected1=false,starSelected2=false,starSelected3=false,starSelected4=false,starSelected5=false;

    private Long conversationOrderId = -1L;
    private boolean isBuyer = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.leave_review_activity);

		reviewText = (EditText) findViewById(R.id.reviewText);
		countText = (TextView) findViewById(R.id.countText);
        title1Text = (TextView) findViewById(R.id.title1Text);
		submitImage = (ImageView) findViewById(R.id.submitImage);
		backImage = (ImageView) findViewById(R.id.backImage);
		star1 = (ImageView) findViewById(R.id.star1);
		star2 = (ImageView) findViewById(R.id.star2);
		star3 = (ImageView) findViewById(R.id.star3);
		star4 = (ImageView) findViewById(R.id.star4);
		star5 = (ImageView) findViewById(R.id.star5);

        this.conversationOrderId = getIntent().getLongExtra(ViewUtil.BUNDLE_KEY_ID, 0L);
        this.isBuyer = getIntent().getBooleanExtra(ViewUtil.BUNDLE_KEY_ARG1, false);

        String title1 = getString(R.string.leave_review_title_1);
        if (isBuyer) {
            title1 += " " + getString(R.string.seller);
        } else {
            title1 += " " + getString(R.string.buyer);
        }
        title1Text.setText(title1);

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
                if (starSelected5) {
                    star5.setImageResource(R.drawable.star_unselected);
                    score = 4.0;
                    starSelected5 = false;
                } else {
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

		final Long conversationOrderId = getIntent().getLongExtra(ViewUtil.BUNDLE_KEY_ID, 0L);

		ViewUtil.showSpinner(this);
		AppController.getApiService().getReview(conversationOrderId, new Callback<ReviewVM>() {
			@Override
			public void success(ReviewVM reviewVM, Response response) {
				ViewUtil.stopSpinner(LeaveReviewActivity.this);

				score = reviewVM.getScore();

				reviewText.setText(reviewVM.getReview());

				if(reviewVM.getScore() >= 0.5 && reviewVM.getScore() <= 1.5){
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
				}else if(reviewVM.getScore() > 4.5 && reviewVM.getScore() <= 5.0){
					star1.setImageResource(R.drawable.star_selected);
					star2.setImageResource(R.drawable.star_selected);
					star3.setImageResource(R.drawable.star_selected);
					star4.setImageResource(R.drawable.star_selected);
					star5.setImageResource(R.drawable.star_selected);
				}
			}

			@Override
			public void failure(RetrofitError error) {
				ViewUtil.stopSpinner(LeaveReviewActivity.this);
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

		reviewText.addTextChangedListener(mTextEditorWatcher);

		submitImage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (reviewText.getText().toString().equals("")) {
					Toast.makeText(LeaveReviewActivity.this, getString(R.string.leave_review_input_review), Toast.LENGTH_LONG).show();
					//showDialog();
				} else if(score == 0.0) {
					Toast.makeText(LeaveReviewActivity.this, getString(R.string.leave_review_input_rating), Toast.LENGTH_LONG).show();
				}else{
					showDialog();
				}
			}
		});
	}

	public void showDialog(){
		final AlertDialog.Builder alertDialog = new AlertDialog.Builder(LeaveReviewActivity.this);

		alertDialog.setMessage(getString(R.string.leave_review_confirm));

		alertDialog.setPositiveButton(getString(R.string.submit), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                NewReviewVM newReviewVM = new NewReviewVM();
                newReviewVM.setConversationOrderId(conversationOrderId);
                newReviewVM.setScore(score);
                newReviewVM.setReview(reviewText.getText().toString());

                ViewUtil.showSpinner(LeaveReviewActivity.this);

                AppController.getApiService().addReview(newReviewVM, new Callback<Response>() {
                    @Override
                    public void success(Response response, Response response2) {
                        ViewUtil.stopSpinner(LeaveReviewActivity.this);
                        Toast.makeText(getApplicationContext(), "Review submitted successfully !", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        ViewUtil.stopSpinner(LeaveReviewActivity.this);
                        Toast.makeText(getApplicationContext(), "Failed to submit review. Please try again later.", Toast.LENGTH_SHORT).show();
                        error.printStackTrace();
                    }
                });
            }
        });

        alertDialog.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                //Toast.makeText(getApplicationContext(), "You clicked on YES", Toast.LENGTH_SHORT).show();
            }
        });

		alertDialog.show();
	}
}

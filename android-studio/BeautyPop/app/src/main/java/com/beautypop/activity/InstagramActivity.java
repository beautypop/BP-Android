package com.beautypop.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.beautypop.R;
import com.beautypop.instagram.ApplicationData;
import com.beautypop.instagram.InstagramApp;

import java.util.ArrayList;
import java.util.HashMap;

public class InstagramActivity extends Activity {

	private TextView importButton, cancelButton;
	private InstagramApp mApp;

	private HashMap<String, String> userInfoHashmap = new HashMap<String, String>();
	private Handler handler = new Handler(new Handler.Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			if (msg.what == InstagramApp.WHAT_FINALIZE) {

				userInfoHashmap = mApp.getUserInfo();
				mApp.getMedia(InstagramActivity.this);

			} else if (msg.what == InstagramApp.WHAT_FINALIZE) {
				Toast.makeText(InstagramActivity.this, "Check your network.",
						Toast.LENGTH_SHORT).show();
			}
			return false;
		}
	});

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_instagram);

		mApp = new InstagramApp(this, ApplicationData.CLIENT_ID,
				ApplicationData.CLIENT_SECRET, ApplicationData.CALLBACK_URL);
		mApp.setListener(new InstagramApp.OAuthAuthenticationListener() {

			@Override
			public void onSuccess() {
				// tvSummary.setText("Connected as " + mApp.getUserName());
				//btnConnect.setText("Disconnect");
				//llAfterLoginView.setVisibility(View.VISIBLE);
				// userInfoHashmap = mApp.
				mApp.fetchUserName(handler);
			}

			@Override
			public void onFail(String error) {
				Toast.makeText(InstagramActivity.this, error, Toast.LENGTH_SHORT)
						.show();
			}
		});

		importButton = (TextView) findViewById(R.id.importButton);
		cancelButton = (TextView) findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

		importButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				connectOrDisconnectUser();
			}
		});



	}

	private void connectOrDisconnectUser() {
		mApp.authorize();
		/*if (mApp.hasAccessToken()) {
			final AlertDialog.Builder builder = new AlertDialog.Builder(
					InstagramActivity.this);
			builder.setMessage("Disconnect from Instagram?")
					.setCancelable(false)
					.setPositiveButton("Yes",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
													int id) {
									mApp.resetAccessToken();
									// btnConnect.setVisibility(View.VISIBLE);
									//llAfterLoginView.setVisibility(View.GONE);
									//btnConnect.setText("Connect");
									// tvSummary.setText("Not connected");
								}
							})
					.setNegativeButton("No",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
													int id) {
									dialog.cancel();
								}
							});
			final AlertDialog alert = builder.create();
			alert.show();
		} else {
			mApp.authorize();
		}*/
	}
}

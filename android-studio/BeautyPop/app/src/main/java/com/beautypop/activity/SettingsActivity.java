package com.beautypop.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.beautypop.R;
import com.beautypop.app.AppController;
import com.beautypop.app.TrackedFragmentActivity;
import com.beautypop.util.DefaultValues;
import com.beautypop.util.SharedPreferencesUtil;
import com.beautypop.util.ViewUtil;

public class SettingsActivity extends TrackedFragmentActivity {
    private static final String TAG = SettingsActivity.class.getName();

    private TextView appVersionText;
    private RelativeLayout notificationSettingsLayout, logoutLayout, adminLayout;
    private Spinner langSpinner;
    private ImageView backImage;

    private boolean userSelect = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.settings_activity);

        setToolbarTitle(getString(R.string.settings_actionbar_title));

        appVersionText = (TextView) findViewById(R.id.appVersionText);
        langSpinner = (Spinner) findViewById(R.id.langSpinner);
        notificationSettingsLayout = (RelativeLayout) findViewById(R.id.notificationSettingsLayout);
        logoutLayout = (RelativeLayout) findViewById(R.id.logoutLayout);
        adminLayout = (RelativeLayout) findViewById(R.id.adminLayout);

        // version
        appVersionText.setText(AppController.getVersionName());

        final ArrayAdapter<String> languageAdapter = new ArrayAdapter<>(
                this,
                R.layout.spinner_item_right,
                ViewUtil.LANG_OPTIONS);
        langSpinner.setAdapter(languageAdapter);

        String lang = SharedPreferencesUtil.getInstance().getLang();
        int pos = -1;
        if (DefaultValues.LANG_ZH.equalsIgnoreCase(lang)) {
            pos = languageAdapter.getPosition(getString(R.string.lang_zh));
        } else {
            pos = languageAdapter.getPosition(getString(R.string.lang_en));
        }
        langSpinner.setSelection(pos);

        langSpinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                userSelect = true;
                return false;
            }
        });

        final int selected = pos;
        langSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (!userSelect) {
                    return;
                }

                userSelect = false;

                final String lang = languageAdapter.getItem(i);

                AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
                builder.setMessage(R.string.lang_confirm)
                        .setCancelable(false)
                        .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if (getString(R.string.lang_zh).equalsIgnoreCase(lang)) {
                                    SharedPreferencesUtil.getInstance().saveLang(DefaultValues.LANG_ZH);
                                } else {
                                    SharedPreferencesUtil.getInstance().saveLang(DefaultValues.LANG_EN);
                                }
                                ViewUtil.startSplashActivity(SettingsActivity.this, "");
                                finish();
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                langSpinner.setSelection(selected);
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();

                //ViewUtil.alert(SettingsActivity.this, getString(R.string.lang_complete));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        // notifications
        notificationSettingsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewUtil.startNotificationSettingsActivity(SettingsActivity.this);
            }
        });

        // logout
        logoutLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
                builder.setMessage(R.string.logout_message)
                        .setCancelable(false)
                        .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                AppController.getInstance().logout();
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        // admin
        adminLayout.setVisibility(AppController.isUserAdmin() ? View.VISIBLE : View.GONE);
        if (AppController.isUserAdmin()) {
            adminLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ViewUtil.startAdminActivity(SettingsActivity.this);
                }
            });
        }

        backImage = (ImageView) this.findViewById(R.id.backImage);
        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}



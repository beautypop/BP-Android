package com.beautypop.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.beautypop.R;
import com.beautypop.app.AppController;
import com.beautypop.app.DistrictCache;
import com.beautypop.app.TrackedFragmentActivity;
import com.beautypop.util.DefaultValues;
import com.beautypop.util.SharedPreferencesUtil;
import com.beautypop.util.ValidationUtil;
import com.beautypop.util.ViewUtil;
import com.beautypop.viewmodel.LocationVM;

import org.parceler.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SignupDetailActivity extends TrackedFragmentActivity {
    private Spinner locationSpinner;
    private Spinner langSpinner;
    private Button finishButton;
    private EditText displayName;
    private TextView titleText;

    private List<String> districtNames;

    private int locationId = -1;

    private ArrayAdapter<String> languageAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.signup_detail_activity);

        titleText = (TextView) findViewById(R.id.titleText);

        displayName = (EditText) findViewById(R.id.displaynameEdit);
        locationSpinner = (Spinner) findViewById(R.id.locationSpinner);
        langSpinner = (Spinner) findViewById(R.id.langSpinner);
        finishButton = (Button) findViewById(R.id.finishButton);

        // location
        setDistricts();
        locationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                locationId = -1;
                String loc = locationSpinner.getSelectedItem().toString();
                List<LocationVM> districts = DistrictCache.getDistricts();
                for (LocationVM vm : districts) {
                    if (vm.getDisplayName().equals(loc)) {
                        locationId = vm.getId().intValue();
                        break;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // lang
        languageAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                ViewUtil.LANG_OPTIONS);
        langSpinner.setAdapter(languageAdapter);
        langSpinner.setSelection(0);

        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitDetails();
            };
        });
    }

    private void submitDetails() {
        final String displayname = displayName.getText().toString().trim();

        if (isValid()) {
            Log.d(this.getClass().getSimpleName(), "signupInfo: \n displayname="+displayname+"\n locationId="+locationId);

            // lang
            final String lang = languageAdapter.getItem(langSpinner.getSelectedItemPosition());
            if (getString(R.string.lang_zh).equalsIgnoreCase(lang)) {
                SharedPreferencesUtil.getInstance().saveLang(DefaultValues.LANG_ZH);
            } else {
                SharedPreferencesUtil.getInstance().saveLang(DefaultValues.LANG_EN);
            }

            showSpinner();
            AppController.getApiService().signUpInfo(
                    displayname, locationId, new Callback<Response>() {
                        @Override
                        public void success(Response responseObject, Response response) {
                            Log.d(SignupDetailActivity.class.getSimpleName(), "submitDetails: api.signUpInfo.success");
                            stopSpinner();
                            startActivity(new Intent(SignupDetailActivity.this, SplashActivity.class));
                            finish();
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            String errorMsg = ViewUtil.getResponseBody(error.getResponse());
                            if (error.getResponse().getStatus() == 500 &&
                                    error.getResponse() != null &&
                                    !StringUtils.isEmpty(errorMsg)) {
                                ViewUtil.alert(SignupDetailActivity.this, errorMsg);
                            } else {
                                //ActivityUtil.alert(SignupDetailActivity.this, getString(R.string.signup_details_error_info));
                                ViewUtil.alert(SignupDetailActivity.this,
                                        "\""+displayname+"\" "+getString(R.string.signup_details_error_displayname_already_exists));
                            }

                            stopSpinner();
                            Log.e(SignupDetailActivity.class.getSimpleName(), "submitDetails.api.signUpInfo: failure", error);
                        }
                    });
        }
    }

    private void setDistricts(){
        List<LocationVM> districts = DistrictCache.getDistricts();
        districtNames = new ArrayList<>();
        districtNames.add(getString(R.string.signup_details_location));
        for (int i = 0; i < districts.size(); i++) {
            districtNames.add(districts.get(i).getDisplayName());
        }

        ArrayAdapter<String> locationAdapter = new ArrayAdapter<>(
                SignupDetailActivity.this,
                android.R.layout.simple_spinner_item,
                districtNames);
        locationSpinner.setAdapter(locationAdapter);
    }

    private boolean isValid(){
        boolean valid = true;
        String error = "";
        if (!ValidationUtil.isValidDisplayName(displayName)) {
            error = ValidationUtil.appendError(error, getString(R.string.signup_details_error_displayname_format));
            valid = false;
        }

        if (locationId == -1) {
            error = ValidationUtil.appendError(error, getString(R.string.signup_details_error_location_not_entered));
            valid = false;
        }

        if (!valid) {
            Toast.makeText(this, error, Toast.LENGTH_LONG).show();
        }
        return valid;
    }

    private void showSpinner() {
        showSpinner(true);
    }

    private void stopSpinner() {
        showSpinner(false);
    }

    private void showSpinner(boolean show) {
        if (show) {
            ViewUtil.showSpinner(this);
        } else {
            ViewUtil.stopSpinner(this);
        }

        if (finishButton != null) {
            finishButton.setEnabled(!show);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        ViewUtil.startLoginActivity(this);
    }
}
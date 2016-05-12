package com.beautypop.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.beautypop.R;
import com.beautypop.app.AppController;
import com.beautypop.app.UserInfoCache;
import com.beautypop.fragment.ImportImagePagerFragment;
import com.beautypop.instagram.InstagramVM;
import com.beautypop.util.ViewUtil;
import com.beautypop.view.AdaptiveViewPager;
import com.beautypop.viewmodel.NewPostVM;
import com.beautypop.viewmodel.ResponseStatusVM;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.MultipartTypedOutput;

public class EditImportImageActivity extends FragmentActivity {

    private static final String TAG = EditImportImageActivity.class.getName();
    private EditImagePagerAdapter imagePagerAdapter;
    private AdaptiveViewPager imagePager;
    private ArrayList<InstagramVM> instagramVMs;
    private TextView nextButton, selectCountText;
    List<NewPostVM> multipartTypedOutputs = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_import_image);

        imagePager = (AdaptiveViewPager) findViewById(R.id.imagePager);

        instagramVMs = getIntent().getParcelableArrayListExtra("items");

        imagePagerAdapter = new EditImagePagerAdapter(getSupportFragmentManager(),instagramVMs ,false);
        imagePager.setAdapter(imagePagerAdapter);
        imagePager.setCurrentItem(0);
        nextButton = (TextView) findViewById(R.id.nextButton);
        selectCountText = (TextView) findViewById(R.id.selectCountText);
        selectCountText.setText((imagePager.getCurrentItem() + 1)+"/"+instagramVMs.size());
        if((imagePager.getCurrentItem() + 1) == instagramVMs.size()) {
            nextButton.setText("Done");
        }
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ViewUtil.showSpinner(EditImportImageActivity.this);
                ImportImagePagerFragment currentPager = (ImportImagePagerFragment) imagePagerAdapter.instantiateItem(imagePager, imagePager.getCurrentItem());


                NewPostVM newPost = currentPager.getNewPost();
                if(newPost == null){
                    return;
                }
                multipartTypedOutputs.add(newPost);
                if(imagePager.getCurrentItem() == (instagramVMs.size() - 1)) {
                    confirmationDialog();
                } else {
                    ViewUtil.stopSpinner(EditImportImageActivity.this);
                    imagePager.setCurrentItem(imagePager.getCurrentItem() + 1);
                    selectCountText.setText((imagePager.getCurrentItem() + 1) + "/" + (instagramVMs.size()));
                    if((imagePager.getCurrentItem() + 1) == instagramVMs.size()) {
                        nextButton.setText("Done");
                    }
                }

            }


        });
    }

    private void newInstagramPost() {
        AppController.getApiService().newInstagramPost(multipartTypedOutputs, new Callback<ResponseStatusVM>() {
            @Override
            public void success(ResponseStatusVM responseStatus, Response response) {

                for(NewPostVM newPostVM : multipartTypedOutputs){
                    UserInfoCache.incrementNumProducts();
                }
                Intent intent = new Intent(EditImportImageActivity.this, MainActivity.class);
                intent.putExtra("profilePage", true);
                ViewUtil.stopSpinner(EditImportImageActivity.this);
                EditImportImageActivity.this.startActivity(intent);
            }
            @Override
            public void failure(RetrofitError error) {
                ViewUtil.stopSpinner(EditImportImageActivity.this);
                Toast.makeText(EditImportImageActivity.this, String.format(EditImportImageActivity.this.getString(R.string.post_failed), getActionTypeText()), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "doPost: failure", error);
            }
        });
    }

    protected void confirmationDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(EditImportImageActivity.this);
        builder.setMessage("Import "+instagramVMs.size()+" products to BeautyPop now ?")
                .setCancelable(false)
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        nextButton.setEnabled(false);
                        newInstagramPost();
                    }
                })
                .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    protected String getActionTypeText() {
        return getString(R.string.new_post_action);
    }
}

class EditImagePagerAdapter extends FragmentStatePagerAdapter {
    private static final String TAG = ProductImagePagerAdapter.class.getName();

    private ArrayList<InstagramVM> images;
    private boolean sold;
    private FragmentManager mFragmentManager;

    public EditImagePagerAdapter(FragmentManager fm, ArrayList<InstagramVM> images, boolean sold) {
        super(fm);
        this.mFragmentManager = fm;
        this.images = images;
        this.sold = sold;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "";
    }

    @Override
    public int getCount() {
        return images == null ? 0 : images.size();
    }

    @Override
    public Fragment getItem(int position) {
        Log.d(TAG, "getItem: item - " + position);
        switch (position) {
            default: {
                ImportImagePagerFragment fragment = new ImportImagePagerFragment();
                //ImportImagePagerFragment.instantiate(get, ImportImagePagerFragment.class.getName(), null);
                fragment.setImageId(images.get(position).getUrl());
                fragment.setDescription(images.get(position).getCaption());
                fragment.setMediaId(images.get(position).getMediaId());

                return fragment;
            }
        }
    }

    /**
     * HACK... returns POSITION_NONE will refresh pager more frequent than needed... but works in this case
     * http://stackoverflow.com/questions/12510404/reorder-pages-in-fragmentstatepageradapter-using-getitempositionobject-object
     *
     * @param object
     * @return
     */
    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    public Fragment getActiveFragment(ViewPager container, int position) {
        String name = makeFragmentName(container.getId(), position);
        return  mFragmentManager.findFragmentByTag(name);
    }

    private static String makeFragmentName(int viewId, int index) {
        return "android:switcher:" + viewId + ":" + index;
    }
}



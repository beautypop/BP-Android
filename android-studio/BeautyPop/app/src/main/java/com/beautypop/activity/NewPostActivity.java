package com.beautypop.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.beautypop.R;
import com.beautypop.adapter.PopupCategoryListAdapter;
import com.beautypop.app.AppController;
import com.beautypop.app.CategoryCache;
import com.beautypop.app.CountryCache;
import com.beautypop.app.NotificationCounter;
import com.beautypop.app.TrackedFragmentActivity;
import com.beautypop.app.UserInfoCache;
import com.beautypop.util.DefaultValues;
import com.beautypop.util.ImageUtil;
import com.beautypop.util.SelectedImage;
import com.beautypop.util.SharedPreferencesUtil;
import com.beautypop.util.ViewUtil;
import com.beautypop.viewmodel.CategoryVM;
import com.beautypop.viewmodel.CountryVM;
import com.beautypop.viewmodel.NewPostVM;
import com.beautypop.viewmodel.ResponseStatusVM;

import org.parceler.apache.commons.lang.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class NewPostActivity extends TrackedFragmentActivity{
    private static final String TAG = NewPostActivity.class.getName();

    protected LinearLayout imagesLayout, selectCatLayout, selectSubCatLayout, sellerLayout, sharingLayout;
    protected RelativeLayout catLayout;
    protected TextView selectCatText, selectSubCatText;
    protected ImageView selectCatIcon, selectSubCatIcon;
    protected TextView catName, subCatName;
    protected ImageView catIcon, subCatIcon;
    protected ImageView backImage;
    protected TextView titleEdit, descEdit, priceEdit, originalPriceEdit, postAction, editTextInFocus;
    protected Spinner conditionTypeSpinner, countrySpinner;
    protected CheckBox freeDeliveryCheckBox;
    protected TextView autoCompleteText;

	protected ImageView selectThemeIcon,selectTrendIcon,trendIcon,themeIcon;
	protected TextView selectThemeText,selectTrendText,themeName,trendName;
	protected RelativeLayout themeLayout,trendLayout;
	protected LinearLayout selectThemeLayout,selectTrendLayout;

    protected List<ImageView> postImages = new ArrayList<>();

    protected int selectedPostImageIndex = -1;
    protected Uri selectedImageUri = null;
    protected List<SelectedImage> selectedImages = new ArrayList<>();

    protected ViewUtil.PostConditionType conditionType;

    protected CountryVM country;

    protected CategoryVM category;
    protected CategoryVM subCategory;
    protected CategoryVM themeCategory;
    protected CategoryVM trendCategory;
    protected PopupWindow categoryPopup;
    protected PopupWindow subCategoryPopup;
    protected PopupCategoryListAdapter adapter;

	protected PopupWindow themePopup;
	protected PopupWindow trendPopup;

    protected ToggleButton fbSharingButton;

    protected boolean pending = false;
    protected boolean postSuccess = false;

    protected String getActionTypeText() {
        return getString(R.string.new_post_action);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.new_post_activity);

        fbSharingButton =(ToggleButton)findViewById(R.id.facebookshare);

        backImage = (ImageView) findViewById(R.id.backImage);
        postAction = (TextView) findViewById(R.id.postAction);
        imagesLayout = (LinearLayout) findViewById(R.id.imagesLayout);
        catLayout = (RelativeLayout) findViewById(R.id.catLayout);
        selectCatLayout = (LinearLayout) findViewById(R.id.selectCatLayout);
        selectSubCatLayout = (LinearLayout) findViewById(R.id.selectSubCatLayout);
        selectCatText = (TextView) findViewById(R.id.selectCatText);
        selectCatIcon = (ImageView) findViewById(R.id.selectCatIcon);
        selectSubCatText = (TextView) findViewById(R.id.selectSubCatText);
        selectSubCatIcon = (ImageView) findViewById(R.id.selectSubCatIcon);
        catIcon = (ImageView) findViewById(R.id.catIcon);
        catName = (TextView) findViewById(R.id.catName);
        subCatIcon = (ImageView) findViewById(R.id.subCatIcon);
        subCatName = (TextView) findViewById(R.id.subCatName);
        titleEdit = (TextView) findViewById(R.id.titleEdit);
        descEdit = (TextView) findViewById(R.id.descEdit);
        priceEdit = (TextView) findViewById(R.id.priceEdit);
        conditionTypeSpinner = (Spinner) findViewById(R.id.conditionTypeSpinner);
        sellerLayout = (LinearLayout) findViewById(R.id.sellerLayout);
        originalPriceEdit = (TextView) findViewById(R.id.originalPriceEdit);
        freeDeliveryCheckBox = (CheckBox) findViewById(R.id.freeDeliveryCheckBox);
        countrySpinner = (Spinner) findViewById(R.id.countrySpinner);
        sharingLayout = (LinearLayout) findViewById(R.id.sharingLayout);
        editTextInFocus = titleEdit;

		trendIcon = (ImageView) findViewById(R.id.trendIcon);
		themeIcon = (ImageView) findViewById(R.id.themeIcon);
		selectTrendIcon = (ImageView) findViewById(R.id.selectTrendIcon);
		selectThemeIcon = (ImageView) findViewById(R.id.selectThemeIcon);
		selectThemeText = (TextView) findViewById(R.id.selectThemeText);
		selectTrendText = (TextView) findViewById(R.id.selectTrendText);
		themeName = (TextView) findViewById(R.id.themeName);
		trendName = (TextView) findViewById(R.id.trendName);

		themeLayout = (RelativeLayout) findViewById(R.id.themeLayout);
		trendLayout = (RelativeLayout) findViewById(R.id.trendLayout);

		selectTrendLayout = (LinearLayout) findViewById(R.id.selectTrendLayout);
		selectThemeLayout = (LinearLayout) findViewById(R.id.selectThemeLayout);

        ViewUtil.hideInputMethodWindow(this, titleEdit);

        SharedPreferencesUtil.getInstance().saveUserLocation("");

        autoCompleteText = (TextView) findViewById(R.id.autoCompleteText);
        autoCompleteText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(NewPostActivity.this,LocationActivity.class));
            }
        });

        titleEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    editTextInFocus = titleEdit;
                }
            }
        });

        descEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    editTextInFocus = descEdit;
                }
            }
        });

        Long id = getIntent().getLongExtra(ViewUtil.BUNDLE_KEY_CATEGORY_ID, -1L);
        if (id > 0L) {
            // set category, subcategory
            CategoryVM cat = CategoryCache.getCategory(id);
            if (cat.parentId > 0) {
                category = CategoryCache.getCategory(cat.parentId);
                subCategory = cat;
            } else {
                category = cat;
                subCategory = null;
            }

            setCategory(category);
            setSubCategory(subCategory);
        } else {
            setCategory(null);
            setSubCategory(null);
        }
        //Log.d(TAG, "onCreate: category="+category.id+" subCategory="+subCategory.id);

        selectCatLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initCategoryPopup(categoryPopup, CategoryCache.getCategories(), false);
            }
        });

		selectThemeLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				initThemePopup(themePopup, CategoryCache.getThemeCategories(), true);
			}
		});

		selectTrendLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				initThemePopup(trendPopup, CategoryCache.getTrendCategories(), false);
			}
		});

        selectSubCatLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (category == null) {
                    Toast.makeText(NewPostActivity.this, getString(R.string.invalid_post_no_category), Toast.LENGTH_SHORT).show();
                    return;
                }
                initCategoryPopup(subCategoryPopup, CategoryCache.getSubCategories(category.id), true);
            }
        });

        // select image

        if (postImages.size() == 0) {
            ImageView postImage1 = (ImageView) findViewById(R.id.postImage1);
            postImage1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int index = 0;
                    SelectedImage image = getSelectedPostImage(index);
                    if (image == null) {
                        // select
                        selectedPostImageIndex = index;
                        ImageUtil.openPhotoPicker(NewPostActivity.this);
                    } else {
                        // remove
                        removeSelectedPostImage(index);
                    }
                }
            });
            postImages.add(postImage1);

            ImageView postImage2 = (ImageView) findViewById(R.id.postImage2);
            postImage2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int index = 1;
                    SelectedImage image = getSelectedPostImage(index);
                    if (image == null) {
                        // select
                        selectedPostImageIndex = index;
                        ImageUtil.openPhotoPicker(NewPostActivity.this);
                    } else {
                        // remove
                        removeSelectedPostImage(index);
                    }
                }
            });
            postImages.add(postImage2);

            ImageView postImage3 = (ImageView) findViewById(R.id.postImage3);
            postImage3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int index = 2;
                    SelectedImage image = getSelectedPostImage(index);
                    if (image == null) {
                        // select
                        selectedPostImageIndex = index;
                        ImageUtil.openPhotoPicker(NewPostActivity.this);
                    } else {
                        // remove
                        removeSelectedPostImage(index);
                    }
                }
            });
            postImages.add(postImage3);

            ImageView postImage4 = (ImageView) findViewById(R.id.postImage4);
            postImage4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int index = 3;
                    SelectedImage image = getSelectedPostImage(index);
                    if (image == null) {
                        // select
                        selectedPostImageIndex = index;
                        ImageUtil.openPhotoPicker(NewPostActivity.this);
                    } else {
                        // remove
                        removeSelectedPostImage(index);
                    }
                }
            });
            postImages.add(postImage4);
        }

        // condition spinner

        initConditionTypeSpinner();

        conditionTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (conditionTypeSpinner.getSelectedItem() != null) {
                    String value = conditionTypeSpinner.getSelectedItem().toString();
                    conditionType = ViewUtil.parsePostConditionTypeFromValue(value);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // seller layout

        sellerLayout.setVisibility(View.GONE);
        if (AppController.isUserAdmin() ||
                UserInfoCache.getUser().isPromotedSeller() ||
                UserInfoCache.getUser().isVerifiedSeller()) {
            sellerLayout.setVisibility(View.VISIBLE);
        }

        freeDeliveryCheckBox.setChecked(false);

        initCountrySpinner();

        countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (countrySpinner.getSelectedItem() != null) {
                    String value = countrySpinner.getSelectedItem().toString();
                    country = CountryCache.getCountryWithName(value);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // sharing

        /*
        Log.d(TAG, "isSharingFacebookWall="+SharedPreferencesUtil.getInstance().isSharingFacebookWall());
        if (UserInfoCache.getUser().isFbLogin()) {
            sharingLayout.setVisibility(View.VISIBLE);
            fbSharingButton.setChecked(SharedPreferencesUtil.getInstance().isSharingFacebookWall());
            fbSharingButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    // no opt
                }
            });
        } else {
            sharingLayout.setVisibility(View.GONE);
            fbSharingButton.setChecked(false);
        }
        */

        // post

        postAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doPost();
            }
        });

        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        if(!SharedPreferencesUtil.getInstance().getUserLocation().equals("")) {
            autoCompleteText.setText(SharedPreferencesUtil.getInstance().getUserLocation());
        }
    }

    protected void setConditionTypeSpinner(ViewUtil.PostConditionType conditionType) {
        String value = ViewUtil.getPostConditionTypeValue(conditionType);
        int pos = ((ArrayAdapter)conditionTypeSpinner.getAdapter()).getPosition(value);
        conditionTypeSpinner.setSelection(pos);
    }

    protected void initConditionTypeSpinner() {
        List<String> conditionTypes = new ArrayList<>();
        conditionTypes.add(getString(R.string.spinner_select));
        conditionTypes.addAll(ViewUtil.getPostConditionTypeValues());

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.spinner_item_right,
                conditionTypes);
        conditionTypeSpinner.setAdapter(adapter);
    }

    protected void setCountrySpinner(String code) {
        CountryVM country = CountryCache.getCountryWithCode(code);
        if (country != null) {
            int pos = ((ArrayAdapter)countrySpinner.getAdapter()).getPosition(country.name);
            countrySpinner.setSelection(pos);
        }
    }

    protected void initCountrySpinner() {
        List<String> countries = new ArrayList<>();
        countries.add(getString(R.string.spinner_select));
        for (CountryVM country : CountryCache.getCountries()) {
            countries.add(country.name);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.spinner_item_right,
                countries);
        countrySpinner.setAdapter(adapter);
    }


	protected void updateSelectThemeLayout() {
		if (themeCategory != null) {
			themeName.setText(themeCategory.getName());
            if(themeCategory.getIcon() != null) {
                ImageUtil.displayImage(themeCategory.getIcon(), themeIcon);
                themeIcon.setVisibility(View.VISIBLE);
            } else {
                themeIcon.setVisibility(View.GONE);
            }

			selectThemeText.setVisibility(View.GONE);
			selectThemeIcon.setVisibility(View.GONE);

			themeName.setVisibility(View.VISIBLE);
		} else {
			selectThemeText.setVisibility(View.VISIBLE);
			selectThemeIcon.setVisibility(View.VISIBLE);
			themeIcon.setVisibility(View.GONE);
			themeName.setVisibility(View.GONE);
		}
	}

	protected void updateSelectTrendLayout() {
		if (trendCategory != null) {
			trendName.setText(trendCategory.getName());
            if(trendCategory.getIcon() != null) {
                ImageUtil.displayImage(trendCategory.getIcon(), trendIcon);
                trendIcon.setVisibility(View.VISIBLE);
            } else {
                trendIcon.setVisibility(View.GONE);
            }
			selectTrendText.setVisibility(View.GONE);
			selectTrendIcon.setVisibility(View.GONE);
			trendName.setVisibility(View.VISIBLE);
		} else {
			selectTrendText.setVisibility(View.VISIBLE);
			selectTrendIcon.setVisibility(View.VISIBLE);
			trendIcon.setVisibility(View.GONE);
			trendName.setVisibility(View.GONE);
		}
	}

    protected void updateSelectCategoryLayout() {
        if (category != null) {
            catName.setText(category.getName());
            ImageUtil.displayImage(category.getIcon(), catIcon);
            selectCatText.setVisibility(View.GONE);
            selectCatIcon.setVisibility(View.GONE);
            catIcon.setVisibility(View.VISIBLE);
            catName.setVisibility(View.VISIBLE);
        } else {
            selectCatText.setVisibility(View.VISIBLE);
            selectCatIcon.setVisibility(View.VISIBLE);
            catIcon.setVisibility(View.GONE);
            catName.setVisibility(View.GONE);
        }
    }

    protected void updateSelectSubCategoryLayout() {
        if (subCategory != null) {
            subCatName.setText(subCategory.getName());
            ImageUtil.displayImage(subCategory.getIcon(), subCatIcon);
            selectSubCatText.setVisibility(View.GONE);
            selectSubCatIcon.setVisibility(View.GONE);
            subCatIcon.setVisibility(View.VISIBLE);
            subCatName.setVisibility(View.VISIBLE);
        } else {
            selectSubCatText.setVisibility(View.VISIBLE);
            selectSubCatIcon.setVisibility(View.VISIBLE);
            subCatIcon.setVisibility(View.GONE);
            subCatName.setVisibility(View.GONE);
        }
    }

    protected void selectPostImage(Bitmap bp, int index, Uri uri) {
        ImageView postImage = postImages.get(index);
        postImage.setImageDrawable(new BitmapDrawable(this.getResources(), bp));
        postImage.setVisibility(View.VISIBLE);
        selectedImages.add(new SelectedImage(index, ImageUtil.getRealPathFromUri(this, uri)));
    }

    protected void selectPostImage(int index, String imagePath) {
        if (!StringUtils.isEmpty(imagePath)) {
            selectedImages.add(new SelectedImage(index, imagePath));

            ImageView imageView = postImages.get(index);
            Bitmap bp = ImageUtil.resizeAsPreviewThumbnail(imagePath);
            imageView.setImageBitmap(bp);
            //imageView.setImageURI(Uri.parse(imagePath);

            Log.d(TAG, "selectPostImage: index="+index+" imagePath="+imagePath);
        }
    }

    protected void removeSelectedPostImage(int index){
        SelectedImage toRemove = getSelectedPostImage(index);
        selectedImages.remove(toRemove);
        postImages.get(index).setImageDrawable(getResources().getDrawable(R.drawable.img_camera));
    }

    protected SelectedImage getSelectedPostImage(int index) {
        for (SelectedImage image : selectedImages) {
            if (image.index.equals(index)) {
                return image;
            }
        }
        return null;
    }

    protected NewPostVM getNewPost() {
        String title = titleEdit.getText().toString().trim();
        String body = descEdit.getText().toString().trim();
        String priceValue = priceEdit.getText().toString().trim();

        if (StringUtils.isEmpty(title)) {
            Toast.makeText(this, getString(R.string.invalid_post_title_empty), Toast.LENGTH_SHORT).show();
            return null;
        }

        /*
        if (StringUtils.isEmpty(body)) {
            Toast.makeText(this, getString(R.string.invalid_post_desc_empty), Toast.LENGTH_SHORT).show();
            return null;
        }
        */

        if (StringUtils.isEmpty(priceValue)) {
            Toast.makeText(this, getString(R.string.invalid_post_price_empty), Toast.LENGTH_SHORT).show();
            return null;
        }

        Long price = -1L;
        try {
            price = Long.valueOf(priceValue);
            if (price < 0) {
                Toast.makeText(this, getString(R.string.invalid_post_price_negative), Toast.LENGTH_SHORT).show();
                return null;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, getString(R.string.invalid_post_price_not_number), Toast.LENGTH_SHORT).show();
            return null;
        }

        if (conditionType == null) {
            Toast.makeText(this, getString(R.string.invalid_post_condition_empty), Toast.LENGTH_SHORT).show();
            return null;
        }

        if (category == null) {
            initCategoryPopup(categoryPopup, CategoryCache.getCategories(), false);
            return null;
        }

        if (subCategory == null) {
            initCategoryPopup(subCategoryPopup, CategoryCache.getSubCategories(category.id), true);
            return null;
        }


        String countryCode = "";
        if (country != null) {
            countryCode = country.code;
        }

        Long originalPrice = -1L;
        Boolean freeDelivery = false;
        Long themeId = -1L, trendId = -1L;
        if (AppController.isUserAdmin() ||
                UserInfoCache.getUser().isPromotedSeller() ||
                UserInfoCache.getUser().isVerifiedSeller()) {
            try {
                String originalPriceValue = originalPriceEdit.getText().toString().trim();
                originalPrice = Long.valueOf(originalPriceValue);
                if (price < 0) {
                    Toast.makeText(this, getString(R.string.invalid_post_price_negative), Toast.LENGTH_SHORT).show();
                    return null;
                } else if (originalPrice <= price) {
                    Toast.makeText(this, getString(R.string.invalid_post_original_price_less_than_price), Toast.LENGTH_SHORT).show();
                    return null;
                }
            } catch (NumberFormatException e) {
            }

            freeDelivery = freeDeliveryCheckBox.isChecked();
            if (freeDelivery == null) {
                freeDelivery = false;
            }

            if(themeCategory != null) {
                themeId = themeCategory.id;
            }

            if(trendCategory != null) {
                trendId = trendCategory.id;
            }
        }

        NewPostVM newPost = new NewPostVM(
                subCategory.id, themeId, trendId, title, body, price, conditionType, selectedImages,
                originalPrice, freeDelivery, countryCode);
        return newPost;
    }

    protected void doPost() {
        if (pending) {
            return;
        }

        if (selectedImages.size() == 0) {
            Toast.makeText(this, getString(R.string.invalid_post_no_photo), Toast.LENGTH_SHORT).show();
            return;
        }

        final NewPostVM newPost = getNewPost();
        if (newPost == null) {
            return;
        }

        ViewUtil.showSpinner(this);

        postAction.setEnabled(false);
        pending = true;
        AppController.getApiService().newPost(newPost, new Callback<ResponseStatusVM>() {
            @Override
            public void success(ResponseStatusVM responseStatus, Response response) {
                /*
                if (UserInfoCache.getUser().isFbLogin()) {
                    boolean fbShare = fbSharingButton.isChecked();
                    if (fbShare) {
                        PostVM post = new PostVM();
                        post.id = responseStatus.objId;
                        post.price = newPost.getPrice();
                        post.body = newPost.getBody();
                        post.title = newPost.getTitle();
                        SharingUtil.shareToFacebook(post, NewPostActivity.this);
                    }
                    SharedPreferencesUtil.getInstance().setSharingFacebookWall(fbShare);
                }
                */

                UserInfoCache.incrementNumProducts();
                complete();
                pending = false;
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(NewPostActivity.this, String.format(NewPostActivity.this.getString(R.string.post_failed), getActionTypeText()), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "doPost: failure", error);
                pending = false;
            }
        });
    }

    protected void reset() {
        postAction.setEnabled(true);
        selectedImages.clear();

        if (categoryPopup != null) {
            categoryPopup.dismiss();
            categoryPopup = null;
        }

        ViewUtil.stopSpinner(NewPostActivity.this);
    }

    protected void complete() {
        reset();

        postSuccess = true;
        Toast.makeText(NewPostActivity.this, String.format(getString(R.string.post_success), getActionTypeText()), Toast.LENGTH_LONG).show();
        ViewUtil.setActivityResult(this, true);
        NotificationCounter.refresh();
        finish();
    }

    protected void setCategory(CategoryVM cat) {
        this.category = cat;
        updateSelectCategoryLayout();
    }

	protected void setTheme(CategoryVM cat) {
		this.themeCategory = cat;
		updateSelectThemeLayout();
	}

	protected void setTrend(CategoryVM cat) {
		this.trendCategory = cat;
		updateSelectTrendLayout();
	}

    protected void setSubCategory(CategoryVM cat) {
        this.subCategory = cat;
        updateSelectSubCategoryLayout();
    }

    protected void initCategoryPopup(PopupWindow popup, List<CategoryVM> categories, final boolean isSubCategory) {
        try {
            LayoutInflater inflater = (LayoutInflater) NewPostActivity.this
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View layout = inflater.inflate(R.layout.category_popup_window,
                    (ViewGroup) findViewById(R.id.popupElement));

            if (popup == null) {
                popup = new PopupWindow(
                        layout,
                        ViewUtil.getRealDimension(DefaultValues.CATEGORY_PICKER_POPUP_WIDTH),
                        ViewGroup.LayoutParams.WRAP_CONTENT,    //ViewUtil.getRealDimension(DefaultValues.CATEGORY_PICKER_POPUP_HEIGHT),
                        true);
            }

            popup.setBackgroundDrawable(new BitmapDrawable(getResources(), ""));
            popup.setOutsideTouchable(false);
            popup.setFocusable(true);
            popup.showAtLocation(layout, Gravity.CENTER, 0, 0);

            ListView listView = (ListView) layout.findViewById(R.id.categoryList);
            adapter = new PopupCategoryListAdapter(this, categories);
            listView.setAdapter(adapter);

            final PopupWindow thisPopup = popup;
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    CategoryVM cat = adapter.getItem(position);
                    if (!isSubCategory) {
                        setCategory(cat);
                        setSubCategory(null);
                    } else {
                        setSubCategory(cat);
                    }

                    thisPopup.dismiss();
                    Log.d(TAG, "initCategoryPopup: listView.onItemClick: category=" + category.getId() + "|" + category.getName());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	protected void initThemePopup(PopupWindow popup, List<CategoryVM> categories, final boolean isTheme) {
		try {
			LayoutInflater inflater = (LayoutInflater) NewPostActivity.this
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			View layout = inflater.inflate(R.layout.category_popup_window,
					(ViewGroup) findViewById(R.id.popupElement));

			if (popup == null) {
				popup = new PopupWindow(
						layout,
						ViewUtil.getRealDimension(DefaultValues.CATEGORY_PICKER_POPUP_WIDTH),
						ViewGroup.LayoutParams.WRAP_CONTENT,    //ViewUtil.getRealDimension(DefaultValues.CATEGORY_PICKER_POPUP_HEIGHT),
						true);
			}

			popup.setBackgroundDrawable(new BitmapDrawable(getResources(), ""));
			popup.setOutsideTouchable(false);
			popup.setFocusable(true);
			popup.showAtLocation(layout, Gravity.CENTER, 0, 0);

            List<CategoryVM> categorieList = new ArrayList<>();
            categorieList.add(new CategoryVM(-1L, "- SELECT -"));
            categorieList.addAll(categories);
			ListView listView = (ListView) layout.findViewById(R.id.categoryList);
			adapter = new PopupCategoryListAdapter(this, categorieList);
			listView.setAdapter(adapter);

			final PopupWindow thisPopup = popup;
			listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					CategoryVM cat = adapter.getItem(position);
					if (isTheme) {
						setTheme(cat);
						//setSubCategory(null);
					} else {
						setTrend(cat);
					}

					thisPopup.dismiss();
					//Log.d(TAG, "initCategoryPopup: listView.onItemClick: category=" + category.getId() + "|" + category.getName());
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (selectedImages.size() >= DefaultValues.MAX_POST_IMAGES) {
                Toast.makeText(NewPostActivity.this,
                        String.format(NewPostActivity.this.getString(R.string.new_post_max_images), DefaultValues.MAX_POST_IMAGES), Toast.LENGTH_SHORT).show();
                return;
            }

            if ( (requestCode == ViewUtil.SELECT_GALLERY_IMAGE_REQUEST_CODE  && data != null) ||
                    requestCode == ViewUtil.SELECT_CAMERA_IMAGE_REQUEST_CODE )  {

                String imagePath = "";
                if (requestCode == ViewUtil.SELECT_GALLERY_IMAGE_REQUEST_CODE && data != null) {
                    selectedImageUri = data.getData();
                    imagePath = ImageUtil.getRealPathFromUri(this, selectedImageUri);
                } else if (requestCode == ViewUtil.SELECT_CAMERA_IMAGE_REQUEST_CODE) {
                    File picture = new File(ImageUtil.CAMERA_IMAGE_TEMP_PATH);
                    selectedImageUri = Uri.fromFile(picture);
                    imagePath = picture.getPath();
                }

                Log.d(TAG, "onActivityResult: imagePath=" + imagePath);

                ViewUtil.startSelectImageActivity(this, selectedImageUri);

                /*
                Bitmap bitmap = ImageUtil.resizeToUpload(imagePath);
                if (bitmap != null) {
                    ViewUtil.startSelectImageActivity(this, selectedImageUri);
                } else {
                    Toast.makeText(this, getString(R.string.photo_size_too_big), Toast.LENGTH_SHORT).show();
                }
                */
            } else if (requestCode == ViewUtil.CROP_IMAGE_REQUEST_CODE) {
				String croppedImagePath = data.getStringExtra(ViewUtil.INTENT_RESULT_OBJECT);
				Log.d(TAG, "onActivityResult: croppedImagePath=" + croppedImagePath);

                // adjusted?
                if (data.getData() != null) {
                    selectedImageUri = data.getData();
                    croppedImagePath = ImageUtil.getRealPathFromUri(this, data.getData());
                }

                selectPostImage(selectedPostImageIndex, croppedImagePath);

                /*
				if (data.getData() != null) {
					try {
						Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
						selectedImageUri = data.getData();
						selectPostImage(bitmap, selectedPostImageIndex, selectedImageUri);
					} catch (IOException e) {
                        Log.e(TAG, "onActivityResult: crop image error", e);
					}
				} else {
					selectPostImage(selectedPostImageIndex, croppedImagePath);
				}
				*/
            }

            // pop back soft keyboard
            //ViewUtil.popupInputMethodWindow(this);
		}
    }

    @Override
    public void onBackPressed() {
        String title = titleEdit.getText().toString().trim();
        String desc = descEdit.getText().toString().trim();
        String price = priceEdit.getText().toString().trim();

        if (postSuccess ||
                (selectedImages.size() == 0 && StringUtils.isEmpty(title) && StringUtils.isEmpty(desc) && StringUtils.isEmpty(price))) {
            super.onBackPressed();
            reset();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(NewPostActivity.this);
        builder.setMessage(String.format(getString(R.string.cancel_post), getActionTypeText()))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        NewPostActivity.super.onBackPressed();
                        reset();
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

}

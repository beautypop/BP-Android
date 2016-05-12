package com.beautypop.fragment;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.beautypop.R;
import com.beautypop.activity.EditImportImageActivity;
import com.beautypop.adapter.PopupCategoryListAdapter;
import com.beautypop.app.AppController;
import com.beautypop.app.CategoryCache;
import com.beautypop.app.CountryCache;
import com.beautypop.app.UserInfoCache;
import com.beautypop.util.DefaultValues;
import com.beautypop.util.ImageUtil;
import com.beautypop.util.SelectedImage;
import com.beautypop.util.ViewUtil;
import com.beautypop.viewmodel.CategoryVM;
import com.beautypop.viewmodel.CountryVM;
import com.beautypop.viewmodel.NewPostVM;
import com.squareup.picasso.Picasso;

import org.parceler.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;


public class ImportImagePagerFragment extends Fragment {
    private static final String TAG = EditImportImageActivity.class.getName();

    public ImageView image;

    public EditText title;
    public EditText prize;
    public TextView titleEdit, descEdit, priceEdit, originalPriceEdit, editTextInFocus;
    protected List<SelectedImage> selectedImages = new ArrayList<>();

    public CategoryVM category;
    public CategoryVM subCategory;
    public PopupWindow categoryPopup;
    public PopupWindow subCategoryPopup;
    public PopupCategoryListAdapter adapter;

    public String imageId;
    public String descriptionText;
    public String mediaId;
    public boolean isAlredyExist;
    public LinearLayout selectCatLayout, selectSubCatLayout, sellerLayout;
    public RelativeLayout catLayout;
    public TextView selectCatText, selectSubCatText;
    public ImageView selectCatIcon, selectSubCatIcon;
    public TextView catName, subCatName;
    public ImageView catIcon, subCatIcon;
    public View view;
    public ViewUtil.PostConditionType conditionType;
    public Spinner conditionTypeSpinner, countrySpinner;
    protected CheckBox freeDeliveryCheckBox;

    protected CountryVM country;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.import_image_pager_fragment, container, false);
        image = (ImageView) view.findViewById(R.id.image);
        title = (EditText) view.findViewById(R.id.titleEdit);
        prize = (EditText) view.findViewById(R.id.priceEdit);
        catLayout = (RelativeLayout) view.findViewById(R.id.catLayout);
        selectCatLayout = (LinearLayout) view.findViewById(R.id.selectCatLayout);
        selectSubCatLayout = (LinearLayout) view.findViewById(R.id.selectSubCatLayout);
        selectCatText = (TextView) view.findViewById(R.id.selectCatText);
        selectCatIcon = (ImageView) view.findViewById(R.id.selectCatIcon);
        selectSubCatText = (TextView) view.findViewById(R.id.selectSubCatText);
        selectSubCatIcon = (ImageView) view.findViewById(R.id.selectSubCatIcon);
        catIcon = (ImageView) view.findViewById(R.id.catIcon);
        catName = (TextView) view.findViewById(R.id.catName);
        subCatIcon = (ImageView) view.findViewById(R.id.subCatIcon);
        subCatName = (TextView) view.findViewById(R.id.subCatName);
        titleEdit = (TextView) view.findViewById(R.id.titleEdit);
        descEdit = (TextView) view.findViewById(R.id.descEdit);
        priceEdit = (TextView) view.findViewById(R.id.priceEdit);
        conditionTypeSpinner = (Spinner) view.findViewById(R.id.conditionTypeSpinner);
		Picasso.with(getActivity()).load(imageId).into(image);
        selectedImages.add(new SelectedImage(0, imageId));

        freeDeliveryCheckBox = (CheckBox) view.findViewById(R.id.freeDeliveryCheckBox);
        countrySpinner = (Spinner) view.findViewById(R.id.countrySpinner);

        sellerLayout = (LinearLayout) view.findViewById(R.id.sellerLayout);
        // seller layout

        sellerLayout.setVisibility(View.GONE);
        if (AppController.isUserAdmin() ||
                UserInfoCache.getUser().isPromotedSeller() ||
                UserInfoCache.getUser().isVerifiedSeller()) {
            sellerLayout.setVisibility(View.VISIBLE);
        }

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

        descEdit.setText(descriptionText);

        setCategory(null);
        setSubCategory(null);
        //Log.d(TAG, "onCreate: category="+category.id+" subCategory="+subCategory.id);

        selectCatLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initCategoryPopup(categoryPopup, CategoryCache.getCategories(), false);
            }
        });

        selectSubCatLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (category == null) {
                    Toast.makeText(getActivity(), getString(R.string.invalid_post_no_category), Toast.LENGTH_SHORT).show();
                    return;
                }
                initCategoryPopup(subCategoryPopup, CategoryCache.getSubCategories(category.id), true);
            }
        });

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

        return view;
    }

    protected void initCategoryPopup(PopupWindow popup, List<CategoryVM> categories, final boolean isSubCategory) {
        try {
            LayoutInflater inflater = (LayoutInflater) getActivity()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View layout = inflater.inflate(R.layout.category_popup_window,
                    (ViewGroup) view.findViewById(R.id.popupElement));

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
            adapter = new PopupCategoryListAdapter(getActivity(), categories);
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

    protected void setCategory(CategoryVM cat) {
        this.category = cat;
        updateSelectCategoryLayout();
    }

    protected void setSubCategory(CategoryVM cat) {
        this.subCategory = cat;
        updateSelectSubCategoryLayout();
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


    protected void setConditionTypeSpinner(ViewUtil.PostConditionType conditionType) {
        String value = ViewUtil.getPostConditionTypeValue(conditionType);
        int pos = ((ArrayAdapter)conditionTypeSpinner.getAdapter()).getPosition(value);
        conditionTypeSpinner.setSelection(pos);
    }

    protected void initConditionTypeSpinner() {
        List<String> conditionTypes = new ArrayList<>();
        conditionTypes.add(getString(R.string.spinner_select));
        conditionTypes.addAll(ViewUtil.getPostConditionTypeValues());

        ArrayAdapter<String> adapter = new ArrayAdapter<>( getActivity(),
                R.layout.spinner_item_right,
                conditionTypes);
        conditionTypeSpinner.setAdapter(adapter);
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public void setDescription(String description) {
        this.descriptionText = description;
    }

    public String getDescription() {
      return this.descEdit.getText().toString();
    }

    public NewPostVM getNewPost() {
        String title = titleEdit.getText().toString().trim();
        String body = descEdit.getText().toString().trim();
        String priceValue = priceEdit.getText().toString().trim();

        if (StringUtils.isEmpty(title)) {
            Toast.makeText(getActivity(), getString(R.string.invalid_post_title_empty), Toast.LENGTH_SHORT).show();
            return null;
        }

        /*
        if (StringUtils.isEmpty(body)) {
            Toast.makeText(this, getString(R.string.invalid_post_desc_empty), Toast.LENGTH_SHORT).show();
            return null;
        }
        */

        if (StringUtils.isEmpty(priceValue)) {
            Toast.makeText(getActivity(), getString(R.string.invalid_post_price_empty), Toast.LENGTH_SHORT).show();
            return null;
        }

        Long price = -1L;
        try {
            price = Long.valueOf(priceValue);
            if (price < 0) {
                Toast.makeText(getActivity(), getString(R.string.invalid_post_price_negative), Toast.LENGTH_SHORT).show();
                return null;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(getActivity(), getString(R.string.invalid_post_price_not_number), Toast.LENGTH_SHORT).show();
            return null;
        }

        if (conditionType == null) {
            Toast.makeText(getActivity(), getString(R.string.invalid_post_condition_empty), Toast.LENGTH_SHORT).show();
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

        Long originalPrice = -1L;
        Boolean freeDelivery = false;
        String countryCode = "";
        if (AppController.isUserAdmin() ||
                UserInfoCache.getUser().isPromotedSeller() ||
                UserInfoCache.getUser().isVerifiedSeller()) {
            try {
                String originalPriceValue = originalPriceEdit.getText().toString().trim();
                originalPrice = Long.valueOf(originalPriceValue);
                if (price < 0) {
                    Toast.makeText(getActivity(), getString(R.string.invalid_post_price_negative), Toast.LENGTH_SHORT).show();
                    return null;
                } else if (originalPrice <= price) {
                    Toast.makeText(getActivity(), getString(R.string.invalid_post_original_price_less_than_price), Toast.LENGTH_SHORT).show();
                    return null;
                }
            } catch (NumberFormatException e) {
            }

            freeDelivery = freeDeliveryCheckBox.isChecked();
            if (freeDelivery == null) {
                freeDelivery = false;
            }

            if (country != null) {
                countryCode = country.code;
            }
        }

        NewPostVM newPost = new NewPostVM(
                subCategory.id, title, body, price, conditionType, selectedImages,
                originalPrice, freeDelivery, countryCode);
        //String path = selectedImages.get(0).getFile().getAbsolutePath();
        String path = selectedImages.get(0).getPathUri().toString();
        newPost.setImage(path);
        newPost.setMediaId(this.mediaId);
        return newPost;
    }


    public String getMediaId() {
        return mediaId;
    }

    public void setMediaId(String mediaId) {
        this.mediaId = mediaId;
    }
}

package com.beautypop.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.beautypop.R;
import com.beautypop.app.TrackedFragment;
import com.beautypop.util.ImageUtil;
import com.beautypop.util.ViewUtil;

public class ThemeImagePagerFragment extends TrackedFragment {

    private ImageView image;
    private ImageView soldImage;

    private int imageId;
    private boolean sold;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.theme_image_pager_fragment, container, false);

        image = (ImageView) view.findViewById(R.id.image);

		image.setImageResource(imageId);


        return view;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

}

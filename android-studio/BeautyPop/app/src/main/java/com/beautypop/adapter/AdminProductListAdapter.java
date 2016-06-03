package com.beautypop.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.beautypop.R;
import com.beautypop.util.DateTimeUtil;
import com.beautypop.util.ImageUtil;
import com.beautypop.util.ViewUtil;
import com.beautypop.viewmodel.PostVMLite;

import java.util.List;

public class AdminProductListAdapter extends BaseAdapter {
    private static final String TAG = AdminProductListAdapter.class.getName();

    private LinearLayout itemLayout;
    private ImageView postImage, sellerImage;
    private TextView priceText, titleText, postIdText, createdDateText, ownerText;
    private TextView numChatsText, numLikesText, numCommentsText, numViewsText;

    private Activity activity;
    private LayoutInflater inflater;

    private List<PostVMLite> posts;

    public AdminProductListAdapter(Activity activity, List<PostVMLite> posts) {
        this.activity = activity;
        this.posts = posts;
    }

    @Override
    public int getCount() {
        if (posts == null)
            return 0;
        return posts.size();
    }

    @Override
    public PostVMLite getItem(int location) {
        if (posts == null || location > posts.size()-1)
            return null;
        return posts.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public synchronized View getView(int position, View convertView, final ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null)
            convertView = inflater.inflate(R.layout.admin_product_list_item, null);

        itemLayout = (LinearLayout) convertView.findViewById(R.id.itemLayout);
        postImage = (ImageView) convertView.findViewById(R.id.postImage);
        sellerImage = (ImageView) convertView.findViewById(R.id.sellerImage);
        priceText = (TextView) convertView.findViewById(R.id.priceText);
        titleText = (TextView) convertView.findViewById(R.id.titleText);
        postIdText = (TextView) convertView.findViewById(R.id.postIdText);
        createdDateText = (TextView) convertView.findViewById(R.id.createdDateText);
        ownerText = (TextView) convertView.findViewById(R.id.ownerText);
        numViewsText = (TextView) convertView.findViewById(R.id.numViewsText);
        numLikesText = (TextView) convertView.findViewById(R.id.numLikesText);
        numChatsText = (TextView) convertView.findViewById(R.id.numChatsText);
        numCommentsText = (TextView) convertView.findViewById(R.id.numCommentsText);

        final PostVMLite item = posts.get(position);

        ImageUtil.displayPostImage(item.getImages()[0], postImage);
        ImageUtil.displayThumbnailProfileImage(item.getOwnerId(), sellerImage);

        itemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewUtil.startProductActivity(activity, item.getId());
            }
        });

        sellerImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewUtil.startUserProfileActivity(activity, item.getOwnerId());
            }
        });

        priceText.setText(ViewUtil.formatPrice(item.price));
        titleText.setText(item.title);

        postIdText.setText("ID: "+item.id);
        createdDateText.setText("Posted: "+DateTimeUtil.getTimeAgo(item.getCreatedDate()));
        ownerText.setText("Seller: "+item.ownerName+" ("+item.ownerId+")");

        // stats
        numViewsText.setText("Views: "+item.numViews);
        numViewsText.setTypeface(item.numViews > 0 ? Typeface.DEFAULT_BOLD : Typeface.DEFAULT);
        numLikesText.setText("Likes: "+item.numLikes);
        numLikesText.setTypeface(item.numLikes > 0 ? Typeface.DEFAULT_BOLD : Typeface.DEFAULT);
        numChatsText.setText("Chats: "+item.numChats);
        numChatsText.setTypeface(item.numChats > 0? Typeface.DEFAULT_BOLD : Typeface.DEFAULT);
        numCommentsText.setText("Comments: "+item.numComments);
        numCommentsText.setTypeface(item.numComments > 0? Typeface.DEFAULT_BOLD : Typeface.DEFAULT);

        return convertView;
    }
}
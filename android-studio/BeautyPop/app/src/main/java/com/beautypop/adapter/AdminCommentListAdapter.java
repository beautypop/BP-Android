package com.beautypop.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.beautypop.R;
import com.beautypop.app.AppController;
import com.beautypop.util.DateTimeUtil;
import com.beautypop.util.ImageUtil;
import com.beautypop.util.ViewUtil;
import com.beautypop.viewmodel.AdminCommentVM;
import com.beautypop.viewmodel.CommentVM;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class AdminCommentListAdapter extends BaseAdapter {
    private ImageView userImage, postImage;
    private TextView userNameText, commentText, timeText, soldText;

    private Activity activity;
    private LayoutInflater inflater;

    private List<AdminCommentVM> comments;

    public AdminCommentListAdapter(Activity activity, List<AdminCommentVM> comments) {
        this.activity = activity;
        this.comments = comments;
    }

    @Override
    public int getCount() {
        if (comments == null)
            return 0;
        return comments.size();
    }

    @Override
    public AdminCommentVM getItem(int location) {
        if (comments == null || location > comments.size()-1)
            return null;
        return comments.get(location);
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
            convertView = inflater.inflate(R.layout.admin_comment_list_item, null);

        userImage = (ImageView) convertView.findViewById(R.id.userImage);
        userNameText = (TextView) convertView.findViewById(R.id.userNameText);
        timeText = (TextView) convertView.findViewById(R.id.timeText);
        commentText = (TextView) convertView.findViewById(R.id.commentText);
        postImage = (ImageView) convertView.findViewById(R.id.postImage);
        soldText = (TextView) convertView.findViewById(R.id.soldText);

        final AdminCommentVM item = comments.get(position);

        // admin fields
        LinearLayout adminLayout = (LinearLayout) convertView.findViewById(R.id.adminLayout);
        if (AppController.isUserAdmin()) {
            ImageView androidIcon = (ImageView) convertView.findViewById(R.id.androidIcon);
            ImageView iosIcon = (ImageView) convertView.findViewById(R.id.iosIcon);
            ImageView mobileIcon = (ImageView) convertView.findViewById(R.id.mobileIcon);
            androidIcon.setVisibility(AppController.DeviceType.ANDROID.name().equals(item.deviceType)? View.VISIBLE : View.GONE);
            iosIcon.setVisibility(AppController.DeviceType.IOS.name().equals(item.deviceType)? View.VISIBLE : View.GONE);
            mobileIcon.setVisibility(AppController.DeviceType.WAP.name().equals(item.deviceType)? View.VISIBLE : View.GONE);
            adminLayout.setVisibility(View.VISIBLE);
        } else {
            adminLayout.setVisibility(View.GONE);
        }

        ViewUtil.setHtmlText(item.body, commentText, activity, true, true);

        userNameText.setText(item.ownerName);
        userNameText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewUtil.startUserProfileActivity(activity, item.ownerId);
            }
        });

        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewUtil.startUserProfileActivity(activity, item.ownerId);
            }
        });

        postImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewUtil.startProductActivity(activity, item.postId);
            }
        });

        soldText.setVisibility(item.postSold ? View.VISIBLE : View.GONE);

        timeText.setText(DateTimeUtil.getTimeAgo(item.createdDate));

        // profile pic
        ImageUtil.displayThumbnailProfileImage(item.ownerId, userImage);

        // post image
        ImageUtil.displayPostImage(item.postImage, postImage);

        return convertView;
    }
}
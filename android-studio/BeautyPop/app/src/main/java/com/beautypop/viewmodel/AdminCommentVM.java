package com.beautypop.viewmodel;

import java.io.Serializable;

public class AdminCommentVM implements Serializable {
    public Long id;
    public Long createdDate;
    public Long ownerId;
    public String ownerName;
    public String body;

    public Long postId;
    public Long postImage;
    public String postTitle;
    public Long postPrice;
    public boolean postSold;

    public String deviceType;
}
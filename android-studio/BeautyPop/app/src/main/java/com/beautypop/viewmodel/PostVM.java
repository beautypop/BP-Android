package com.beautypop.viewmodel;

import java.util.List;

public class PostVM extends PostVMLite {
    public Long ownerNumProducts;
    public Long ownerNumFollowers;
    public Long ownerLastLogin;
    public String body;
    public Long categoryId;
    public String categoryType;
    public String categoryName;
    public String categoryIcon;
    public Long subCategoryId;
    public String subCategoryType;
    public String subCategoryName;
    public String subCategoryIcon;
    public List<CommentVM> latestComments;

    public boolean isOwner = false;
    public boolean isFollowingOwner = false;

    public String deviceType;

    public Long getOwnerNumProducts() {
        return ownerNumProducts;
    }

    public void setOwnerNumProducts(Long ownerNumProducts) {
        this.ownerNumProducts = ownerNumProducts;
    }

    public Long getOwnerNumFollowers() {
        return ownerNumFollowers;
    }

    public void setOwnerNumFollowers(Long ownerNumFollowers) {
        this.ownerNumFollowers = ownerNumFollowers;
    }

    public Long getOwnerLastLogin() {
        return ownerLastLogin;
    }

    public void setOwnerLastLogin(Long ownerLastLogin) {
        this.ownerLastLogin = ownerLastLogin;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryType() {
        return categoryType;
    }

    public void setCategoryType(String categoryType) {
        this.categoryType = categoryType;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryIcon() {
        return categoryIcon;
    }

    public void setCategoryIcon(String categoryIcon) {
        this.categoryIcon = categoryIcon;
    }

    public Long getSubCategoryId() {
        return subCategoryId;
    }

    public void setSubCategoryId(Long subCategoryId) {
        this.subCategoryId = subCategoryId;
    }

    public String getSubCategoryType() {
        return subCategoryType;
    }

    public void setSubCategoryType(String subCategoryType) {
        this.subCategoryType = subCategoryType;
    }

    public String getSubCategoryName() {
        return subCategoryName;
    }

    public void setSubCategoryName(String subCategoryName) {
        this.subCategoryName = subCategoryName;
    }

    public String getSubCategoryIcon() {
        return subCategoryIcon;
    }

    public void setSubCategoryIcon(String subCategoryIcon) {
        this.subCategoryIcon = subCategoryIcon;
    }

    public List<CommentVM> getLatestComments() {
        return latestComments;
    }

    public void setLatestComments(List<CommentVM> latestComments) {
        this.latestComments = latestComments;
    }

    public boolean isOwner() {
        return isOwner;
    }

    public void setIsOwner(boolean isOwner) {
        this.isOwner = isOwner;
    }

    public boolean isFollowingOwner() {
        return isFollowingOwner;
    }

    public void setFollowingOwner(boolean isFollowingOwner) {
        this.isFollowingOwner = isFollowingOwner;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }
}
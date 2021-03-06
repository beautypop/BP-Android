package com.beautypop.viewmodel;

import java.util.List;

public class CategoryVM {
    public Long id;
    public String icon;
    public String thumbnail;
    public String name;
    public String description;
    public String categoryType;
    public int seq;
    public Long parentId;
    public boolean featured;
    public List<CategoryVM> subCategories;

    public CategoryVM(){}

    public CategoryVM(Long id, String name){
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategoryType() {
        return categoryType;
    }

    public void setCategoryType(String categoryType) {
        this.categoryType = categoryType;
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public List<CategoryVM> getSubCategories() {
        return subCategories;
    }

    public void setSubCategories(List<CategoryVM> subCategories) {
        this.subCategories = subCategories;
    }

	public boolean isFeatured() { return featured; }

	public void setFeatured(boolean featured) {	this.featured = featured; }
}

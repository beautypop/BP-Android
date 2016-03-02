package com.beautypop.viewmodel;

import java.util.List;

public class CategoryVM {
    public Long id;
    public String icon;
    public String name;
    public String description;
    public String categoryType;
    public int seq;
    public List<CategoryVM> subCategories;

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

    public List<CategoryVM> getSubCategories() {
        return subCategories;
    }

    public void setSubCategories(List<CategoryVM> subCategories) {
        this.subCategories = subCategories;
    }
}

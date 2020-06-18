package com.mymeatshop.model.response.dashboard;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PCat {

    @SerializedName("is_active")
    private boolean isActive;

    @SerializedName("level")
    private int level;

    @SerializedName("parent_id")
    private int parentId;

    @SerializedName("product_count")
    private int productCount;

    @SerializedName("name")
    private String name;

    @SerializedName("id")
    private int id;

    @SerializedName("position")
    private int position;

    @SerializedName("children_data")
    private List<ChildrenDataItem> childrenData;

    public boolean isIsActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public int getProductCount() {
        return productCount;
    }

    public void setProductCount(int productCount) {
        this.productCount = productCount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public List<ChildrenDataItem> getChildrenData() {
        return childrenData;
    }

    public void setChildrenData(List<ChildrenDataItem> childrenData) {
        this.childrenData = childrenData;
    }

    @Override
    public String toString() {
        return
                "PCat{" +
                        "is_active = '" + isActive + '\'' +
                        ",level = '" + level + '\'' +
                        ",parent_id = '" + parentId + '\'' +
                        ",product_count = '" + productCount + '\'' +
                        ",name = '" + name + '\'' +
                        ",id = '" + id + '\'' +
                        ",position = '" + position + '\'' +
                        ",children_data = '" + childrenData + '\'' +
                        "}";
    }
}
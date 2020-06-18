package com.mymeatshop.model.response.product_detail;

import com.google.gson.annotations.SerializedName;

public class MediaGalleryItem {

    @SerializedName("file")
    private String file;

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    @Override
    public String toString() {
        return
                "MediaGalleryItem{" +
                        "file = '" + file + '\'' +
                        "}";
    }
}

package com.clickitproduct.Beans;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Product {

    @SerializedName("product_img")
    @Expose
    private String productImg;
    @SerializedName("product_name")
    @Expose
    private String productName;
    @SerializedName("product_price")
    @Expose
    private long productPrice;
    @SerializedName("product_category")
    @Expose
    private String productCategory;
    @SerializedName("product_description")
    @Expose
    private String productDescription;
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("totalviews")
    @Expose
    private long totalviews;
    @SerializedName("views")
    @Expose
    private List<ProductView> views = null;

    public String getProductImg() {
        return productImg;
    }

    public void setProductImg(String productImg) {
        this.productImg = productImg;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public long getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(long productPrice) {
        this.productPrice = productPrice;
    }

    public String getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(String productCategory) {
        this.productCategory = productCategory;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getTotalviews() {
        return totalviews;
    }

    public void setTotalviews(long totalviews) {
        this.totalviews = totalviews;
    }

    public List<ProductView> getViews() {
        return views;
    }

    public void setViews(List<ProductView> views) {
        this.views = views;
    }

}

package com.clickitproduct.Beans;

import java.io.Serializable;

public class Cart implements Serializable
{
    String productId,userId,shopId, productImg, productNm, productPrice;

    public String getProductImg() {
        return productImg;
    }

    public void setProductImg(String productImg) {
        this.productImg = productImg;
    }

    public String getProductNm() {
        return productNm;
    }

    public void setProductNm(String productNm) {
        this.productNm = productNm;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public int getIsLogClick() {

        return isLogClick;
    }

    public void setIsLogClick(int isLogClick) {
        this.isLogClick = isLogClick;
    }

    int isLogClick;

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    @Override
    public String toString() {
        return "{" +
                "productId=" + productId +
                ", user_id=" + userId +
                ", shop_id=" + shopId +
                ", product_img=" + productImg +
                ", product_name=" + productNm +
                ", product_price=" + productPrice +
                '}';
    }
}

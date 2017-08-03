
package com.clickitproduct.Beans;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Coupon {

    @SerializedName("coupon_minimum_price")
    @Expose
    private String couponMinimumPrice;
    @SerializedName("coupon_discount")
    @Expose
    private String couponDiscount;
    @SerializedName("coupon_quantity")
    @Expose
    private long couponQuantity;
    @SerializedName("coupon_img")
    @Expose
    private String couponImg;
    @SerializedName("coupon_validity")
    @Expose
    private String couponValidity;
    @SerializedName("coupon_remaining")
    @Expose
    private long couponRemaining;
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("coupon_grabbed")
    @Expose
    private long couponGrabbed;
    @SerializedName("coupon_registered")
    @Expose
    private String couponRegistered;

    public String getCouponMinimumPrice() {
        return couponMinimumPrice;
    }

    public void setCouponMinimumPrice(String couponMinimumPrice) {
        this.couponMinimumPrice = couponMinimumPrice;
    }

    public String getCouponDiscount() {
        return couponDiscount;
    }

    public void setCouponDiscount(String couponDiscount) {
        this.couponDiscount = couponDiscount;
    }

    public long getCouponQuantity() {
        return couponQuantity;
    }

    public void setCouponQuantity(long couponQuantity) {
        this.couponQuantity = couponQuantity;
    }

    public String getCouponImg() {
        return couponImg;
    }

    public void setCouponImg(String couponImg) {
        this.couponImg = couponImg;
    }

    public String getCouponValidity() {
        return couponValidity;
    }

    public void setCouponValidity(String couponValidity) {
        this.couponValidity = couponValidity;
    }

    public long getCouponRemaining() {
        return couponRemaining;
    }

    public void setCouponRemaining(long couponRemaining) {
        this.couponRemaining = couponRemaining;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getCouponGrabbed() {
        return couponGrabbed;
    }

    public void setCouponGrabbed(long couponGrabbed) {
        this.couponGrabbed = couponGrabbed;
    }

    public String getCouponRegistered() {
        return couponRegistered;
    }

    public void setCouponRegistered(String couponRegistered) {
        this.couponRegistered = couponRegistered;
    }

}

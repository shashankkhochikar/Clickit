
package com.clickitproduct.Beans;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Offer {

    @SerializedName("offer_img")
    @Expose
    private String offerImg;
    @SerializedName("offer_name")
    @Expose
    private String offerName;
    @SerializedName("offer_from_date")
    @Expose
    private String offerFromDate;
    @SerializedName("offer_to_date")
    @Expose
    private String offerToDate;
    @SerializedName("offer_description")
    @Expose
    private String offerDescription;
    @SerializedName("_id")
    @Expose
    private String id;

    public String getOfferImg() {
        return offerImg;
    }

    public void setOfferImg(String offerImg) {
        this.offerImg = offerImg;
    }

    public String getOfferName() {
        return offerName;
    }

    public void setOfferName(String offerName) {
        this.offerName = offerName;
    }

    public String getOfferFromDate() {
        return offerFromDate;
    }

    public void setOfferFromDate(String offerFromDate) {
        this.offerFromDate = offerFromDate;
    }

    public String getOfferToDate() {
        return offerToDate;
    }

    public void setOfferToDate(String offerToDate) {
        this.offerToDate = offerToDate;
    }

    public String getOfferDescription() {
        return offerDescription;
    }

    public void setOfferDescription(String offerDescription) {
        this.offerDescription = offerDescription;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}

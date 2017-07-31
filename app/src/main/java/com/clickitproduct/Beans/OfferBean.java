package com.clickitproduct.Beans;

import android.graphics.Bitmap;

public class OfferBean
{
    int id;
    String offer_name;
    String offer_desc;
    String offer_date_from, offer_date_to;
    Bitmap offerimage;

    public OfferBean()
    {
        id = 0;
        offer_name = "";
        offer_desc = "";
        offerimage = null;
        offer_date_from="";
        offer_date_to="";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOffer_name() {
        return offer_name;
    }

    public void setOffer_name(String offer_name) {
        this.offer_name = offer_name;
    }

    public String getOffer_desc() {
        return offer_desc;
    }

    public void setOffer_desc(String offer_desc) {
        this.offer_desc = offer_desc;
    }

    public Bitmap getOfferimage() {
        return offerimage;
    }

    public void setOfferimage(Bitmap offerimage) {
        this.offerimage = offerimage;
    }

    public String getOffer_date_from() {
        return offer_date_from;
    }

    public void setOffer_date_from(String offer_date_from) {
        this.offer_date_from = offer_date_from;
    }

    public String getOffer_date_to() {
        return offer_date_to;
    }

    public void setOffer_date_to(String offer_date_to) {
        this.offer_date_to = offer_date_to;
    }
}

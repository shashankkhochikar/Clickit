package com.clickitproduct.Beans;

import android.graphics.Bitmap;

public class ProductBean
{
    int id;
    String product_name;
    String product_price;
    int product_image;
    Bitmap prodimage;
    String category, sub_category;
    String desc;

    public ProductBean()
    {
        id = 0;
        product_name = "";
        product_price = "";
        product_image = 0;
        prodimage = null;
        category = "";
        desc = "";
        sub_category = "";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getProduct_price() {
        return product_price;
    }

    public void setProduct_price(String product_price) {
        this.product_price = product_price;
    }

    public int getProduct_image() {
        return product_image;
    }

    public void setProduct_image(int product_image) {
        this.product_image = product_image;
    }

    public Bitmap getProdimage() {
        return prodimage;
    }

    public void setProdimage(Bitmap prodimage) {
        this.prodimage = prodimage;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getSub_category() {
        return sub_category;
    }

    public void setSub_category(String sub_category) {
        this.sub_category = sub_category;
    }
}

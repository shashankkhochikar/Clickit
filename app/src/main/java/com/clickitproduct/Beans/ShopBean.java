package com.clickitproduct.Beans;

public class ShopBean {

    String _id, sales_id, expiry_date, user_id, establish_date, email, mobile, shop_name, last_updated, approval, register_date,
            shop_time, holiday, time_to, time_from, shop_img1, shop_img2, shop_img3, category, longitude, latitude, pincode,
            city, addres;

    String[] images;
    String[] address;

    public ShopBean()
    {
        _id=""; sales_id=""; expiry_date=""; user_id=""; establish_date=""; email=""; mobile=""; shop_name=""; last_updated=""; approval=""; register_date="";
        shop_time=""; holiday=""; time_to=""; time_from=""; shop_img1=""; shop_img2=""; shop_img3=""; category=""; longitude=""; latitude=""; pincode="";
        city=""; addres="";
        images = null;
        address = null;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getSales_id() {
        return sales_id;
    }

    public void setSales_id(String sales_id) {
        this.sales_id = sales_id;
    }

    public String getExpiry_date() {
        return expiry_date;
    }

    public void setExpiry_date(String expiry_date) {
        this.expiry_date = expiry_date;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getEstablish_date() {
        return establish_date;
    }

    public void setEstablish_date(String establish_date) {
        this.establish_date = establish_date;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getShop_name() {
        return shop_name;
    }

    public void setShop_name(String shop_name) {
        this.shop_name = shop_name;
    }

    public String getLast_updated() {
        return last_updated;
    }

    public void setLast_updated(String last_updated) {
        this.last_updated = last_updated;
    }

    public String getApproval() {
        return approval;
    }

    public void setApproval(String approval) {
        this.approval = approval;
    }

    public String getRegister_date() {
        return register_date;
    }

    public void setRegister_date(String register_date) {
        this.register_date = register_date;
    }

    public String getShop_time() {
        return shop_time;
    }

    public void setShop_time(String shop_time) {
        this.shop_time = shop_time;
    }

    public String getHoliday() {
        return holiday;
    }

    public void setHoliday(String holiday) {
        this.holiday = holiday;
    }

    public String getTime_to() {
        return time_to;
    }

    public void setTime_to(String time_to) {
        this.time_to = time_to;
    }

    public String getTime_from() {
        return time_from;
    }

    public void setTime_from(String time_from) {
        this.time_from = time_from;
    }

    public String getShop_img1() {
        return shop_img1;
    }

    public void setShop_img1(String shop_img1) {
        this.shop_img1 = shop_img1;
    }

    public String getShop_img2() {
        return shop_img2;
    }

    public void setShop_img2(String shop_img2) {
        this.shop_img2 = shop_img2;
    }

    public String getShop_img3() {
        return shop_img3;
    }

    public void setShop_img3(String shop_img3) {
        this.shop_img3 = shop_img3;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddres() {
        return addres;
    }

    public void setAddres(String addres) {
        this.addres = addres;
    }

    public String[] getAddress() {
        return address;
    }

    public void setAddress(String[] address) {
        this.address = address;
    }

    public String[] getImages() {
        return images;
    }

    public void setImages(String[] images) {
        this.images = images;
    }

    @Override
    public String toString() {
        return shop_name;
    }
}

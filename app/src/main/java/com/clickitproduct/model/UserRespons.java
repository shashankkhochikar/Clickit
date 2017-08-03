package com.clickitproduct.model;

import com.google.gson.annotations.SerializedName;

public class UserRespons
{
    @SerializedName("success")
    public boolean success;
    @SerializedName("message")
    public String message;
    @SerializedName("data")
    public Data data;
    @SerializedName("follow")
    public int follow;
    @SerializedName("profilepicdir")
    public String profilepicdir;
    @SerializedName("coverpicdir")
    public String coverpicdir;

    public static class Data {
        @SerializedName("_id")
        public String _id;
        @SerializedName("profile_pic")
        public String profile_pic;
        @SerializedName("fcm_id")
        public String fcm_id;
        @SerializedName("password")
        public String password;
        @SerializedName("email")
        public String email;
        @SerializedName("mobile")
        public String mobile;
        @SerializedName("last_name")
        public String last_name;
        @SerializedName("first_name")
        public String first_name;
        @SerializedName("__v")
        public int __v;
        @SerializedName("tag_line")
        public String tag_line;
        @SerializedName("cover_pic")
        public String cover_pic;
        @SerializedName("lastupdated")
        public String lastupdated;
        @SerializedName("clickpic")
        public int clickpic;

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public String getProfile_pic() {
            return profile_pic;
        }

        public void setProfile_pic(String profile_pic) {
            this.profile_pic = profile_pic;
        }

        public String getFcm_id() {
            return fcm_id;
        }

        public void setFcm_id(String fcm_id) {
            this.fcm_id = fcm_id;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
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

        public String getLast_name() {
            return last_name;
        }

        public void setLast_name(String last_name) {
            this.last_name = last_name;
        }

        public String getFirst_name() {
            return first_name;
        }

        public void setFirst_name(String first_name) {
            this.first_name = first_name;
        }

        public int get__v() {
            return __v;
        }

        public void set__v(int __v) {
            this.__v = __v;
        }

        public String getTag_line() {
            return tag_line;
        }

        public void setTag_line(String tag_line) {
            this.tag_line = tag_line;
        }

        public String getCover_pic() {
            return cover_pic;
        }

        public void setCover_pic(String cover_pic) {
            this.cover_pic = cover_pic;
        }

        public String getLastupdated() {
            return lastupdated;
        }

        public void setLastupdated(String lastupdated) {
            this.lastupdated = lastupdated;
        }

        public int getClickpic() {
            return clickpic;
        }

        public void setClickpic(int clickpic) {
            this.clickpic = clickpic;
        }

        public int getIsleader() {
            return isleader;
        }

        public void setIsleader(int isleader) {
            this.isleader = isleader;
        }

        public String getDob() {
            return dob;
        }

        public void setDob(String dob) {
            this.dob = dob;
        }

        @SerializedName("isleader")
        public int isleader;
        @SerializedName("dob")
        public String dob;
    }
}
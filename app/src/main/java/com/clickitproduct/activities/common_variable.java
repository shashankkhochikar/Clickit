package com.clickitproduct.activities;

import android.content.SharedPreferences;
import com.clickitproduct.Beans.ShopBean;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.List;

public class common_variable
{
    public static int IsShopAvailable = 0;
    public static int screenWidth, screenHeight;
    public static String User_id="";
    public static String User_Name="";
    public static String User_Profile_Pic="";
    public static String User_Profile_Pic_URL="";
    public static String main_web_url="http://www.clickit.in";
   // public static String main_web_url="http://192.168.0.11:3000";
    public static JsonObject UserDetail;
    public static String FCM_Token = "";
    public static SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs";
    public static JsonArray ShopDetails;
    public static int changed_profile_detail = 0;
    public static String[] Holidays = {"None", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
    public static String[] Time_Format = {"AM", "PM"};
    public static String Shop_ID;
    public static String[] Classified_Types = {"Sell", "Rent", "Other"};
    public static int Refresh_Shop = 0;
    public static int Refresh_Product = 0;
    public static int Refresh_Offer = 0;
    public static int Refresh_Classified = 0;
    public static int Refresh_Service = 0;
    public static int forgot_password = 0;
    public static String User_Shop_ID = "";
    public static String User_Shop_Name = "";
    public static String User_Shop_Category = "";
    public static int Refresh_noti_count = 0;
    public static String[] Classified_Categories = {"Mobile", "Tablet", "Cars", "Bikes", "Furniture", "Real Estate", "Electronics", "Other"};
    public static JsonObject jShopProductObj;
}

package com.clickitproduct.commonutil;

/**
 * @author Sandip
 */
public class AppSetting extends AppSettingMain implements AppConstants {

  
    private static String KEY_REQUEST_ID = "reuest_id";
   
    private static String KEY_LOGIN = "login";

    private static String KEY_LOGIN_REMEBERME = "remeberMe";
    private static String KEY_LOGIN_EMAIL = "email";
    private static String KEY_LOGIN_USERNAME = "name";
    private static String KEY_PROFILE_URL = "purl";
    public static String KEY_URL;
   
    private static String KEY_USER_ID = "id";
    private static String KEY_LAT = "latitude";
    private static String KEY_LONG = "longitude";
    
    private static String KEY_UPDATE_OFFLINEDATA = "update";
   
   
   

    public static boolean getUpdateOfflinedata() {
        return getBoolean(KEY_UPDATE_OFFLINEDATA, false);
    }


    public static void setUpdateOfflinedata(boolean isUpdate) {
        setBoolean(KEY_UPDATE_OFFLINEDATA, isUpdate);
    }

   
    public static boolean isLogin() {
        return getBoolean(KEY_LOGIN, false);
    }


    public static void setLogin(boolean isLogin) {
        setBoolean(KEY_LOGIN, isLogin);
    }

    public static String getRequestId() {
        return getString(KEY_REQUEST_ID, null);
    }

    public static void setRequestId(String reqestid) {
        setString(KEY_REQUEST_ID, reqestid);
    }

    public static boolean getRemeberMe() {
        return getBoolean(KEY_LOGIN_REMEBERME, false);
    }

    /**
     * @para Product 1
     */
    public static void setRemeberMe(Boolean bselect) {
        setBoolean(KEY_LOGIN_REMEBERME, bselect);
    }

    /**
     * @return user login id
     */
    public static String getUserLoginEmail() {
        return getString(KEY_LOGIN_EMAIL, null);
    }


    public static void setUserLoginEmail(String userLoginemail) {
        setString(KEY_LOGIN_EMAIL, userLoginemail);
    }

    public static String getUserId() {
        return getString(KEY_USER_ID, null);
    }


    public static void setUserId(String userLoginemail) {
        setString(KEY_USER_ID, userLoginemail);
    }

    public static String getapiURL() {
        return getString(KEY_URL, null);
    }


    public static void setapiURL(String apiURL) {
        setString(KEY_URL, apiURL);
    }
    public static String getUserName() {
        return getString(KEY_LOGIN_USERNAME, null);
    }


    public static void setUserName(String userName) {
        setString(KEY_LOGIN_USERNAME, userName);
    }

  
    public static String getUserLatitude() {
        return getString(KEY_LAT, null);
    }


    public static void setUserLatitude(String userLat) {
        setString(KEY_LAT, userLat);
    }

    public static String getUserLongitude() {
        return getString(KEY_LONG, null);
    }


    public static void setUserLongitude(String userLong) {
        setString(KEY_LONG, userLong);
    }
   


}

package com.clickitproduct.commonutil;

public interface AppConstants
{
	public static int SIGN_IN_SPLASH = 1;

	public static int ERROR_CODE_INT = -1;

	public static int SPLASH_TIME_OUT = 300;

	public static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 101;
	public static int CROP_IMAGE_ACTIVITY_REQUEST_CODE = 201;
	public static int PICK_IMAGE_ACTIVITY_REQUEST_CODE = 301;

	public static final int STATUS_OK = 0;
	public static final String SERVER_URL = "http://120.63.240.30:8085/PCPLWebServices/Service1.asmx";
	public static String EMAIL_REGEX = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
	public static String FIRSTNAME_REGEX = "[a-zA-Z][a-zA-Z]*";
	public static String LASTNAME_REGEX = "[a-zA-z]+([ '-][a-zA-Z]+)*";
	
	public static String RequestDetailArea="Area";
	public static String RequestDetailPincode="Pincode";
	public static String RequestDetailProperty="Property";
	public static String RequestDetailOccupied="Occupied";
	public static String RequestDetailOccupiedSubType="OccupiedSubType";
	

	public enum Buttons
	{	OK, RETRY, CANCEL, OKCANCEL		}
}

package com.clickitproduct.commonutil;

import android.content.Context;
import android.content.SharedPreferences;

public class AppSettingMain
{
	
	private static final String PREFERENCE_APPLICATION = "Pcpl";

	private static SharedPreferences getPref() {
		return AppDetails.getContext().getSharedPreferences(
				PREFERENCE_APPLICATION, Context.MODE_PRIVATE);
	}

	/**
	 * 
	 * @return Shared Preferences Editor object
	 */
	private static SharedPreferences.Editor getEditor() {
		return getPref().edit();
	}

	/**
	 * 
	 * @param keyValue 
	 * @param value
	 */
	public static void setBoolean(String keyValue, boolean value) {
		SharedPreferences.Editor editor = getEditor();
		editor.putBoolean(keyValue, value);
		editor.commit();
	}

	/**
	 * 
	 * @param key
	 * @param defValue
	 * @return
	 */
	public static boolean getBoolean(String key, boolean defValue) {
		SharedPreferences pref = getPref();
		return pref.getBoolean(key, defValue);
	}

	/**
	 * 
	 * @param keyValue
	 * @param value
	 */
	public static void setString(String keyValue, String value) {
		SharedPreferences.Editor editor = getEditor();
		editor.putString(keyValue, value);
		editor.commit();
	}

	/**
	 * 
	 * @param key
	 * @param defValue
	 * @return
	 */
	public static String getString(String key, String defValue) {
		SharedPreferences pref = getPref();
		return pref.getString(key, defValue);
	}

	/**
	 * 
	 * @param keyValue
	 * @param value
	 */
	public static void setInt(String keyValue, int value) {
		SharedPreferences.Editor editor = getEditor();
		editor.putInt(keyValue, value);
		editor.commit();
	}

	/**
	 * 
	 * @param
	 * @param defValue
	 * @return
	 */
	public static int getInt(String keyValue, int defValue) {
		SharedPreferences pref = getPref();
		return pref.getInt(keyValue, defValue);
	}

	/**
	 * remove data from pref
	 */
	public static void removePrefData(){
		SharedPreferences.Editor editor = getEditor();
		editor.clear();
		editor.commit();
	}
}

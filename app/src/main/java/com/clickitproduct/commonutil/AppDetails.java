package com.clickitproduct.commonutil;

import android.app.Activity;
import android.content.Context;

import com.clickitproduct.Clickit;

public class AppDetails
{
	private static Context mContext;
	private static Activity mActivity;

	public static Clickit getApp() {    return (Clickit) AppDetails.getContext().getApplicationContext();   }

	public static Context getContext() {
		return mContext;
	}

	public static void setContext(Context context) {
		mContext = context;
	}
	
	public static void setActivity(Activity activity){
		mActivity = activity;
	}

	public static Activity getActivity() {
		return mActivity;
	}
}

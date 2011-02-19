package com.gemokodero.zk.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;

import com.gemokodero.zk.CatsActivity;
import com.gemokodero.zk.HomeActivity;
import com.gemokodero.zk.ResultDetails;
import com.gemokodero.zk.core.Result;

public class Utils {
	
	// have no f.clue why I named these variables like this
	public static String XPATH_RESULT_COUNT = "//div[@class='squestions']/p//span[1]/text()";
	public static String XPATH_NO_RESULTS = "//div[@class='result-left']/ul/h2[@class='page-title']";
	public static String XPATH_RESULTS_ALL = "//div[@class='result-left']/ul/li[%s]/div//p";
	public static String XPATH_RESULTS_NAME = "//div[@class='result-left']/ul/li[%s]/div/h2";
	public static String XPATH_RESULTS_NUM = "//div[@class='result-left']/ul//li";
	public static String XPATH_RESULTS_METAINFO = "//div[@class='result-left']/ul/li[%s]/p[1]/a/[text()='Веб страница']/@href";
	
	public Utils() {
	}
	
	public static void goHome(Context context) {
        final Intent intent = new Intent(context, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }
	
	public static void goCategoriesList(Context context) {
        final Intent intent = new Intent(context, CatsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }
	
	public static boolean isOnline(Context context) {
		ConnectivityManager con=(ConnectivityManager) context.getSystemService(Activity.CONNECTIVITY_SERVICE);
        boolean wifi=con.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();
        boolean internet=con.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();
        return (wifi || internet);      
	}
	
	public static void openWebsite(Context context, String url) {
		Intent websiteIntent = new Intent("android.intent.action.VIEW");
		websiteIntent.setData(Uri.parse(url));
		context.startActivity(websiteIntent);
	}

	public static void callNumber(Context context, String phoneNumber) {
		Intent callIntent = new Intent(Intent.ACTION_CALL);
		callIntent.setData(Uri.parse("tel:" + phoneNumber));
        context.startActivity(callIntent);		
	}

	public static void showOnMap(Context appContext, Context viewContext, Result result) {
		Intent resultDetails = new Intent(appContext, ResultDetails.class);

		Bundle passBundle = new Bundle();
		passBundle.putString("name", result.getName());
		passBundle.putString("phonenumber", result.getPhoneNumber());
		passBundle.putString("address", result.getAddress());
		passBundle.putString("location", result.getLocation());
		passBundle.putString("website", result.getWebsite());
		resultDetails.putExtras(passBundle);
		appContext.startActivity(resultDetails);		
	}
}


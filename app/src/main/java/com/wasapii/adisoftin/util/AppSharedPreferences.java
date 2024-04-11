package com.wasapii.adisoftin.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.util.Patterns;


public class AppSharedPreferences {

	public static final String PREFS_NAME = "AOP_PREFS";
	public static final String USER_EMAIL = "email";
	public static final String USER_PASS = "pass";
	public static final String FULL_NAME = "fullname";
	public static final String GENDER = "gender";
	public static final String DOB = "dob";
	public static final String FCM_TOKEN = "fcm token";
	public static final String PHONE = "phone";
	public static final String PROFILE_PIC = "Profile pic";
	public static final String INTERESTS = "interests";
	public static final String LAT = "lat";
	public static final String LNG = "lng";


	public static final String KEY_SHARED_PREFERENCE_REMEMBERED_USERNAME = "remembered_user_name";
	public static final String KEY_SHARED_PREFERENCE_REMEMBERED_USER_PASSWORD = "remembered_password";




	public AppSharedPreferences() {
		super();
	}

	public static final String getGlobalString(Context activity, final String key) {
		SharedPreferences sharedPref = activity.getSharedPreferences("App", Context.MODE_PRIVATE);
		return sharedPref.getString(key, "");
	}

	public static final boolean setGlobalString(Context context, final String key, final String value) {
		SharedPreferences sharedPref = context.getSharedPreferences("App", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putString(key, value);
		editor.commit();
		return true;
	}

	public final static boolean isValidEmail(CharSequence target) {
		if (target == null) {
			return false;
		} else {
			return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
		}
	}


	public final static boolean isValidPhone(String phone) {
		if (phone == null) {
			return false;
		} else {
			return Patterns.PHONE.matcher(phone).matches();
		}
	}



	public static void saveUserIDToPreferences(Context ctx, String UserID) {

		try {
			// MY_PREFS_NAME - a static String variable like:
			//public static final String MY_PREFS_NAME = "MyPrefsFile";
			Editor editor = ctx.getSharedPreferences("AppPref", Context.MODE_PRIVATE).edit();
			editor.putString("UserID", UserID);

			editor.commit();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static String loadUserIDFromPreference(Context ctx) {
		String UserID = "";
		try {
			SharedPreferences prefs = ctx.getSharedPreferences("AppPref" , Context.MODE_PRIVATE);
			UserID = prefs.getString("UserID", "");

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return UserID;
	}







public static void saveIsFoursquarePlaceSavedPreferences(Context ctx, boolean isFoursquarePlaceSaved) {

		try {
			// MY_PREFS_NAME - a static String variable like:
			//public static final String MY_PREFS_NAME = "MyPrefsFile";
			Editor editor = ctx.getSharedPreferences("AppPref", Context.MODE_PRIVATE).edit();
			editor.putBoolean("isFoursquarePlaceSaved", isFoursquarePlaceSaved);

			editor.commit();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static boolean loadIsFoursquarePlaceSavedPreference(Context ctx) {
		boolean isFoursquarePlaceSaved= false;
		try {
			SharedPreferences prefs = ctx.getSharedPreferences("AppPref" , Context.MODE_PRIVATE);
			isFoursquarePlaceSaved = prefs.getBoolean("isFoursquarePlaceSaved", false);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return isFoursquarePlaceSaved;
	}

	public static void putFcmToken(Context context, String text) {

		Log.e("wasapii","putFcmToken called ");

		SharedPreferences settings;
		Editor editor;
		settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		editor = settings.edit();
		editor.putString(FCM_TOKEN, text);

		editor.commit();
	}

	public static String getFcmToken(Context context) {

		SharedPreferences settings;

		String text;

		settings = context.getSharedPreferences(PREFS_NAME,
				Context.MODE_PRIVATE);
		text = settings.getString(FCM_TOKEN, "");
		return text;
	}




	public static void putEmail(Context context, String text) {
		SharedPreferences settings;

		Editor editor;

		settings = context.getSharedPreferences(PREFS_NAME,
				Context.MODE_PRIVATE); // 1
		editor = settings.edit(); // 2

		editor.putString(USER_EMAIL, text); // 3

		editor.commit(); // 4
	}



	public static String getEmail(Context context) {
		SharedPreferences settings;
		String text;

		settings = context.getSharedPreferences(PREFS_NAME,
				Context.MODE_PRIVATE);
		text = settings.getString(USER_EMAIL, "");
		return text;
	}

	public static void putPassword(Context context, String text) {
		SharedPreferences settings;
		Editor editor;
		settings = context.getSharedPreferences(PREFS_NAME,
				Context.MODE_PRIVATE); // 1
		editor = settings.edit(); // 2

		editor.putString(USER_PASS, text); // 3

		editor.commit(); // 4
	}


	public static String getPassword(Context context) {

		SharedPreferences settings;

		String text;

		settings = context.getSharedPreferences(PREFS_NAME,
				Context.MODE_PRIVATE);
		text = settings.getString(USER_PASS, "");
		return text;
	}


	public static  void putFullName(Context context,String fullname){

		SharedPreferences settings;
		Editor editor;
		settings = context.getSharedPreferences(PREFS_NAME,
				Context.MODE_PRIVATE); // 1
		editor = settings.edit(); // 2

		editor.putString(FULL_NAME, fullname); // 3

		editor.commit(); // 4
	}


	public static String getFullName(Context context) {
		SharedPreferences settings;
		String text;

		settings = context.getSharedPreferences(PREFS_NAME,
				Context.MODE_PRIVATE);
		text = settings.getString(FULL_NAME, "");

		return text;
	}


	public static  void putGender(Context context,String gender){

		SharedPreferences settings;
		Editor editor;
		settings = context.getSharedPreferences(PREFS_NAME,
				Context.MODE_PRIVATE); // 1
		editor = settings.edit(); // 2

		editor.putString(GENDER, gender); // 3

		editor.commit(); // 4
	}


	public static String getGender(Context context) {

		SharedPreferences settings;
		String flag;

		settings = context.getSharedPreferences(PREFS_NAME,
				Context.MODE_PRIVATE);

		flag = settings.getString(GENDER, "");

		return flag;
	}

	public static  void putDOB(Context context,String dob){

		SharedPreferences settings;
		Editor editor;
		settings = context.getSharedPreferences(PREFS_NAME,
				Context.MODE_PRIVATE); // 1

		editor = settings.edit(); // 2

		editor.putString(DOB, dob); // 3

		editor.commit(); // 4
	}


	public static String getDOB(Context context) {
		SharedPreferences settings;
		String flag;
		settings = context.getSharedPreferences(PREFS_NAME,
				Context.MODE_PRIVATE);

		flag = settings.getString(DOB, "");

		return flag;
	}




	public static  void putMobile(Context context,String phone){

		SharedPreferences settings;
		Editor editor;
		settings = context.getSharedPreferences(PREFS_NAME,
				Context.MODE_PRIVATE); // 1
		editor = settings.edit(); // 2

		editor.putString(PHONE, phone); // 3

		editor.commit(); // 4
	}


	public static String getMobile(Context context) {

		SharedPreferences settings;

		String text;

		settings = context.getSharedPreferences(PREFS_NAME,
				Context.MODE_PRIVATE);

		text = settings.getString(PHONE, "");

		return text;
	}



	public static  void putProfilePic(Context context,String image){

		SharedPreferences settings;
		Editor editor;
		settings = context.getSharedPreferences(PREFS_NAME,
				Context.MODE_PRIVATE); // 1
		editor = settings.edit(); // 2

		editor.putString(PROFILE_PIC, image); // 3

		editor.commit(); // 4
	}


	public static String getProfilePic(Context context) {

		SharedPreferences settings;

		String text;

		settings = context.getSharedPreferences(PREFS_NAME,
				Context.MODE_PRIVATE);
		text = settings.getString(PROFILE_PIC, "");

		return text;
	}


	public static  void putInterests(Context context,String interests){

		SharedPreferences settings;
		Editor editor;
		settings = context.getSharedPreferences(PREFS_NAME,
				Context.MODE_PRIVATE); // 1
		editor = settings.edit(); // 2

		editor.putString(INTERESTS, interests); // 3

		editor.commit(); // 4
	}


	public static String getInterests(Context context) {

		SharedPreferences settings;

		String text;

		settings = context.getSharedPreferences(PREFS_NAME,
				Context.MODE_PRIVATE);

		text = settings.getString(INTERESTS, "");

		return text;
	}


    public static void puttLat(Context context,double lat){
		SharedPreferences settings;
		Editor editor;
		settings = context.getSharedPreferences(PREFS_NAME,
				Context.MODE_PRIVATE); // 1
		editor = settings.edit(); // 2

		editor.putLong(LAT, Double.doubleToLongBits(lat)); // 3

		editor.commit(); // 4
	}

	public static void puttLng(Context context,double lng){
		SharedPreferences settings;
		Editor editor;
		settings = context.getSharedPreferences(PREFS_NAME,
				Context.MODE_PRIVATE); // 1
		editor = settings.edit(); // 2

		editor.putLong(LNG, Double.doubleToLongBits(lng)); // 3

		editor.commit(); // 4
	}

	public static double getLat(Context context) {

		SharedPreferences settings;

		double lat=0.0;

		settings = context.getSharedPreferences(PREFS_NAME,
				Context.MODE_PRIVATE);

		lat = Double.longBitsToDouble(settings.getLong(LAT, 0));

		return lat;
	}


	public static double getLng(Context context) {

		SharedPreferences settings;

		double lat = 0.0;

		settings = context.getSharedPreferences(PREFS_NAME,
				Context.MODE_PRIVATE);

		lat = Double.longBitsToDouble(settings.getLong(LNG, 0));

		return lat;
	}

}

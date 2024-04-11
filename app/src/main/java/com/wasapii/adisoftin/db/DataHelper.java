package com.wasapii.adisoftin.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.util.Log;

import com.wasapii.adisoftin.model.User;
import com.wasapii.adisoftin.util.AppSharedPreferences;
import com.wasapii.adisoftin.util.QueryConstants;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;


/**
 * The Class DataHelper.
 */
public class DataHelper {

	/** The Constant DATABASE_NAME. */
	private static final String DATABASE_NAME = "wasapii.db";

	/** The Constant DATABASE_NAME. */
	private static final String TAG = "query";

	/** The Constant DATABASE_VERSION. */
	private static final int DATABASE_VERSION = 1;

	/** The context context. */
	private Context context;

	/** The database db. */
	private SQLiteDatabase db;

	private static DataHelper sDataHelper = null;

	/**
	 * Instantiates a new data helper.
	 *
	 * @param context
	 *            the context
	 */
	private DataHelper(Context context) {
		this.context = context;
		OpenHelper openHelper = new OpenHelper(this.context);
		this.db = openHelper.getWritableDatabase();
	}

	public static DataHelper getInstance(Context ctx) {

		if (sDataHelper == null) {
			sDataHelper = new DataHelper(ctx.getApplicationContext());
		}

		return sDataHelper;
	}

	/* execute insert update query */
	public void executeQuery(String query) {
		try {
			db.execSQL(query);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	public void insertUserInfo(String user_id,String name,String password,String email,String phone,String gender,
							   String dob,String profilePhoto,String image1 , String image2 , String image3 , String interests) {

		ContentValues values = new ContentValues();
		values.put("user_id",user_id);
		values.put("name",name);
		values.put("password",password);
		values.put("email",email);
		values.put("contact_no",phone);
		values.put("gender",gender);
		values.put("dob",dob);
		values.put("profile_img",profilePhoto);
		values.put("image1",image1);
		values.put("image2",image2);
		values.put("image3",image3);
		values.put("user_interests",interests);

		db.insert("UserInfo",null,values);

	}

	public User getUserInfo(){
		User user = null;

		Cursor cursor = db.rawQuery("SELECT name,dob,gender,user_interests,profile_img,image1,image2,image3 FROM UserInfo",null);

		while (cursor.moveToNext()){
			user = new User(cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4),cursor.getString(5),cursor.getString(6),cursor.getString(7));
		}

		return user;
	}


	public void updateUserInfo(String name,String gender,
							   String dob,String profilePhoto,String image1 , String image2 , String image3 ,
							   String interests) {

		ContentValues values = new ContentValues();
		values.put("name",name);
		values.put("gender",gender);
		values.put("dob",dob);
		values.put("profile_img",profilePhoto);
		values.put("image1",image1);
		values.put("image2",image2);
		values.put("image3",image3);
		values.put("user_interests",interests);

		db.update("UserInfo",values,"user_id='"+AppSharedPreferences.loadUserIDFromPreference(context)+"'",null);

	}




	public void deleteUserInfo() {
		db.delete("UserInfo","",null);
	}


	public  boolean checkUserInfo(){

		boolean flag=false;

		Cursor cursor = db.rawQuery("SELECT * FROM  UserInfo ",null);

		if(cursor.getCount()>0){
			flag=true;
		}

		cursor.close();
		return flag;
	}


	/**
	 * Closes the database.
	 */
	public void close() {
		this.db.close();
	}

	/**
	 * The Class OpenHelper.
	 */
	private static class OpenHelper extends SQLiteOpenHelper {

		/**
		 * Instantiates a new open helper.
		 *
		 * @param context
		 *            the context
		 */
		OpenHelper(Context context) {

			super(context, DATABASE_NAME, null, DATABASE_VERSION);

		}

		/**
		 * On create called when DB is created.
		 * @param
		 *
		 */
		@Override
		public void onCreate(SQLiteDatabase db) {

			db.execSQL(QueryConstants.CREATE_USER_TABLE);

		}

		/**
		 * On upgrade called when DB is upgraded.
		 *
		 * @param
		 *
		 * @param
		 *
		 * @param
		 *
		 */
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

			Log.w("Example", "Upgrading database, this will drop tables and recreate.");

			db.execSQL(QueryConstants.DROP_USER_TABLE);

			onCreate(db);
		}

		@Override
		public void onOpen(SQLiteDatabase db) {
			super.onOpen(db);
			if (!db.isReadOnly()) {
				// Enable foreign key constraints
				db.execSQL("PRAGMA foreign_keys=ON;");
			}
		}
	}


}
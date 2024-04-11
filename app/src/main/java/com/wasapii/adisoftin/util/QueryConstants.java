package com.wasapii.adisoftin.util;

public class QueryConstants {

	public static final String CREATE_USER_TABLE = "create table UserInfo"+
			"( " +"row_id"+" integer primary key autoincrement,"+ "user_id TEXT , name TEXT, password TEXT, email TEXT , contact_no TEXT, gender TEXT,dob TEXT, profile_img TEXT ,image1 TEXT , image2 TEXT , image3 TEXT , user_interests TEXT );";


	public static final String CREATE_CHAT_TABLE = "create table chat"+
			"( " +"row_id"+" integer primary key autoincrement,"+ "from TEXT , to TEXT, message TEXT , readStatus INTEGER );";


	public static final String DROP_USER_TABLE = "DROP TABLE IF EXISTS UserInfo";

}

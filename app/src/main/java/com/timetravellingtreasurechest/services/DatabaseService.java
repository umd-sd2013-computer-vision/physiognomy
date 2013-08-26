package com.timetravellingtreasurechest.services;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseService extends SQLiteOpenHelper {

	public DatabaseService(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
	}
	
	public void onCreate(SQLiteDatabase arg0) {
		
	}
	
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		
	}

}

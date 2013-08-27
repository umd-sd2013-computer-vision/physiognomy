package com.timetravellingtreasurechest.services;

import java.io.File;

import com.timetravellingtreasurechest.report.ReportData;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

public class DatabaseService extends SQLiteOpenHelper {
	
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "report_history";
	
	private static final String TABLE_REPORTS = "reports";
	private static final String KEY_PIC_PATH = "pic_path";
	private static final String KEY_THUMB_PATH = "thumb_path";
	private static final String KEY_REPORT = "report";	
	
	private static final String TABLE_REPORTS_CREATE = 
			"create table " + TABLE_REPORTS + " (" + 
			KEY_PIC_PATH + " TEXT, " +
			KEY_THUMB_PATH + " TEXT, " +
			KEY_REPORT + " TEXT);";

	public DatabaseService(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(TABLE_REPORTS_CREATE);
	}
	
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_REPORTS);
		onCreate(db);
	}
	
	public void addReport(String picPath, String thumbPath, String report) {
		// basically i had issues with reports being added more than once, i couldnt track down why
		// i think because of back interaction between activities, so this is easier (quicker) way to fix
		if (getReportDataFromPicture(picPath) != null)
			return;
		
		SQLiteDatabase db = this.getWritableDatabase();
				
		ContentValues values = new ContentValues();
		values.put(KEY_PIC_PATH, picPath);
		values.put(KEY_THUMB_PATH, thumbPath);
		values.put(KEY_REPORT, report);
		
		db.insert(TABLE_REPORTS, null, values);
	}
	
	public String getReportText(String thumbPath) {
		SQLiteDatabase db = this.getReadableDatabase();
		
		Cursor cursor = db.query(TABLE_REPORTS, new String [] { KEY_REPORT }, KEY_THUMB_PATH + "=?", new String[] {thumbPath}, null, null, null, null);
		if (cursor.getCount() == 0)
			return null;
		
		if (cursor != null)
			cursor.moveToFirst();
		
		return cursor.getString(0);
	}
	
	public ReportData getReportDataFromThumb(String thumbPath) {
		SQLiteDatabase db = this.getReadableDatabase();
		
		Cursor cursor = db.query(TABLE_REPORTS, new String [] { KEY_PIC_PATH, KEY_THUMB_PATH, KEY_REPORT }, KEY_THUMB_PATH + "=?", new String[] {thumbPath}, null, null, null, null);
		if (cursor.getCount() == 0)
			return null;
		
		if (cursor != null)
			cursor.moveToFirst();
		
		return new ReportData(cursor.getString(0), cursor.getString(1), cursor.getString(2));
	}
	
	public ReportData getReportDataFromPicture(String imagePath) {
		SQLiteDatabase db = this.getReadableDatabase();
		
		Cursor cursor = db.query(TABLE_REPORTS, new String [] { KEY_PIC_PATH, KEY_THUMB_PATH, KEY_REPORT }, KEY_PIC_PATH + "=?", new String[] {imagePath}, null, null, null, null);
		if (cursor.getCount() == 0)
			return null;
		
		if (cursor != null)
			cursor.moveToFirst();
		
		return new ReportData(cursor.getString(0), cursor.getString(1), cursor.getString(2));
	}
	
	public void deleteReport(String imagePath) {
		SQLiteDatabase db = this.getReadableDatabase();
		
		ReportData report = getReportDataFromPicture(imagePath);
		File image = new File(report.getImageUri().getPath());
		File thumbnail = new File(report.getThumbUri().getPath());
		
		try {			
			if (image.exists() && !image.isDirectory())
				image.delete();
			if (thumbnail.exists() && !thumbnail.isDirectory())
				thumbnail.delete();
		} catch (Exception e) {
			e.printStackTrace();
		}		
		
		db.delete(TABLE_REPORTS, KEY_PIC_PATH + "=?", new String[] { imagePath });
	}
}

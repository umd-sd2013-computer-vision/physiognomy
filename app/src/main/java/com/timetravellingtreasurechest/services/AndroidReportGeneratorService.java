package com.timetravellingtreasurechest.services;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;

import com.googlecode.javacv.cpp.opencv_core.CvMat;
import com.timetravellingtreasurechest.report.ReportData;
import com.timetravellingtreasurechest.vision.FacialFeatures;

public class AndroidReportGeneratorService implements IReportGeneratorService {

	@Override
	public ReportData getReport(FacialFeatures features, CvMat image) {
		ReportData newReport = new ReportData(features, image);
		saveToFile(newReport);
		return newReport;
	}
	
	@Override
	public void saveToFile(ReportData d) {
		if (!d.reportSucessful() || d.getImageUri() != null || d.getThumbUri() != null)
			return;
		
		SimpleDateFormat form = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
		String title = form.format(new Date()) + ".jpg";
		
		Bitmap picture = ImageConverter.cvMatToBitmap(d.image);
		
		File picFile = null;
		File thumbFile = null;
		try {
			File picDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Physiognomy");
			File thumbDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Physiognomy/.thumb");
			
			picDir.mkdirs();
			thumbDir.mkdirs();
			
			picFile = new File(picDir, title);
			thumbFile = new File(thumbDir, title);
			
			FileOutputStream picOut = new FileOutputStream(picFile);
			FileOutputStream thumbOut = new FileOutputStream(thumbFile);
			
			picture.compress(Bitmap.CompressFormat.JPEG, 80, picOut);
			Bitmap.createScaledBitmap(picture, 200, 200, true).compress(Bitmap.CompressFormat.JPEG, 80, thumbOut);
			
			picOut.flush();	picOut.close();
			thumbOut.flush(); thumbOut.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}			
		
		d.thumbUri = Uri.fromFile(thumbFile);
		d.imageUri = Uri.fromFile(picFile);
		
		DatabaseService db = new DatabaseService(ServiceServer.getAndroidContext());
		db.addReport(d.imageUri.getPath(), d.thumbUri.getPath(), d.report);
	}
}

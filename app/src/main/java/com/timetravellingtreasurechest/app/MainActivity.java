package com.timetravellingtreasurechest.app;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.media.AudioManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;

import com.googlecode.javacv.cpp.opencv_core;
import com.googlecode.javacv.cpp.opencv_core.CvMat;
import com.googlecode.javacv.cpp.opencv_highgui;
import com.timetravellingtreasurechest.app.R;
import com.timetravellingtreasurechest.camera.Preview;
import com.timetravellingtreasurechest.report.ReportData;
import com.timetravellingtreasurechest.services.AndroidFacialFeatureService;
import com.timetravellingtreasurechest.services.AndroidReportGeneratorService;
import com.timetravellingtreasurechest.services.FacialFeatureService;
import com.timetravellingtreasurechest.services.IFacialFeatureService;
import com.timetravellingtreasurechest.services.IReportGeneratorService;
import com.timetravellingtreasurechest.services.ImageConverter;
import com.timetravellingtreasurechest.services.ReportGeneratorService;
import com.timetravellingtreasurechest.services.ServiceServer;

public class MainActivity extends Activity {

	public static String REPORT = "ReportData";
	// Dependencies
	Preview cameraSurfaceView;
	public static ReportData latestReport;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ServiceServer.setFacialFeatureService(new AndroidFacialFeatureService(
				this.getApplicationContext()));
		ServiceServer
				.setReportGeneratorService(new AndroidReportGeneratorService());

		setContentView(R.layout.activity_main);

		// Setup the FrameLayout with the Camera Preview Screen
		cameraSurfaceView = new Preview(this);
		FrameLayout preview = (FrameLayout) this
				.findViewById(R.id.PreviewFrame);
		if (preview == null) {
			System.out.println("PreviewFrame not found!");
		} else {
			System.out.println("PreviewFrame found");
			preview.addView(cameraSurfaceView);
		}

		Button takeAPicture = (Button) this.findViewById(R.id.TakePhoto);
		takeAPicture.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				//generateAndViewReport(getPictureFromFile("face"));
				Camera camera = cameraSurfaceView.getCamera();
				AudioManager mgr = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
				mgr.setStreamMute(AudioManager.STREAM_SYSTEM, true);
				camera.takePicture(null, null, new HandlePictureStorage());
			}
		});

	}

	private class HandlePictureStorage implements PictureCallback {

		@Override
		public void onPictureTaken(byte[] picture, Camera camera) {
			cameraSurfaceView.stopPreview();
			Size size = camera.getParameters().getPictureSize();
			CvMat cvPicture = ImageConverter
					.getCvMatFromRawImage(picture, new Rect(0, 0, size.width,
							size.height), camera.getParameters()
							.getPictureFormat() == ImageFormat.NV21);
			generateAndViewReport(cvPicture);

			cameraSurfaceView.startPreview();
		}
	}

	private void generateAndViewReport(CvMat cvPicture) {
		Intent myIntent = new Intent();
		myIntent.setClassName(MainActivity.this,
				"com.timetravellingtreasurechest.gui.ManageReportActivity");

		ReportData report = ServiceServer.getReportGeneratorService()
				.getReport(
						ServiceServer.getFacialFeatureService().getFeatures(
								cvPicture), cvPicture);
		
		//myIntent.putExtra(MainActivity.REPORT, report);
		if(latestReport.getOriginalImage() != null)
			latestReport.getOriginalImage().deallocate();
		latestReport = report;
		System.out.println("Starting new activity");
		startActivity(myIntent);
		System.out.println("Activity returned");
	}

	private CvMat getPictureFromFile(String name) /* throws IOException */{
		// InputStream image =
		// getApplicationContext().getResources().getAssets().open(name +
		// ".png");
		// Drawable demoDraw =
		// getApplicationContext().getResources().getDrawable(
		// R.drawable.face);
		// Bitmap bitmap = ((BitmapDrawable) demoDraw).getBitmap();
		//
		// ByteArrayOutputStream stream = new ByteArrayOutputStream();
		// bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
		// byte[] bitmapdata = stream.toByteArray();
		// return ImageConverter.getCvMatFromRawImage(bitmapdata, new Rect(0, 0,
		// demoDraw.getIntrinsicWidth(), demoDraw.getIntrinsicHeight()),
		// false);

		return (CvMat) opencv_highgui.cvLoadImageM("/mnt/sdcard/face.jpg");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
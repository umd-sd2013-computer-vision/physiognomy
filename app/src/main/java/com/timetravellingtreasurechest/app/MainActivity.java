package com.timetravellingtreasurechest.app;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;

import com.timetravellingtreasurechest.app.R;
import com.timetravellingtreasurechest.camera.Preview;
import com.timetravellingtreasurechest.report.IReportGeneratorService;
import com.timetravellingtreasurechest.report.ReportData;
import com.timetravellingtreasurechest.report.ReportGeneratorService;
import com.timetravellingtreasurechest.services.AndroidFacialFeatureService;
import com.timetravellingtreasurechest.services.FacialFeatureService;
import com.timetravellingtreasurechest.services.IFacialFeatureService;
import com.timetravellingtreasurechest.services.ImageConverter;
import com.timetravellingtreasurechest.services.ServiceServer;

public class MainActivity extends Activity {

	public static String REPORT = "ReportData";
	// Dependencies
	IFacialFeatureService facialFeatureService;
	IReportGeneratorService reportGeneratorService = new ReportGeneratorService();
	Preview cameraSurfaceView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		ServiceServer.setFacialFeatureService(new AndroidFacialFeatureService(null));
		
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
				Camera camera = cameraSurfaceView.getCamera();
				camera.takePicture(null, null, new HandlePictureStorage());
			}
		});

	}

	private class HandlePictureStorage implements PictureCallback {

		@Override
		public void onPictureTaken(byte[] picture, Camera camera) {
			cameraSurfaceView.stopPreview();
			System.out.println("Picture successfully taken: " + picture);
			Intent myIntent = new Intent();
			myIntent.setClassName(MainActivity.this,
					"com.timetravellingtreasurechest.gui.ManageReportActivity");
			
			//ImageConverter.getCvMatFromRawImage(picture, , )
			
			ReportData report = reportGeneratorService
					.getReport(ServiceServer.getFacialFeatureService().getFeatures(picture));
			myIntent.putExtra(MainActivity.REPORT, report);
			System.out.println("Starting new activity");
			startActivity(myIntent);
			System.out.println("Activity returned");
			cameraSurfaceView.startPreview();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
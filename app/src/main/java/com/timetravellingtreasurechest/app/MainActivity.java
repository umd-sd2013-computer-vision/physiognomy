package com.timetravellingtreasurechest.app;

import android.media.AudioManager;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;

import com.googlecode.javacv.cpp.opencv_core.CvMat;
import com.googlecode.javacv.cpp.opencv_highgui;
import com.timetravellingtreasurechest.app.R;
import com.timetravellingtreasurechest.camera.Preview;
import com.timetravellingtreasurechest.gui.ConfigureCameraActivity;
import com.timetravellingtreasurechest.gui.ManageReportActivity;
import com.timetravellingtreasurechest.report.ReportData;
import com.timetravellingtreasurechest.services.AndroidFacialFeatureService;
import com.timetravellingtreasurechest.services.AndroidReportGeneratorService;
import com.timetravellingtreasurechest.services.ImageConverter;
import com.timetravellingtreasurechest.services.ServiceServer;

public class MainActivity extends Activity {

	public static String REPORT = "ReportData";
	// Dependencies
	public static ReportData latestReport;
	public static Preview cameraSurfaceView = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ServiceServer.setAndroidContext(getApplicationContext());
		ServiceServer.setFacialFeatureService(new AndroidFacialFeatureService(
				this.getApplicationContext()));
		ServiceServer
				.setReportGeneratorService(new AndroidReportGeneratorService());

		setContentView(R.layout.activity_main);

		// Setup the FrameLayout with the Camera Preview Screen
		if(cameraSurfaceView == null) {
			cameraSurfaceView = new Preview(this, Camera.CameraInfo.CAMERA_FACING_BACK);
		}
		FrameLayout preview = (FrameLayout) this
				.findViewById(R.id.PreviewFrame);
		preview.addView(cameraSurfaceView);		

		Button takeAPicture = (Button) this.findViewById(R.id.TakePhoto);
		takeAPicture.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				handleNewPhoto();
			}
		});

	}

	private void handleNewPhoto() {
		// generateAndViewReport(getPictureFromFile("face"));
		Camera camera = cameraSurfaceView.getCamera();
		try {
			AudioManager mgr = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
			mgr.setStreamMute(AudioManager.STREAM_SYSTEM, true);
		} catch (Exception e) {
			System.out.println("Failed to mute camera");
			e.printStackTrace();
		}
		camera.takePicture(null, null, new HandlePictureStorage());
	}

	private class HandlePictureStorage implements PictureCallback {

		@Override
		public void onPictureTaken(byte[] picture, Camera camera) {
			cameraSurfaceView.stop();
			Size size = camera.getParameters().getPictureSize();
			CvMat cvPicture = ImageConverter
					.getCvMatFromRawImage(picture, new Rect(0, 0, size.width,
							size.height), camera.getParameters()
							.getPictureFormat() == ImageFormat.NV21);
			generateAndViewReport(cvPicture);
			cameraSurfaceView.setupCameraView();
		}
	}

	private void generateAndViewReport(CvMat cvPicture) {
		Intent myIntent = new Intent();
		myIntent.setClass(MainActivity.this,ManageReportActivity.class);
		ReportData report = ServiceServer.getReportGeneratorService()
				.getReport(
						ServiceServer.getFacialFeatureService().getFeatures(
								cvPicture), cvPicture);

		// myIntent.putExtra(MainActivity.REPORT, report);
		if (latestReport != null && latestReport.getOriginalImage() != null)
			latestReport.getOriginalImage().deallocate();
		latestReport = report;
		System.out.println("Starting new activity");
		startActivity(myIntent);
		System.out.println("Activity returned");
	}

	@SuppressWarnings("unused")
	private CvMat getPictureFromFile(String name) /* throws IOException */{
		return (CvMat) opencv_highgui.cvLoadImageM(Environment
				.getExternalStorageDirectory().getPath() + name + ".jpg");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.camera_config:
			cameraSurfaceView.stop();
			Intent myIntent = new Intent();
			myIntent.setClass(MainActivity.this,ConfigureCameraActivity.class);
			startActivity(myIntent);
			//cameraSurfaceView.startPreview();
			return true;
		case R.id.report_history:
			System.out.println("Handle report history");
			return true;
		default:
			System.out.println("Some other menu was pressed?");
			return false;
		}
	}
	@Override
	protected void onPause() {
	    super.onPause();
	    cameraSurfaceView.stop();
	}
}
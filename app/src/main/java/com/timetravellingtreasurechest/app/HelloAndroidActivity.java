package com.timetravellingtreasurechest.app;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;

import com.timetravellingtreasurechest.app.R;
import com.timetravellingtreasurechest.vision.FacialFeatures;
import com.googlecode.javacv.cpp.*;

import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_highgui.CV_LOAD_IMAGE_COLOR;
import static com.googlecode.javacv.cpp.opencv_highgui.cvLoadImageM;

public class HelloAndroidActivity extends Activity {

	Preview cameraSurfaceView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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
			System.out.println("Picture successfully taken: " + picture);
			ImageView imageView = (ImageView) findViewById(R.id.imageView1);
			imageView.setImageDrawable(getResources().getDrawable(R.drawable.face));
//			CvMat image = cvLoadImageM(getResources().getDrawable(R.drawable.face));
			//CvMat image = cvLoadImageM(, CV_LOAD_IMAGE_COLOR);
			 CvMat mat = new CvMat();
			// Intent myIntent = new Intent();
			// myIntent.setClassName(HelloAndroidActivity.this,
			// "com.timetravellingtreasurechest.gui.ManageReportActivity");
			// ReportData report =
			// reportGeneratorService.getReport(facialFeatureService.getFeatures(picture));
			// myIntent.putExtra(MainActivity.REPORT, report);
			// System.out.println("Starting new activity");
			// startActivity(myIntent);
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
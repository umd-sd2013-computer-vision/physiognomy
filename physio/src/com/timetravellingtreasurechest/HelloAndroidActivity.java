package com.timetravellingtreasurechest;

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
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;

import com.timetravellingtreasurechest.R;

import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;

public class HelloAndroidActivity extends Activity {

	Preview cameraSurfaceView;
	private Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {	   
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		context = this.getApplicationContext();
		
		// Setup the FrameLayout with the Camera Preview Screen
		cameraSurfaceView = new Preview(this);
		FrameLayout preview = (FrameLayout) this.findViewById(R.id.PreviewFrame);
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
			camera.stopPreview();
			
			Size size = camera.getParameters().getPictureSize();
			CvMat rgbImage;
			
			// all this converts w/e format android camera captures with and converts to a BGR
			if (camera.getParameters().getPictureFormat() == ImageFormat.NV21) {
				if (size.height % 2 != 0)
					throw new RuntimeException("Odd height not supported");
				
				CvMat nv21Image = CvMat.create(size.height + (size.height/2), size.width, CV_8UC1);
				rgbImage = CvMat.create(size.height, size.width, CV_8UC3);
		        
		        nv21Image.getByteBuffer().put(picture);
		        cvCvtColor(nv21Image, rgbImage, CV_YUV2BGR_NV21);
		        nv21Image.release();
			} else {
		        Bitmap bitmap = BitmapFactory.decodeByteArray(picture, 0, picture.length);
		        if (bitmap.getConfig() == Bitmap.Config.RGB_565) {
		        	CvMat rgb565Image = CvMat.create(size.height, size.width, CV_8UC2);
		        	bitmap.copyPixelsToBuffer(rgb565Image.getByteBuffer());
		        	rgbImage = CvMat.create(size.height, size.width, CV_8UC3);
		        	cvCvtColor(rgb565Image, rgbImage, CV_BGR5652BGR);
		        	rgb565Image.release();
		        } else if (bitmap.getConfig() == Bitmap.Config.ARGB_8888) {
		        	rgbImage = CvMat.create(size.height, size.width, CV_8UC4);
		        	bitmap.copyPixelsToBuffer(rgbImage.getByteBuffer());
		        } else
		        	throw new RuntimeException("Unsupported bitmap config: " + bitmap.getConfig());
		        
		        bitmap.recycle();
			}
			
			FacialFeatures image = new FacialFeatures(rgbImage, context);
			String report = (new ReportData(image)).getReportText();
			
			System.out.println("capture:");
			System.out.println("  foreheadHeight: " + image.foreheadHeight);
			System.out.println("  eyeSize: " + image.eyeSize);
			System.out.println("  eyeSpace: " + image.eyeSpace);
			System.out.println("  noseSize: " + image.noseSize);
			System.out.println("  noseHeight: " + image.noseHeight);
			System.out.println("  noseWidth: " + image.noseWidth);
			System.out.println("  mouthSize: " + image.mouthSize);
			System.out.println("  mouthHeight: " + image.mouthHeight);
			System.out.println("  mouthWidth: " + image.mouthWidth);
			
			// report activity displays fortune text and image captured
			Intent intent = new Intent(HelloAndroidActivity.this, ReportActivity.class);
			intent.putExtra("report", report);
			startActivity(intent);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}
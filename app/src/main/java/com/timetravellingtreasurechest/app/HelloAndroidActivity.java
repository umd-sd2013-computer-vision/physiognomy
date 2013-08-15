package com.timetravellingtreasurechest.app;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

public class HelloAndroidActivity extends Activity {

	/**
	 * Called when the activity is first created.
	 * 
	 * @param savedInstanceState
	 *            If the activity is being re-initialized after previously being
	 *            shut down then this Bundle contains the data it most recently
	 *            supplied in onSaveInstanceState(Bundle). <b>Note: Otherwise it
	 *            is null.</b>
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		new com.timetravellingtreasurechest.vision.FacialFeatures(
		 (com.googlecode.javacv.cpp.opencv_core.CvMat) null);
		getMenuInflater().inflate(
				com.timetravellingtreasurechest.app.R.menu.main, menu);
		return true;
	}

}

/*
 import android.content.Context;
 import android.graphics.Bitmap;
 import android.graphics.BitmapFactory;
 import android.graphics.Canvas;
 import android.graphics.Color;
 import android.graphics.Paint;
 import android.graphics.PointF;
 import android.media.FaceDetector;
 import android.media.FaceDetector.Face;
// */
//import android.app.Activity;
//import android.os.Bundle;
//import android.view.Menu;
////import android.view.View;
//
//public class HelloAndroidActivity extends Activity {
//
//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(com.timetravellingtreasurechest.app.R.layout.activity_main);
//		// setContentView(new myView(this));
//	}
//
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		// new com.timetravellingtreasurechest.vision.FacialFeatures(
//		// (com.googlecode.javacv.cpp.opencv_core.CvMat) null);
//		getMenuInflater().inflate(
//				com.timetravellingtreasurechest.app.R.menu.main, menu);
//		return true;
//	}
//
//	// private class myView extends View {
//	//
//	// private int imageWidth, imageHeight;
//	// private int numberOfFace = 5;
//	// private FaceDetector myFaceDetect;
//	// private FaceDetector.Face[] myFace;
//	// float myEyesDistance;
//	// int numberOfFaceDetected;
//	//
//	// Bitmap myBitmap;
//	//
//	// public myView(Context context) {
//	// super(context);
//	//
//	// /*BitmapFactory.Options BitmapFactoryOptionsbfo = new
//	// BitmapFactory.Options();
//	// BitmapFactoryOptionsbfo.inPreferredConfig = Bitmap.Config.zRGB_565;
//	// myBitmap = BitmapFactory.decodeResource(getResources(),
//	// com.timetravellingtreasurechest.app.R.drawable.face,
//	// BitmapFactoryOptionsbfo);
//	// imageWidth = myBitmap.getWidth();
//	// imageHeight = myBitmap.getHeight();
//	// myFace = new FaceDetector.Face[numberOfFace];
//	// myFaceDetect = new FaceDetector(imageWidth, imageHeight,
//	// numberOfFace);
//	// numberOfFaceDetected = myFaceDetect.findFaces(myBitmap, myFace);*/
//	//
//	// }
//	//
//	// @Override
//	// protected void onDraw(Canvas canvas) {
//	//
//	// canvas.drawBitmap(myBitmap, 0, 0, null);
//	//
//	// Paint myPaint = new Paint();
//	// myPaint.setColor(Color.GREEN);
//	// myPaint.setStyle(Paint.Style.STROKE);
//	// myPaint.setStrokeWidth(3);
//	//
//	// for (int i = 0; i < numberOfFaceDetected; i++) {
//	// Face face = myFace[i];
//	// PointF myMidPoint = new PointF();
//	// face.getMidPoint(myMidPoint);
//	// myEyesDistance = face.eyesDistance();
//	//
//	// canvas.drawRect((int) (myMidPoint.x - myEyesDistance * 2),
//	// (int) (myMidPoint.y - myEyesDistance * 2),
//	// (int) (myMidPoint.x + myEyesDistance * 2),
//	// (int) (myMidPoint.y + myEyesDistance * 2), myPaint);
//	// }
//	// }
//	// }
//
//}

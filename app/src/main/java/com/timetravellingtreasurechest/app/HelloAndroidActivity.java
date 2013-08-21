package com.timetravellingtreasurechest.app;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

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

	private String[] allLibs = { "/sdcard/libjniopencv_core.so" };
	private String[] paths = { "lib/bin" };

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
			imageView.setImageDrawable(getResources().getDrawable(
					R.drawable.face));
			//
			// new Dir().process(new java.io.File("/"));
			//
			// CvMat image =
			// cvLoadImageM(getResources().getDrawable(R.drawable.face));
			// CvMat image = cvLoadImageM(, CV_LOAD_IMAGE_COLOR);
			
			// com.googlecode.javacpp.Loader.getProperties().setProperty(
			// "library.prefix", "libjni");
			// com.googlecode.javacpp.Loader.loadLibrary(opencv_core.class,
			// new String[]{getApplicationContext().getPackageResourcePath()}, "opencv_core");
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

		public class Dir {
			int spc_count = -1;
			Set<String> doneDirectories = new HashSet<String>();

			public Dir() {
				doneDirectories.add("/proc");
				doneDirectories.add("/sys");
			}

			public void process(File aFile) {
				spc_count++;
				String spcs = "";
				boolean found = false;
				// for (int i = 0; i < spc_count * 2; i++)
				// spcs += " ";
				try {
					if (aFile.isFile()
							&& aFile.getName().contains("opencv_core")) {
						found = true;
						System.out.println(aFile.getCanonicalPath()
								+ aFile.getName());
					}
					// System.out.println(spcs + "[FIL] " + aFile.getPath()
					// + aFile.getName());
					else if (aFile.isDirectory()
							&& !doneDirectories.contains(aFile
									.getCanonicalPath())) {
						doneDirectories.add(aFile.getCanonicalPath());
						// System.out.println(spcs + "[DIR] " + aFile.getPath()
						// + aFile.getName());
						File[] listOfFiles = aFile.listFiles();
						if (listOfFiles != null) {
							for (int i = 0; i < listOfFiles.length; i++)
								process(listOfFiles[i]);
						} else {
							// System.out.println(spcs + " [ACCESS DENIED]");
						}
					}
				} catch (Exception e) {
					System.out
							.println("[UNK] An error occured reading the file");
				}
				for (int i = 0; i < spc_count * 2; i++)
					spcs += " ";
				if (!found) {
					System.out.println(spcs + "Library files not found");
				}
				spc_count--;

			}
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	private boolean unpackZip(String path, String zipname, String destinationPath)
	{       
	     InputStream is;
	     ZipInputStream zis;
	     try 
	     {
	         String filename;
	         is = new FileInputStream(path + zipname);
	         zis = new ZipInputStream(new BufferedInputStream(is));          
	         ZipEntry ze;
	         byte[] buffer = new byte[1024];
	         int count;

	         while ((ze = zis.getNextEntry()) != null) 
	         {
	             // zapis do souboru
	             filename = ze.getName();

	             // Need to create directories if not exists, or
	             // it will generate an Exception...
	             if (ze.isDirectory()) {
	                File fmd = new File(destinationPath + filename);
	                fmd.mkdirs();
	                continue;
	             }

	             FileOutputStream fout = new FileOutputStream(destinationPath + filename);

	             // cteni zipu a zapis
	             while ((count = zis.read(buffer)) != -1) 
	             {
	                 fout.write(buffer, 0, count);             
	             }

	             fout.close();               
	             zis.closeEntry();
	         }

	         zis.close();
	     } 
	     catch(IOException e)
	     {
	         e.printStackTrace();
	         return false;
	     }

	    return true;
	}

}
package com.timetravellingtreasurechest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import android.content.*;

import com.timetravellingtreasurechest.R;

import com.googlecode.javacv.cpp.*;
import com.googlecode.javacv.cpp.opencv_core.CvArr;

import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import static com.googlecode.javacv.cpp.opencv_objdetect.*;

public class FacialFeatures {
	
	private static CvHaarClassifierCascade face_cascade;
	private static CvHaarClassifierCascade leye_cascade;
	private static CvHaarClassifierCascade reye_cascade;
	private static CvHaarClassifierCascade mouth_cascade;
	private static CvHaarClassifierCascade nose_cascade;
	
	private static File cascadeDir;
	private static File face_cascade_file;
	private static File leye_cascade_file;
	private static File reye_cascade_file;
	private static File mouth_cascade_file;
	private static File nose_cascade_file;
	
	private CvMat image;
	private CvMat small_image;

	private CvRect face;
	private CvRect leye;
	private CvRect reye;
	private CvRect mouth;
	private CvRect nose;

	public double foreheadHeight;
	public double eyeSize;
	public double eyeSpace;
	public double noseSize;
	public double noseHeight;
	public double noseWidth;
	public double mouthSize;
	public double mouthHeight;
	public double mouthWidth;
	
	public FacialFeatures(CvMat image, Context context) {
		
		if (cascadeDir == null)
			cascadeDir = context.getDir("cascade", Context.MODE_PRIVATE);
		
		// android cant open xml files by dir inside a apk which is what cascade classifier needs
		// have to create a temporary xml file via resources and read that into the cascade
		if (face_cascade == null) {
			try {
				InputStream is = context.getResources().openRawResource(R.raw.haarcascade_frontalface_alt);
				face_cascade_file = new File(cascadeDir, "face.xml");
				FileOutputStream os = new FileOutputStream(face_cascade_file);
				
				byte[] buffer = new byte[4096];
				int bytesRead;
				while ((bytesRead = is.read(buffer)) != -1) {
					os.write(buffer,0,bytesRead);
				}
				is.close();
				os.close();
				
				face_cascade = new CvHaarClassifierCascade(cvLoad(face_cascade_file.getAbsolutePath()));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		if (leye_cascade == null) {
			try {
				InputStream is = context.getResources().openRawResource(R.raw.haarcascade_mcs_lefteye);
				leye_cascade_file = new File(cascadeDir, "leye.xml");
				FileOutputStream os = new FileOutputStream(leye_cascade_file);
				
				byte[] buffer = new byte[4096];
				int bytesRead;
				while ((bytesRead = is.read(buffer)) != -1) {
					os.write(buffer,0,bytesRead);
				}
				is.close();
				os.close();
				
				leye_cascade = new CvHaarClassifierCascade(cvLoad(leye_cascade_file.getAbsolutePath()));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		if (reye_cascade == null) {
			try {
				InputStream is = context.getResources().openRawResource(R.raw.haarcascade_mcs_righteye);
				reye_cascade_file = new File(cascadeDir, "reye.xml");
				FileOutputStream os = new FileOutputStream(reye_cascade_file);
				
				byte[] buffer = new byte[4096];
				int bytesRead;
				while ((bytesRead = is.read(buffer)) != -1) {
					os.write(buffer,0,bytesRead);
				}
				is.close();
				os.close();
				
				reye_cascade = new CvHaarClassifierCascade(cvLoad(reye_cascade_file.getAbsolutePath()));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		if (mouth_cascade == null) {
			try {
				InputStream is = context.getResources().openRawResource(R.raw.haarcascade_mcs_mouth);
				mouth_cascade_file = new File(cascadeDir, "mouth.xml");
				FileOutputStream os = new FileOutputStream(mouth_cascade_file);
				
				byte[] buffer = new byte[4096];
				int bytesRead;
				while ((bytesRead = is.read(buffer)) != -1) {
					os.write(buffer,0,bytesRead);
				}
				is.close();
				os.close();
				
				mouth_cascade = new CvHaarClassifierCascade(cvLoad(mouth_cascade_file.getAbsolutePath()));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		if (nose_cascade == null) {
			try {
				InputStream is = context.getResources().openRawResource(R.raw.haarcascade_mcs_nose);
				nose_cascade_file = new File(cascadeDir, "nose.xml");
				FileOutputStream os = new FileOutputStream(nose_cascade_file);
				
				byte[] buffer = new byte[4096];
				int bytesRead;
				while ((bytesRead = is.read(buffer)) != -1) {
					os.write(buffer,0,bytesRead);
				}
				is.close();
				os.close();
				
				nose_cascade = new CvHaarClassifierCascade(cvLoad(nose_cascade_file.getAbsolutePath()));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		this.image = image;
		// for CV stuff work with a smaller image
		// (phone 8MP camera is like 3k x 3k pixels which is slow as hell
		this.small_image = cvMatResize(image, 400); 
		
		// android is dumb and some manufacturers mount camera different ways, this will look for a face,
		// if one is not found it will rotate image and try again until a full 360 degrees has been completed
		// stops after 4 rotations
		// also manipulates the original image so that it is facing correctly when all is done
		for (int i = 0; i < 4 && (findFeatures() == false || face.width() == 0 || face.height() == 0); i++) {
			this.small_image = cvRotateStep(small_image,i+1);
			this.image = cvRotateStep(image,i+1);
		}

		if (face == null || face.width() == 0 || face.height() == 0) {
			System.out.println("error: no faces detected in given image");
			return;
		}

		// forehead height relative to face height
		if (leye != null && reye != null) // if both eyes were found
			foreheadHeight = (((leye.y() + reye.y()) / 2.0) - face.y()) / face.height();
		else if (leye == null && reye != null) // left eye found but not right eye
			foreheadHeight = (reye.y() - face.y()) / (double) face.height();
		else if (reye == null && leye != null) // right eye found but not left one
			foreheadHeight = (leye.y() - face.y()) / (double) face.height();
		else // neither eye was found
			foreheadHeight = 0.0;

		// eye size (area) relative to face size (area)
		if (leye != null && reye != null)
			eyeSize = ((cvRectArea(leye) + cvRectArea(reye)) / 2.0) / (cvRectArea(face));
		else if (leye == null && reye != null)
			eyeSize = cvRectArea(reye) / cvRectArea(face);
		else if (reye == null && leye != null)
			eyeSize = cvRectArea(leye) / cvRectArea(face);
		else
			eyeSize = 0.0;		
			
		// eye spacing relative to face width
		if (leye != null && reye != null) // both eyes found
			eyeSpace = ((reye.x() + (reye.width() / 2.0)) - (leye.x() + (leye.width() / 2.0))) / face.width();
		else // if either eye was not found, we cannot find spacing
			eyeSpace = 0.0;		

		
		if (nose != null) {
			// nose size (area) relative to face size (area)
			noseSize = cvRectArea(nose) / cvRectArea(face);
			
			// nose height relative to face height
			noseHeight = (double) nose.height() / face.height();
			
			// nose width relative to face width
			noseWidth = (double) nose.width() / face.width();
		} else {
			noseSize = 0.0;
			noseWidth = 0.0;
		}

		if (mouth != null) {
			// mouth size (area) relative to face size (area)
			mouthSize = cvRectArea(mouth) / cvRectArea(face);
	
			// mouth height relative to face height
			mouthHeight = (double) mouth.height() / face.height();
		
			// mouth width relative to face width
			mouthWidth = (double) mouth.width() / face.width();
		} else {
			mouthSize = 0.0;
			mouthHeight = 0.0;
			mouthWidth = 0.0;
		}
	}
	
	private boolean findFeatures() {		
		// opencv detection works 234234324x faster on grayscale images
		CvMat gray_image = CvMat.create(small_image.rows(), small_image.cols(), CV_8U, 1);
		cvCvtColor(small_image, gray_image, CV_RGB2GRAY);
		cvEqualizeHist(gray_image, gray_image);

		// detect face first to create roi to search for facial features, can
		// greatly increase speed (esp for large images)
		face = detectFeature(face_cascade, gray_image, 3);

		if (face == null)
			return false;

		// separate regions of interests to reduce search area
		CvRect leftFace = new CvRect(face.x(), face.y(), face.width() / 2, face.height());
		CvRect rightFace = new CvRect(face.x() + face.width() / 2, face.y(), face.width() / 2, face.height());
		CvRect lowerFace = new CvRect(face.x(), face.y() + face.height() / 2, face.width(), face.height() / 2);

		leye = detectFeature(leye_cascade, FacialFeatures.cvCrop(gray_image, leftFace), 6);
		reye = detectFeature(reye_cascade, FacialFeatures.cvCrop(gray_image, rightFace), 6);
		mouth = detectFeature(mouth_cascade, FacialFeatures.cvCrop(gray_image, lowerFace), 5);
		nose = detectFeature(nose_cascade, FacialFeatures.cvCrop(gray_image, face), 6);
		
		// realign rects from roi x,y coords -> image x,y coords
		// test for null first to ensure we arent trying to manipulate null objects (in the case that no feature was found)
		if (leye != null) {
			leye.x(leye.x() + leftFace.x());
			leye.y(leye.y() + leftFace.y());
		}
		
		if (reye != null) {
			reye.x(reye.x() + rightFace.x());
			reye.y(reye.y() + rightFace.y());
		}
		
		if (mouth != null) {
			mouth.x(mouth.x() + lowerFace.x());
			mouth.y(mouth.y() + lowerFace.y());
		}
		
		if (nose != null) {
			nose.x(nose.x() + face.x());
			nose.y(nose.y() + face.y());
		}

		return true;
	}

	// detect feature (indicated by casc parameter) from image
	// uses simple binary search to find a single object (with the minBoxes as
	// the search term)
	// returns CvRect object containing feature
	private static CvRect detectFeature(CvHaarClassifierCascade casc, CvMat in, int minSize) {
		CvMemStorage storage = CvMemStorage.create();
		CvSeq detected = new CvSeq();

		// basically a binary search to find min near bounding boxes
		for (int low = 3, high = 50, mid; detected.total() != 1 && low <= high;) {
			mid = (low + high) / 2;

			cvClearMemStorage(storage);
			detected = cvHaarDetectObjects((CvArr) in, casc, storage, 1.1, mid, opencv_objdetect.CV_HAAR_SCALE_IMAGE, new CvSize(in.rows()/minSize,in.cols()/minSize), new CvSize());

			if (detected.total() == 0)
				high = mid - 1;
			else if (detected.total() > 1)
				low = mid + 1;
		}

		if (detected.total() == 0)
			return null;
		return new CvRect(cvGetSeqElem(detected, 0));
	}

	// returns cropped CvMat from the CvMat image given to the CvRect roi
	private static CvMat cvCrop(CvMat src, CvRect roi) {
		IplImage srcIpl = src.asIplImage().clone();
		IplImage cropped = cvCreateImage(cvSize(roi.width(), roi.height()), srcIpl.depth(), srcIpl.nChannels());

		cvSetImageROI(srcIpl, roi);
		cvCopy(srcIpl, cropped);
		return cropped.asCvMat();
	}
	
	// rotates image 90 degrees * steps
	private static CvMat cvRotateStep(CvMat in, int steps) {
	    CvMat rotated;

	    if (steps != 2)
	        rotated = cvCreateMat(in.cols(),in.rows(),in.type());//cvCreateImage(new CvSize(in.rows(), in.cols()), in.depth(), in.channels());
	    else
	        rotated = in.clone();

	    if (steps != 2)
	        cvTranspose(in, rotated);

	    if (steps == 3)
	        cvFlip(rotated, null, 1);
	    else if (steps == 1)
	        cvFlip(rotated, null, 0);
	    else if (steps == 2)
	        cvFlip(rotated, null, -1);

	    return rotated;
	}
	
	// resizes mat to height specified, width is automatically determined to maintain aspect ratio
	private static CvMat cvMatResize(CvMat src, int new_height) {
		double aspect = ((double) src.rows()) / src.cols();
		int height = new_height;
		int width = (int) (((double) new_height) / aspect);
		
	    CvMat dest = cvCreateMat(height, width, src.type());
	    cvResize(src, dest, CV_INTER_LINEAR);
	    return dest;
	} 
	
	// return area of rect, 0 if none
	private static double cvRectArea(CvRect rect) {
		if (rect == null)
			return 0.0;
		return rect.width() * rect.height();
	}
}
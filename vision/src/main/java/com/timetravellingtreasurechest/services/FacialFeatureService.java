package com.timetravellingtreasurechest.services;

import static com.googlecode.javacv.cpp.opencv_core.CV_8U;
import static com.googlecode.javacv.cpp.opencv_core.cvCreateMat;
import static com.googlecode.javacv.cpp.opencv_core.cvFlip;
import static com.googlecode.javacv.cpp.opencv_core.cvLoad;
import static com.googlecode.javacv.cpp.opencv_core.cvTranspose;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_INTER_LINEAR;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_RGB2GRAY;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvCvtColor;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvEqualizeHist;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvResize;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;

import com.googlecode.javacv.cpp.opencv_core.CvMat;
import com.googlecode.javacv.cpp.opencv_objdetect.CvHaarClassifierCascade;
import com.timetravellingtreasurechest.vision.FacialFeatures;

public class FacialFeatureService implements IFacialFeatureService {

	public static CvHaarClassifierCascade getCascadeClassifier(String identifier) {
		return new CvHaarClassifierCascade(cvLoad("src\\haarcascades\\" + identifier + ".xml"));
	}
	
	@Override
	public FacialFeatures getFeatures(CvMat image) {
		
		CvMat small_image = cvMatResize(image, 400);
		CvMat final_image = CvMat.create(small_image .rows(), small_image.cols(), CV_8U, 1);
		cvCvtColor(image, final_image, CV_RGB2GRAY);
		cvEqualizeHist(final_image, final_image);
		
		FacialFeatures features = new FacialFeatures(final_image);
		for (int i = 0; i < 4 && features.getFace() == null; i++) {
			final_image = cvRotateStep(final_image,i+1);
			features = new FacialFeatures(final_image);
		}

		return features;
	}
	
	private static CvMat cvMatResize(CvMat src, int new_height) {
		double aspect = ((double) src.rows()) / src.cols();
		int height = new_height;
		int width = (int) (((double) new_height) / aspect);
		
	    CvMat dest = cvCreateMat(height, width, src.type());
	    cvResize(src, dest, CV_INTER_LINEAR);
	    return dest;
	} 
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
}

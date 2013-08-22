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

import com.googlecode.javacv.cpp.opencv_core.CvMat;
import com.googlecode.javacv.cpp.opencv_objdetect.CvHaarClassifierCascade;
import com.timetravellingtreasurechest.vision.FacialFeatures;

public class FacialFeatureService implements IFacialFeatureService {

	@Override
	public CvHaarClassifierCascade getCascadeClassifier(String identifier) {
		return new CvHaarClassifierCascade(cvLoad("src\\haarcascades\\" + identifier + ".xml"));
	}
	
	@Override
	public CvMat getResizedImage(CvMat original) {
		return cvMatResize(original, 400);
	}
	@Override
	public CvMat getGrayedImage(CvMat original) {
		CvMat gray_image = CvMat.create(original.rows(), original.cols(), CV_8U, 1);
		cvCvtColor(original, gray_image, CV_RGB2GRAY);
		cvEqualizeHist(gray_image, gray_image);
		return gray_image;
	}
	
	@Override
	public FacialFeatures getFeatures(CvMat image) {
		CvMat final_image = getGrayedImage(getResizedImage(image));
		
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

package com.timetravellingtreasurechest.services;

import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;

import com.googlecode.javacv.cpp.opencv_core.CvMat;
import com.googlecode.javacv.cpp.opencv_imgproc;
import com.googlecode.javacv.cpp.opencv_objdetect.CvHaarClassifierCascade;
import com.timetravellingtreasurechest.vision.FacialFeatures;

public class AndroidFacialFeatureService implements IFacialFeatureService {
	public Context context;
	public AndroidFacialFeatureService(Context context) {
		this.context = context;
	}
	
	@Override
	public CvHaarClassifierCascade getCascadeClassifier(String identifier) {
		try {
			InputStream is = context.getResources().openRawResource(context.getResources().getIdentifier(identifier, "raw", context.getPackageName()));
			File cascade_file = new File(context.getDir("cascade", Context.MODE_PRIVATE), identifier + ".xml");
			FileOutputStream os = new FileOutputStream(cascade_file);
			
			byte[] buffer = new byte[4096];
			int bytesRead;
			while ((bytesRead = is.read(buffer)) != -1) {
				os.write(buffer,0,bytesRead);
			}
			is.close();
			os.close();
			return new CvHaarClassifierCascade(cvLoad(cascade_file.getAbsolutePath()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	@Override
	public CvMat getResizedImage(CvMat original) {
		return ImageConverter.cvMatResize(original, 400);
	}
	
	@Override
	public CvMat getGrayedImage(CvMat original) {
		CvMat gray_image = CvMat.create(original.rows(), original.cols(), CV_8U, 1);
		cvCvtColor(original, gray_image, opencv_imgproc.CV_BGRA2GRAY);
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
	
	public static CvMat cvRotateStep(CvMat in, int steps) {
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

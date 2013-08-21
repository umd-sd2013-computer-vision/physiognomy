package com.timetravellingtreasurechest.services;

import com.googlecode.javacv.cpp.opencv_core.CvMat;
import com.googlecode.javacv.cpp.opencv_objdetect.CvHaarClassifierCascade;
import com.timetravellingtreasurechest.vision.FacialFeatures;


public interface IFacialFeatureService {
	public FacialFeatures getFeatures(CvMat image);
}

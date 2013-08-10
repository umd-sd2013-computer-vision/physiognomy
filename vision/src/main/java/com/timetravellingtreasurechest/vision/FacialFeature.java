package com.timetravellingtreasurechest.vision;


import com.googlecode.javacv.cpp.opencv_core.CvPoint;
import com.googlecode.javacv.cpp.opencv_core.CvRect;

public class FacialFeature {
	
	public CvRect boundingBox;
	public CvPoint offset;
	
	public FacialFeature(CvRect boundingBox) {
		this.boundingBox = boundingBox;
	}
}

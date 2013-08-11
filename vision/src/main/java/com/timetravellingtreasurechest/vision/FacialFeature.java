package com.timetravellingtreasurechest.vision;


import com.googlecode.javacv.cpp.opencv_core.CvRect;

public class FacialFeature {
	
	public CvRect boundingBox;
	
	public FacialFeature(CvRect boundingBox) {
		this.boundingBox = boundingBox;
	}
}

package com.timetravellingtreasurechest.vision;


import com.googlecode.javacv.cpp.opencv_core.CvPoint;
import com.googlecode.javacv.cpp.opencv_core.CvRect;

public class FacialFeature {
	
	public CvRect boundingBox;
	public CvPoint offset;
	
	public FacialFeature(CvRect boundingBox, CvPoint offset) {
		this.boundingBox = boundingBox;
		
		if (boundingBox == null) // feature was not detected
			this.offset = new CvPoint(0,0);
		else
			this.offset = offset;
	}
}

package com.timetravellingtreasurechest.vision;

import java.awt.Point;

import com.googlecode.javacv.cpp.opencv_core.CvRect;

public class FacialFeature {
	public FacialFeature(CvRect boundingBox, Point offset) {
		this.boundingBox = boundingBox;
		this.offset = offset;
	}
	public CvRect boundingBox;
	public Point offset;
}

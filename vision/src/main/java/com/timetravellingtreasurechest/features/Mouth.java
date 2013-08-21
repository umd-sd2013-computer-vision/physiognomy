package com.timetravellingtreasurechest.features;

import com.googlecode.javacv.cpp.opencv_core.CvMat;
import com.googlecode.javacv.cpp.opencv_core.CvRect;
import com.googlecode.javacv.cpp.opencv_objdetect.CvHaarClassifierCascade;
import com.timetravellingtreasurechest.services.FacialFeatureService;

public class Mouth extends FacialFeature<Mouth> {
	private static final CvHaarClassifierCascade mouth_cascade = FacialFeatureService.getCascadeClassifier("haarcascade_mcs_mouth");
	
	protected Mouth() {
		super(null);
	}
	
	Mouth(CvRect bounds) {
		super(bounds);
	}
	
	@Override
	public Mouth fromImage(CvMat image) {
		return new Mouth(detectFeature(mouth_cascade, image));
	}
	
	@Override
	public CvRect getFaceQuadrant(Face face) {
		CvRect f = face.getBounds();
		return new CvRect(f).y(f.y() + (f.height() / 2) ).height(f.height() / 2);
	}
}

package com.timetravellingtreasurechest.features;

import com.googlecode.javacv.cpp.opencv_core.CvMat;
import com.googlecode.javacv.cpp.opencv_core.CvRect;
import com.googlecode.javacv.cpp.opencv_objdetect.CvHaarClassifierCascade;
import com.timetravellingtreasurechest.services.ServiceServer;

public class Mouth extends FacialFeature<Mouth> {
	private static final CvHaarClassifierCascade mouth_cascade = ServiceServer
			.getFacialFeatureService().getCascadeClassifier(
					"haarcascade_mcs_mouth");

	protected Mouth() {
		super(null);
	}

	Mouth(CvRect bounds) {
		super(bounds);
	}

	@Override
	public Mouth fromImage(CvMat image) {
		return new Mouth(detectFeature(mouth_cascade, image, 5));
	}

	@Override
	public CvRect getFaceQuadrant(Face face) {
		CvRect f = face.getBounds();
		return new CvRect(f.x(), f.y() + (f.height() / 2), f.width(), f.height() / 2);
	}
	
	@Override
	public String toString() {
		return "mouth";
	}
}

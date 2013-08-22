package com.timetravellingtreasurechest.features;

import com.googlecode.javacv.cpp.opencv_core.CvMat;
import com.googlecode.javacv.cpp.opencv_core.CvRect;
import com.googlecode.javacv.cpp.opencv_objdetect.CvHaarClassifierCascade;
import com.timetravellingtreasurechest.services.ServiceServer;

public class Nose extends FacialFeature<Nose> {
	private static final CvHaarClassifierCascade nose_cascade = ServiceServer
			.getFacialFeatureService().getCascadeClassifier(
					"haarcascade_mcs_nose");

	protected Nose() {
		super(null);
	}

	Nose(CvRect bounds) {
		super(bounds);
	}

	@Override
	public Nose fromImage(CvMat image) {
		return new Nose(detectFeature(nose_cascade, image, 6));
	}
	
	@Override
	public String toString() {
		return "nose";
	}
}

package com.timetravellingtreasurechest.features;

import com.googlecode.javacv.cpp.opencv_core.CvMat;
import com.googlecode.javacv.cpp.opencv_core.CvRect;
import com.googlecode.javacv.cpp.opencv_objdetect.CvHaarClassifierCascade;
import com.timetravellingtreasurechest.services.ServiceServer;

public class Face extends FacialFeature<Face> {
	private static final CvHaarClassifierCascade face_cascade = ServiceServer
			.getFacialFeatureService().getCascadeClassifier(
					"haarcascade_frontalface_alt");

	protected Face() {
		super(null);
	};

	Face(CvRect bounds) {
		super(bounds);
	}

	@Override
	public Face fromImage(CvMat image) {
		return new Face(detectFeature(face_cascade, image, 3));
	}
	
	@Override
	public String toString() {
		return "face";
	}
}

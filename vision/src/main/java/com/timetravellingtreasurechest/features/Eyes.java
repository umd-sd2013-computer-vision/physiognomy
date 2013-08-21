package com.timetravellingtreasurechest.features;


import com.googlecode.javacv.cpp.opencv_core.CvMat;
import com.googlecode.javacv.cpp.opencv_core.CvRect;
import com.googlecode.javacv.cpp.opencv_objdetect.CvHaarClassifierCascade;
import com.timetravellingtreasurechest.services.ServiceServer;

public class Eyes extends FacialFeature<Eyes> {
	private static final CvHaarClassifierCascade leye_cascade = ServiceServer.getFacialFeatureService().getCascadeClassifier("src\\haarcascades\\haarcascade_mcs_lefteye.xml");
	private static final CvHaarClassifierCascade reye_cascade = ServiceServer.getFacialFeatureService().getCascadeClassifier("src\\haarcascades\\haarcascade_mcs_righteye.xml");

	private final CvRect leftEye;
	private final CvRect rightEye;

	protected Eyes() {
		super(null);
		leftEye = null;
		rightEye = null;
	}

	Eyes(CvRect e1, CvRect e2) {
		super(null);
		this.leftEye = e1.x() <= e2.x() ? e1 : e2;
		this.rightEye = e2.x() <= e1.x() ? e2 : e1;
		int maxHeight = Math.max(e1.y() + e1.height(), e2.x() + e2.height());
		int height = maxHeight - Math.min(e1.y(), e1.y());
		int width = rightEye.x() - leftEye.x() + rightEye.width();
		this.bounds = new CvRect(leftEye.x(), Math.min(e1.y(), e2.y()), width,
				height);
	}

	@Override
	public Eyes fromImage(CvMat image) {
		return new Eyes(detectFeature(leye_cascade, image), detectFeature(
				reye_cascade, image));
	}

	@Override
	public Eyes fromImage(CvMat image, Face face) {
		return new Eyes(detectFeature(leye_cascade,
				FacialFeature.crop(image, getFaceLQuadrant(face))),
				detectFeature(reye_cascade,
						FacialFeature.crop(image, getFaceRQuadrant(face))));
	}

	public CvRect getFaceRQuadrant(Face face) {
		CvRect f = face.getBounds();
		return new CvRect(f).y(f.x() + (f.width() / 2)).width(f.width() / 2);
	}

	public CvRect getFaceLQuadrant(Face face) {
		CvRect f = face.getBounds();
		return new CvRect(f).y(f.x()).width(f.width() / 2);
	}
	
	public double getForeheadHeight(Face face) {
		double foreheadHeight = 0;
		CvRect f = face.getBounds();
		// forehead height relative to face height
		if (leftEye != null && rightEye != null) // if both eyes were found
			foreheadHeight = (((leftEye.y() + rightEye.y()) / 2.0) - f.y()) / f.height();
		else if (leftEye == null && rightEye != null) // left eye found but not right eye
			foreheadHeight = (rightEye.y() - f.y()) / (double) f.height();
		else if (rightEye == null && leftEye != null) // right eye found but not left one
			foreheadHeight = (leftEye.y() - f.y()) / (double) f.height();
		else // neither eye was found
			foreheadHeight = 0.0;
		return foreheadHeight;
	}
	
	public double getEyeSpacing(Face face) {
		double eyeSpace = 0;
		CvRect f = face.getBounds();
		// eye spacing relative to face width
		if (leftEye != null && rightEye != null) // both eyes found
			eyeSpace = ((rightEye.x() + (rightEye.width() / 2.0)) - (leftEye.x() + (leftEye.width() / 2.0))) / f.width();
		else // if either eye was not found, we cannot find spacing
			eyeSpace = 0.0;
		return eyeSpace;
	}
}

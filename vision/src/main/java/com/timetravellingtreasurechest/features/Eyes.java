package com.timetravellingtreasurechest.features;

import static com.googlecode.javacv.cpp.opencv_core.cvRectangle;

import com.googlecode.javacv.cpp.opencv_core.CvMat;
import com.googlecode.javacv.cpp.opencv_core.CvPoint;
import com.googlecode.javacv.cpp.opencv_core.CvRect;
import com.googlecode.javacv.cpp.opencv_core.CvScalar;
import com.googlecode.javacv.cpp.opencv_objdetect.CvHaarClassifierCascade;
import com.timetravellingtreasurechest.services.ServiceServer;

public class Eyes extends FacialFeature<Eyes> {
	private static final CvHaarClassifierCascade leye_cascade = ServiceServer
			.getFacialFeatureService().getCascadeClassifier(
					"haarcascade_mcs_lefteye");
	private static final CvHaarClassifierCascade reye_cascade = ServiceServer
			.getFacialFeatureService().getCascadeClassifier(
					"haarcascade_mcs_righteye");

	private final CvRect leftEye;
	private final CvRect rightEye;

	protected Eyes() {
		super(null);
		leftEye = null;
		rightEye = null;
	}

	Eyes(CvRect e1, CvRect e2) {
		super(null);
		CvRect lEye = e1.x() <= e2.x() ? e1 : e2;
		this.leftEye = new CvRect(lEye.x(), lEye.y(), lEye.width(), lEye.height());
		CvRect rEye = e1.x() <= e2.x() ? e2 : e1;
		this.rightEye = new CvRect(rEye.x(), rEye.y(), rEye.width(), rEye.height());
		
		int maxHeight = Math.max(e1.y() + e1.height(), e2.y() + e2.height());
		int height = maxHeight - Math.min(e1.y(), e1.y());
		int width = rEye.x() - lEye.x() + rEye.width();
		this.bounds = new CvRect(lEye.x(), Math.min(e1.y(), e2.y()), width,
				height);
	}

	@Override
	public Eyes fromImage(CvMat image) {
		return new Eyes(detectFeature(leye_cascade, image, 6), detectFeature(
				reye_cascade, image, 6));
	}

	@Override
	public Eyes fromImage(CvMat image, Face face) {
		return new Eyes(detectFeature(leye_cascade,
				FacialFeature.crop(image, getFaceLQuadrant(face)), 6),
				detectFeature(reye_cascade,
						FacialFeature.crop(image, getFaceRQuadrant(face)), 6));
	}

	public CvRect getFaceRQuadrant(Face face) {
		CvRect f = face.getBounds();
		return new CvRect(f.x() + (f.width() / 2), f.y(), f.width() / 2,
				f.height());
	}

	public CvRect getFaceLQuadrant(Face face) {
		CvRect f = face.getBounds();
		return new CvRect(f.x(), f.y(), f.width() / 2, f.height());
	}

	public double getForeheadHeight(Face face) {
		double foreheadHeight = 0;
		CvRect f = face.getBounds();
		// forehead height relative to face height
		if (leftEye != null && rightEye != null) // if both eyes were found
			foreheadHeight = (((leftEye.y() + rightEye.y()) / 2.0) - f.y())
					/ f.height();
		else if (leftEye == null && rightEye != null) // left eye found but not
														// right eye
			foreheadHeight = (rightEye.y() - f.y()) / (double) f.height();
		else if (rightEye == null && leftEye != null) // right eye found but not
														// left one
			foreheadHeight = (leftEye.y() - f.y()) / (double) f.height();
		else
			// neither eye was found
			foreheadHeight = 0.0;
		return foreheadHeight;
	}

	public double getEyeSpacing(Face face) {
		double eyeSpace = 0;
		CvRect f = face.getBounds();
		// eye spacing relative to face width
		if (leftEye != null && rightEye != null) // both eyes found
			eyeSpace = ((rightEye.x() + (rightEye.width() / 2.0)) - (leftEye
					.x() + (leftEye.width() / 2.0))) / f.width();
		else
			// if either eye was not found, we cannot find spacing
			eyeSpace = 0.0;
		return eyeSpace;
	}

	@Override
	public void drawBounds(CvMat image, Face face) {
		CvRect l = getFaceLQuadrant(face);
		CvRect r = getFaceRQuadrant(face);
		drawBounds(image, leftEye, new CvPoint(l.x(), l.y()));
		drawBounds(image, rightEye, new CvPoint(r.x(), r.y()));
	}
	
	@Override
	public String toString() {
		return "eyes";
	}
}

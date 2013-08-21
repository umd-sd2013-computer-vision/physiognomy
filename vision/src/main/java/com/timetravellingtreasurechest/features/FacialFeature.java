package com.timetravellingtreasurechest.features;

import static com.googlecode.javacv.cpp.opencv_core.cvClearMemStorage;
import static com.googlecode.javacv.cpp.opencv_core.cvCopy;
import static com.googlecode.javacv.cpp.opencv_core.cvCreateImage;
import static com.googlecode.javacv.cpp.opencv_core.cvGetSeqElem;
import static com.googlecode.javacv.cpp.opencv_core.cvLoad;
import static com.googlecode.javacv.cpp.opencv_core.cvSetImageROI;
import static com.googlecode.javacv.cpp.opencv_core.cvSize;
import static com.googlecode.javacv.cpp.opencv_objdetect.cvHaarDetectObjects;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;

import com.googlecode.javacv.cpp.opencv_objdetect;
import com.googlecode.javacv.cpp.opencv_core.CvArr;
import com.googlecode.javacv.cpp.opencv_core.CvMat;
import com.googlecode.javacv.cpp.opencv_core.CvMemStorage;
import com.googlecode.javacv.cpp.opencv_core.CvRect;
import com.googlecode.javacv.cpp.opencv_core.CvSeq;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import com.googlecode.javacv.cpp.opencv_objdetect.CvHaarClassifierCascade;

public abstract class FacialFeature<T extends FacialFeature<T>> {
	private static Context context;
	
	public static void setContext(Context androidContext) {
		context = androidContext;
	}
	
	protected CvRect bounds;
	FacialFeature(CvRect bounds) {
		this.bounds = bounds;
	}
	
	public abstract T fromImage(CvMat image);

	public T fromImage(CvMat image, Face face) {
		return this.fromImage(image);
	}
	
	public CvRect getNormalizedBounds(Face face) {
		return getRelativeBounds(face, this);
	}
	public double getNormalizedArea(Face face) {
		return cvRectArea(getBounds()) / cvRectArea(face.getBounds());
	}
	

	public CvRect getBounds() {
		return new CvRect(bounds);
	}

	public CvRect getFaceQuadrant(Face face) {
		return face.getBounds();
	}

	// Normalize with respect to face
	public static CvRect getRelativeBounds(Face face, FacialFeature<?> feature) {
		CvRect f = face.getBounds();
		CvRect foff = new CvRect(feature.getBounds());
		double scaleX = (foff.width() / f.width())*1000;
		double scaleY = (foff.height() / f.height())*1000;
		foff = foff.x((int) (foff.x() / scaleX));
		foff = foff.y((int) (foff.y() / scaleY));
		foff = foff.width((int) scaleX).height((int) scaleY);
		return foff;
	}
	
	public static double getRelativeArea(Face face, FacialFeature<?> feature) {
		return cvRectArea(feature.getBounds()) / cvRectArea(face.getBounds());
	}

	public static <T extends FacialFeature<T>> T getEmptyFeature(Class<T> clazz) {
		T feature = null;
		try {
			feature = (T) clazz.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return feature;
	}

	public static <T extends FacialFeature<T>> T getImage(Class<T> clazz,
			CvMat image) {
		T feature = getEmptyFeature(clazz).fromImage(image);
		if (feature == null || feature.getBounds().height() <= 0
				|| feature.getBounds().width() <= 0) {
			return null;
		}
		return feature;
	}

	public static <T extends FacialFeature<T>> T getImage(Class<T> clazz,
			CvMat image, CvRect regionOfInterest) {
		return getImage(clazz, crop(image, regionOfInterest));
	}

	public static <T extends FacialFeature<T>> T getFromImageAndFace(
			Class<T> clazz, CvMat image, Face face) {
		return getEmptyFeature(clazz).fromImage(image, face);
	}

	// detect feature (indicated by casc parameter) from image
	// uses simple binary search to find a single object (with the minBoxes as
	// the search term)
	// returns CvRect object containing feature
	protected static CvRect detectFeature(CvHaarClassifierCascade casc, CvMat in) {
		CvMemStorage storage = CvMemStorage.create();
		CvSeq detected = new CvSeq();

		for (int low = 3, high = 150, mid; detected.total() != 1 && low <= high;) {
			mid = (low + high) / 2;

			cvClearMemStorage(storage);
			detected = cvHaarDetectObjects((CvArr) in, casc, storage, 1.1, mid,
					opencv_objdetect.CV_HAAR_SCALE_IMAGE);

			if (detected.total() == 0)
				high = mid - 1;
			else if (detected.total() > 1)
				low = mid + 1;
		}

		if (detected.total() == 0)
			return null;
		return new CvRect(cvGetSeqElem(detected, 0));
	}

	// returns cropped CvMat from the CvMat image given to the CvRect roi
	protected static CvMat crop(CvMat src, CvRect roi) {
		IplImage srcIpl = src.asIplImage().clone();
		IplImage cropped = cvCreateImage(cvSize(roi.width(), roi.height()),
				srcIpl.depth(), srcIpl.nChannels());

		cvSetImageROI(srcIpl, roi);
		cvCopy(srcIpl, cropped);
		return cropped.asCvMat();
	}

	protected static double cvRectArea(CvRect rect) {
		if (rect == null)
			return 0.0;
		return rect.width() * rect.height();
	}
}

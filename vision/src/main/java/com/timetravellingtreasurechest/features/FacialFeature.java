package com.timetravellingtreasurechest.features;

import static com.googlecode.javacv.cpp.opencv_core.cvClearMemStorage;
import static com.googlecode.javacv.cpp.opencv_core.cvCopy;
import static com.googlecode.javacv.cpp.opencv_core.cvCreateImage;
import static com.googlecode.javacv.cpp.opencv_core.cvGetSeqElem;
import static com.googlecode.javacv.cpp.opencv_core.cvRectangle;
import static com.googlecode.javacv.cpp.opencv_core.cvSetImageROI;
import static com.googlecode.javacv.cpp.opencv_core.cvSize;
import static com.googlecode.javacv.cpp.opencv_objdetect.cvHaarDetectObjects;

import java.io.Serializable;

import com.googlecode.javacv.cpp.opencv_objdetect;
import com.googlecode.javacv.cpp.opencv_core.CvArr;
import com.googlecode.javacv.cpp.opencv_core.CvMat;
import com.googlecode.javacv.cpp.opencv_core.CvMemStorage;
import com.googlecode.javacv.cpp.opencv_core.CvPoint;
import com.googlecode.javacv.cpp.opencv_core.CvRect;
import com.googlecode.javacv.cpp.opencv_core.CvScalar;
import com.googlecode.javacv.cpp.opencv_core.CvSeq;
import com.googlecode.javacv.cpp.opencv_core.CvSize;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import com.googlecode.javacv.cpp.opencv_objdetect.CvHaarClassifierCascade;

public abstract class FacialFeature<T extends FacialFeature<T>> implements Serializable {

	protected myRect bounds;

	FacialFeature(CvRect bounds) {
		if(bounds == null) {
			this.bounds = null;
		} else {
			this.bounds = new myRect(bounds.x(), bounds.y(), bounds.width(), bounds.height());
		}
		
	}

	public abstract T fromImage(CvMat image);

	public T fromImage(CvMat image, Face face) {
		return this.fromImage(crop(image, getFaceQuadrant(face)));
	}

	public double getNormalizedArea(Face face) {
		return cvRectArea(getBounds()) / cvRectArea(face.getBounds());
	}

	public CvRect getBounds() {
		if(bounds == null) return null;
		return new CvRect(bounds.x, bounds.y, bounds.width,
				bounds.height);
	}

	public CvRect getFaceQuadrant(Face face) {
		return face.getBounds();
	}

	// Normalize with respect to face
	public static double getRelativeWidth(Face face, FacialFeature<?> feature) {
		CvRect f = face.getBounds();
		CvRect foff = feature.getBounds();
		return ((double) foff.width()) / f.width();
	}

	public static double getRelativeHeight(Face face, FacialFeature<?> feature) {
		CvRect f = face.getBounds();
		CvRect foff = feature.getBounds();
		return ((double) foff.height()) / f.height();
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
		if (feature == null || feature.getBounds() == null || feature.getBounds().height() <= 0
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
	protected static CvRect detectFeature(CvHaarClassifierCascade casc, CvMat in, int minSize) {
		CvMemStorage storage = CvMemStorage.create();
		CvSeq detected = new CvSeq();

		// basically a binary search to find min near bounding boxes
		for (int low = 3, high = 50, mid; detected.total() != 1 && low <= high;) {
			mid = (low + high) / 2;

			cvClearMemStorage(storage);
			detected = cvHaarDetectObjects((CvArr) in, casc, storage, 1.1, mid,
					opencv_objdetect.CV_HAAR_SCALE_IMAGE, new CvSize(in.rows()
							/ minSize, in.cols() / minSize), new CvSize());

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
	public static CvMat crop(CvMat src, CvRect roi) {
//		IplImage srcIpl = new IplImage(src.length());
//		cvCopy()
//		srcIpl.asByteBuffer().put(src.asByteBuffer().array());
		IplImage srcIpl = src.asIplImage();
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

	public void drawBounds(CvMat image, Face face) {
		CvRect f = getFaceQuadrant(face);
		drawBounds(image, new CvRect(bounds.x, bounds.y, bounds.width, bounds.height), new CvPoint(f.x(), f.y()));
	}

	protected static void drawBounds(CvMat image, CvRect toDraw, CvPoint offset) {
		if (toDraw == null)
			return;
		cvRectangle(image, new CvPoint(toDraw.x() + offset.x(), toDraw.y() + offset.y()), new CvPoint(
				toDraw.x() + offset.x() + toDraw.width(), toDraw.y() + offset.y() + toDraw.height()),
				new CvScalar(0, 0, 0, 0), 1, 8, 0);
	}

	public void drawBounds(CvMat image) {
		drawBounds(image, new CvRect(bounds.x, bounds.y, bounds.width, bounds.height), new CvPoint(0,0));
	}
	
	class myRect implements Serializable {
		static final long serialVersionUID = 1L;
		int x;
		int y;
		int width;
		int height;
		
		myRect(int x, int y, int width, int height) {
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
		}
	}
}

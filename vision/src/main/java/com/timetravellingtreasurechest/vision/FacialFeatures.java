package com.timetravellingtreasurechest.vision;

import java.awt.Point;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.googlecode.javacv.CanvasFrame;
import com.googlecode.javacv.cpp.*;
import com.googlecode.javacv.cpp.opencv_objdetect.CvHaarClassifierCascade;

import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import static com.googlecode.javacv.cpp.opencv_highgui.*;
import static com.googlecode.javacv.cpp.opencv_objdetect.*;

public class FacialFeatures {

	// detect what features ?
	private static final boolean FACE = true;
	private static final boolean LEYE = true;
	private static final boolean REYE = true;
	private static final boolean MOUTH = true;
	private static final boolean NOSE = true;

	private static final boolean OUTPUT_IMAGE = true; // output test image to
														// file including boxes,
														// only for testing. not
														// included in final
														// project
	private List<FacialFeature> features = new ArrayList<FacialFeature>();
	private static CvHaarClassifierCascade face_cascade = new CvHaarClassifierCascade(
			cvLoad("src\\haarcascades\\haarcascade_frontalface_alt.xml"));
	private static CvHaarClassifierCascade leye_cascade = new CvHaarClassifierCascade(
			cvLoad("src\\haarcascades\\haarcascade_mcs_lefteye.xml"));
	private static CvHaarClassifierCascade reye_cascade = new CvHaarClassifierCascade(
			cvLoad("src\\haarcascades\\haarcascade_mcs_righteye.xml"));
	private static CvHaarClassifierCascade mouth_cascade = new CvHaarClassifierCascade(
			cvLoad("src\\haarcascades\\haarcascade_mcs_mouth.xml"));
	private static CvHaarClassifierCascade nose_cascade = new CvHaarClassifierCascade(
			cvLoad("src\\haarcascades\\haarcascade_mcs_nose.xml"));;

	private CvMat image;

	private CvRect face;
	private CvRect leye;
	private CvRect reye;
	private CvRect mouth;
	private CvRect nose;

	private float headWidth;
	private float foreheadSize;
	private float eyeSize;
	private float eyeSpace;
	private float noseSize;
	private float noseWidth;
	private float mouthSize;
	private float mouthWidth;
	private float chinHeight;

	public FacialFeatures(CvMat image) {
		this.image = image;

		if (findFeatures() == false) {
			System.out.println("error: no faces detected in given image");
			return;
		}
		extractFeatures();
	}

	public List<FacialFeature> extractFeatures() {
		return features;
	}

	private boolean findFeatures() {
		// opencv detection works 234234324x faster on grayscale images
		CvMat gray_image = CvMat.create(image.rows(), image.cols(), CV_8U, 1);
		cvCvtColor(image, gray_image, CV_RGB2GRAY);
		cvEqualizeHist(gray_image, gray_image);

		// detect face first to create roi to search for facial features, can
		// greatly increase speed (esp for large images)
		face = detectFeature(face_cascade, gray_image);

		if (face == null)
			return false;

		CvRect faceROI = new CvRect(face.x(), face.y(), face.width(),
				face.height());
		CvMat face_crop_image = FacialFeatures.crop(gray_image, faceROI);

		// separate regions of interests to reduce search area
		CvRect upperFaceROI = new CvRect(face.x(), face.y(), face.width(),
				face.height() / 2);
		CvRect lowerFaceROI = new CvRect(face.x(),
				face.y() + face.height() / 2, face.width(), face.height() / 2);
		CvRect leftFaceROI = new CvRect(face.x(), face.y(), face.width() / 2,
				face.height());
		CvRect rightFaceROI = new CvRect(face.x() + face.width() / 2, face.y(),
				face.width() / 2, face.height());

		CvMat upper_face_crop_image = FacialFeatures.crop(gray_image,
				upperFaceROI);
		CvMat lower_face_crop_image = FacialFeatures.crop(gray_image,
				lowerFaceROI);
		CvMat left_face_crop_image = FacialFeatures.crop(gray_image,
				leftFaceROI);
		CvMat right_face_crop_image = FacialFeatures.crop(gray_image,
				rightFaceROI);

		if (LEYE)
			leye = detectFeature(leye_cascade, left_face_crop_image);
		if (REYE)
			reye = detectFeature(reye_cascade, right_face_crop_image);
		if (MOUTH)
			mouth = detectFeature(mouth_cascade, lower_face_crop_image);
		if (NOSE)
			nose = detectFeature(nose_cascade, face_crop_image);
		
		features.add(new FacialFeature(face, new Point(0,0)));
		features.add(new FacialFeature(leye, new Point(leftFaceROI.x(), leftFaceROI.y())));
		features.add(new FacialFeature(reye, new Point(rightFaceROI.x(), rightFaceROI.y())));
		features.add(new FacialFeature(mouth, new Point(lowerFaceROI.x(), lowerFaceROI.y())));
		features.add(new FacialFeature(nose, new Point(faceROI.x(), faceROI.y())));

		return true;
	}

	// detect feature (indicated by casc parameter) from image
	// uses simple binary search to find a single object (with the minBoxes as
	// the search term)
	// returns Rect object containing feature
	private CvRect detectFeature(CvHaarClassifierCascade casc, CvMat in) {
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

	// draw rects on image relative to offsets given (for roi placement)
	// only used for testing (if OUTPUT_IMAGE is set), not for final project
	public static void putRects(CvMat image, CvRect rect, int offset_x, int offset_y) {
		if (rect == null)
			return;

		CvPoint pt1 = new CvPoint(rect.x() + offset_x, rect.y() + offset_y);
		CvPoint pt2 = new CvPoint(rect.x() + rect.width() + offset_x, rect.y()
				+ rect.height() + offset_y);

		opencv_core.cvRectangle(image, pt1, pt2, new CvScalar(0, 255, 0, 0), 1,
				8, 0);
	}

	private static CvMat crop(CvMat src, CvRect roi) {
		IplImage srcIpl = src.asIplImage().clone();
		IplImage cropped = cvCreateImage(cvSize(roi.width(), roi.height()),
				srcIpl.depth(), srcIpl.nChannels());

		cvSetImageROI(srcIpl, roi);
		cvCopy(srcIpl, cropped);
		return cropped.asCvMat();
	}
}
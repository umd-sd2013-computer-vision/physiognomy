package com.timetravellingtreasurechest.vision;

import java.util.ArrayList;
import java.util.List;

import com.googlecode.javacv.cpp.*;

import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import static com.googlecode.javacv.cpp.opencv_objdetect.*;

public class FacialFeatures {

	private List<FacialFeature> features = new ArrayList<FacialFeature>();
	
	private static final CvHaarClassifierCascade face_cascade = new CvHaarClassifierCascade(cvLoad("src\\haarcascades\\haarcascade_frontalface_alt.xml"));
	private static final CvHaarClassifierCascade leye_cascade = new CvHaarClassifierCascade(cvLoad("src\\haarcascades\\haarcascade_mcs_lefteye.xml"));
	private static final CvHaarClassifierCascade reye_cascade = new CvHaarClassifierCascade(cvLoad("src\\haarcascades\\haarcascade_mcs_righteye.xml"));
	private static final CvHaarClassifierCascade mouth_cascade = new CvHaarClassifierCascade(cvLoad("src\\haarcascades\\haarcascade_mcs_mouth.xml"));
	private static final CvHaarClassifierCascade nose_cascade = new CvHaarClassifierCascade(cvLoad("src\\haarcascades\\haarcascade_mcs_nose.xml"));;

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
	private float cheekWidth;

	public FacialFeatures(CvMat image) {
		this.image = image;

		if (findFeatures() == false) {
			System.out.println("error: no faces detected in given image");
			return;
		}
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

		// separate regions of interests to reduce search area
		CvRect leftFace = new CvRect(face.x(), face.y(), face.width() / 2, face.height());
		CvRect rightFace = new CvRect(face.x() + face.width() / 2, face.y(), face.width() / 2, face.height());
		CvRect lowerFace = new CvRect(face.x(), face.y() + face.height() / 2, face.width(), face.height() / 2);

		leye = detectFeature(leye_cascade, FacialFeatures.crop(gray_image, leftFace));
		reye = detectFeature(reye_cascade, FacialFeatures.crop(gray_image, rightFace));
		mouth = detectFeature(mouth_cascade, FacialFeatures.crop(gray_image, lowerFace));
		nose = detectFeature(nose_cascade, FacialFeatures.crop(gray_image, face));
		
		features.add(new FacialFeature(face, new CvPoint(0,0)));
		features.add(new FacialFeature(leye, new CvPoint(leftFace.x(), leftFace.y())));
		features.add(new FacialFeature(reye, new CvPoint(rightFace.x(), rightFace.y())));
		features.add(new FacialFeature(mouth, new CvPoint(lowerFace.x(), lowerFace.y())));
		features.add(new FacialFeature(nose, new CvPoint(face.x(), face.y())));

		return true;
	}

	// detect feature (indicated by casc parameter) from image
	// uses simple binary search to find a single object (with the minBoxes as
	// the search term)
	// returns CvRect object containing feature
	private static CvRect detectFeature(CvHaarClassifierCascade casc, CvMat in) {
		CvMemStorage storage = CvMemStorage.create();
		CvSeq detected = new CvSeq();

		for (int low = 3, high = 150, mid; detected.total() != 1 && low <= high;) {
			mid = (low + high) / 2;

			cvClearMemStorage(storage);
			detected = cvHaarDetectObjects((CvArr) in, casc, storage, 1.1, mid, opencv_objdetect.CV_HAAR_SCALE_IMAGE);

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
	private static CvMat crop(CvMat src, CvRect roi) {
		IplImage srcIpl = src.asIplImage().clone();
		IplImage cropped = cvCreateImage(cvSize(roi.width(), roi.height()), srcIpl.depth(), srcIpl.nChannels());

		cvSetImageROI(srcIpl, roi);
		cvCopy(srcIpl, cropped);
		return cropped.asCvMat();
	}
}
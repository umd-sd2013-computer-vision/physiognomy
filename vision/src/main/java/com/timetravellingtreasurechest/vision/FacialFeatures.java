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

	public double foreheadHeight;
	public double eyeSize;
	public double eyeSpace;
	public double noseSize;
	public double noseWidth;
	public double mouthSize;
	public double mouthWidth;

	public FacialFeatures(CvMat image) {
		if(image == null) return;
		this.image = image;

		if (findFeatures() == false || face.width() == 0 || face.height() == 0) {
			System.out.println("error: no faces detected in given image");
			return;
		}

		// forehead height relative to face height
		if (leye != null && reye != null) // if both eyes were found
			foreheadHeight = (((leye.y() + reye.y()) / 2.0) - face.y()) / face.height();
		else if (leye == null && reye != null) // left eye found but not right eye
			foreheadHeight = (reye.y() - face.y()) / (double) face.height();
		else if (reye == null && leye != null) // right eye found but not left one
			foreheadHeight = (leye.y() - face.y()) / (double) face.height();
		else // neither eye was found
			foreheadHeight = 0.0;

		// eye size (area) relative to face size (area)
		if (leye != null && reye != null)
			eyeSize = ((cvRectArea(leye) + cvRectArea(reye)) / 2.0) / (cvRectArea(face));
		else if (leye == null && reye != null)
			eyeSize = cvRectArea(reye) / cvRectArea(face);
		else if (reye == null && leye != null)
			eyeSize = cvRectArea(leye) / cvRectArea(face);
		else
			eyeSize = 0.0;		
			
		// eye spacing relative to face width
		if (leye != null && reye != null) // both eyes found
			eyeSpace = ((reye.x() + (reye.width() / 2.0)) - (leye.x() + (leye.width() / 2.0))) / face.width();
		else // if either eye was not found, we cannot find spacing
			eyeSpace = 0.0;		

		// nose size (area) relative to face size (area)
		noseSize = cvRectArea(nose) / cvRectArea(face);

		// nose width relative to face width
		noseWidth = (double) nose.width() / face.width();

		// mouth size (area) relative to face size (area)
		mouthSize = cvRectArea(mouth) / cvRectArea(face);

		// mouth width relative to face width
		mouthWidth = (double) mouth.width() / face.width();
	}

	public List<FacialFeature> getFeatures() {
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
		
		// realign rects from roi x,y coords -> image x,y coords
		// test for null first to ensure we arent trying to manipulate null objects (in the case that no feature was found)
		if (leye != null) {
			leye.x(leye.x() + leftFace.x());
			leye.y(leye.y() + leftFace.y());
		}
		
		if (reye != null) {
			reye.x(reye.x() + rightFace.x());
			reye.y(reye.y() + rightFace.y());
		}
		
		if (mouth != null) {
			mouth.x(mouth.x() + lowerFace.x());
			mouth.y(mouth.y() + lowerFace.y());
		}
		
		if (nose != null) {
			nose.x(nose.x() + face.x());
			nose.y(nose.y() + face.y());
		}
		
		features.add(new FacialFeature(face));
		features.add(new FacialFeature(leye));
		features.add(new FacialFeature(reye));
		features.add(new FacialFeature(mouth));
		features.add(new FacialFeature(nose));

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
	
	private static double cvRectArea(CvRect rect) {
		if (rect == null)
			return 0.0;
		return rect.width() * rect.height();
	}
}
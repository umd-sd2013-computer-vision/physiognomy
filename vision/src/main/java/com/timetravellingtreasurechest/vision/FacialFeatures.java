package com.timetravellingtreasurechest.vision;

import java.util.ArrayList;
import java.util.List;

import com.googlecode.javacv.cpp.*;
import com.googlecode.javacv.cpp.opencv_core.CvRect;
import com.googlecode.javacv.cpp.opencv_objdetect.CascadeClassifier;

import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import static com.googlecode.javacv.cpp.opencv_highgui.*;

public class FacialFeatures {
	
	// detect what features ?
	private static final boolean FACE = true;
	private static final boolean LEYE = true;
	private static final boolean REYE = true;
	private static final boolean MOUTH = true;
	private static final boolean NOSE = true;
	
	private static final boolean OUTPUT_IMAGE = true; // output test image to file including boxes, only for testing. not included in final project
	
	private static CascadeClassifier face_cascade;
	private static CascadeClassifier leye_cascade;
	private static CascadeClassifier reye_cascade;
	private static CascadeClassifier mouth_cascade;
	private static CascadeClassifier nose_cascade;
	
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
    
    public FacialFeatures(String imageFile) {
    	//System.loadLibrary(opencv_core.windowsx64Linkpath);
    	
    	// load haarcascades xml files, doing this here to avoid link errors
    	if (face_cascade == null)
    		face_cascade = new CascadeClassifier("src\\haarcascades\\haarcascade_frontalface_alt.xml");
    	if (leye_cascade == null)
    		leye_cascade = new CascadeClassifier("src\\haarcascades\\haarcascade_mcs_lefteye.xml");
    	if (reye_cascade == null)
    		reye_cascade = new CascadeClassifier("src\\haarcascades\\haarcascade_mcs_righteye.xml");
    	if (mouth_cascade == null)
    		mouth_cascade = new CascadeClassifier("src\\haarcascades\\haarcascade_mcs_mouth.xml");
    	if (nose_cascade == null)
    		nose_cascade = new CascadeClassifier("src\\haarcascades\\haarcascade_mcs_nose.xml");
    	
    	image = cvLoadImageM(imageFile, CV_LOAD_IMAGE_COLOR);
    	
    	if (image.size() == 0) {
	        System.out.println("error: cannot open "+ imageFile + "\n");
	        return;
    	}
    	
    	if (findFeatures() == false) {
    		System.out.println("error: no faces detected in " + imageFile + "\n");
    		return;
    	}
    	
    	//write image with boxes to file
    	if (OUTPUT_IMAGE)
    		opencv_highgui.cvSaveImage("cv_" + imageFile, image);
        
        extractFeatures();
    }
    
    private void extractFeatures() {
    	
    }
    
    private boolean findFeatures() {                 
        // opencv detection works 234234324x faster on grayscale images 
    	CvMat gray_image = CvMat.create(image.rows(), image.cols(),CV_8U,1);
    	cvCvtColor(image, gray_image, CV_RGB2GRAY );
        cvEqualizeHist(gray_image, gray_image); 
        
        opencv_highgui.cvSaveImage("out", gray_image.asIplImage());

        // detect face first to create roi to search for facial features, can greatly increase speed (esp for large images)
        face = detectFeature(face_cascade, gray_image, new CvSize(gray_image.cols()/5, gray_image.rows()/5));

        if (face == null)
        	return false;
        
        CvRect faceROI = new CvRect(face.x(), face.y(), face.width(), face.height());
        CvMat face_crop_image = gray_image.clone();
        cvSetImageROI(face_crop_image.asIplImage(), faceROI);
        //CvMat face_crop_image = new CvMat(gray_image, faceROI);
        
        // separate regions of interests to reduce search area
        CvRect upperFaceROI = new CvRect(face.x(),                face.y(),                 face.width(),   face.height()/2);
		CvRect lowerFaceROI = new CvRect(face.x(),                face.y() + face.height()/2, face.width(),   face.height()/2);
		CvRect leftFaceROI =  new CvRect(face.x(),                face.y(),                 face.width()/2, face.height());
		CvRect rightFaceROI = new CvRect(face.x() + face.width()/2, face.y(),                 face.width()/2, face.height());
		
		CvMat upper_face_crop_image = face_crop_image.clone();
		CvMat lower_face_crop_image = face_crop_image.clone();
		CvMat left_face_crop_image = face_crop_image.clone();
		CvMat right_face_crop_image = face_crop_image.clone();
		
		cvSetImageROI(upper_face_crop_image.asIplImage(), upperFaceROI);
		cvSetImageROI(lower_face_crop_image.asIplImage(), lowerFaceROI);
		cvSetImageROI(left_face_crop_image.asIplImage(), leftFaceROI);
		cvSetImageROI(right_face_crop_image.asIplImage(), rightFaceROI);
				
		/*CvMat upper_face_crop_image = new CvMat(gray_image, upperFaceROI);
		CvMat lower_face_crop_image = new CvMat(gray_image, lowerFaceROI);
		CvMat left_face_crop_image =  new CvMat(gray_image, leftFaceROI);
		CvMat right_face_crop_image = new CvMat(gray_image, rightFaceROI);*/
		
		if (LEYE)
		    leye = detectFeature(leye_cascade, left_face_crop_image, null);
		if (REYE)
		    reye = detectFeature(reye_cascade, right_face_crop_image, null);
		if (MOUTH)
		    mouth = detectFeature(mouth_cascade, lower_face_crop_image, null);
		if (NOSE)
		    nose = detectFeature(nose_cascade, face_crop_image, null);
		
		
		if (OUTPUT_IMAGE) {
			putRects(image, face, 0, 0); 							// face rectangle
			putRects(image, leye, face.x(), face.y()); 					// left eye rectangle
			putRects(image, reye, face.x() + face.width()/2, face.y()); 	// right eye rectangle
		    putRects(image, mouth, face.x(), face.y() + face.height()/2); // mouth rectangle
		    putRects(image, nose, face.x(), face.y()); 					// nose rectangle
		}
		
		return true;
    }
    
    // detect feature (indicated by casc parameter) from image
    // uses simple binary search to find a single object (with the minBoxes as the search term)
    // returns Rect object containing feature
    private CvRect detectFeature(CascadeClassifier casc, CvMat image,  CvSize min) {
    	//CvMatOfRect rects = new CvMatOfRect();
    	List<CvRect> detected = new ArrayList<CvRect>();    	
    	if (min == null)
    		min = new CvSize(1,1);
    	
        for (int low = 3, high = 150, mid; detected.size() != 1 && low <= high;) {
          mid = (low + high) / 2;
          casc.detectMultiScale((CvArr) image, (CvRect) detected, 1.1, mid, opencv_objdetect.CV_HAAR_SCALE_IMAGE, min, new CvSize(image.rows(),image.cols()));
          
          //detected = rects.toList();
          if (detected.size() == 0)
            high = mid - 1;
          else if (detected.size() > 1)
            low = mid + 1;
        }
        
        if (detected.size() == 0)
        	return null;
        return detected.get(0);
    }
    
    // draw rects on image relative to offsets given (for roi placement)
    // only used for testing (if OUTPUT_IMAGE is set), not for final project
    private void putRects(CvMat image, CvRect rect, int offset_x, int offset_y) {
    	if (rect == null)
    		return;
    	
    	CvPoint pt1 = new CvPoint(rect.x() + rect.width() + offset_x, rect.y() + rect.height() + offset_y);
	    CvPoint pt2 = new CvPoint(rect.x() + offset_x, rect.y() + offset_y);
	    
	    cvRectangle(image, pt1, pt2, new CvScalar(0,255,0,0),1,8,0);
	}
}
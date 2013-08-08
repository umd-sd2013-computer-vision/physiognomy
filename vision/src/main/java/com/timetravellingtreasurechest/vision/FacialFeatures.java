package com.timetravellingtreasurechest.vision;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;

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
	
	private Mat image;
	
	private Rect face;
	private Rect leye;
	private Rect reye;
	private Rect mouth;
	private Rect nose;	
	
    public static void main(String[] args) {   	
        for (int i = 0; i < args.length; i++) {
        	new FacialFeatures(args[i]);
        }
    }
    
    public FacialFeatures(String imageFile) {
    	System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    	
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
    	
    	image = Highgui.imread(imageFile, Highgui.CV_LOAD_IMAGE_COLOR);
    	
    	if (image.size().area() == 0) {
	        System.out.println("error: cannot open "+ imageFile + "\n");
	        return;
    	}
    	
    	if (findFeatures() == false) {
    		System.out.println("error: no faces detected in " + imageFile + "\n");
    		return;
    	}
    	
    	//write image with boxes to file
    	if (OUTPUT_IMAGE)
    		Highgui.imwrite("cv_" + imageFile, image);
        
        extractFeatures();
    }
    
    private void extractFeatures() {
    	
    }
    
    private boolean findFeatures() {                 
        // opencv detection works 234234324x faster on grayscale images
        Mat gray_image = new Mat();       
        Imgproc.cvtColor(image, gray_image, Imgproc.COLOR_RGB2GRAY);
        Imgproc.equalizeHist(gray_image, gray_image);        

        // detect face first to create roi to search for facial features, can greatly increase speed (esp for large images)
        face = detectFeature(face_cascade, gray_image, new Size(gray_image.cols()/5, gray_image.rows()/5));

        if (face == null)
        	return false;
        
        Rect faceROI = new Rect(face.x, face.y, face.width, face.height);
        Mat face_crop_image = new Mat(gray_image, faceROI);
	
		  // separate regions of interests to reduce search area
		Rect upperFaceROI = new Rect(face.x,                face.y,                 face.width,   face.height/2);
		Rect lowerFaceROI = new Rect(face.x,                face.y + face.height/2, face.width,   face.height/2);
		Rect leftFaceROI =  new Rect(face.x,                face.y,                 face.width/2, face.height);
		Rect rightFaceROI = new Rect(face.x + face.width/2, face.y,                 face.width/2, face.height);
			  
		Mat upper_face_crop_image = new Mat(gray_image, upperFaceROI);
		Mat lower_face_crop_image = new Mat(gray_image, lowerFaceROI);
		Mat left_face_crop_image =  new Mat(gray_image, leftFaceROI);
		Mat right_face_crop_image = new Mat(gray_image, rightFaceROI);
		
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
			putRects(image, leye, face.x, face.y); 					// left eye rectangle
			putRects(image, reye, face.x + face.width/2, face.y); 	// right eye rectangle
		    putRects(image, mouth, face.x, face.y + face.height/2); // mouth rectangle
		    putRects(image, nose, face.x, face.y); 					// nose rectangle
		}
		
		return true;
    }
    
    // detect feature (indicated by casc parameter) from image
    // uses simple binary search to find a single object (with the minBoxes as the search term)
    // returns Rect object containing feature
    private Rect detectFeature(CascadeClassifier casc, Mat image,  Size min) {
    	MatOfRect rects = new MatOfRect();
    	List<Rect> detected = new ArrayList<Rect>();
    	if (min == null)
    		min = new Size(1,1);
    	
        for (int low = 3, high = 150, mid; detected.size() != 1 && low <= high;) {
          mid = (low + high) / 2;
          casc.detectMultiScale(image, rects, 1.1, mid, Objdetect.CASCADE_SCALE_IMAGE, min, new Size(image.height(),image.width()));
          
          detected = rects.toList();
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
    private void putRects(Mat image, Rect rect, int offset_x, int offset_y) {
    	if (rect == null)
    		return;
    	
	    Point pt1 = new Point(rect.x + rect.width + offset_x, rect.y + rect.height + offset_y);
	    Point pt2 = new Point(rect.x + offset_x, rect.y + offset_y);
	    
	    Core.rectangle(image, pt1, pt2, new Scalar(0,255,0,0),1,8,0);
	}
}
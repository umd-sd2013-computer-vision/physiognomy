package com.timetravellingtreasurechest.vision;

import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_highgui.CV_LOAD_IMAGE_COLOR;
import static com.googlecode.javacv.cpp.opencv_highgui.cvLoadImageM;

import java.util.List;

import com.googlecode.javacv.CanvasFrame;
import com.googlecode.javacv.cpp.opencv_core.CvPoint;
import com.googlecode.javacv.cpp.opencv_core.CvScalar;
import com.timetravellingtreasurechest.features.Eyes;
import com.timetravellingtreasurechest.features.Face;
import com.timetravellingtreasurechest.features.FacialFeature;
import com.timetravellingtreasurechest.services.FacialFeatureService;
import com.timetravellingtreasurechest.services.ServiceServer;

public class App {
	public static void main(String[] args) {   	
		FacialFeatures current;

        for (int i = 0; i < args.length; i++) {
        	
        	CvMat image = cvLoadImageM(args[i], CV_LOAD_IMAGE_COLOR);
        	if (image.size() == 0) {
    			System.out.println("error: cannot open " + image + "\n");
    			continue;
        	}
        	
        	ServiceServer.setFacialFeatureService(new FacialFeatureService());
        	current = ServiceServer.getFacialFeatureService().getFeatures(image);
        	
    		displayFeature(current, current.getFace(), image);
    		displayFeature(current, current.getEyes(), image);
    		displayFeature(current, current.getMouth(), image);
    		displayFeature(current, current.getNose(), image);
        	 
        }
    }

	public static void displayFeature(FacialFeatures current, FacialFeature<?> f,CvMat image) {
		CvMat displayMat = new CvMat(image.clone());
		if(!Face.class.isInstance(f))
			f.drawBounds(displayMat, current.getFace());
		else
			f.drawBounds(displayMat);
		
		CvRect r = f.getFaceQuadrant(current.getFace());
		cvRectangle(displayMat, new CvPoint(r.x(), r.y()), new CvPoint(
				r.x() + r.width(), r.y() + r.height()),
				new CvScalar(255, 255, 255, 0), 1, 8, 0);
		if(Eyes.class.isInstance(f)) {
			r = ((Eyes)f).getFaceLQuadrant(current.getFace());
			System.out.println("Left eye search box: " + r.x() + ", " + r.y());
			cvRectangle(displayMat, new CvPoint(r.x(), r.y()), new CvPoint(
					r.x() + r.width(), r.y() + r.height()),
					new CvScalar(0, 255, 0, 0), 3, 8, 0);
			r = ((Eyes)f).getFaceRQuadrant(current.getFace());
			System.out.println("Right eye search box: " + r.x() + ", " + r.y());
			cvRectangle(displayMat, new CvPoint(r.x(), r.y()), new CvPoint(
					r.x() + r.width(), r.y() + r.height()),
					new CvScalar(0, 255, 0, 0), 3, 8, 0);
		}
		
		
		System.out.println("Displaying: " + f.toString());
		
		CanvasFrame canvas = new CanvasFrame("Face with boxes");
		canvas.showImage(displayMat.asIplImage());				
		try {
			canvas.waitKey();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		canvas.dispose();
		displayMat.deallocate();
	}
}

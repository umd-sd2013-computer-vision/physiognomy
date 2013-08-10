package com.timetravellingtreasurechest.vision;

import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_highgui.CV_LOAD_IMAGE_COLOR;
import static com.googlecode.javacv.cpp.opencv_highgui.cvLoadImageM;

import java.util.List;

import com.googlecode.javacv.CanvasFrame;
import com.googlecode.javacv.cpp.opencv_core.*;

public class App {
	public static void main(String[] args) {   	

        for (int i = 0; i < args.length; i++) {
        	CvMat image = cvLoadImageM(args[i], CV_LOAD_IMAGE_COLOR);
        	if (image.size() == 0) {
    			System.out.println("error: cannot open " + image + "\n");
    			return;
    		}
        	
        	List<FacialFeature> features = new FacialFeatures(image).extractFeatures();
        	
        	for(FacialFeature f : features) 
        		putRects(image, f.boundingBox, f.offset.x(), f.offset.y()); // face rectangle
        	
        	try {
        		CanvasFrame canvas = new CanvasFrame("Face with boxes");
				canvas.showImage(image.asIplImage());
				canvas.waitKey();
				canvas.dispose();
        	} catch (InterruptedException e) {
				e.printStackTrace();
			}
        	 
        }
    }
	
	// draw rects on image relative to offsets given (for roi placement)
	// only used for testing (if OUTPUT_IMAGE is set), not for final project
	public static void putRects(CvMat image, CvRect rect, int offset_x, int offset_y) {
		if (rect == null)
			return;

		CvPoint pt1 = new CvPoint(rect.x() + offset_x, rect.y() + offset_y);
		CvPoint pt2 = new CvPoint(rect.x() + rect.width() + offset_x, rect.y() + rect.height() + offset_y);

		cvRectangle(image, pt1, pt2, new CvScalar(0, 255, 0, 0), 1, 8, 0);
	}
}

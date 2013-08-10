package com.timetravellingtreasurechest.vision;

import static com.googlecode.javacv.cpp.opencv_core.cvLoad;
import static com.googlecode.javacv.cpp.opencv_highgui.CV_LOAD_IMAGE_COLOR;
import static com.googlecode.javacv.cpp.opencv_highgui.cvLoadImageM;

import java.util.List;

import com.googlecode.javacv.CanvasFrame;
import com.googlecode.javacv.cpp.opencv_core.CvMat;
import com.timetravellingtreasurechest.vision.FacialFeatures;

/**
 * Hello world!
 */
public class App {
	public static void main(String[] args) {   	

        for (int i = 0; i < args.length; i++) {
        	CvMat image = cvLoadImageM(args[i], CV_LOAD_IMAGE_COLOR);
        	if (image.size() == 0) {
    			System.out.println("error: cannot open " + image + "\n");
    			return;
    		}
        	
        	List<FacialFeature> features = new FacialFeatures(image).extractFeatures();
        	
        	for(FacialFeature f : features) {
        		FacialFeatures.putRects(image, f.boundingBox, f.offset.x, f.offset.y); // face rectangle
        	}
        	try {
        		CanvasFrame canvas = new CanvasFrame("Face with boxes");
				canvas.showImage(image.asIplImage());
				canvas.waitKey();
				canvas.dispose();
        	}
        	catch (InterruptedException e) {
				e.printStackTrace();
			}
        	 
        }
    }
}

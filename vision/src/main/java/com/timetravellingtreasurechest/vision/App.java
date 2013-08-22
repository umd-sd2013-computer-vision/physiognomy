package com.timetravellingtreasurechest.vision;

import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_highgui.CV_LOAD_IMAGE_COLOR;
import static com.googlecode.javacv.cpp.opencv_highgui.cvLoadImageM;

import com.googlecode.javacv.CanvasFrame;
import com.timetravellingtreasurechest.features.FacialFeature;
import com.timetravellingtreasurechest.report.ReportData;
import com.timetravellingtreasurechest.services.FacialFeatureService;
import com.timetravellingtreasurechest.services.ReportGeneratorService;
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
			ServiceServer.setReportGeneratorService(new ReportGeneratorService());

			current = ServiceServer.getFacialFeatureService()
					.getFeatures(image);
			image = ServiceServer.getFacialFeatureService().getResizedImage(
					image);
			if (current.getFace() == null) {
				System.out.println("No face found");
				continue;
			}

			ReportData report = ServiceServer.getReportGeneratorService()
					.getReport(current, image);
			view(report.getFacialFeatures().draw(report.getOriginalImage()));
			System.out.println(report.getReportText());
		}
	}
	private static void view(CvMat image) {
		CanvasFrame canvas = new CanvasFrame("Face with boxes");
		canvas.showImage(image.asIplImage());				
		try {
			canvas.waitKey();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		canvas.dispose();
	}

}

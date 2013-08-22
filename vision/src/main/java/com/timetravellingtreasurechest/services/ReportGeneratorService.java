package com.timetravellingtreasurechest.services;

import static com.googlecode.javacv.cpp.opencv_core.*;
import com.googlecode.javacv.CanvasFrame;
import com.googlecode.javacv.cpp.opencv_core.CvFont;
import com.googlecode.javacv.cpp.opencv_core.CvMat;
import com.timetravellingtreasurechest.report.ReportData;
import com.timetravellingtreasurechest.vision.FacialFeatures;

public class ReportGeneratorService implements IReportGeneratorService {
	
	@Override
	public ReportData getReport(FacialFeatures features, CvMat originalImage) {
		return new ReportData(features, originalImage);
	}

//	@Override
//	public void setReportFrame(Object frame) {
//		this.frame = (CanvasFrame) frame;
//	}
//
//	@Override
//	public Class<?> getReportFrameType() {
//		return CanvasFrame.class;
//	}
//
//	@Override
//	public void drawReport(ReportData data) {
//		CvMat useImage = data.getOriginalImage();
//		data.getFacialFeatures().draw(useImage);
//		
//		String test_text = data.getReportText();		
//		CvFont use_font = new CvFont(0,1.0,1);
//		cvPutText(useImage, test_text, new CvPoint(20,50), use_font, new CvScalar(0,255,0,0));
//		
//		
//		this.frame.showImage(useImage.asIplImage());				
//		try {
//			this.frame.waitKey();
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//	}

}

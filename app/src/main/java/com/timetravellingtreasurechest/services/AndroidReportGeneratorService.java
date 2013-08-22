package com.timetravellingtreasurechest.services;


import com.googlecode.javacv.cpp.opencv_core.CvMat;
import com.timetravellingtreasurechest.report.ReportData;
import com.timetravellingtreasurechest.vision.FacialFeatures;

public class AndroidReportGeneratorService implements IReportGeneratorService {

	@Override
	public ReportData getReport(FacialFeatures features, CvMat image) {
		return new ReportData(features, image);
	}

}

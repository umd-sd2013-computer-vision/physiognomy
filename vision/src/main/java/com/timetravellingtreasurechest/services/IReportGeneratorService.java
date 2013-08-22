package com.timetravellingtreasurechest.services;

import com.googlecode.javacv.cpp.opencv_core.CvMat;
import com.timetravellingtreasurechest.report.ReportData;
import com.timetravellingtreasurechest.vision.FacialFeatures;

public interface IReportGeneratorService {

	public ReportData getReport(FacialFeatures features, CvMat originalImage);

}

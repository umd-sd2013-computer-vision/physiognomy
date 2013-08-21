package com.timetravellingtreasurechest.report;

import com.timetravellingtreasurechest.vision.FacialFeatures;

public interface IReportGeneratorService {

	public ReportData getReport(FacialFeatures features);
	
}

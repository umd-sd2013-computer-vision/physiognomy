package com.timetravellingtreasurechest.report;

import com.timetravellingtreasurechest.vision.FacialFeatures;

public class ReportGeneratorService implements IReportGeneratorService {

	@Override
	public ReportData getReport(FacialFeatures features) {
		return new ReportData(features);
	}

}

package com.timetravellingtreasurechest.physiognomy.service;

import com.timetravellingtreasurechest.physiognomy.FacialFeatures;
import com.timetravellingtreasurechest.physiognomy.ReportData;

public class ReportGeneratorService implements IReportGeneratorService {

	@Override
	public ReportData getReport(FacialFeatures features) {
		return new ReportData();
	}

}

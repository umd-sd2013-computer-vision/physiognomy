package com.timetravellingtreasurechest.physiognomy.service;

import com.timetravellingtreasurechest.physiognomy.ReportData;
import com.timetravellingtreasurechest.physiognomy.FacialFeatures;

public interface IReportGeneratorService {

	public ReportData getReport(FacialFeatures features);
	
}

package com.timetravellingtreasurechest.services;

public class ServiceServer {
	private static IFacialFeatureService facialFeatureService;
	public static void setFacialFeatureService(IFacialFeatureService f) {
		facialFeatureService = f;
	}
	public static IFacialFeatureService getFacialFeatureService() {
		return facialFeatureService;
	}
	
	private static IReportGeneratorService reportGeneratorService;
	public static void setReportGeneratorService(IReportGeneratorService f) {
		reportGeneratorService = f;
	}
	public static IReportGeneratorService getReportGeneratorService() {
		return reportGeneratorService;
	}
}

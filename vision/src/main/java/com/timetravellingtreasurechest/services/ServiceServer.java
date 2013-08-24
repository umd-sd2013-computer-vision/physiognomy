package com.timetravellingtreasurechest.services;

import android.content.Context;

public class ServiceServer {
	private static IFacialFeatureService facialFeatureService;
	private static Context context;
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
	
	public static Context getAndroidContext() {
		return context;
	}
	
	public static void setAndroidContext(Context context) {
		ServiceServer.context = context;
	}
}

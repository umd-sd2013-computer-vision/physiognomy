package com.timetravellingtreasurechest.services;

import com.timetravellingtreasurechest.services.FacialFeatureService;

public class ServiceServer {
	private static IFacialFeatureService facialFeatureService;
	public static void setFacialFeatureService(IFacialFeatureService f) {
		facialFeatureService = f;
	}
	public static IFacialFeatureService getFacialFeatureService() {
		return facialFeatureService;
	}
}

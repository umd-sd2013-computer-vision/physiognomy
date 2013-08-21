package com.timetravellingtreasurechest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ReportData {
	
	private static final double AVG_FOREHEAD_HEIGHT = 0.2939684744147797;
	private static final double AVG_EYE_SIZE = 0.057461373502253284;
	private static final double AVG_EYE_SPACE = 0.39088657211005245;
	private static final double AVG_NOSE_SIZE = 0.08370801614796489;
	private static final double AVG_NOSE_HEIGHT = 0.260168510692947;
	private static final double AVG_NOSE_WIDTH = 0.3132768055566671;
	private static final double AVG_MOUTH_SIZE = 0.09756818299160343;
	private static final double AVG_MOUTH_HEIGHT = 0.23787354797703603;
	private static final double AVG_MOUTH_WIDTH = 0.3984773779768884;
	
	private static final String[] ABOVE_AVERAGE_FOREHEAD_HEIGHT = { 
		"above average forehead height 1. ",
		"above average forehead height 2. ",
		"above average forehead height 3. "};
	
	private static final String[] BELOW_AVERAGE_FOREHEAD_HEIGHT = { 
		"below average forehead height 1. ",
		"below average forehead height 2. ",
		"below average forehead height 3. "};
	
	
	private static final String[] ABOVE_AVERAGE_EYE_SIZE = { 
		"above average eye size 1. ",
		"above average eye size 2. ",
		"above average eye size 3. "};
	
	private static final String[] BELOW_AVERAGE_EYE_SIZE = { 
		"below average eye size 1. ",
		"below average eye size 2. ",
		"below average eye size 3. "};
	
	
	private static final String[] ABOVE_AVERAGE_EYE_SPACE = { 
		"above average eye space 1. ",
		"above average eye space 2. ",
		"above average eye space 3. "};
	
	private static final String[] BELOW_AVERAGE_EYE_SPACE = { 
		"below average eye space 1. ",
		"below average eye space 2. ",
		"below average eye space 3. "};
	
	
	private static final String[] ABOVE_AVERAGE_NOSE_SIZE = { 
		"above average nose size 1. ",
		"above average nose size 2. ",
		"above average nose size 3. "};
	
	private static final String[] BELOW_AVERAGE_NOSE_SIZE = { 
		"below average nose size 1. ",
		"below average nose size 2. ",
		"below average nose size 3. "};
	
	
	private static final String[] ABOVE_AVERAGE_NOSE_HEIGHT = { 
		"above average nose height 1. ",
		"above average nose height 2. ",
		"above average nose height 3. "};
	
	private static final String[] BELOW_AVERAGE_NOSE_HEIGHT = { 
		"below average nose height 1. ",
		"below average nose height 2. ",
		"below average nose height 3. "};
	
	
	private static final String[] ABOVE_AVERAGE_NOSE_WIDTH = { 
		"above average nose width 1. ",
		"above average nose width 2. ",
		"above average nose width 3. "};
	
	private static final String[] BELOW_AVERAGE_NOSE_WIDTH = { 
		"below average nose width 1. ",
		"below average nose width 2. ",
		"below average nose width 3. "};
	
	
	private static final String[] ABOVE_AVERAGE_MOUTH_SIZE = { 
		"above average mouth size 1. ",
		"above average mouth size 2. ",
		"above average mouth size 3. "};
	
	private static final String[] BELOW_AVERAGE_MOUTH_SIZE = { 
		"below average mouth size 1. ",
		"below average mouth size 2. ",
		"below average mouth size 3. "};
	
	
	private static final String[] ABOVE_AVERAGE_MOUTH_HEIGHT = { 
		"above average mouth height 1. ",
		"above average mouth height 2. ",
		"above average mouth height 3. "};
	
	private static final String[] BELOW_AVERAGE_MOUTH_HEIGHT = { 
		"below average mouth height 1. ",
		"below average mouth height 2. ",
		"below average mouth height 3. "};
	
	
	private static final String[] ABOVE_AVERAGE_MOUTH_WIDTH = { 
		"above average mouth width 1. ",
		"above average mouth width 2. ",
		"above average mouth width 3. "};
	
	private static final String[] BELOW_AVERAGE_MOUTH_WIDTH = { 
		"below average mouth width 1. ",
		"below average mouth width 2. ",
		"below average mouth width 3. "};
	
	
	private String report = "";
	
	public ReportData(FacialFeatures face) {
		List<ReportFeature> features = new ArrayList<ReportFeature>();
		
		features.add(new ReportFeature(face.foreheadHeight, AVG_FOREHEAD_HEIGHT, ABOVE_AVERAGE_FOREHEAD_HEIGHT, BELOW_AVERAGE_FOREHEAD_HEIGHT));
		features.add(new ReportFeature(face.eyeSize, AVG_EYE_SIZE, ABOVE_AVERAGE_EYE_SIZE, BELOW_AVERAGE_EYE_SIZE));
		features.add(new ReportFeature(face.eyeSpace, AVG_EYE_SPACE, ABOVE_AVERAGE_EYE_SPACE, BELOW_AVERAGE_EYE_SPACE));
		features.add(new ReportFeature(face.noseSize, AVG_NOSE_SIZE, ABOVE_AVERAGE_NOSE_SIZE, BELOW_AVERAGE_NOSE_SIZE));
		features.add(new ReportFeature(face.noseHeight, AVG_NOSE_HEIGHT, ABOVE_AVERAGE_NOSE_HEIGHT, BELOW_AVERAGE_NOSE_HEIGHT));
		features.add(new ReportFeature(face.noseWidth, AVG_NOSE_WIDTH, ABOVE_AVERAGE_NOSE_WIDTH, BELOW_AVERAGE_NOSE_WIDTH));
		features.add(new ReportFeature(face.mouthSize, AVG_MOUTH_SIZE, ABOVE_AVERAGE_MOUTH_SIZE, BELOW_AVERAGE_MOUTH_SIZE));
		features.add(new ReportFeature(face.mouthHeight, AVG_MOUTH_HEIGHT, ABOVE_AVERAGE_MOUTH_HEIGHT, BELOW_AVERAGE_MOUTH_HEIGHT));
		features.add(new ReportFeature(face.mouthWidth, AVG_MOUTH_WIDTH, ABOVE_AVERAGE_MOUTH_WIDTH, BELOW_AVERAGE_MOUTH_WIDTH));
		
		Collections.sort(features);
		
		for (int i = 0; i < 3; i++)
			report += features.get(i).getReportText();
	}
	
	public String getReportText() {
		return report;
	}
	
	private class ReportFeature implements Comparable<ReportFeature> {
		private double avgDifference;
		private String reportText;
		
		public ReportFeature(double featureVal, double avg, String[] aboveText, String[] belowText) {
			if (featureVal == 0.0) {
				avgDifference = 0.0;
				reportText = "";
				return;
			}
			
			avgDifference = Math.abs(featureVal - avg);
			
			if (featureVal > avg)
				reportText = aboveText[(int) (Math.random() * aboveText.length)];
			else
				reportText = belowText[(int) (Math.random() * belowText.length)];
		}
		
		public double getDifference() {
			return avgDifference;
		}
		
		public String getReportText() {
			return reportText;
		}
		
		public int compareTo(ReportFeature r) {
			// this returns values that will sort descending
			if (this.avgDifference > r.getDifference())
				return -1;
			else 
				return 1;
		}
	}
}

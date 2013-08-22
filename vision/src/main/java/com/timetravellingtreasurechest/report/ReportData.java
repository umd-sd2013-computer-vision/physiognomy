package com.timetravellingtreasurechest.report;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.googlecode.javacv.cpp.opencv_core.CvMat;
import com.timetravellingtreasurechest.features.Eyes;
import com.timetravellingtreasurechest.features.Face;
import com.timetravellingtreasurechest.features.FacialFeature;
import com.timetravellingtreasurechest.features.Mouth;
import com.timetravellingtreasurechest.features.Nose;
import com.timetravellingtreasurechest.services.ImageConverter;
import com.timetravellingtreasurechest.vision.FacialFeatures;

public class ReportData implements Serializable {
	
	private static final long serialVersionUID = 6300208314239945582L;
	
	private static final double AVG_FOREHEAD_HEIGHT = 0.2939684744147797;
	private static final double AVG_EYE_SIZE = 0.057461373502253284;
	private static final double AVG_EYE_SPACE = 0.39088657211005245;
	private static final double AVG_NOSE_SIZE = 0.08370801614796489;
	private static final double AVG_NOSE_HEIGHT = 0.260168510692947;
	private static final double AVG_NOSE_WIDTH = 0.3132768055566671;
	private static final double AVG_MOUTH_SIZE = 0.09756818299160343;
	//private static final double AVG_MOUTH_HEIGHT = 0.23787354797703603;
	private static final double AVG_MOUTH_WIDTH = 0.3984773779768884;
	
	private static final String[] ABOVE_AVERAGE_FOREHEAD_HEIGHT = { 
		"spend lots of time in your head, thinking, imagining and analyzing. ",
		"are a strong minded person with gifts of perserverence and egalitarianism. "};
	
	private static final String[] BELOW_AVERAGE_FOREHEAD_HEIGHT = { 
		"don't like to make quick decisions. ",
		"use your down-to-earth sensibilities to deliberate long and hard and come to a fair and equitable conclusion. ",
		"take a while to form an opinion, but once formed it's hard to budge you into another point of view. "};
	
	
	private static final String[] ABOVE_AVERAGE_EYE_SIZE = { 
		"carry healthier emotional and spiritual balance. ",
		"possess intelligence, imagination, and a desire for attention. "};
	
	private static final String[] BELOW_AVERAGE_EYE_SIZE = { 
		"are more conservative with your emotions. ",
		"become impatient with the big emotions of others. ",
		"possess a curious nature within your own small circle of interests. "};
	
	
	private static final String[] ABOVE_AVERAGE_EYE_SPACE = { 
		"see the big picture of any situation. ",
		"are blessed with a good memory. ",
		"are open to all sorts of new ideas and events, especially romantic ones. ",
		"are naive which can add to your charms or get you in trouble. ",
		"hold exceptionally high expectations for yourself. "};
	
	private static final String[] BELOW_AVERAGE_EYE_SPACE = { 
		"are social, entertaining, and a great host with extended circles of friends. ",
		"have a gift of focus, particularily with minute details. ",
		"carry natural analytical abilities and a strong ego which enable you to be a powerful leader. "};
	
	
	private static final String[] ABOVE_AVERAGE_NOSE_SIZE = { 
		"require a strong desire to work independently. ",
		"accept great power, ego, drive, and leadership. "};
	
	private static final String[] BELOW_AVERAGE_NOSE_SIZE = { 
		"are best in groups activities where you use your creative imagination and spontaneity. ",
		"think of others and are always willing to help out for the greater good. ",
		"love to play and have to love what you do in order to work hard. "};
	
	
	private static final String[] ABOVE_AVERAGE_NOSE_HEIGHT = { 
		"own a good nose for business and common sense. ",
		"hold a healthy sense of ambition and great instincts. ",
		"possess leadership skills that people respond to positively. "};
	
	private static final String[] BELOW_AVERAGE_NOSE_HEIGHT = { 
		"are loyal and compassionate, but generally short on drive and ambition. ",
		"don't have the emotional stamina to thrive in competitive conditions. ",
		"are often wary and overwhelmed by those who have strong egos and drive. "};
	
	
	private static final String[] ABOVE_AVERAGE_NOSE_WIDTH = { 
		"tend to be a mega-expert, honing in on the most detailed, esoteric specifics of your chosen interests. ",
		"do not spend money frivolously or frequently. "};
	
	private static final String[] BELOW_AVERAGE_NOSE_WIDTH = { 
		"are refined, elegant, and delicate, with impeccable taste. ",
		"can appear to be self-centered. ",
		"would rather be left alone to sort things out. "};
	
	
	private static final String[] ABOVE_AVERAGE_MOUTH_SIZE = { 
		"have a large personality. ",
		"own a natural generosity that pretty much guarantees you a large circle of friends. "};
	
	private static final String[] BELOW_AVERAGE_MOUTH_SIZE = { 
		"have difficulty relating to another's point of view or circumstances. ",
		"are hypersensitive about not getting what you perceive as your due. ",
		"tend to have an overinflated sense of your worth so you constantly imagine yourself as victims who need to fight for your rights. ",
		"are not embarrassed to thrust yourself into the center of any attention. "};
	
	
//	private static final String[] ABOVE_AVERAGE_MOUTH_HEIGHT = { 
//		"above average mouth height 1. ",
//		"above average mouth height 2. ",
//		"above average mouth height 3. "};
//	
//	private static final String[] BELOW_AVERAGE_MOUTH_HEIGHT = { 
//		"below average mouth height 1. ",
//		"below average mouth height 2. ",
//		"below average mouth height 3. "};
	
	
	private static final String[] ABOVE_AVERAGE_MOUTH_WIDTH = { 
		"laugh a lot, showing off your naturally great smile. ",
		"are a lively, sensual person who needs to be showered with love. "};
	
	private static final String[] BELOW_AVERAGE_MOUTH_WIDTH = { 
		"are more practical than passionate. ",
		"have fragile health that may contribute to your settled lifestyle. "};
	
	private static final String[] SECOND_SENTANCE_PREFIX = {
		"Also, you ",
		"Addtionally, you ",
		"People agree that you ",
		"Further, you ",
		"Likewise, you ",
		"Still, "
	};
	
	private String report = "You ";
	private byte[] image;
	private FacialFeatures features; 
	
	public ReportData(FacialFeatures f, CvMat image) {
		this.image = ImageConverter.cvMatToByteArray(image);
		this.features = f;
		List<ReportFeature> features = new ArrayList<ReportFeature>();
		Face face = f.getFace();
		Eyes eyes = f.getEyes();
		Nose nose = f.getNose();
		Mouth mouth = f.getMouth();
		features.add(new ReportFeature(f.getForeheadHeight(), AVG_FOREHEAD_HEIGHT, ABOVE_AVERAGE_FOREHEAD_HEIGHT, BELOW_AVERAGE_FOREHEAD_HEIGHT));
		features.add(new ReportFeature(FacialFeature.getRelativeArea(face, eyes), AVG_EYE_SIZE, ABOVE_AVERAGE_EYE_SIZE, BELOW_AVERAGE_EYE_SIZE));
		features.add(new ReportFeature(f.getEyeSpacing(), AVG_EYE_SPACE, ABOVE_AVERAGE_EYE_SPACE, BELOW_AVERAGE_EYE_SPACE));
		features.add(new ReportFeature(FacialFeature.getRelativeArea(face, nose), AVG_NOSE_SIZE, ABOVE_AVERAGE_NOSE_SIZE, BELOW_AVERAGE_NOSE_SIZE));
		features.add(new ReportFeature(FacialFeature.getRelativeHeight(face, nose), AVG_NOSE_HEIGHT, ABOVE_AVERAGE_NOSE_HEIGHT, BELOW_AVERAGE_NOSE_HEIGHT));
		features.add(new ReportFeature(FacialFeature.getRelativeWidth(face, nose), AVG_NOSE_WIDTH, ABOVE_AVERAGE_NOSE_WIDTH, BELOW_AVERAGE_NOSE_WIDTH));
		features.add(new ReportFeature(FacialFeature.getRelativeArea(face, mouth), AVG_MOUTH_SIZE, ABOVE_AVERAGE_MOUTH_SIZE, BELOW_AVERAGE_MOUTH_SIZE));
		//features.add(new ReportFeature(FacialFeature.getRelativeHeight(face, mouth), AVG_MOUTH_HEIGHT, ABOVE_AVERAGE_MOUTH_HEIGHT, BELOW_AVERAGE_MOUTH_HEIGHT));
		features.add(new ReportFeature(FacialFeature.getRelativeWidth(face, mouth), AVG_MOUTH_WIDTH, ABOVE_AVERAGE_MOUTH_WIDTH, BELOW_AVERAGE_MOUTH_WIDTH));
		
		Collections.sort(features);
		
		for (int i = 0; i < 2; i++)
			report += features.get(i).getReportText();
	}
	
	public String getReportText() {
		return report;
	}
	
	public FacialFeatures getFacialFeatures() {
		return features;
	}
	
	public CvMat getOriginalImage() {
		return ImageConverter.byteArrayToCvMat(image).clone();
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
		
		public int compareTo(ReportFeature o) {
			// this returns values that will sort descending
			if (this.avgDifference > ((ReportFeature) o).getDifference())
				return -1;
			else 
				return 1;
		}
	}
}

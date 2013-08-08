package com.timetravellingtreasurechest.vision;

import com.timetravellingtreasurechest.vision.FacialFeatures;

/**
 * Hello world!
 */
public class App {
	public static void main(String[] args) {   	
		System.out.println("Hello World!");
		
        for (int i = 0; i < args.length; i++) {
        	new FacialFeatures(args[i]);
        }
    }
}

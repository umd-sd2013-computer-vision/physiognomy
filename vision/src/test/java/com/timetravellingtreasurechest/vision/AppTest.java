package com.timetravellingtreasurechest.vision;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Unit test for simple App.
 */
@RunWith(JUnit4.class)
public class AppTest {
    @Test
    public void testApp() {
        App.main(new String[]{"images/face.jpg"});
    }
    
    @Test
    public void testOpenCv() {
    	FacialFeatures.testOpenCv();
    }
}
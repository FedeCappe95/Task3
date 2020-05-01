package com.lsmsdb.task3.ui;

/**
 * Graphic component to show a loading splash screen at the boot phase of the
 * application
 */
public class SplashScreen extends SplashScreenBase {
    
    public void setJob(String str) {
        labelJob.setText(str);
    }
    
}

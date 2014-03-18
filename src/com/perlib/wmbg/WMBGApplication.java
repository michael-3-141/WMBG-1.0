package com.perlib.wmbg;

import android.app.Application;

public class WMBGApplication extends Application {
	private static WMBGApplication instance; 
	
	public WMBGApplication() {
		super();
		WMBGApplication.instance = this;
	}

	public static WMBGApplication getInstance() {
		return instance;
	}
}

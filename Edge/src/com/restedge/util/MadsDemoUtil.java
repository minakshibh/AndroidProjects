package com.restedge.util;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.adgoji.mraid.adview.AdViewCore;
import com.adgoji.mraid.adview.AdViewCore.OnAdDownload;


public class MadsDemoUtil {
	
	public static void setupUI(final AdViewCore adView, final Activity act) {
		
	
		
		adView.setOnAdDownload(new OnAdDownload() {
			
			public void noad(AdViewCore sender) {
				System.out.println("sender ");
			}
			
			public void error(AdViewCore sender, String error) {}
			
			public void end(AdViewCore sender) {
				((ViewGroup)act.findViewById(Window.ID_ANDROID_CONTENT)).forceLayout();
				System.out.println("end ");
			}
			
			public void begin(AdViewCore sender) {
				System.out.println("Begin ");
			}
		});
		
	}
}

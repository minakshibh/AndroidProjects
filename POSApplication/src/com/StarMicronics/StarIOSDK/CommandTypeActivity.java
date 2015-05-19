package com.StarMicronics.StarIOSDK;

import android.app.*;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.example.posapplication.R;

public class CommandTypeActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.commandtype);
        
        Log.i("tag", "enter command type activity");
        
        TextView lineText = (TextView)findViewById(R.id.AboutLineModeTextView);
        lineText.setTextColor(Color.BLUE);
        
        TextView rasterText = (TextView)findViewById(R.id.AboutRasterModeTextView);
        rasterText.setTextColor(Color.BLUE);
        
    }
    
    public void onClickLineModeButton(View view) {
    	if (!checkClick.isClickEvent()) return;
    	
    	Intent myIntent = new Intent(this, StarIOSDKPOSPrinterLineModeActivity.class);
    	startActivityFromChild(this, myIntent, 0);
    }
    
    public void onClickLineModeDotPrinterButton(View view) {
    	if (!checkClick.isClickEvent()) return;
    	
    	Intent myIntent = new Intent(this, StarIOSDKDotPOSPrinterLineModeActivity.class);
    	startActivityFromChild(this, myIntent, 0);
    }
    
    public void onClickRasterModeButton(View view) {
    	if (!checkClick.isClickEvent()) return;
    	
    	Intent myIntent = new Intent(this, StarIOSDKPOSPrinterRasterModeActivity.class);
    	startActivityFromChild(this, myIntent, 0);
    	Log.i("tag", "go to Star Raster Mode Activity");
    }
    
    public void onClickAboutLineMode(View view) {
    	if (!checkClick.isClickEvent()) return;
    	
         Intent myIntent = new Intent(this, LineModeHelpActivity.class);
         startActivityFromChild(this, myIntent, 0);
    }
    
    public void onClickAboutRasterMode(View view) {
    	if (!checkClick.isClickEvent()) return;
    	
        Intent myIntent = new Intent(this, RasterModeHelpActivity.class);
        startActivityFromChild(this, myIntent, 0);
    }
    
    public void onClickAboutImpactDotMatrix(View view) {
    	if (!checkClick.isClickEvent()) return;
    	
         Intent myIntent = new Intent(this, LineModeforImpactDotMatrixHelpActivity.class);
         startActivityFromChild(this, myIntent, 0);
    }
}
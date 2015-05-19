package com.rapidride;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

public class ViewRides_Activity extends Activity{
	TextView tv_title;
	Typeface typeFace;
	WebView webview;
	String url="";
	 protected void onCreate(Bundle savedInstanceState) {
	    	requestWindowFeature(Window.FEATURE_NO_TITLE);
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.viewrides_activity);
	        
	   tv_title=(TextView)findViewById(R.id.textView_rapid_title);
	   typeFace=Typeface.createFromAsset(getAssets(),"MYRIADPROREGULAR.ttf");
	   tv_title.setTypeface(typeFace);
	   
	   webview=(WebView)findViewById(R.id.webView);
	    url= getIntent().getStringExtra("url");
	    System.err.println("url=="+url);
	    startWebView(url);
	  
	   }


	 private void startWebView(String url) {
        
	        /**Create new web view Client to show progress dialog **/
	              
		 webview.setWebViewClient(new WebViewClient() {      
	           // ProgressDialog progressDialog;
	          
	            /** If you will not use this method url links are open in new brower not in web view **/
	            public boolean shouldOverrideUrlLoading(WebView view, String url) {              
	                view.loadUrl(url);
	                return true;
	            }
	        
	            /**  Show loader on url load **/
	            public void onLoadResource (WebView view, String url) {
	            /*    if (progressDialog == null) {
	                    // in standard case YourActivity.this
	                    progressDialog = new ProgressDialog(ViewRides_Activity.this);
//	                    progressDialog.setTitle("Loading");
	                    progressDialog.setMessage("Please wait...");
	                    progressDialog.show();*/
	                //}
	            }
	            public void onPageFinished(WebView view, String url) {
	                try{
	            /*    if (progressDialog.isShowing()) {
	                    progressDialog.dismiss();
	                    progressDialog.setCancelable(false);
	                    progressDialog = null;
	                }*/
	                }catch(Exception exception){
	                    exception.printStackTrace();
	                }
	            }
	             
	        }); 
	          
	        /*** Javascript inabled on web view  ***/
		 webview.getSettings().setJavaScriptEnabled(true); 
	         
	        // Other webview options
	        
//		 wb_help.getSettings().setLoadWithOverviewMode(true);
//		 wb_help.getSettings().setUseWideViewPort(true);
//		 wb_help.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
//		 wb_help.setScrollbarFadingEnabled(false);
//		 wb_help.getSettings().setBuiltInZoomControls(true);
	        
	         
	        /*
	         String summary = "<html><body>You scored <b>192</b> points.</body></html>";
	         webview.loadData(summary, "text/html", null); 
	         */
	         
	        //Load url in webview
		 webview.loadUrl(url);
	          
	          
	    }
//	 @Override
	public void onBackPressed() {
		if(!(getIntent().getStringExtra("drivervr")==null))
			{
		Intent i=new Intent(ViewRides_Activity.this,DriverView_Activity.class);
		finish();
		startActivity(i);
				}
		if(!(getIntent().getStringExtra("ridervr")==null))
		{
			Intent i=new Intent(ViewRides_Activity.this,MapView_Activity.class);
			finish();
			startActivity(i);
			}
	}
	        
	 }
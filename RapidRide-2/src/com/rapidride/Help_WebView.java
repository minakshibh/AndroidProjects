package com.rapidride;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class Help_WebView extends Activity {
	
	WebView wb_help;
	Typeface typeFace;
	SharedPreferences prefs;
	String url="";
protected void onCreate(Bundle savedInstanceState) {
	requestWindowFeature(Window.FEATURE_NO_TITLE);
    super.onCreate(savedInstanceState);
    setContentView(R.layout.help_webview);
    
	        
	    wb_help=(WebView)findViewById(R.id.webView_help);
	    url= getIntent().getStringExtra("url");
	    System.err.println("url=="+url);
	    startWebView(url);
	  
	   }


	 private void startWebView(String url) {
         
	        /**Create new web view Client to show progress dialog **/
	              
		 wb_help.setWebViewClient(new WebViewClient() {      
	           // ProgressDialog progressDialog;
	          
	            /** If you will not use this method url links are open in new brower not in web view **/
	            public boolean shouldOverrideUrlLoading(WebView view, String url) {              
	                view.loadUrl(url);
	                return true;
	            }
	        
	            /**  Show loader on url load **/
	            public void onLoadResource (WebView view, String url) {
	          /*      if (progressDialog == null) {
	                    // in standard case YourActivity.this
	                    progressDialog = new ProgressDialog(Help_WebView.this);
//	                    progressDialog.setTitle("Loading");
	                  //  progressDialog.setCancelable(false);
	                    progressDialog.setMessage("Please wait...");
	                    progressDialog.show();
	                }*/
	            }
	            public void onPageFinished(WebView view, String url) {
	            /*    try{
	                if (progressDialog.isShowing()) {
	                    progressDialog.dismiss();
	                    progressDialog = null;
	                }
	                }catch(Exception exception){
	                    exception.printStackTrace();
	                }*/
	            }
	             
	        }); 
	          
	        /*** Javascript inabled on web view  ***/
		 wb_help.getSettings().setJavaScriptEnabled(true); 
	         
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
		 wb_help.loadUrl(url);
	          
	          
	    }
//	 @Override
//	public void onBackPressed() {
//	Intent i=new Intent(Help_WebView.this,Help_Activity.class);
//	startActivity(i);
//	}
	        
	 	}

package com.print;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

public class EmailComposer {

	Context mcontext;
	public EmailComposer(Context context) {
		mcontext =context;
	}
	public void showIntent()
	{
		String filepath = "POS";
    	File pdfFile=new File(Environment.getExternalStorageDirectory()
		         +File.separator
		         +filepath //folder name
		         +File.separator
		         +com.pos.retail.ReceiptGenerate.TransactionId+".pdf");
		
		Intent intent = new Intent(Intent.ACTION_SEND ,Uri.parse("mailto:")); // it's not ACTION_SEND
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_SUBJECT, "Card Set ");
		intent.putExtra(Intent.EXTRA_TEXT, "");
		intent.putExtra(Intent.EXTRA_STREAM,Uri.fromFile(pdfFile));
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // this will make such that when user returns to your app, your app is displayed, instead of the email app.
		mcontext.startActivity(intent);
		
	}
}

package com.StarMicronics.StarIOSDK;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.example.posapplication.R;
import com.print.Utility;
public class imagePrintingActivity extends Activity implements OnItemSelectedListener {
//SharedPreferences imgPathPref;
Spinner spinner_paper_width;
String imagePath="";
int paperWidth=0;
String source="";
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.printingimage);

        
        for (String Value : Utility.pathList()) {
//			  Log.i("tag","Receieve Path:"+Value);
			  imagePath=Value;
		  }
        printText();
        
//        Spinner spinner_Image = (Spinner)findViewById(R.id.spinner_Image);
//        SpinnerAdapter ad = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, new String[] {"Receipt"});
//    
//        spinner_Image.setAdapter(ad);
//        spinner_Image.setVisibility(View.GONE);
//        spinner_Image.setOnItemSelectedListener(this);
        
//        spinner_paper_width = (Spinner)findViewById(R.id.spinner_paper_width);
//        SpinnerAdapter ad_paper_width = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, new String[] {"2inch", "3inch", "4inch"});
//        spinner_paper_width.setAdapter(ad_paper_width);
//        spinner_paper_width.setOnItemSelectedListener(this);
//        
//        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//        
//        CheckBox checkbox_bitImage_CompressAPI = (CheckBox)findViewById(R.id.checkbox_bitImage_CompressAPI);
//        CheckBox checkbox_bitImage_pageMode = (CheckBox)findViewById(R.id.checkbox_bitImage_pageMode);
//        
//		if(PrinterTypeActivity.getPortSettings().toUpperCase().equals("MINI") || PrinterTypeActivity.getPortSettings().toUpperCase().equals("MINI;L"))
//		{
//			//
//		}
//		else
//		{
////			TextView textView_paper_width = (TextView)findViewById(R.id.textView_paper_width);
////			checkbox_bitImage_pageMode.setVisibility(View.GONE);
////			textView_paper_width.setVisibility(View.GONE);
////	  	    spinner_paper_width.setVisibility(View.GONE);
//		}
//		
//		checkbox_bitImage_CompressAPI.setChecked(true);
//		checkbox_bitImage_pageMode.setChecked(true);
	}

	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
//		paperWidth = 576;
		paperWidth = 576;
		
		switch(spinner_paper_width.getSelectedItemPosition())
		{
		    case 0:
		    	paperWidth = 384;    // 2inch (384 dot)
			    break;
		    case 1:
		    	paperWidth = 576;    // 3inch (576 dot)
			    break;
		    case 2:
		    	paperWidth = 832;    // 4inch (832 dot)
			    break;
		}

	}

	public void onNothingSelected(AdapterView<?> arg0) {
		//
	}
	
	public void printText()
	{
	
		/*boolean compressionEnable = false;
		boolean pageModeEnable = false;
		paperWidth=576;*/
		
		boolean compressionEnable = false;
		  boolean pageModeEnable = false;
		  paperWidth=576;
//		  paperWidth = 700;
		
//        CheckBox checkbox_bitImage_CompressAPI = (CheckBox)findViewById(R.id.checkbox_bitImage_CompressAPI);
//		if(checkbox_bitImage_CompressAPI.isChecked() == true)
//		{
//			compressionEnable = true;
//		}
//
//        CheckBox checkbox_bitImage_pageMode = (CheckBox)findViewById(R.id.checkbox_bitImage_pageMode);
//		if(checkbox_bitImage_pageMode.isChecked() == true)
//		{
//			pageModeEnable = true;
//		}
		
		String portName = PrinterTypeActivity.getPortName();
		String portSettings = PrinterTypeActivity.getPortSettings();
		
//		for (String Value : Utility.pathList()) {
//			  Log.i("tag","Receieve Path:"+Value);
//			  source=Value;
			  
		if(portSettings.toUpperCase().equals("MINI") || portSettings.toUpperCase().equals("MINI;L"))
		{
			MiniPrinterFunctions.PrintBitmapImage(this, portName, portSettings,Utility.pathList(), paperWidth, compressionEnable, pageModeEnable);
//			try{
//				Thread.sleep(2000);
//			}catch(Exception e)
//			{
//				
//			}
			
		}
		else
		{
			PrinterFunctions.PrintBitmapImage(this, portName, portSettings, Utility.pathList(), paperWidth, compressionEnable);
//			try{
//				Thread.sleep(2000);
//			}catch(Exception e)
//			{
//				
//			}
		}
		
//		}
	}
	
	public void Help(View view)
	{
		if (!checkClick.isClickEvent()) return;
		String helpString = "";
		String portSettings = PrinterTypeActivity.getPortSettings();
		if(portSettings.toUpperCase().equals("MINI") || portSettings.toUpperCase().equals("MINI;L"))
		{
			helpString = "<UnderlineTitle>Define Bit Image</UnderlineTitle><br/><br/>" +
					"<Code>ASCII:</Code> <CodeDef>ESC X 4 <StandardItalic>x y d1...dk</StandardItalic></CodeDef><br/>" +
					"<Code>Hex:</Code> <CodeDef>1B 58 34 <StandardItalic>x y d1...dk</StandardItalic></CodeDef><br/><br/>" +
					"<rightMov>x</rightMov> <rightMov_NOI>Width of the image divided by 8</rightMov_NOI><br/>" +
					"<rightMov>y</rightMov> <rightMov_NOI>Vertical number of dots to be printed.  This value shouldn't exceed 24.  If you need to print an image taller than 24 then you should use this command multiple times</rightMov_NOI><br/><br/><br/><br/><br/><br/>" +
					"<rightMov>d1...dk</rightMov> <rightMov_NOI2>The dots that should be printed.  When all vertical dots are printed the head moves horizontally to the next vertical set of dots</rightMov_NOI2><br/><br/><br/><br/><br/><br/>" +
					"<UnderlineTitle>Print Bit Image</UnderlineTitle><br/><br/>" +
					"<Code>ASCII:</Code> <CodeDef>ESC X 2 <StandardItalic>y</StandardItalic></CodeDef><br/>" +
					"<Code>Hex:</Code> <CodeDef>1B 58 32<StandardItalics>y</StandardItalics></CodeDef><br/><br/>" +
					"<rightMov>y</rightMov> <rightMov_NOI>The value y must be the same value that was used int eh ESC X 4 command for define a bit image</rightMov_NOI><br/><br/><br/><br/>" +
					"Note: The command ESC X 2 must be used after each usage of ESC X 4 inorder to print images";
		}
		else
		{
			helpString = "<UnderlineTitle>Enter Raster Mode</UnderlineTitle><br/><br/>" +
                			"<Code>ASCII:</Code> <CodeDef>ESC * r A</CodeDef><br/>" +
                			"<Code>Hex:</Code> <CodeDef>1B 2A 72 41</CodeDef><br/><br/>" +
                			"<UnderlineTitle>Initiallize raster mode</UnderlineTitle><br/>" +
                			"<Code>ASCII:</Code> <CodeDef>ESC * r R</CodeDef><br/>" +
                			"<Code>Hex:</Code> <CodeDef>1B 2A 72 52</CodeDef><br/><br/>" +
                			"<UnderlineTitle>Set Raster EOT mode</UnderlineTitle><br/><br/>" +
                			"<Code>ASCII:</Code> <CodeDef>ESC * r E <StandardItalic>n</StandardItalic> NUL</CodeDef><br/>" +
                			"<Code>Hex:</Code> <CodeDef>1B 2A 72 45 <StandardItalic>n</StandardItalic> 00</CodeDef><br/>" +
                			"<div class=\"div-tableCut\">" +
                			"<div class=\"div-table-rowCut\">" +
                				"<div class=\"div-table-colRaster\"><center>n</center></div>" +
                				"<div class=\"div-table-colRaster\"><center>FormFeed</center></div>" +
                				"<div class=\"div-table-colRaster\"><center>Cut Feed</center></div>" +
                				"<div class=\"div-table-colRaster\"><center>Cutter</center></div>" +
                				"<div class=\"div-table-colRaster\"><center>Presenter</center></div>" +
                			"</div>" +
                			"<div class=\"div-table-rowCut\">" +
                				"<div class=\"div-table-colRaster\"><center>0</center></div>" +
                				"<div class=\"div-table-colRaster\"><center>Default</center></div>" +
                				"<div class=\"div-table-colRaster\"><center>Default</center></div>" +
                				"<div class=\"div-table-colRaster\"><center>Default</center></div>" +
                				"<div class=\"div-table-colRaster\"><center>Default</center></div>" +
                			"</div>" +
                			"<div class=\"div-table-rowCut\">" +
                				"<div class=\"div-table-colRaster\"><center>1</center></div>" +
                				"<div class=\"div-table-colRaster\"><center>&#x25CB;</center></div>" +
                				"<div class=\"div-table-colRaster\"><center>-</center></div>" +
                				"<div class=\"div-table-colRaster\"><center>-</center></div>" +
                				"<div class=\"div-table-colRaster\"><center>-</center></div>" +
                			"</div>" +
                			"<div class=\"div-table-rowCut\">" +
                				"<div class=\"div-table-colRaster\"><center>2</center></div>" +
                				"<div class=\"div-table-colRaster\"><center>&#x25CB;</center></div>" +
                				"<div class=\"div-table-colRaster\"><center>&#x25CB;</center></div>" +
                				"<div class=\"div-table-colRaster\"><center>-</center></div>" +
                				"<div class=\"div-table-colRaster\"><center>-</center></div>" +
                			"</div>" +
                			"<div class=\"div-table-rowCut\">" +
                				"<div class=\"div-table-colRaster\"><center>3</center></div>" +
                				"<div class=\"div-table-colRaster\"><center>&#x25CB;</center></div>" +
                				"<div class=\"div-table-colRaster\"><center>TearBar</center></div>" +
                				"<div class=\"div-table-colRaster\"><center>-</center></div>" +
                				"<div class=\"div-table-colRaster\"><center>-</center></div>" +
                			"</div>" +
                			"<div class=\"div-table-rowCut\">" +
                				"<div class=\"div-table-colRaster\"><center>8</center></div>" +
                				"<div class=\"div-table-colRaster\"><center>&#x25CB;</center></div>" +
                				"<div class=\"div-table-colRaster\"><center>-</center></div>" +
                				"<div class=\"div-table-colRaster\"><center>Full Cut</center></div>" +
                				"<div class=\"div-table-colRaster\"><center>-</center></div>" +
                			"</div>" +
                			"<div class=\"div-table-rowCut\">" +
                				"<div class=\"div-table-colRaster\"><center>9</center></div>" +
                				"<div class=\"div-table-colRaster\"><center>&#x25CB;</center></div>" +
                				"<div class=\"div-table-colRaster\"><center>&#x25CB;</center></div>" +
                				"<div class=\"div-table-colRaster\"><center>Full Cut</center></div>" +
                				"<div class=\"div-table-colRaster\"><center>-</center></div>" +
                			"</div>" +
                			"<div class=\"div-table-rowCut\">" +
                				"<div class=\"div-table-colRaster\"><center>12</center></div>" +
                				"<div class=\"div-table-colRaster\"><center>&#x25CB;</center></div>" +
                				"<div class=\"div-table-colRaster\"><center>-</center></div>" +
                				"<div class=\"div-table-colRaster\"><center>Partial Cut</center></div>" +
                				"<div class=\"div-table-colRaster\"><center>-</center></div>" +
                			"</div>" +
                			"<div class=\"div-table-rowCut\">" +
                				"<div class=\"div-table-colRaster\"><center>13</center></div>" +
                				"<div class=\"div-table-colRaster\"><center>&#x25CB;</center></div>" +
                				"<div class=\"div-table-colRaster\"><center>&#x25CB;</center></div>" +
                				"<div class=\"div-table-colRaster\"><center>Partial Cut</center></div>" +
                				"<div class=\"div-table-colRaster\"><center>-</center></div>" +
                			"</div>" +
                			"<div class=\"div-table-rowCut\">" +
                				"<div class=\"div-table-colRaster\"><center>36</center></div>" +
                				"<div class=\"div-table-colRaster\"><center>&#x25CB;</center></div>" +
                				"<div class=\"div-table-colRaster\"><center>-</center></div>" +
                				"<div class=\"div-table-colRaster\"><center>Full Cut</center></div>" +
                				"<div class=\"div-table-colRaster\"><center>Eject</center></div>" +
                			"</div>" +
                			"<div class=\"div-table-rowCut\">" +
                				"<div class=\"div-table-colRaster\"><center>37</center></div>" +
                				"<div class=\"div-table-colRaster\"><center>&#x25CB;</center></div>" +
                				"<div class=\"div-table-colRaster\"><center>&#x25CB;</center></div>" +
                				"<div class=\"div-table-colRaster\"><center>Full Cut</center></div>" +
                				"<div class=\"div-table-colRaster\"><center>Eject</center></div>" +
                			"</div>" +
                			"</div><br/><br/>" +
                			"<UnderlineTitle>Set Raster FF mode</UnderlineTitle><br/><br/>" +
                			"<Code>ASCII:</Code> <CodeDef>ESC * r F <StandardItalic>n</StandardItalic> NUL</CodeDef><br/>" +
                			"<Code>Hex:</Code> <CodeDef>1B 2A 72 46 <StandardItalic>n</StandardItalic> 00</CodeDef><br/>" +
                			"<div class=\"div-tableCut\">" +
                				"<div class=\"div-table-rowCut\">" +
                				"<div class=\"div-table-colRaster\"><center>n</center></div>" +
                				"<div class=\"div-table-colRaster\"><center>FormFeed</center></div>" +
                				"<div class=\"div-table-colRaster\"><center>Cut Feed</center></div>" +
                				"<div class=\"div-table-colRaster\"><center>Cutter</center></div>" +
                				"<div class=\"div-table-colRaster\"><center>Presenter</center></div>" +
                			"</div>" +
                			"<div class=\"div-table-rowCut\">" +
                				"<div class=\"div-table-colRaster\"><center>0</center></div>" +
                				"<div class=\"div-table-colRaster\"><center>Default</center></div>" +
                				"<div class=\"div-table-colRaster\"><center>Default</center></div>" +
                				"<div class=\"div-table-colRaster\"><center>Default</center></div>" +
                				"<div class=\"div-table-colRaster\"><center>Default</center></div>"	+
                			"</div>" +
                			"<div class=\"div-table-rowCut\">" +
                				"<div class=\"div-table-colRaster\"><center>1</center></div>" +
                				"<div class=\"div-table-colRaster\"><center>&#x25CB;</center></div>" +
                				"<div class=\"div-table-colRaster\"><center>-</center></div>" +
                				"<div class=\"div-table-colRaster\"><center>-</center></div>" +
                				"<div class=\"div-table-colRaster\"><center>-</center></div>" +
                			"</div>" +
                			"<div class=\"div-table-rowCut\">" +
                				"<div class=\"div-table-colRaster\"><center>2</center></div>" +
                				"<div class=\"div-table-colRaster\"><center>&#x25CB;</center></div>" +
                				"<div class=\"div-table-colRaster\"><center>&#x25CB;</center></div>" +
                				"<div class=\"div-table-colRaster\"><center>-</center></div>" +
                				"<div class=\"div-table-colRaster\"><center>-</center></div>" +
                			"</div>" +
                			"<div class=\"div-table-rowCut\">" +
                				"<div class=\"div-table-colRaster\"><center>3</center></div>" +
                				"<div class=\"div-table-colRaster\"><center>&#x25CB;</center></div>" +
                				"<div class=\"div-table-colRaster\"><center>TearBar</center></div>" +
                				"<div class=\"div-table-colRaster\"><center>-</center></div>" +
                				"<div class=\"div-table-colRaster\"><center>-</center></div>" +
                			"</div>" +
                			"<div class=\"div-table-rowCut\">" +
                				"<div class=\"div-table-colRaster\"><center>8</center></div>" +
                				"<div class=\"div-table-colRaster\"><center>&#x25CB;</center></div>" +
                				"<div class=\"div-table-colRaster\"><center>-</center></div>" +
                				"<div class=\"div-table-colRaster\"><center>Full Cut</center></div>" +
                				"<div class=\"div-table-colRaster\"><center>-</center></div>" +
                			"</div>" +
                			"<div class=\"div-table-rowCut\">" +
                				"<div class=\"div-table-colRaster\"><center>9</center></div>" +
                				"<div class=\"div-table-colRaster\"><center>&#x25CB;</center></div>" +
                				"<div class=\"div-table-colRaster\"><center>&#x25CB;</center></div>" +
                				"<div class=\"div-table-colRaster\"><center>Full Cut</center></div>" +
                				"<div class=\"div-table-colRaster\"><center>-</center></div>" +
                			"</div>" +
                			"<div class=\"div-table-rowCut\">" +
                				"<div class=\"div-table-colRaster\"><center>12</center></div>" +
                				"<div class=\"div-table-colRaster\"><center>&#x25CB;</center></div>" +
                				"<div class=\"div-table-colRaster\"><center>-</center></div>" +
                				"<div class=\"div-table-colRaster\"><center>Partial Cut</center></div>" +
                				"<div class=\"div-table-colRaster\"><center>-</center></div>" +
                			"</div>" +
                			"<div class=\"div-table-rowCut\">" +
                				"<div class=\"div-table-colRaster\"><center>13</center></div>" +
                				"<div class=\"div-table-colRaster\"><center>&#x25CB;</center></div>" +
                				"<div class=\"div-table-colRaster\"><center>&#x25CB;</center></div>" +
                				"<div class=\"div-table-colRaster\"><center>Partial Cut</center></div>" +
                				"<div class=\"div-table-colRaster\"><center>-</center></div>" +
                			"</div>" +
                			"<div class=\"div-table-rowCut\">" +
                				"<div class=\"div-table-colRaster\"><center>36</center></div>" +
                				"<div class=\"div-table-colRaster\"><center>&#x25CB;</center></div>" +
                				"<div class=\"div-table-colRaster\"><center>-</center></div>" +
                				"<div class=\"div-table-colRaster\"><center>Full Cut</center></div>" +
                				"<div class=\"div-table-colRaster\"><center>Eject</center></div>" +
                			"</div>" +
                			"<div class=\"div-table-rowCut\">" +
                				"<div class=\"div-table-colRaster\"><center>37</center></div>" +
                				"<div class=\"div-table-colRaster\"><center>&#x25CB;</center></div>" +
                				"<div class=\"div-table-colRaster\"><center>&#x25CB;</center></div>" +
                				"<div class=\"div-table-colRaster\"><center>Full Cut</center></div>" +
                				"<div class=\"div-table-colRaster\"><center>Eject</center></div>" +
                			"</div>" +
                			"</div><br/><br/>" +
                			"<UnderlineTitle>Set raster page length</UnderlineTitle><br/>" +
                			"<Code>ASCII:</Code> <CodeDef>ESC * r P <StandardItalic>n</StandardItalic> NUL</CodeDef><br/>" +
                			"<Code>Hex:</Code> <CodeDef>1B 2A 72 50 <StandardItalic>n</StandardItalic> NUL</CodeDef><br/><br/>" +
                			"<rightMov>0 = Continuous print mode (no page length setting)</rightMov><br/><br/>" +
                			"<rightMov>1&#60;n = Specify page length</rightMov><br/><br/>" +
                			"<UnderlineTitle>Set raster print quality</UnderlineTitle><br/>" +
                			"<Code>ASCII:</Code> <CodeDef>ESC * r Q <StandardItalic>n</StandardItalic> NUL</CodeDef><br/>" +
                			"<Code>Hex:</Code> <CodeDef>1B 2A 72 51 <StandardItalic>n</StandardItalic> 00</CodeDef><br/><br/>" +
                			"<rightMov>0 = Specify high speed printing</rightMov><br/>" +
                			"<rightMov>1 = Normal print quality</rightMov><br/>" +
                			"<rightMov>2 = High print quality</rightMov><br/><br/>" +
                			"<UnderlineTitle>Set raster left margin</UnderlineTitle><br/><br/>" +
                			"<Code>ASCII:</Code> <CodeDef>ESC * r m l <StandardItalic>n</StandardItalic> NUL</CodeDef><br/>" +
                			"<Code>Hex:</Code> <CodeDef>1B 2A 72 6D 6C <StandardItalic>n</StandardItalic> 00</CodeDef><br/><br/>" +
                			"<UnderlineTitle>Send raster data (auto line feed)</UnderlineTitle><br/><br/>" +
                			"<Code>ASCII:</Code> <CodeDef>b <StandardItalic>n1 n2 d1 d2 ... dk</StandardItalic></CodeDef><br/>" +
                			"<Code>Hex:</Code> <CodeDef>62 <StandardItalic>n1 n2 d1 d2 ... dk</StandardItalic></CodeDef><br/><br/>" +
                			"<rightMov>n1 = (image width / 8) Mod 256</rightMov><br/>" +
                			"<rightMov>n2 = image width / 256</rightMov><br/>" +
                			"<rightMov>k = n1 + n2 * 256</rightMov><br/>" +
                			"* Each byte send in d1 ... dk represents 8 horizontal bits.  The values n1 and n2 tell the printer how many byte are sent with d1 ... dk.  The printer automatically feeds when the last value for d1 ... dk is sent<br/><br/>" +
                			"<UnderlineTitle>Quit raster mode</UnderlineTitle><br/></br>" +
                			"<Code>ASCII:</Code> <CodeDef>ESC * r B</CodeDef><br/>" +
                			"<Code>Hex:</Code> <CodeDef>1B 2A 72 42</CodeDef><br/><br/>" +
                			"* This command automatically executes a EOT(cut) command before quiting.  Use the set EOT command to change the action of this command.";
		}
		helpMessage.SetMessage(helpString);
 
		Intent myIntent = new Intent(this, helpMessage.class);
		startActivityFromChild(this, myIntent, 0);
	}
}

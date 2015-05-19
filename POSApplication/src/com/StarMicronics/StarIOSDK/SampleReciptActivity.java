package com.StarMicronics.StarIOSDK;

import android.app.*;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.TextView;
import com.example.posapplication.R;
public class SampleReciptActivity extends Activity {
	
	private Context me = this;
	private Intent intent;
	private String strPrintArea = "";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.language);
        
        Log.i("tag", "enter in sample reciept");
          	
        intent = getIntent();
        
        if(intent.getIntExtra("PRINTERTYPE", 1) == 2){ // RasterMode
        	TextView titleSBCSText = (TextView)findViewById(R.id.text_SBCS);
        	TextView titleDBCSText = (TextView)findViewById(R.id.text_DBSC);
        	TextView titleDBCSDescriptionText = (TextView)findViewById(R.id.text_DBSC_Description);
        	titleSBCSText.setVisibility(View.GONE);
        	titleDBCSText.setVisibility(View.GONE);
        	titleDBCSDescriptionText.setVisibility(View.GONE);

        	setTitle("StarIOSDK - Raster Mode Samples");
        }
        else if((intent.getIntExtra("PRINTERTYPE", 1) == 1)){ //Thermal LineMode
        	TextView rasterDescriptionText = (TextView)findViewById(R.id.text_Raster_Description);
        	rasterDescriptionText.setVisibility(View.GONE);
        	
        	setTitle("StarIOSDK - Line Mode Samples");
        }
        else if(intent.getIntExtra("PRINTERTYPE", 1) == 0){ //DotPrinter
        	TextView rasterDescriptionText = (TextView)findViewById(R.id.text_Raster_Description);
        	rasterDescriptionText.setVisibility(View.GONE);
        	
        	setTitle("StarIOSDK - Impact Dot Matrix Printer Samples");
        }
        else if(intent.getIntExtra("PRINTERTYPE", 1) == 3){ //Portable Printer
        	TextView rasterDescriptionText = (TextView)findViewById(R.id.text_Raster_Description);
        	rasterDescriptionText.setVisibility(View.GONE);
        	
        	TextView DBCSDescriptionText = (TextView)findViewById(R.id.text_DBSC_Description);
        	DBCSDescriptionText.setText("These samples will require the correct DBCS character set to be loaded.");

        	
        	
      	    Button SimplifiedChineseButton = (Button)findViewById(R.id.button_SimplifiedChinese);
        	SimplifiedChineseButton.setVisibility(View.GONE);

        	
        	setTitle("StarIOSDK - Portable Printer Samples");
        }
    }
    
    public void English(View view)
    {
    	Log.i("tag", "select English Mode");
    	
    	if (!checkClick.isClickEvent()) return;
		
    	switch( intent.getIntExtra("PRINTERTYPE", 1)){
    	    case 0://Dot Printer
    		    PrinterFunctions.PrintSampleReceiptbyDotPrinter(me, PrinterTypeActivity.getPortName(), PrinterTypeActivity.getPortSettings());	
    		    break;
    		
    	    case 1://Thermal Line Mode
            default:
	            final String item_list[] = new String[] {
	            		getResources().getString(R.string.printArea3inch),
	            		getResources().getString(R.string.printArea4inch), 
	                    };
	            
	            strPrintArea = getResources().getString(R.string.printArea3inch);
	            
	            Builder printableAreaDialog = new AlertDialog.Builder(this);
	            printableAreaDialog.setIcon(android.R.drawable.checkbox_on_background);
	            printableAreaDialog.setTitle("Printable Area List");
	            printableAreaDialog.setCancelable(false);
	            printableAreaDialog.setSingleChoiceItems(item_list, 0, new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int whichButton) {
	                	strPrintArea = item_list[whichButton];
	                }
	            });
	            printableAreaDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int whichButton) {
	                	((AlertDialog)dialog).getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
	                	((AlertDialog)dialog).getButton(DialogInterface.BUTTON_NEGATIVE).setEnabled(false);
		            		
	                	String commandType  = "Line";
	                	
	                	PrinterFunctions.PrintSampleReceipt(me, PrinterTypeActivity.getPortName(), PrinterTypeActivity.getPortSettings(), commandType, getResources(), strPrintArea);
	                }
	            });
	            printableAreaDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int whichButton) {
	                }
	            });
	            printableAreaDialog.show();
    		
    		    break;
    		
	    	case 2://Thermal Raster Mode
	    		Log.i("tag", "going to raster mode");
	    		
	            final String item_listRaster[] = new String[] {
	            		getResources().getString(R.string.printArea3inch),
	            		getResources().getString(R.string.printArea4inch), 
	                    };
	            
	            strPrintArea = getResources().getString(R.string.printArea3inch);
	            
	            Builder printableAreaDialogRaster = new AlertDialog.Builder(this);
	            printableAreaDialogRaster.setIcon(android.R.drawable.checkbox_on_background);
	            printableAreaDialogRaster.setTitle("Printable Area List");
	            printableAreaDialogRaster.setCancelable(false);
	            printableAreaDialogRaster.setSingleChoiceItems(item_listRaster, 0, new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int whichButton) {
	                	strPrintArea = item_listRaster[whichButton];
	                }
	            });
	            printableAreaDialogRaster.setPositiveButton("OK", new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int whichButton) {
	              	    ((AlertDialog)dialog).getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
	              	    ((AlertDialog)dialog).getButton(DialogInterface.BUTTON_NEGATIVE).setEnabled(false);
	                	String commandType  = "Raster";
	                	Log.i("tag", "call print sample Receipt");
	                	PrinterFunctions.PrintSampleReceipt(me, PrinterTypeActivity.getPortName(), PrinterTypeActivity.getPortSettings(), commandType, getResources(), strPrintArea);
	                }
	            });
	            printableAreaDialogRaster.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int whichButton) {
	                    //
	                }
	            });
	            printableAreaDialogRaster.show();
	    		break;
	    		
	    	case 3://Mobile Printer  
	            final String item_Mobilelist[] = new String[] {
        		    getResources().getString(R.string.printArea2inch),
        		    getResources().getString(R.string.printArea3inch),
        		    getResources().getString(R.string.printArea4inch),
                    };
        
                strPrintArea = getResources().getString(R.string.printArea2inch);
        
                Builder printableAreaDialogMobile = new AlertDialog.Builder(this);
                printableAreaDialogMobile.setIcon(android.R.drawable.checkbox_on_background);
                printableAreaDialogMobile.setTitle("Printable Area List");
                printableAreaDialogMobile.setCancelable(false);
                printableAreaDialogMobile.setSingleChoiceItems(item_Mobilelist, 0, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
            	        strPrintArea = item_Mobilelist[whichButton];
                    }
                });
                
                printableAreaDialogMobile.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
            	        ((AlertDialog)dialog).getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
                        ((AlertDialog)dialog).getButton(DialogInterface.BUTTON_NEGATIVE).setEnabled(false);
            	
            	        MiniPrinterFunctions.PrintSampleReceipt(me, PrinterTypeActivity.getPortName(), PrinterTypeActivity.getPortSettings(), strPrintArea);
                    }
                });
                printableAreaDialogMobile.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                });
                printableAreaDialogMobile.show();
	    		break;
    		
    		
    	}
    	
		
    }
    
    public void Japanese(View view)
    {
    	
    	if (!checkClick.isClickEvent()) return;
		
    	switch( intent.getIntExtra("PRINTERTYPE", 1)){
    	    case 0://Dot Printer
    	    	PrinterFunctions.PrintSampleReceiptJpbyDotPrinter(me, PrinterTypeActivity.getPortName(), PrinterTypeActivity.getPortSettings());	
    		    break;
    		
    	    case 1://Thermal Line Mode
            default:
	            final String item_list[] = new String[] {
	            		getResources().getString(R.string.printArea3inch),
	            		getResources().getString(R.string.printArea4inch), 
	                    };
	            
	            strPrintArea = getResources().getString(R.string.printArea3inch);
	            
	            Builder printableAreaDialog = new AlertDialog.Builder(this);
	            printableAreaDialog.setIcon(android.R.drawable.checkbox_on_background);
	            printableAreaDialog.setTitle("Printable Area List");
	            printableAreaDialog.setCancelable(false);
	            printableAreaDialog.setSingleChoiceItems(item_list, 0, new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int whichButton) {
	                	strPrintArea = item_list[whichButton];
	                }
	            });
	            printableAreaDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int whichButton) {
	                	((AlertDialog)dialog).getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
	                	((AlertDialog)dialog).getButton(DialogInterface.BUTTON_NEGATIVE).setEnabled(false);
		            		
	                	String commandType  = "Line";
	                	PrinterFunctions.PrintSampleReceiptJp(me, PrinterTypeActivity.getPortName(), PrinterTypeActivity.getPortSettings(), commandType, strPrintArea);
	                }
	            });
	            printableAreaDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int whichButton) {
	                }
	            });
	            printableAreaDialog.show();
    		
    		    break;
    		
	    	case 2://Thermal Raster Mode
	            final String item_listRaster[] = new String[] {
	            		getResources().getString(R.string.printArea3inch),
	            		getResources().getString(R.string.printArea4inch), 
	                    };
	            
	            strPrintArea = getResources().getString(R.string.printArea3inch);
	            
	            Builder printableAreaDialogRaster = new AlertDialog.Builder(this);
	            printableAreaDialogRaster.setIcon(android.R.drawable.checkbox_on_background);
	            printableAreaDialogRaster.setTitle("Printable Area List");
	            printableAreaDialogRaster.setCancelable(false);
	            printableAreaDialogRaster.setSingleChoiceItems(item_listRaster, 0, new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int whichButton) {
	                	strPrintArea = item_listRaster[whichButton];
	                }
	            });
	            printableAreaDialogRaster.setPositiveButton("OK", new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int whichButton) {
	              	    ((AlertDialog)dialog).getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
	              	    ((AlertDialog)dialog).getButton(DialogInterface.BUTTON_NEGATIVE).setEnabled(false);
	                	String commandType  = "Raster";
	                	
	                	PrinterFunctions.PrintSampleReceiptJp(me, PrinterTypeActivity.getPortName(), PrinterTypeActivity.getPortSettings(), commandType, strPrintArea);
	                }
	            });
	            printableAreaDialogRaster.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int whichButton) {
	                    //
	                }
	            });
	            printableAreaDialogRaster.show();
	    		break;
	    		
	    	case 3://Mobile Printer
	            final String item_Mobilelist[] = new String[] {
	        		    getResources().getString(R.string.printArea2inch),
	        		    getResources().getString(R.string.printArea3inch),
	        		    getResources().getString(R.string.printArea4inch),
	                    };
	        
	                strPrintArea = getResources().getString(R.string.printArea2inch);
	        
	                Builder printableAreaDialogMobile = new AlertDialog.Builder(this);
	                printableAreaDialogMobile.setIcon(android.R.drawable.checkbox_on_background);
	                printableAreaDialogMobile.setTitle("Printable Area List");
	                printableAreaDialogMobile.setCancelable(false);
	                printableAreaDialogMobile.setSingleChoiceItems(item_Mobilelist, 0, new DialogInterface.OnClickListener() {
	                    public void onClick(DialogInterface dialog, int whichButton) {
	            	        strPrintArea = item_Mobilelist[whichButton];
	                    }
	                });
	                
	                printableAreaDialogMobile.setPositiveButton("OK", new DialogInterface.OnClickListener() {
	                    public void onClick(DialogInterface dialog, int whichButton) {
	            	        ((AlertDialog)dialog).getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
	                        ((AlertDialog)dialog).getButton(DialogInterface.BUTTON_NEGATIVE).setEnabled(false);
	            	
	            	        MiniPrinterFunctions.PrintSampleReceiptJp(me, PrinterTypeActivity.getPortName(), PrinterTypeActivity.getPortSettings(), strPrintArea);
	                    }
	                });
	                printableAreaDialogMobile.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	                    public void onClick(DialogInterface dialog, int whichButton) {
	                    }
	                });
	                printableAreaDialogMobile.show();
	    		break;
    		
    		
    	}


    }
    
    public void SimplifiedChinese(View view)
    {
    	
    	if (!checkClick.isClickEvent()) return;
		
    	switch( intent.getIntExtra("PRINTERTYPE", 1)){
    	    case 0://Dot Printer
    	    	PrinterFunctions.PrintSampleReceiptCHSbyDotPrinter(me, PrinterTypeActivity.getPortName(), PrinterTypeActivity.getPortSettings());
    		    break;
    		
    	    case 1://Thermal Line Mode
            default:
	            final String item_list[] = new String[] {
	            		getResources().getString(R.string.printArea3inch),
	            		getResources().getString(R.string.printArea4inch), 
	                    };
	            
	            strPrintArea = getResources().getString(R.string.printArea3inch);
	            
	            Builder printableAreaDialog = new AlertDialog.Builder(this);
	            printableAreaDialog.setIcon(android.R.drawable.checkbox_on_background);
	            printableAreaDialog.setTitle("Printable Area List");
	            printableAreaDialog.setCancelable(false);
	            printableAreaDialog.setSingleChoiceItems(item_list, 0, new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int whichButton) {
	                	strPrintArea = item_list[whichButton];
	                }
	            });
	            printableAreaDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int whichButton) {
	                	((AlertDialog)dialog).getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
	                	((AlertDialog)dialog).getButton(DialogInterface.BUTTON_NEGATIVE).setEnabled(false);
		            		
	                	String commandType  = "Line";
	                	PrinterFunctions.PrintSampleReceiptCHS(me, PrinterTypeActivity.getPortName(), PrinterTypeActivity.getPortSettings(), commandType, strPrintArea);
	                }
	            });
	            printableAreaDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int whichButton) {
	                }
	            });
	            printableAreaDialog.show();
    		
    		    break;
    		
	    	case 2://Thermal Raster Mode
	            final String item_listRaster[] = new String[] {
	            		getResources().getString(R.string.printArea3inch),
	            		getResources().getString(R.string.printArea4inch), 
	                    };
	            
	            strPrintArea = getResources().getString(R.string.printArea3inch);
	            
	            Builder printableAreaDialogRaster = new AlertDialog.Builder(this);
	            printableAreaDialogRaster.setIcon(android.R.drawable.checkbox_on_background);
	            printableAreaDialogRaster.setTitle("Printable Area List");
	            printableAreaDialogRaster.setCancelable(false);
	            printableAreaDialogRaster.setSingleChoiceItems(item_listRaster, 0, new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int whichButton) {
	                	strPrintArea = item_listRaster[whichButton];
	                }
	            });
	            printableAreaDialogRaster.setPositiveButton("OK", new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int whichButton) {
	              	    ((AlertDialog)dialog).getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
	              	    ((AlertDialog)dialog).getButton(DialogInterface.BUTTON_NEGATIVE).setEnabled(false);
	                	String commandType  = "Raster";
	                	
	                	PrinterFunctions.PrintSampleReceiptCHS(me, PrinterTypeActivity.getPortName(), PrinterTypeActivity.getPortSettings(), commandType, strPrintArea);
	                }
	            });
	            printableAreaDialogRaster.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int whichButton) {
	                    //
	                }
	            });
	            printableAreaDialogRaster.show();
	    		break;
	    		
	    	case 3://Mobile Printer

	    		break;
    		
    		
    	}
    	
    	
    }

    public void TraditionalChinese(View view)
    {
    	//TODO:
    	if (!checkClick.isClickEvent()) return;
      	switch( intent.getIntExtra("PRINTERTYPE", 1)){
	    case 0://Dot Printer
	    	PrinterFunctions.PrintSampleReceiptCHTbyDotPrinter(me, PrinterTypeActivity.getPortName(), PrinterTypeActivity.getPortSettings());
		    break;
		
	    case 1://Thermal Line Mode
        default:
                	//String commandType  = "Line";
                	//String strPrintArea = "3inch (78mm)";
                	PrinterFunctions.PrintSampleReceiptCHT(me, PrinterTypeActivity.getPortName(), PrinterTypeActivity.getPortSettings(), "Line", getResources(), "3inch (78mm)");
		
		    break;
		
    	case 2://Thermal Raster Mode
            final String item_listRaster[] = new String[] {
            		getResources().getString(R.string.printArea3inch),
            		getResources().getString(R.string.printArea4inch), 
                    };
            
            strPrintArea = getResources().getString(R.string.printArea3inch);
            
            Builder printableAreaDialogRaster = new AlertDialog.Builder(this);
            printableAreaDialogRaster.setIcon(android.R.drawable.checkbox_on_background);
            printableAreaDialogRaster.setTitle("Printable Area List");
            printableAreaDialogRaster.setCancelable(false);
            printableAreaDialogRaster.setSingleChoiceItems(item_listRaster, 0, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                	strPrintArea = item_listRaster[whichButton];
                }
            });
            printableAreaDialogRaster.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
              	    ((AlertDialog)dialog).getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
              	    ((AlertDialog)dialog).getButton(DialogInterface.BUTTON_NEGATIVE).setEnabled(false);
                	String commandType  = "Raster";
                	
                	PrinterFunctions.PrintSampleReceiptCHT(me, PrinterTypeActivity.getPortName(), PrinterTypeActivity.getPortSettings(), commandType, getResources(), strPrintArea);
                }
            });
            printableAreaDialogRaster.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    //
                }
            });
            printableAreaDialogRaster.show();
    		break;
    		
    	case 3://Mobile Printer
                strPrintArea = getResources().getString(R.string.printArea3inch);
       	        MiniPrinterFunctions.PrintSampleReceiptCHT(me, PrinterTypeActivity.getPortName(), PrinterTypeActivity.getPortSettings(), strPrintArea);
    		break;
	    }
    	
    }

      
}
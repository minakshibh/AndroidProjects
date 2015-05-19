package com.StarMicronics.StarIOSDK;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.example.posapplication.R;
import com.StarMicronics.StarIOSDK.RasterDocument.RasPageEndMode;
import com.StarMicronics.StarIOSDK.RasterDocument.RasSpeed;
import com.StarMicronics.StarIOSDK.RasterDocument.RasTopMargin;
import com.starmicronics.stario.PortInfo;
import com.starmicronics.stario.StarIOPort;
import com.starmicronics.stario.StarIOPortException;
import com.starmicronics.stario.StarPrinterStatus;

import android.app.*;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.View;
import android.widget.*;
import android.view.WindowManager.LayoutParams;


public class DKAirCashActivity extends Activity{

	private Context me = this;
	private String strInterface = "";
	private String strPrintArea = "";
	private PrintRecieptThread printthread = null;
	private static int printableArea = 576;    // for raster data
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.dkaircash);     
        EditText portNameField       = (EditText)findViewById(R.id.editText_PortName);
        EditText drawerportNameField = (EditText)findViewById(R.id.editText_DrawerPortName);      
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        portNameField.setText(pref.getString("printerportName", "TCP:192.168.192.45"));
        drawerportNameField.setText(pref.getString("drawerportName", "TCP:192.168.192.10"));
       
  	    InitializeComponent();
    	//setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    private void InitializeComponent() {    // delete view of some function button for POS Printer Line mode

  	    Spinner spinner_bluetooth_connectRetry_type = (Spinner)findViewById(R.id.spinner_bluetooth_connectRetry_type);
  	    SpinnerAdapter ad_bluetooth_connectRetry_type = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, new String[] {"OFF", "ON"});
  	    spinner_bluetooth_connectRetry_type.setAdapter(ad_bluetooth_connectRetry_type);

    	Spinner spinner_printerType = (Spinner)findViewById(R.id.spinner_printerType);
    	SpinnerAdapter ad_spinner_printerType = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, new String[] {"POS Printer", "Portable Printer"});
    	spinner_printerType.setAdapter(ad_spinner_printerType);
    	
        Spinner spinner_tcp_port_number = (Spinner)findViewById(R.id.spinner_tcp_port_number);
        SpinnerAdapter ad_tcp_port_number = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, new String[] {"Standard", "9100", "9101", "9102", "9103", "9104", "9105", "9106", "9107", "9108", "9109"});
        spinner_tcp_port_number.setAdapter(ad_tcp_port_number);
        
        Spinner spinner_bluetooth_communication_type = (Spinner)findViewById(R.id.spinner_bluetooth_communication_type);
        SpinnerAdapter ad_bluetooth_communication_type = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, new String[] {"SSP", "PIN Code"});
        spinner_bluetooth_communication_type.setAdapter(ad_bluetooth_communication_type);
        
        Spinner spinner_dkaircash_input_password = (Spinner)findViewById(R.id.spinner_displayInputPassword);
        SpinnerAdapter ad_dkaircash_input_password = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, new String[] {"ON", "OFF"});
        spinner_dkaircash_input_password.setAdapter(ad_dkaircash_input_password);
        
    }
    
    public void Help(View view)
    {
    	if (!checkClick.isClickEvent()) return;
		
    	Intent myIntent = new Intent(this, helpActivity.class);
		startActivityFromChild(this, myIntent, 0);
    }

    public void CashDrawerBluetoothSetting(View view)
    {
    	if (!checkClick.isClickEvent()) return;
    	EditText drawerportNameField = (EditText)findViewById(R.id.editText_DrawerPortName);
    	String drawerportName = drawerportNameField.getText().toString();
    	String drawerportSettings = getPortSettingsOption(drawerportName);
    	
    	//Check bluetooth interface 
    	if(!drawerportName.startsWith("BT:"))
    	{
    		
			new AlertDialog.Builder(me)
			.setTitle(getString(R.string.error))
			.setMessage(getString(R.string.bluetooth_interface_only))
			.setNegativeButton("OK", null)
			.show();
    	}
		else
    	{
			SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
			Editor editor = pref.edit();
			editor.putString("bluetoothSettingPortName", drawerportName);
			editor.putString("bluetoothSettingPortSettings", drawerportSettings);
			editor.commit();

			Intent myIntent = new Intent(this, BluetoothSettingActivity.class);
			startActivityFromChild(this, myIntent, 0);
		}
    }

    private String getBluetoothRetrySettings() {
    	String retrySetting = "";
    	
    	Spinner spinner_bluetooth_connectRetry_type = (Spinner)findViewById(R.id.spinner_bluetooth_connectRetry_type);
    	switch(spinner_bluetooth_connectRetry_type.getSelectedItemPosition())
    	{
    	case 0:
    		retrySetting = "";
    		break;
    	case 1:
    		retrySetting = ";l";
    		break;
    	}
    	
    	return retrySetting;
    }
    
    private String getTCPPortSettings() {
    	String portSettings = "";
    	
		Spinner spinner_tcp_port_number = (Spinner)findViewById(R.id.spinner_tcp_port_number);
		switch(spinner_tcp_port_number.getSelectedItemPosition())
		{
            case 0:
    	        portSettings = "";
	            break;
	        case 1:
	            portSettings = "9100";
	            break;
	        case 2:
	            portSettings = "9101";
	            break;
	        case 3:
	            portSettings = "9102";
	            break;
	        case 4:
	            portSettings = "9103";
	            break;
	        case 5:
	            portSettings = "9104";
	            break;
	        case 6:
	            portSettings = "9105";
	            break;
	        case 7:
	            portSettings = "9106";
	            break;
	        case 8:
	            portSettings = "9107";
	            break;
	        case 9:
	            portSettings = "9108";
	            break;
	        case 10:
	            portSettings = "9109";
	            break;
		}
    	
    	return portSettings;
    }
    
    private String getBluetoothCommunicationType() {
    	String portSettings = "";
    	
		Spinner spinner_bluetooth_communication_type = (Spinner)findViewById(R.id.spinner_bluetooth_communication_type);
		switch(spinner_bluetooth_communication_type.getSelectedItemPosition())
		{
            case 0:
    	        portSettings = "";
	            break;
	        case 1:
	            portSettings = ";p";
	            break;
		}
    	
    	return portSettings;
    }
    
    private String getPortSettingsOption(String portName) {
    	String portSettings = "";
    	
    	if (portName.toUpperCase().startsWith("TCP:")) {
    		portSettings += getTCPPortSettings();
    	} else if (portName.toUpperCase().startsWith("BT:")) {
        	portSettings += getBluetoothCommunicationType();    // Bluetooth option of "portSettings" must be last.
        	portSettings += getBluetoothRetrySettings();
    	}
    	
    	return portSettings;
    }
    
    private boolean getPrinterType() {
		Spinner spinner_printerType = (Spinner)findViewById(R.id.spinner_printerType);

        switch (spinner_printerType.getSelectedItemPosition()) {
            case 0:    // "POS Printer"
                return true;
            case 1:    // "Portable Printer"
            	return false;
            default:
            	return true;
        }
    }

    private boolean getInputPassword() {
		Spinner spinner_InputPassword = (Spinner)findViewById(R.id.spinner_displayInputPassword);

        switch (spinner_InputPassword.getSelectedItemPosition()) {
            case 0:    // "ON"
            default:
                return true;
            case 1:    // "OFF"
            	return false;
        }
    }
    
    public void PortDiscovery(View view)
    {	
    	if (!checkClick.isClickEvent()) return;
				
    	//Check Printer Type
    	if(getPrinterType()){
    		//POS printer
            final String item_list[] = new String[] {
            		"LAN",
            		"Bluetooth",
            		"All",
                    };
            
            strInterface = "LAN";
            
            Builder portDiscoveryDialog = new AlertDialog.Builder(this);
            portDiscoveryDialog.setIcon(android.R.drawable.checkbox_on_background);
            portDiscoveryDialog.setTitle("Port Discovery List");
            portDiscoveryDialog.setCancelable(false);
            portDiscoveryDialog.setSingleChoiceItems(item_list, 0, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {

                	strInterface = item_list[whichButton];
                }
            });
            
            portDiscoveryDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                	((AlertDialog)dialog).getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
                	((AlertDialog)dialog).getButton(DialogInterface.BUTTON_NEGATIVE).setEnabled(false);
                	
                    if (true == strInterface.equals("LAN")) {
                    	getPortDiscovery("LAN");
                    }
                    else if (strInterface.equals("Bluetooth")) {
                    	getPortDiscovery("Bluetooth");
                    }
                    else {
                    	getPortDiscovery("All");
                    }
                    
                }
            });
            portDiscoveryDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                }
            });
            portDiscoveryDialog.show();			    		
    		   		
    	}else{
    		//Portable printer
    		getPortablePortDiscovery(view);
    	}
    	
    }
    
    private void getPortDiscovery(String interfaceName) {
    	List<PortInfo> BTPortList;
    	List<PortInfo> TCPPortList;
        final EditText editPortName;

    	final ArrayList<PortInfo> arrayDiscovery;
    	ArrayList<String> arrayPortName;

		arrayDiscovery = new ArrayList<PortInfo>();
		arrayPortName = new ArrayList<String>();
		
    	try {
    		if (true == interfaceName.equals("Bluetooth") || true == interfaceName.equals("All")) {
    			BTPortList  = StarIOPort.searchPrinter("BT:");   
    	    	
    			for (PortInfo portInfo : BTPortList) {
  	    		  arrayDiscovery.add(portInfo);
  	    	    }
    		}
    		if (true == interfaceName.equals("LAN") || true == interfaceName.equals("All")) {
    			TCPPortList = StarIOPort.searchPrinter("TCP:");
    			
    	    	for (PortInfo portInfo : TCPPortList) {
    	    		arrayDiscovery.add(portInfo);
    	    		
    	    		//Check SAC10 model
    	    		if(portInfo.getModelName().startsWith("SAC") == true)
    	    		{
    	    			arrayDiscovery.remove(portInfo);
    	    		}		
    	    	}
    		}
    		
    		arrayPortName = new ArrayList<String>();  		

			for(PortInfo discovery : arrayDiscovery)
			{
				String portName;

				portName = discovery.getPortName();
				
					if(discovery.getMacAddress().equals("") == false)
					{
						portName += "\n - " + discovery.getMacAddress();
						if(discovery.getModelName().equals("") == false)
						{
							portName += "\n - " + discovery.getModelName();
						}
					}

				arrayPortName.add(portName);
			}


		} catch (StarIOPortException e) {
			e.printStackTrace();
		}
    	
    	editPortName = new EditText(this);
    	
        new AlertDialog.Builder(this)
		.setIcon(android.R.drawable.checkbox_on_background) 
		.setTitle("Please Select IP Address or Input Port Name") 
		.setCancelable(false)
		.setView(editPortName) 
		.setPositiveButton("OK", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int button)
			{
            	((AlertDialog)dialog).getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
            	((AlertDialog)dialog).getButton(DialogInterface.BUTTON_NEGATIVE).setEnabled(false);
            	
				EditText portNameField = (EditText)findViewById(R.id.editText_PortName);
    			portNameField.setText(editPortName.getText());   			
    			
    	        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
    	        Editor editor = pref.edit();
    			editor.putString("printerportName", portNameField.getText().toString());
    			editor.commit();
			}
		})
		.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int button){
			}
		})
		.setItems(arrayPortName.toArray(new String[0]), new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int select)
			{           	
				EditText portNameField       = (EditText)findViewById(R.id.editText_PortName);
				portNameField.setText(arrayDiscovery.get(select).getPortName());
    			
    	        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
    	        Editor editor = pref.edit();
    			editor.putString("printerportName", portNameField.getText().toString());
    			editor.commit();
			}
		})
		.show();
    }
    
    private void getDrawerPortDiscovery(String interfaceName) {
    	List<PortInfo> BTPortList;
    	List<PortInfo> TCPPortList;
        final EditText editPortName;

    	final ArrayList<PortInfo> arrayDiscovery;
    	ArrayList<String> arrayPortName;

		arrayDiscovery = new ArrayList<PortInfo>();
		arrayPortName = new ArrayList<String>();
		
    	try {
    		if (true == interfaceName.equals("Bluetooth") || true == interfaceName.equals("All")) {
    			BTPortList  = StarIOPort.searchPrinter("BT:");   
    	    	
    			for (PortInfo portInfo : BTPortList) {
  	    		  arrayDiscovery.add(portInfo);
  	    	    }
    		}
    		if (true == interfaceName.equals("LAN") || true == interfaceName.equals("All")) {
    			TCPPortList = StarIOPort.searchPrinter("TCP:");
    			
    	    	for (PortInfo portInfo : TCPPortList) {
    	    		
    	    		arrayDiscovery.add(portInfo);
    	    		
    	    		//Check SAC10 model
    	    		if(portInfo.getModelName().startsWith("SAC") == false)
    	    		{
    	    			arrayDiscovery.remove(portInfo);
    	    		}
    	    		
    	    	}
    		}
    		
    		arrayPortName = new ArrayList<String>();  		

			for(PortInfo discovery : arrayDiscovery)
			{
				String portName;

				portName = discovery.getPortName();
				
					if(discovery.getMacAddress().equals("") == false)
					{
						portName += "\n - " + discovery.getMacAddress();
						if(discovery.getModelName().equals("") == false)
						{
							portName += "\n - " + discovery.getModelName();
						}
					}

				arrayPortName.add(portName);
				
			}


		} catch (StarIOPortException e) {
			e.printStackTrace();
		}
    	
    	editPortName = new EditText(this);
    	
        new AlertDialog.Builder(this)
		.setIcon(android.R.drawable.checkbox_on_background) 
		.setTitle("Please Select IP Address or Input Port Name")
		.setCancelable(false)
		.setView(editPortName) 
		.setPositiveButton("OK", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int button)
			{
            	((AlertDialog)dialog).getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
            	((AlertDialog)dialog).getButton(DialogInterface.BUTTON_NEGATIVE).setEnabled(false);
            	  
				EditText drawerportNameField = (EditText)findViewById(R.id.editText_DrawerPortName); 
    			drawerportNameField.setText(editPortName.getText()); 			    			
    	        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
    	        Editor editor = pref.edit();
    			editor.putString("drawerportName", drawerportNameField.getText().toString());
    			editor.commit();
			}
		})
		.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int button){
			}
		})
		.setItems(arrayPortName.toArray(new String[0]), new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int select)
			{
            	((AlertDialog)dialog).getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
            	((AlertDialog)dialog).getButton(DialogInterface.BUTTON_NEGATIVE).setEnabled(false);
            	  
				EditText drawerportNameField = (EditText)findViewById(R.id.editText_DrawerPortName);
				drawerportNameField.setText(arrayDiscovery.get(select).getPortName());
    			
    	        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
    	        Editor editor = pref.edit();
    			editor.putString("drawerportName", drawerportNameField.getText().toString());
    			editor.commit();
			}
		})
		.show();
    }
     
    public void getPortablePortDiscovery(View view)
    {
    	List<PortInfo> BTPortList;

        final EditText editPortName;
    	final ArrayList<PortInfo> arrayDiscovery;

    	ArrayList<String> arrayPortName;

		arrayDiscovery = new ArrayList<PortInfo>();
		arrayPortName = new ArrayList<String>();

    	try {
    		BTPortList  = StarIOPort.searchPrinter("BT:");    // "port discovery" of Portable printer is support only Bluetooth port 

    		for (PortInfo portInfo : BTPortList) {
	    		arrayDiscovery.add(portInfo);
	    	}

    		arrayPortName = new ArrayList<String>();

			for(PortInfo discovery : arrayDiscovery)
			{
				String portName;

				portName = discovery.getPortName();

				if(discovery.getMacAddress().equals("") == false)
				{
					portName += "\n - " + discovery.getMacAddress();
					if(discovery.getModelName().equals("") == false)
					{
						portName += "\n - " + discovery.getModelName();
					}
				}
				arrayPortName.add(portName);
			}

		} catch (StarIOPortException e) {
			e.printStackTrace();
		}

    	editPortName = new EditText(this);
    	
        new AlertDialog.Builder(this)
		.setIcon(R.drawable.icon) 
		.setTitle("Please Select IP Address or Input Port Name")
		.setCancelable(false)
		.setView(editPortName) 
		.setPositiveButton("OK", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int button)
			{
            	((AlertDialog)dialog).getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
            	((AlertDialog)dialog).getButton(DialogInterface.BUTTON_NEGATIVE).setEnabled(false);
            	  
				EditText portNameField = (EditText)findViewById(R.id.editText_PortName);
    			portNameField.setText(editPortName.getText());
    			
    	        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
    	        Editor editor = pref.edit();
    			editor.putString("printerportName", portNameField.getText().toString());
    			editor.commit();
			}
		})
		.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int button){
			}
		})
		.setItems(arrayPortName.toArray(new String[0]), new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int select)
			{
				EditText portNameField       = (EditText)findViewById(R.id.editText_PortName);
				EditText drawerportNameField = (EditText)findViewById(R.id.editText_DrawerPortName); 
				
				portNameField.setText(arrayDiscovery.get(select).getPortName());
				
    	        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
    	        Editor editor = pref.edit();
    			editor.putString("printerportName", portNameField.getText().toString());
    			editor.putString("drawerportName", drawerportNameField.getText().toString());
    			editor.commit();
			}
		})
		.show();
    }

    public void DrawerPortDiscovery(View view){
    	
    	if (!checkClick.isClickEvent()) return;
    	
        final String item_list[] = new String[] {
        		"LAN",
        		"Bluetooth",
        		"All",
                };
        
        strInterface = "LAN";
        
        Builder portDiscoveryDialog = new AlertDialog.Builder(this);
        portDiscoveryDialog.setIcon(android.R.drawable.checkbox_on_background);
        portDiscoveryDialog.setTitle("Port Discovery List");
        portDiscoveryDialog.setSingleChoiceItems(item_list, 0, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            	strInterface = item_list[whichButton];
            }
        });
        
        portDiscoveryDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            	((AlertDialog)dialog).getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
            	((AlertDialog)dialog).getButton(DialogInterface.BUTTON_NEGATIVE).setEnabled(false);
                if (true == strInterface.equals("LAN")) {
                	getDrawerPortDiscovery("LAN");
                }
                else if (strInterface.equals("Bluetooth")) {
                	getDrawerPortDiscovery("Bluetooth");
                }
                else {
                	getDrawerPortDiscovery("All");
                }
                
            }
        });
        portDiscoveryDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        portDiscoveryDialog.show();			    		
    	
    }

    public void GetStatus(View view)
    {
    	if (!checkClick.isClickEvent()) return;
		
    	EditText portNameField = (EditText)findViewById(R.id.editText_PortName);
    	String portName = portNameField.getText().toString();
    	String portSettings;
    	
    	if(getPrinterType()){
    		 portSettings = getPortSettingsOption(portName);
    		 
    	    //The portable printer and non portable printer have the same 
    		 CheckStatus(this, portName, portSettings, true);
    	}else{
    		 portSettings = "mini";
    		 
    	    //The portable printer and non portable printer have the same 
    	    MiniPrinterFunctions.CheckStatus(this, portName, portSettings);
    	}  		   	
    }

    
    public void GetPrinterFirmwareInfo(View view)
    {
    	if (!checkClick.isClickEvent()) return;
		
    	EditText portNameField = (EditText)findViewById(R.id.editText_PortName);
    	String portName = portNameField.getText().toString();
    	String portSettings;
    	
    	if(getPrinterType()){
    		 portSettings = getPortSettingsOption(portName);
    		 
    	    //The portable printer and non portable printer have the same 
    		 PrinterFunctions.CheckFirmwareVersion(this, portName, portSettings);
    	}else{
    		 portSettings = "mini";
    		 
    	    //The portable printer and non portable printer have the same 
    		 MiniPrinterFunctions.CheckFirmwareVersion(this, portName, portSettings);
    	}  		   	
    }
    
    
    public void SampleReceipt(final View view)
    {   
    	if (!checkClick.isClickEvent()) return;
		
       	//showDialog(DIALOG_PRINTABLEAREA_ID);
        final String item_list[];
        

        if(getPrinterType()){//POSPrinter
            item_list = new String[] {
            		getResources().getString(R.string.printArea3inch),
            		getResources().getString(R.string.printArea4inch), 
                    };
            
            strPrintArea = getResources().getString(R.string.printArea3inch);
        	
        }else{//PortablePrinter
            item_list = new String[] {
            		getResources().getString(R.string.printArea2inch),
            		getResources().getString(R.string.printArea3inch),
            		getResources().getString(R.string.printArea4inch), 
                    };
            
            strPrintArea = getResources().getString(R.string.printArea2inch);
        	
        }
        
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
            	  
            	//Printer Port Name
            	EditText portNameField = (EditText)findViewById(R.id.editText_PortName);
            	PrinterTypeActivity.setPortName(portNameField.getText().toString());
            	
            	//Drawer Port Name
            	EditText drawerportNameField = (EditText)findViewById(R.id.editText_DrawerPortName);
    	    	final String drawerportName = drawerportNameField.getText().toString();
    	    	final String drawerportSettings = getPortSettingsOption(drawerportName);
    	    	
        		final EditText editView = new EditText(DKAirCashActivity.this);
        		editView.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        		editView.setFilters( new InputFilter[]{ new InputFilter.LengthFilter(4) } );

        		final byte[] commands = new byte[] {0x07}; //Drawer open command
        		
            	if(getPrinterType()){//POS Printer
         			            		
                	dialog.dismiss();
                	
            		//POSPrinter
                	PrinterTypeActivity.setPortSettings(getPortSettingsOption(PrinterTypeActivity.getPortName()));
                	String commandType  = "Raster";
                	
                	ArrayList<Byte> list = CreateSampleReceipt(me, PrinterTypeActivity.getPortName(), PrinterTypeActivity.getPortSettings(), commandType, getResources(), strPrintArea);

                	byte[] commandToSendToPrinter = convertFromListByteArrayTobyteArray(list);
                	
                	printthread = new PrintRecieptThread(me, PrinterTypeActivity.getPortName(), PrinterTypeActivity.getPortSettings(), commandToSendToPrinter);
                	printthread.start();//Start Thread
                	
                	int result = printthread.startPrint();//Start Printing
                	           			
                	
                	if(result == 0)
                	{
                		
                		
                		if(getInputPassword()){ //ON
                			   
                			//show password dialog 
                	        new AlertDialog.Builder(DKAirCashActivity.this)
                	            .setIcon(android.R.drawable.ic_dialog_info)
                	            .setTitle("Please Input Password")
                	            .setView(editView)
                	            .setCancelable(false)
                	            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                	                public void onClick(DialogInterface dialog, int whichButton) {
                	              	  ((AlertDialog)dialog).getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
                	              	  ((AlertDialog)dialog).getButton(DialogInterface.BUTTON_NEGATIVE).setEnabled(false);

                	                	String sText = editView.getText().toString();                	
                	                	
                	                	//Password check
                	                	if(sText.matches("1234"))
                	                	{
                	                		
                	                		communicationOpenDrawer(drawerportName, drawerportSettings ,me, commands);
                	                	}
                	                	else
                	                	{            			
                	            		    //Drawer ReleasePort
                	            			Builder dialog1 = new AlertDialog.Builder(me);
                	            			dialog1.setNegativeButton("Ok", null);
                	            			AlertDialog alert = dialog1.create();
                	            			alert.setTitle("Failure");
                	            			alert.setMessage("The password is incorrect. stop the process.");
                	            			alert.setCancelable(false);
                	            			alert.show();
                	                	}                	
                	                }
                	            })
                	            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                	                public void onClick(DialogInterface dialog, int whichButton) {
                	              	  ((AlertDialog)dialog).getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
                	              	  ((AlertDialog)dialog).getButton(DialogInterface.BUTTON_NEGATIVE).setEnabled(false);
                	                	
                	                }
                	            })
                	            .show();
                			
                			
                		}
                		else{//OFF
                			communicationOpenDrawer(drawerportName, drawerportSettings ,me, commands);
                		}                		
                	}
                	else{
                		//
                	}               	
           

            	}else{//Portable Printer
           		
                	dialog.dismiss();

                	
            		//Portable Printer
                 	PrinterTypeActivity.setPortSettings("mini");
                 	
                 	ArrayList<Byte> list = CreateMiniSampleReceipt(me, PrinterTypeActivity.getPortName(), PrinterTypeActivity.getPortSettings(), strPrintArea);
                 	byte[] commandToSendToPrinter = convertFromListByteArrayTobyteArray(list);
                 	
                 	printthread = new PrintRecieptThread(me, PrinterTypeActivity.getPortName(), PrinterTypeActivity.getPortSettings(), commandToSendToPrinter);
                	printthread.start();//Start Thread
                	
                	int result = printthread.startPrint();//Start Printing
                	
                	if(result == 0)
                	{
                		if(getInputPassword()){ //ON
             			   
                			//show password dialog 
                	        new AlertDialog.Builder(DKAirCashActivity.this)
                	            .setIcon(android.R.drawable.ic_dialog_info)
                	            .setTitle("Please Input Password")
                	            .setView(editView)
                	            .setCancelable(false)
                	            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                	                public void onClick(DialogInterface dialog, int whichButton) {
                	              	  ((AlertDialog)dialog).getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
                	              	  ((AlertDialog)dialog).getButton(DialogInterface.BUTTON_NEGATIVE).setEnabled(false);

                	                	String sText = editView.getText().toString();                	
                	                	
                	                	//Password check
                	                	if(sText.matches("1234"))
                	                	{
                	                		communicationOpenDrawer(drawerportName, drawerportSettings ,me,commands);
                	                	}
                	                	else
                	                	{            			
                	            		    //Drawer ReleasePort
                	            			Builder dialog1 = new AlertDialog.Builder(me);
                	            			dialog1.setNegativeButton("Ok", null);
                	            			AlertDialog alert = dialog1.create();
                	            			alert.setTitle("Failure");
                	            			alert.setMessage("The password is incorrect. stop the process.");
                	            			alert.setCancelable(false);
                	            			alert.show();
                	                	}                	
                	                }
                	            })
                	            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                	                public void onClick(DialogInterface dialog, int whichButton) {
                	              	  ((AlertDialog)dialog).getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
                	              	  ((AlertDialog)dialog).getButton(DialogInterface.BUTTON_NEGATIVE).setEnabled(false);
                	                	
                	                }
                	            })
                	            .show();
                			
                			
                		}
                		else{//OFF
                			
                			communicationOpenDrawer(drawerportName, drawerportSettings ,me, commands);
                		}
                	}
                	else{
                		//
                	}                	                	
	            }           	          	
            }
        });
        printableAreaDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        printableAreaDialog.show();
        
    }
    
    
    protected void communicationOpenDrawer(final String drawerportName, final String drawerportSetting, Context me2, final byte[] drawerCommand) {

		AsyncTask<Void, Void, StarPrinterStatus> DrawerOpenCheckTask = new AsyncTask<Void, Void, StarPrinterStatus>(){

    		StarIOPort port1 = null;
    		StarPrinterStatus status = new StarPrinterStatus();
			@Override
			protected StarPrinterStatus doInBackground(Void... params) {
				try
				{
					port1 = StarIOPort.getPort(drawerportName,drawerportSetting, 10000);
					status = port1.beginCheckedBlock();
					
				} catch (StarIOPortException e) {
										
		    		if (port1 != null)
		    		{
		    			try
		    			{
		    				//ReleasePort
		    				StarIOPort.releasePort(port1);
		    			}
		    			catch (StarIOPortException e1) {
		    				
		    			}
		    		}
					
				}								
				
				return status;
			}
			
			@Override
			protected void onPostExecute(StarPrinterStatus status) {
				
				if(port1 != null)
				{
					if(status.compulsionSwitch == true)
					{								
	            		Builder dialog1 = new AlertDialog.Builder(me);
	            		dialog1.setNegativeButton("Ok", null);
//	            		dialog1.setTitle("Information");
	            		dialog1.setMessage("Drawer was already opened");
	            		dialog1.setCancelable(false);
	            		AlertDialog alert1 = dialog1.create();
	            		alert1.setCancelable(false);
	            		alert1.show();
	            		
			    		if (port1 != null)
			    		{
			    			try
			    			{
			    				//ReleasePort
			    				StarIOPort.releasePort(port1);
			    			}
			    			catch (StarIOPortException e) {
			    				
			    			}
			    		}
					}
					else
					{
	            		Builder waitingopendialog = new AlertDialog.Builder(me);
//	            		waitingopendialog.setTitle("Information");
	            		waitingopendialog.setMessage("Waiting for drawer to open");
	            		waitingopendialog.setCancelable(false);
	            		final AlertDialog waitingopenalert = waitingopendialog.create();
	            		waitingopenalert.setCancelable(false);
	            		waitingopenalert.show(); 
						
						
						AsyncTask<Void, Void, StarPrinterStatus> task = new AsyncTask<Void, Void, StarPrinterStatus>(){

							StarPrinterStatus status1 = new StarPrinterStatus();
							@Override
							protected StarPrinterStatus doInBackground(Void... params) {
				    			
				    			try 
				    			{
									port1.writePort(drawerCommand, 0, drawerCommand.length);

									status1 = port1.endCheckedBlock();
									
									SystemClock.sleep(150);
									
									status1 = port1.retreiveStatus();
									
								} catch (StarIOPortException e) {
																											
									runOnUiThread(new Runnable(){

										public void run() {
						            		Builder notopendialog = new AlertDialog.Builder(me);
						            		notopendialog.setNegativeButton("Ok", null);
						            		notopendialog.setTitle("Error");
						            		notopendialog.setMessage("Drawer didn't open");
						            		notopendialog.setCancelable(false);
						            		AlertDialog notopenalert = notopendialog.create();
						            		notopenalert.setCancelable(false);
						            		notopenalert.show();														
										}
									});

								}
								return status1;
							}
							
							@Override
							protected void onPostExecute(StarPrinterStatus status) {
								waitingopenalert.dismiss();			
								
								if(status.etbCounter == 2)
								{
									
									if(status.compulsionSwitch == true)
									{
					            		Builder opendrawerdialog = new AlertDialog.Builder(me);
//					            		opendrawerdialog.setTitle("Information");
					            		opendrawerdialog.setMessage("Waiting for drawer to close");
					            		opendrawerdialog.setCancelable(false);
					            		final AlertDialog opendraweralert = opendrawerdialog.create();
					            		opendraweralert.setCancelable(false);
					            		opendraweralert.show(); 
					            		
					            		AsyncTask<Void, Void, Boolean> task2 = new AsyncTask<Void, Void, Boolean>(){

					            			StarPrinterStatus status2 = new StarPrinterStatus();
					            			
											@Override
											protected Boolean doInBackground(Void... params) {
						        		        int timeoutMillis = 30000;//30sec
						        		        long startTimeMillis = System.currentTimeMillis();
						        		    	long endTimeMillis = 0;
						        		    	while(true)//check  drawer close status
						        		    	{
						        		    		try 
						        		    		{					        		    			
														status2 = port1.retreiveStatus();
														
														if(status2.compulsionSwitch == false)
														{														
															return true;
														}
														
						        		                endTimeMillis = System.currentTimeMillis();
						        		                if (endTimeMillis - startTimeMillis > timeoutMillis) 
						        		                {
										            		return false;
						        		                }
						        		                
						        		                SystemClock.sleep(150);

													}
						        		    		catch (StarIOPortException e)
													{
														e.printStackTrace();
														
														return false;
													}
						        		    	}
											}
											
											@Override
											protected void onPostExecute(Boolean result) {
												
												if(result == true)
												{
								            		Builder finishdialog = new AlertDialog.Builder(me);
//								            		finishdialog.setTitle("Information");
								            		finishdialog.setMessage("Completed successfully");
								            		finishdialog.setCancelable(false);
								            		final AlertDialog finishalert = finishdialog.create();
								            		finishalert.setCancelable(false);
													
													opendraweralert.dismiss();
													
								            		finishalert.show(); 
								            		
								            		AsyncTask<Void, Void, Boolean> task3 = new AsyncTask<Void, Void, Boolean>(){

														@Override
														protected Boolean doInBackground(Void... params) {

															SystemClock.sleep(2000);
															return true;
														}
														
														@Override
														protected void onPostExecute(Boolean result) {
															finishalert.dismiss();
															
														}
								            			
								            		};
								            		task3.execute();

												}
												else
												{
													opendraweralert.dismiss();
				        		                	
								            		Builder dialog1 = new AlertDialog.Builder(me);
								            		dialog1.setNegativeButton("Ok", null);
								            		dialog1.setTitle("Error");
								            		dialog1.setMessage("Drawer didn't close within 30 seconds");
								            		dialog1.setCancelable(false);
								            		AlertDialog alert1 = dialog1.create();
								            		alert1.setCancelable(false);
								            		alert1.show(); 
												}
									    		if (port1 != null)
									    		{
									    			try
									    			{
									    				//ReleasePort
									    				StarIOPort.releasePort(port1);
									    			}
									    			catch (StarIOPortException e) {
									    				
									    			}
	
									    		}								    		
											}
					            			
					            		};
					            		task2.execute();	
					            		
									}
									else
									{
					            		Builder notopendialog1 = new AlertDialog.Builder(me);
					            		notopendialog1.setNegativeButton("Ok", null);
					            		notopendialog1.setTitle("Error");
					            		notopendialog1.setMessage("Drawer didn't open");
					            		notopendialog1.setCancelable(false);
					            		AlertDialog notopenalert1 = notopendialog1.create();
					            		notopenalert1.setCancelable(false);
					            		notopenalert1.show();

					            		
							    		if (port1 != null)
							    		{
							    			try
							    			{
							    				//ReleasePort
							    				StarIOPort.releasePort(port1);
							    				SystemClock.sleep(1000); //1sec
							    			}
							    			catch (StarIOPortException e) {
							    				
							    			}

							    		}
							    		
									}
								}
								else{																		
						    		if (port1 != null)
						    		{
						    			try
						    			{
						    				//ReleasePort
						    				
						    				StarIOPort.releasePort(port1);
						    				
						    			}
						    			catch (StarIOPortException e) {
						    				
						    			}

						    		}
								}
							}//task onPostExecute
						};
						task.execute();																			
					}
			    }
				else{
            		Builder errordialog = new AlertDialog.Builder(me);
            		errordialog.setNegativeButton("OK", null);
            		errordialog.setMessage("DK-AirCash is turned off or other host is using the DK-AirCash");
            		errordialog.setCancelable(false);
            		AlertDialog erroralert1 = errordialog.create();
            		erroralert1.setCancelable(false);
            		erroralert1.show();
				}
			}
		};
		DrawerOpenCheckTask.execute();

		
	}   
    
    
    
    private void kickCashDrawer(final String drawerportName, final String drawerportSettings, final byte[] command){
    	
		final EditText editView = new EditText(DKAirCashActivity.this);
		editView.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
		editView.setFilters( new InputFilter[]{ new InputFilter.LengthFilter(4) } ); 
	
		if(getInputPassword()){ //ON
			
			
			//show password dialog 
	        new AlertDialog.Builder(DKAirCashActivity.this)
	            .setIcon(android.R.drawable.ic_dialog_info)
	            .setTitle("Please Input Password")
	            .setView(editView)
	            .setCancelable(false)
	            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int whichButton) {
	              	  ((AlertDialog)dialog).getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
	              	  ((AlertDialog)dialog).getButton(DialogInterface.BUTTON_NEGATIVE).setEnabled(false);

	                	String sText = editView.getText().toString();                	
	                	
	                	//Password check
	                	if(sText.matches("1234"))
	                	{
	                		communicationOpenDrawer(drawerportName, drawerportSettings ,me,command);
	                	}
	                	else
	                	{            			
	            		    //Drawer ReleasePort
	            			Builder dialog1 = new AlertDialog.Builder(me);
	            			dialog1.setNegativeButton("Ok", null);
	            			AlertDialog alert = dialog1.create();
	            			alert.setTitle("Failure");
	            			alert.setMessage("The password is incorrect. stop the process.");
	            			alert.setCancelable(false);
	            			alert.show();
	                	}              	
	                }
	            })
	            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int whichButton) {
	              	  ((AlertDialog)dialog).getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
	              	  ((AlertDialog)dialog).getButton(DialogInterface.BUTTON_NEGATIVE).setEnabled(false);
	                	
	                }
	            })
	            .show();				
		}
		else{ //OFF

			communicationOpenDrawer(drawerportName, drawerportSettings ,me, command);
			
		}
    }
        
    public void OpenCashDrawer(View view)
    {
    	if (!checkClick.isClickEvent()) return;
    	
    	//Drawer Port Name
    	EditText drawerportNameField1 = (EditText)findViewById(R.id.editText_DrawerPortName);
    	String drawerportName = drawerportNameField1.getText().toString();
    	String drawerportSettings = getPortSettingsOption(drawerportName);
    	
	
		//show password dialog and Kick cash drawer
		byte[] commands = new byte[] {0x07}; //Drawer open command
		kickCashDrawer(drawerportName, drawerportSettings, commands);
		

    }

    public void OpenCashDrawer2(View view)
    {
    	if (!checkClick.isClickEvent()) return;
    	
    	//Drawer Port Name
    	EditText drawerportNameField1 = (EditText)findViewById(R.id.editText_DrawerPortName);
    	String drawerportName = drawerportNameField1.getText().toString();
    	String drawerportSettings = getPortSettingsOption(drawerportName);
	
		//show password dialog and Kick cash drawer
		byte[] commands = new byte[] {0x1a}; //Drawer open command
		kickCashDrawer(drawerportName, drawerportSettings, commands);
		

    }
    
    public void GetCashDrawerStatus(View view){
    	
    	if (!checkClick.isClickEvent()) return;
    	EditText drawerportNameField = (EditText)findViewById(R.id.editText_DrawerPortName);
    	String portName = drawerportNameField.getText().toString();
    	String portSettings = getPortSettingsOption(portName);
    	
  	    CheckDrawerStatus(this, portName, portSettings);
 
    }
    
    public void GetCashDrawerFirmwareInfo(View view){
    	
    	if (!checkClick.isClickEvent()) return;
    	EditText drawerportNameField = (EditText)findViewById(R.id.editText_DrawerPortName);
    	String portName = drawerportNameField.getText().toString();
    	String portSettings = getPortSettingsOption(portName);
    	
    	PrinterFunctions.CheckFirmwareVersion(this, portName, portSettings);
 
    }
    
    public void GetCashDrawerdipswInfo(View view){
    	
    	if (!checkClick.isClickEvent()) return;
    	EditText drawerportNameField = (EditText)findViewById(R.id.editText_DrawerPortName);
    	String portName = drawerportNameField.getText().toString();
    	String portSettings = getPortSettingsOption(portName);
    	
    	PrinterFunctions.CheckDipSwitchSettings(this, portName, portSettings);
    }
    
    
	private static byte[] convertFromListByteArrayTobyteArray(List<Byte> ByteArray)
	{
		byte[] byteArray = new byte[ByteArray.size()];
		for(int index = 0; index < byteArray.length; index++)
		{
			byteArray[index] = ByteArray.get(index);
		}
		
		return byteArray;
	}   
    
	private static void sendCommand(Context context, String portName, String portSettings, ArrayList<Byte> byteList) {
		StarIOPort port = null;
		try
		{
			/*
				using StarIOPort3.1.jar (support USB Port)
				Android OS Version: upper 2.2
			*/
			port = StarIOPort.getPort(portName, portSettings, 10000, context);
			/* 
				using StarIOPort.jar
				Android OS Version: under 2.1
				port = StarIOPort.getPort(portName, portSettings, 10000);
			*/
			try
			{
				Thread.sleep(100);
			}
			catch (InterruptedException e) { }

			/*
               Using Begin / End Checked Block method
               When sending large amounts of raster data,
               adjust the value in the timeout in the "StarIOPort.getPort"
               in order to prevent "timeout" of the "endCheckedBlock method" while a printing.
               
               *If receipt print is success but timeout error occurs(Show message which is "There was no response of the printer within the timeout period."),
                 need to change value of timeout more longer in "StarIOPort.getPort" method. (e.g.) 10000 -> 30000
			 */
			StarPrinterStatus status = port.beginCheckedBlock();
			
			if (true == status.offline)
			{
				throw new StarIOPortException("A printer is offline");
			}

			byte[] commandToSendToPrinter = convertFromListByteArrayTobyteArray(byteList);
			port.writePort(commandToSendToPrinter, 0, commandToSendToPrinter.length);

			status = port.endCheckedBlock();

			if (true == status.coverOpen)
			{
				throw new StarIOPortException("Printer cover is open");
			}
			else if (true == status.receiptPaperEmpty)
			{
				throw new StarIOPortException("Receipt paper is empty");
			}
			else if (true == status.offline)
			{
				throw new StarIOPortException("Printer is offline");
			}
		}
		catch (StarIOPortException e)
		{
			Builder dialog = new AlertDialog.Builder(context);
			dialog.setNegativeButton("Ok", null);
			AlertDialog alert = dialog.create();
			alert.setTitle("Failure");
			alert.setMessage(e.getMessage());
			alert.show();
		}
		finally
		{
			if (port != null)
			{
				try
				{
					StarIOPort.releasePort(port);
				}
				catch (StarIOPortException e) { }
			}
		}
	}

	/**
	 * This function shows how to create the receipt data of a thermal POS printer.
	 * @param context - Activity for displaying messages to the user
	 * @param portName - Port name to use for communication. This should be (TCP:<IPAddress>)
	 * @param portSettings - Should be blank
	 * @param commandType - Command type to use for printing. This should be ("Line" or "Raster")
	 * @param res - The resources object containing the image data. ( e.g.) getResources())
	 * @param strPrintArea - Printable area size, This should be ("3inch (78mm)" or "4inch (112mm)")
	 * @return print data
	 */
	public static ArrayList<Byte> CreateSampleReceipt(Context context, String portName, String portSettings, String commandType, Resources res, String strPrintArea)
	{	
		ArrayList<Byte> list = new ArrayList<Byte>();
		
		if (commandType == "Line")
		{
			if (strPrintArea.equals("3inch (78mm)"))
			{
	            byte[] data;

	            Byte[] tempList;
	    		
	            list.addAll(Arrays.asList(new Byte[]{0x1b, 0x1d, 0x61, 0x01}));    // Alignment (center)

//	            data = "[If loaded.. Logo1 goes here]\r\n".getBytes();
//	            tempList = new Byte[data.length];
//	            CopyArray(data, tempList);
//	            list.addAll(Arrays.asList(tempList));
//
//	            list.addAll(Arrays.asList(new Byte[]{0x1b, 0x1c, 0x70, 0x01, 0x00, '\r', '\n'}));  //Stored Logo Printing

	            data = "\nStar Clothing Boutique\r\n".getBytes();
	            tempList = new Byte[data.length];
	            CopyArray(data, tempList);
	            list.addAll(Arrays.asList(tempList));

	            data = "123 Star Road\r\nCity, State 12345\r\n\r\n".getBytes();
	            tempList = new Byte[data.length];
	            CopyArray(data, tempList);
	            list.addAll(Arrays.asList(tempList));

	            list.addAll(Arrays.asList(new Byte[]{0x1b, 0x1d, 0x61, 0x00})); // Alignment

	            list.addAll(Arrays.asList(new Byte[]{0x1b, 0x44, 0x02, 0x10, 0x22, 0x00})); //Set horizontal tab

	            data = "Date: MM/DD/YYYY".getBytes();
	            tempList = new Byte[data.length];
	            CopyArray(data, tempList);
	            list.addAll(Arrays.asList(tempList));

	            list.addAll(Arrays.asList(new Byte[]{' ', 0x09, ' '}));   //Moving Horizontal Tab

	            data = "Time:HH:MM PM\r\n------------------------------------------------\r\n\r\n".getBytes();
	            tempList = new Byte[data.length];
	            CopyArray(data, tempList);
	            list.addAll(Arrays.asList(tempList));

	            list.addAll(Arrays.asList(new Byte[]{0x1b, 0x45})); // bold

	            data = "SALE \r\n".getBytes();
	            tempList = new Byte[data.length];
	            CopyArray(data, tempList);
	            list.addAll(Arrays.asList(tempList));

	            list.addAll(Arrays.asList(new Byte[]{0x1b, 0x46})); // bolf off

	            data = "SKU ".getBytes();
	            tempList = new Byte[data.length];
	            CopyArray(data, tempList);
	            list.addAll(Arrays.asList(tempList));

	            list.addAll(Arrays.asList(new Byte[]{0x09}));

	            // Notice that we use a unicode representation because that is how Java expresses these bytes as double byte unicode
	            // This will TAB to the next horizontal position
	            data = "  Description   \u0009         Total\r\n".getBytes();
	            tempList = new Byte[data.length];
	            CopyArray(data, tempList);
	            list.addAll(Arrays.asList(tempList));

	            data = "300678566 \u0009  PLAIN T-SHIRT\u0009         10.99\r\n".getBytes();
	            tempList = new Byte[data.length];
	            CopyArray(data, tempList);
	            list.addAll(Arrays.asList(tempList));

	            data = "300692003 \u0009  BLACK DENIM\u0009         29.99\r\n".getBytes();
	            tempList = new Byte[data.length];
	            CopyArray(data, tempList);
	            list.addAll(Arrays.asList(tempList));

	            data = "300651148 \u0009  BLUE DENIM\u0009         29.99\r\n".getBytes();
	            tempList = new Byte[data.length];
	            CopyArray(data, tempList);
	            list.addAll(Arrays.asList(tempList));

	            data = "300642980 \u0009  STRIPED DRESS\u0009         49.99\r\n".getBytes();
	            tempList = new Byte[data.length];
	            CopyArray(data, tempList);
	            list.addAll(Arrays.asList(tempList));

	            data = "300638471 \u0009  BLACK BOOTS\u0009         35.99\r\n\r\n".getBytes();
	            tempList = new Byte[data.length];
	            CopyArray(data, tempList);
	            list.addAll(Arrays.asList(tempList));

	            data = "Subtotal \u0009\u0009        156.95\r\n".getBytes();
	            tempList = new Byte[data.length];
	            CopyArray(data, tempList);
	            list.addAll(Arrays.asList(tempList));

	            data = "Tax \u0009\u0009          0.00\r\n".getBytes();
	            tempList = new Byte[data.length];
	            CopyArray(data, tempList);
	            list.addAll(Arrays.asList(tempList));

	            data = "------------------------------------------------\r\n".getBytes();
	            tempList = new Byte[data.length];
	            CopyArray(data, tempList);
	            list.addAll(Arrays.asList(tempList));

	            data = "Total".getBytes();
	            tempList = new Byte[data.length];
	            CopyArray(data, tempList);
	            list.addAll(Arrays.asList(tempList));

	            // Character expansion
	            list.addAll(Arrays.asList(new Byte[]{0x06, 0x09, 0x1b, 0x69, 0x01, 0x01}));

	            data = "        $156.95\r\n".getBytes();
	            tempList = new Byte[data.length];
	            CopyArray(data, tempList);
	            list.addAll(Arrays.asList(tempList));

	            list.addAll(Arrays.asList(new Byte[]{0x1b, 0x69, 0x00, 0x00}));  //Cancel Character Expansion

	            data = "------------------------------------------------\r\n\r\n".getBytes();
	            tempList = new Byte[data.length];
	            CopyArray(data, tempList);
	            list.addAll(Arrays.asList(tempList));

	            data = "Charge\r\n159.95\r\n".getBytes();
	            tempList = new Byte[data.length];
	            CopyArray(data, tempList);
	            list.addAll(Arrays.asList(tempList));

	            data = "Visa XXXX-XXXX-XXXX-0123\r\n\r\n".getBytes();
	            tempList = new Byte[data.length];
	            CopyArray(data, tempList);
	            list.addAll(Arrays.asList(tempList));

	            data = "\u001b\u0034Refunds and Exchanges\u001b\u0035\r\n".getBytes();  //Specify/Cancel White/Black Invert
	            tempList = new Byte[data.length];
	            CopyArray(data, tempList);
	            list.addAll(Arrays.asList(tempList));

	            data = ("Within " + "\u001b\u002d\u0001" + "30 days\u001b\u002d\u0000" + " with receipt\r\n").getBytes();  //Specify/Cancel Underline Printing
	            tempList = new Byte[data.length];
	            CopyArray(data, tempList);
	            list.addAll(Arrays.asList(tempList));

	            data = ("And tags attached\r\n\r\n").getBytes();
	            tempList = new Byte[data.length];
	            CopyArray(data, tempList);
	            list.addAll(Arrays.asList(tempList));

	            //1D barcode example 
	            list.addAll(Arrays.asList(new Byte[]{0x1b, 0x1d, 0x61, 0x01}));
	            list.addAll(Arrays.asList(new Byte[]{0x1b, 0x62, 0x06, 0x02, 0x02}));

	            data = (" 12ab34cd56\u001e\r\n").getBytes();
	            tempList = new Byte[data.length];
	            CopyArray(data, tempList);
	            list.addAll(Arrays.asList(tempList));

	            list.addAll(Arrays.asList(new Byte[]{0x1b, 0x64, 0x02}));    // Cut

	            
			}
			else if (strPrintArea.equals("4inch (112mm)"))
			{
	            byte[] data;

	            Byte[] tempList;
	    		
	            list.addAll(Arrays.asList(new Byte[]{0x1b, 0x1d, 0x61, 0x01}));    // Alignment (center)

//	            data = "[If loaded.. Logo1 goes here]\r\n".getBytes();
//	            tempList = new Byte[data.length];
//	            CopyArray(data, tempList);
//	            list.addAll(Arrays.asList(tempList));
//	            list.addAll(Arrays.asList(new Byte[]{0x1b, 0x1c, 0x70, 0x01, 0x00, '\r', '\n'}));  //Stored Logo Printing

	            data = "\nStar Clothing Boutique\r\n".getBytes();
	            tempList = new Byte[data.length];
	            CopyArray(data, tempList);
	            list.addAll(Arrays.asList(tempList));

	            data = "123 Star Road\r\nCity, State 12345\r\n\r\n".getBytes();
	            tempList = new Byte[data.length];
	            CopyArray(data, tempList);
	            list.addAll(Arrays.asList(tempList));

	            list.addAll(Arrays.asList(new Byte[]{0x1b, 0x1d, 0x61, 0x00})); // Alignment

	            list.addAll(Arrays.asList(new Byte[]{0x1b, 0x44, 0x02, 0x10, 0x22, 0x00})); //Set horizontal tab

	            data = "Date: MM/DD/YYYY     \u0009               \u0009       Time:HH:MM PM\r\n".getBytes();
	            tempList = new Byte[data.length];
	            CopyArray(data, tempList);
	            list.addAll(Arrays.asList(tempList));

	            data = "---------------------------------------------------------------------\r\n\r\n".getBytes();
	            tempList = new Byte[data.length];
	            CopyArray(data, tempList);
	            list.addAll(Arrays.asList(tempList));

	            list.addAll(Arrays.asList(new Byte[]{0x1b, 0x45})); // bold

	            data = "SALE \r\n".getBytes();
	            tempList = new Byte[data.length];
	            CopyArray(data, tempList);
	            list.addAll(Arrays.asList(tempList));

	            list.addAll(Arrays.asList(new Byte[]{0x1b, 0x46})); // bolf off

	            data = "SKU ".getBytes();
	            tempList = new Byte[data.length];
	            CopyArray(data, tempList);
	            list.addAll(Arrays.asList(tempList));

	            list.addAll(Arrays.asList(new Byte[]{0x09}));

	            // Notice that we use a unicode representation because that is how Java expresses these bytes as double byte unicode
	            // This will TAB to the next horizontal position
	            data = "            Description         \u0009\u0009\u0009                Total\r\n".getBytes();
	            tempList = new Byte[data.length];
	            CopyArray(data, tempList);
	            list.addAll(Arrays.asList(tempList));

	            data = "300678566      \u0009            PLAIN T-SHIRT\u0009                       10.99\r\n".getBytes();
	            tempList = new Byte[data.length];
	            CopyArray(data, tempList);
	            list.addAll(Arrays.asList(tempList));

	            data = "300692003      \u0009            BLACK DENIM\u0009                         29.99\r\n".getBytes();
	            tempList = new Byte[data.length];
	            CopyArray(data, tempList);
	            list.addAll(Arrays.asList(tempList));

	            data = "300651148      \u0009            BLUE DENIM\u0009                          29.99\r\n".getBytes();
	            tempList = new Byte[data.length];
	            CopyArray(data, tempList);
	            list.addAll(Arrays.asList(tempList));

	            data = "300642980      \u0009            STRIPED DRESS\u0009                       49.99\r\n".getBytes();
	            tempList = new Byte[data.length];
	            CopyArray(data, tempList);
	            list.addAll(Arrays.asList(tempList));

	            data = "300638471      \u0009            BLACK BOOTS\u0009                         35.99\r\n\r\n".getBytes();
	            tempList = new Byte[data.length];
	            CopyArray(data, tempList);
	            list.addAll(Arrays.asList(tempList));

	            data = "Subtotal       \u0009                       \u0009                        156.95\r\n".getBytes();
	            tempList = new Byte[data.length];
	            CopyArray(data, tempList);
	            list.addAll(Arrays.asList(tempList));

	            data = "Tax            \u0009                       \u0009                          0.00\r\n".getBytes();
	            tempList = new Byte[data.length];
	            CopyArray(data, tempList);
	            list.addAll(Arrays.asList(tempList));

	            data = "---------------------------------------------------------------------\r\n".getBytes();
	            tempList = new Byte[data.length];
	            CopyArray(data, tempList);
	            list.addAll(Arrays.asList(tempList));

	            data = "Total".getBytes();
	            tempList = new Byte[data.length];
	            CopyArray(data, tempList);
	            list.addAll(Arrays.asList(tempList));

	            // Character expansion
	            list.addAll(Arrays.asList(new Byte[]{0x06, 0x09, 0x1b, 0x69, 0x01, 0x01}));

	            data = "\u0009         $156.95\r\n".getBytes();
	            tempList = new Byte[data.length];
	            CopyArray(data, tempList);
	            list.addAll(Arrays.asList(tempList));

	            list.addAll(Arrays.asList(new Byte[]{0x1b, 0x69, 0x00, 0x00}));  //Cancel Character Expansion

	            data = "---------------------------------------------------------------------\r\n\r\n".getBytes();
	            tempList = new Byte[data.length];
	            CopyArray(data, tempList);
	            list.addAll(Arrays.asList(tempList));

	            data = "Charge\r\n159.95\r\n".getBytes();
	            tempList = new Byte[data.length];
	            CopyArray(data, tempList);
	            list.addAll(Arrays.asList(tempList));

	            data = "Visa XXXX-XXXX-XXXX-0123\r\n\r\n".getBytes();
	            tempList = new Byte[data.length];
	            CopyArray(data, tempList);
	            list.addAll(Arrays.asList(tempList));

	            data = "\u001b\u0034Refunds and Exchanges\u001b\u0035\r\n".getBytes();  //Specify/Cancel White/Black Invert
	            tempList = new Byte[data.length];
	            CopyArray(data, tempList);
	            list.addAll(Arrays.asList(tempList));

	            data = ("Within " + "\u001b\u002d\u0001" + "30 days\u001b\u002d\u0000" + " with receipt\r\n").getBytes();  //Specify/Cancel Underline Printing
	            tempList = new Byte[data.length];
	            CopyArray(data, tempList);
	            list.addAll(Arrays.asList(tempList));

	            data = ("And tags attached\r\n\r\n").getBytes();
	            tempList = new Byte[data.length];
	            CopyArray(data, tempList);
	            list.addAll(Arrays.asList(tempList));

	            //1D barcode example 
	            list.addAll(Arrays.asList(new Byte[]{0x1b, 0x1d, 0x61, 0x01}));
	            list.addAll(Arrays.asList(new Byte[]{0x1b, 0x62, 0x06, 0x02, 0x02}));

	            data = (" 12ab34cd56\u001e\r\n").getBytes();
	            tempList = new Byte[data.length];
	            CopyArray(data, tempList);
	            list.addAll(Arrays.asList(tempList));

	            list.addAll(Arrays.asList(new Byte[]{0x1b, 0x64, 0x02}));    // Cut

			}
		} else if (commandType == "Raster") {

			if (strPrintArea.equals("3inch (78mm)"))
			{
	            ArrayList<Byte> list3inch = new ArrayList<Byte>();
	            Byte[] tempList;
	            
				printableArea = 576;    // Printable area in paper is 832(dot)
				
				RasterDocument rasterDoc = new RasterDocument(RasSpeed.Medium, RasPageEndMode.FeedAndFullCut, RasPageEndMode.FeedAndFullCut, RasTopMargin.Standard, 0, 0, 0);				
				byte[] command = rasterDoc.BeginDocumentCommandData();
				tempList = new Byte[command.length];
				CopyArray(command, tempList);
				list3inch.addAll(Arrays.asList(tempList));
				
				String textToPrint = ("                       Star Clothing Boutique\r\n" +
						              "                             123 Star Road\r\n" +
						              "                           City, State 12345\r\n\r\n" + 
						              "Date: MM/DD/YYYY                 Time:HH:MM PM\r\n" +
						              "-----------------------------------------------------------------------\r");
	            command = createRasterCommand(textToPrint, 13, 0);
				tempList = new Byte[command.length];
				CopyArray(command, tempList);
				list3inch.addAll(Arrays.asList(tempList));
	            
				textToPrint = ("SALE");
                command = createRasterCommand(textToPrint, 13, Typeface.BOLD);
				tempList = new Byte[command.length];
				CopyArray(command, tempList);
				list3inch.addAll(Arrays.asList(tempList));

				textToPrint = ("SKU \t\t\t                 Description \t\t                Total\r\n" + 
			                   "300678566 \t\t\t      PLAIN T-SHIRT		\t\t    10.99\n" +
			                   "300692003 \t\t\t      BLACK DENIM		\t\t    29.99\n" +
			                   "300651148 \t\t\t      BLUE DENIM		\t\t       29.99\n" +
			                   "300642980 \t\t\t      STRIPED DRESS		\t       49.99\n" +
			                   "300638471 \t\t\t      BLACK BOOTS		\t\t       35.99\n\n" +
			                   "Subtotal\t\t\t\t                                              156.95\r\n" +
			                   "Tax		\t\t\t\t                                                     0.00\r\n" +
			                   "-----------------------------------------------------------------------\r\n" +
			                   "Total   \t                                                   $156.95\r\n" +
			                   "-----------------------------------------------------------------------\r\n\r\n" +
			                   "Charge\r\n159.95\r\n" +
			                   "Visa XXXX-XXXX-XXXX-0123\r\n");
                
                command = createRasterCommand(textToPrint, 13, 0);
				tempList = new Byte[command.length];
				CopyArray(command, tempList);
				list3inch.addAll(Arrays.asList(tempList));
                
                textToPrint = ("Refunds and Exchanges");
                command = createRasterCommand(textToPrint, 13, Typeface.BOLD);
				tempList = new Byte[command.length];
				CopyArray(command, tempList);
				list3inch.addAll(Arrays.asList(tempList));
                
                textToPrint = ("Within 30 days with receipt\r\n" +
	           	               "And tags attached");
                command = createRasterCommand(textToPrint, 13, 0);
				tempList = new Byte[command.length];
				CopyArray(command, tempList);
				list3inch.addAll(Arrays.asList(tempList));
                
	    		Bitmap bm = BitmapFactory.decodeResource(res, R.drawable.qrcode);
	    		StarBitmap starbitmap = new StarBitmap(bm, false, 146);
	    		command = starbitmap.getImageRasterDataForPrinting(true);
				tempList = new Byte[command.length];
				CopyArray(command, tempList);
				list3inch.addAll(Arrays.asList(tempList));
                
				command = rasterDoc.EndDocumentCommandData();
				tempList = new Byte[command.length];
				CopyArray(command, tempList);
				list3inch.addAll(Arrays.asList(tempList));
				
				sendCommand(context, portName, portSettings, list3inch);
			}
			else if (strPrintArea.equals("4inch (112mm)"))
			{
	            ArrayList<Byte> list4inch = new ArrayList<Byte>();
	            Byte[] tempList;
				
				printableArea = 832;    // Printable area in paper is 832(dot)
				
				RasterDocument rasterDoc = new RasterDocument(RasSpeed.Medium, RasPageEndMode.FeedAndFullCut, RasPageEndMode.FeedAndFullCut, RasTopMargin.Standard, 0, 0, 0);				
				byte[] command = rasterDoc.BeginDocumentCommandData();
				tempList = new Byte[command.length];
				CopyArray(command, tempList);
				list4inch.addAll(Arrays.asList(tempList));
				
				String textToPrint = ("                                          Star Clothing Boutique\r\n" +
						              "                                                123 Star Road\r\n" +
						              "                                              City, State 12345\r\n\r\n" + 
						              "Date: MM/DD/YYYY                                                      Time:HH:MM PM\r\n" +
						              "-------------------------------------------------------------------------------------------------------\r");
	            command = createRasterCommand(textToPrint, 13, 0);
				tempList = new Byte[command.length];
				CopyArray(command, tempList);
				list4inch.addAll(Arrays.asList(tempList));
	            
				textToPrint = ("SALE");
                command = createRasterCommand(textToPrint, 13, Typeface.BOLD);
				tempList = new Byte[command.length];
				CopyArray(command, tempList);
				list4inch.addAll(Arrays.asList(tempList));

				textToPrint = ("SKU \t\t\t                                   Description \t\t                                  Total\r\n" + 
			                   "300678566 \t\t\t                        PLAIN T-SHIRT		\t\t                      10.99\n" +
			                   "300692003 \t\t\t                        BLACK DENIM		\t\t                      29.99\n" +
			                   "300651148 \t\t\t                        BLUE DENIM		\t\t                         29.99\n" +
			                   "300642980 \t\t\t                        STRIPED DRESS		\t                         49.99\n" +
			                   "300638471 \t\t\t                        BLACK BOOTS		\t\t                      35.99\n\n");

                command = createRasterCommand(textToPrint, 13, 0);
				tempList = new Byte[command.length];
				CopyArray(command, tempList);
				list4inch.addAll(Arrays.asList(tempList));

				textToPrint = ("Subtotal\t\t\t\t                                                                                  156.95\r\n" +
			                   "Tax		\t\t\t\t                                                                                         0.00\r\n" +
			                   "-------------------------------------------------------------------------------------------------------\r" +
			                   "Total   \t                                                                                       $156.95\r\n" +
			                   "-------------------------------------------------------------------------------------------------------\r\n\r\n" +
			                   "Charge\r\n159.95\r\n" +
			                   "Visa XXXX-XXXX-XXXX-0123\r\n");

                command = createRasterCommand(textToPrint, 13, 0);
				tempList = new Byte[command.length];
				CopyArray(command, tempList);
				list4inch.addAll(Arrays.asList(tempList));
                
                textToPrint = ("Refunds and Exchanges");
                command = createRasterCommand(textToPrint, 13, Typeface.BOLD);
				tempList = new Byte[command.length];
				CopyArray(command, tempList);
				list4inch.addAll(Arrays.asList(tempList));
                
                textToPrint = ("Within 30 days with receipt\r\n" +
	           	               "And tags attached");
                command = createRasterCommand(textToPrint, 13, 0);
				tempList = new Byte[command.length];
				CopyArray(command, tempList);
				list4inch.addAll(Arrays.asList(tempList));
                
	    		Bitmap bm = BitmapFactory.decodeResource(res, R.drawable.qrcode);
	    		StarBitmap starbitmap = new StarBitmap(bm, false, 146);
	    		command = starbitmap.getImageRasterDataForPrinting(true);
				tempList = new Byte[command.length];
				CopyArray(command, tempList);
				list4inch.addAll(Arrays.asList(tempList));

				command = rasterDoc.EndDocumentCommandData();
				tempList = new Byte[command.length];
				CopyArray(command, tempList);
				list4inch.addAll(Arrays.asList(tempList));
				
				sendCommand(context, portName, portSettings, list4inch);
			}
		}

		return list;
	}
	
    private static byte[] createRasterCommand(String printText, int textSize, int bold) {
    	byte[] command;
    	
		Paint paint = new Paint();
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.BLACK);
		paint.setAntiAlias(true);

		Typeface typeface;

		try {
		    typeface = Typeface.create(Typeface.SERIF, bold);
		} catch (Exception e) {
			typeface = Typeface.create(Typeface.DEFAULT, bold);
		}
		
		paint.setTypeface(typeface);
		paint.setTextSize(textSize * 2);
		paint.setLinearText(true);

		TextPaint textpaint = new TextPaint(paint);
        textpaint.setLinearText(true);
		android.text.StaticLayout staticLayout =  new StaticLayout(printText, textpaint, printableArea, Layout.Alignment.ALIGN_NORMAL, 1, 0, false);
		int height = staticLayout.getHeight();

		Bitmap bitmap = Bitmap.createBitmap(staticLayout.getWidth(), height, Bitmap.Config.RGB_565);
		Canvas c = new Canvas(bitmap);
		c.drawColor(Color.WHITE);
		c.translate(0, 0);
		staticLayout.draw(c);
		
		StarBitmap starbitmap = new StarBitmap(bitmap, false, printableArea);
		
		command = starbitmap.getImageRasterDataForPrinting(true);
		
    	return command;
    }
	
	/**
	 * This function shows how to create the receipt data of a portable printer.
	 * @param context - Activity for displaying messages to the user
	 * @param portName - Port name to use for communication. This should be (TCP:<IPAddress> or BT:<Device pair name>)
	 * @param portSettings - Should be mini, the port settings mini is used for portable printers
	 * @param strPrintArea - Printable area size, This should be ("2inch (58mm)" or "3inch (78mm)")
	 */
    public static ArrayList<Byte> CreateMiniSampleReceipt(Context context, String portName, String portSettings, String strPrintArea)
    {		
        ArrayList<Byte> list = new ArrayList<Byte>();
        
    	if (strPrintArea.equals("2inch (58mm)")) {

            Byte[] tempList;
            
			byte[] outputByteBuffer = null;
			list.addAll(Arrays.asList(new Byte[]{0x1d, 0x57, (byte)0x80, 0x31}));     //Page Area Setting     <GS> <W> nL nH  (nL = 128, nH = 1)
			
			list.addAll(Arrays.asList(new Byte[]{0x1b, 0x61, 0x01}));                 //Center Justification  <ESC> a n       (0 Left, 1 Center, 2 Right)
			
//            outputByteBuffer = ("[Print Stored Logo Below]\n\n").getBytes();
//            port.writePort(outputByteBuffer, 0, outputByteBuffer.length);
//			
//            list.addAll(Arrays.asList(new Byte[]{0x1b, 0x66, 0x00}));                 //Stored Logo Printing  <ESC> f n       (n = Store Logo # = 0 or 1 or 2 etc.)
			
			outputByteBuffer = ("Star Clothing Boutique\n" +
					            "123 Star Road\n" +
					            "City, State 12345\n\n").getBytes();
			tempList = new Byte[outputByteBuffer.length];
			CopyArray(outputByteBuffer, tempList);
			list.addAll(Arrays.asList(tempList));
			
			list.addAll(Arrays.asList(new Byte[]{0x1b, 0x61, 0x00}));                 // Left Alignment
			
			outputByteBuffer = ("Date: MM/DD/YYYY   Time:HH:MM PM\n" +
					            "--------------------------------\n").getBytes();
			tempList = new Byte[outputByteBuffer.length];
			CopyArray(outputByteBuffer, tempList);
			list.addAll(Arrays.asList(tempList));
			
			list.addAll(Arrays.asList(new Byte[]{0x1b, 0x45, 0x01}));                 //Set Emphasized Printing ON
			
			outputByteBuffer = ("SALE\n").getBytes();
			tempList = new Byte[outputByteBuffer.length];
			CopyArray(outputByteBuffer, tempList);
			list.addAll(Arrays.asList(tempList));
            
            list.addAll(Arrays.asList(new Byte[]{0x1b, 0x45, 0x00}));                 //Set Emphasized Printing OFF (same command as on)
			
			
			outputByteBuffer = ("300678566  PLAIN T-SHIRT  10.99\n" +
                                "300692003  BLACK DENIM    29.99\n" +
                                "300651148  BLUE DENIM     29.99\n" +
                                "300642980  STRIPED DRESS  49.99\n" +
                                "300638471  BLACK BOOTS    35.99\n\n" +
                                "Subtotal                 156.95" + "\n" +
                                "Tax                        0.00" + "\n" +
                                "--------------------------------\n" +
                                "Total ").getBytes();
			tempList = new Byte[outputByteBuffer.length];
			CopyArray(outputByteBuffer, tempList);
			list.addAll(Arrays.asList(tempList));

			list.addAll(Arrays.asList(new Byte[]{0x1d, 0x21, 0x11}));                 //Width and Height Character Expansion  <GS>  !  n
			
			outputByteBuffer = ("      $156.95\n").getBytes();
			tempList = new Byte[outputByteBuffer.length];
			CopyArray(outputByteBuffer, tempList);
			list.addAll(Arrays.asList(tempList));
            
            list.addAll(Arrays.asList(new Byte[]{0x1d, 0x21, 0x00}));                 //Cancel Expansion - Reference Star Portable Printer Programming Manual
            
            
			outputByteBuffer = ("--------------------------------\n" +
					            "Charge\n" + "$156.95\n" +
                                "Visa XXXX-XXXX-XXXX-0123\n").getBytes();
			tempList = new Byte[outputByteBuffer.length];
			CopyArray(outputByteBuffer, tempList);
			list.addAll(Arrays.asList(tempList));
            
			list.addAll(Arrays.asList(new Byte[]{0x1d, 0x77, 0x02}));                 //for 1D Code39 Barcode
			list.addAll(Arrays.asList(new Byte[]{0x1d, 0x68, 0x64}));                 //for 1D Code39 Barcode
			list.addAll(Arrays.asList(new Byte[]{0x1d, 0x48, 0x01}));                 //for 1D Code39 Barcode
			list.addAll(Arrays.asList(new Byte[]{0x1d, 0x6b, 0x41, 0x0b, 0x30, 0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39, 0x30}));           //for 1D Code39 Barcode
			
			outputByteBuffer = ("\n").getBytes();
			tempList = new Byte[outputByteBuffer.length];
			CopyArray(outputByteBuffer, tempList);
			list.addAll(Arrays.asList(tempList));
			
			list.addAll(Arrays.asList(new Byte[]{0x1d, 0x42, 0x01}));                 //Specify White-Black Invert
            
			outputByteBuffer = ("Refunds and Exchanges\n").getBytes();
			tempList = new Byte[outputByteBuffer.length];
			CopyArray(outputByteBuffer, tempList);
			list.addAll(Arrays.asList(tempList));
            
			list.addAll(Arrays.asList(new Byte[]{0x1d, 0x42, 0x00}));                 //Cancel White-Black Invert

			outputByteBuffer = ("Within ").getBytes();
			tempList = new Byte[outputByteBuffer.length];
			CopyArray(outputByteBuffer, tempList);
			list.addAll(Arrays.asList(tempList));
            
			list.addAll(Arrays.asList(new Byte[]{0x1b, 0x2d, 0x01}));                 //Specify Underline Printing
			
			outputByteBuffer = ("30 days").getBytes();
			tempList = new Byte[outputByteBuffer.length];
			CopyArray(outputByteBuffer, tempList);
			list.addAll(Arrays.asList(tempList));
			
			list.addAll(Arrays.asList(new Byte[]{0x1b, 0x2d, 0x00}));                 //Cancel Underline Printing
			
			outputByteBuffer = (" with receipt\n" +
		                        "And tags attached\n" +
		                        "-------------Sign Here----------\n\n\n" +
		                        "--------------------------------\n" +
		                        "Thank you for buying Star!\n" +
		                        "Scan QR code to visit our site!\n").getBytes();
			tempList = new Byte[outputByteBuffer.length];
			CopyArray(outputByteBuffer, tempList);
			list.addAll(Arrays.asList(tempList));
            
			list.addAll(Arrays.asList(new Byte[]{0x1d, 0x5a, 0x02}));                 //Cancel Underline Printing
			
			byte[] qrcodeByteBuffer = new byte[]{0x1d, 0x5a, 0x02, 0x1b, 0x5a,
					                             0x00, 0x51, 0x04, 0x1C, 0x00,
					                             0x68, 0x74, 0x74, 0x70, 0x3a,
					                             0x2f, 0x2f, 0x77, 0x77, 0x77,
					                             0x2e, 0x53, 0x74, 0x61, 0x72,
					                             0x4d, 0x69, 0x63, 0x72, 0x6f,
					                             0x6e, 0x69, 0x63, 0x73, 0x2e,
					                             0x63, 0x6f, 0x6d};
			
			tempList = new Byte[qrcodeByteBuffer.length];
			CopyArray(qrcodeByteBuffer, tempList);
			list.addAll(Arrays.asList(tempList));       //QR Code (View QR 2D Barcode code for better explanation)

			outputByteBuffer = ("\n\n\n").getBytes();
			tempList = new Byte[outputByteBuffer.length];
			CopyArray(outputByteBuffer, tempList);
			list.addAll(Arrays.asList(tempList));

		} else if (strPrintArea.equals("3inch (78mm)")) {
            Byte[] tempList;
			byte[] outputByteBuffer = null;
			
			list.addAll(Arrays.asList(new Byte[]{0x1d, 0x57, 0x40, 0x32}));           //Page Area Setting     <GS> <W> nL nH  (nL = 64, nH = 2)
			
			list.addAll(Arrays.asList(new Byte[]{0x1b, 0x61, 0x01}));                 //Center Justification  <ESC> a n       (0 Left, 1 Center, 2 Right)
			
//            outputByteBuffer = ("[Print Stored Logo Below]\n\n").getBytes();
//            port.writePort(outputByteBuffer, 0, outputByteBuffer.length);
//			
//            list.addAll(Arrays.asList(new Byte[]{0x1b, 0x66, 0x00}));                 //Stored Logo Printing  <ESC> f n       (n = Store Logo # = 0 or 1 or 2 etc.)
			
			outputByteBuffer = ("\nStar Clothing Boutique\n" +
					            "123 Star Road\n" +
					            "City, State 12345\n\n").getBytes();
			tempList = new Byte[outputByteBuffer.length];
			CopyArray(outputByteBuffer, tempList);
			list.addAll(Arrays.asList(tempList));
			
			list.addAll(Arrays.asList(new Byte[]{0x1b, 0x61, 0x00}));                 // Left Alignment
			
			list.addAll(Arrays.asList(new Byte[]{0x1b, 0x44, 0x02, 0x10, 0x22, 0x00}));     //Setting Horizontal Tab
                                   
			outputByteBuffer = ("Date: MM/DD/YYYY ").getBytes();
			tempList = new Byte[outputByteBuffer.length];
			CopyArray(outputByteBuffer, tempList);
			list.addAll(Arrays.asList(tempList));
			
		    list.addAll(Arrays.asList(new Byte[]{0x09}));                             // Left Alignment"
		    
		    outputByteBuffer = ("Time: HH:MM PM\n" +
		                        "------------------------------------------------ \n").getBytes();
			tempList = new Byte[outputByteBuffer.length];
			CopyArray(outputByteBuffer, tempList);
			list.addAll(Arrays.asList(tempList));
			
			list.addAll(Arrays.asList(new Byte[]{0x1b, 0x45, 0x01}));                 //Set Emphasized Printing ON
			
			outputByteBuffer = ("SALE\n").getBytes();
			tempList = new Byte[outputByteBuffer.length];
			CopyArray(outputByteBuffer, tempList);
			list.addAll(Arrays.asList(tempList));
            
            list.addAll(Arrays.asList(new Byte[]{0x1b, 0x45, 0x00}));                 //Set Emphasized Printing OFF (same command as on)
			
			outputByteBuffer = ("300678566    PLAIN T-SHIRT                 10.99\n" +
                                "300692003    BLACK DENIM                   29.99\n" +
                                "300651148    BLUE DENIM                    29.99\n" +
                                "300642980    STRIPED DRESS                 49.99\n" +
                                "300638471    BLACK BOOTS                   35.99\n\n" +
                                "Subtotal                                  156.95\n" +
                                "Tax                                         0.00\n" +
                                "------------------------------------------------ \n" +
                                "Total   ").getBytes();
			tempList = new Byte[outputByteBuffer.length];
			CopyArray(outputByteBuffer, tempList);
			list.addAll(Arrays.asList(tempList));

			list.addAll(Arrays.asList(new Byte[]{0x1d, 0x21, 0x11}));                 //Width and Height Character Expansion  <GS>  !  n
			
			outputByteBuffer = ("             $156.95\n").getBytes();
			tempList = new Byte[outputByteBuffer.length];
			CopyArray(outputByteBuffer, tempList);
			list.addAll(Arrays.asList(tempList));
            
            list.addAll(Arrays.asList(new Byte[]{0x1d, 0x21, 0x00}));                 //Cancel Expansion - Reference Star Portable Printer Programming Manual
            
            
			outputByteBuffer = ("------------------------------------------------ \n" +
					            "Charge\n" + "$156.95\n" +
                                "Visa XXXX-XXXX-XXXX-0123\n").getBytes();
			tempList = new Byte[outputByteBuffer.length];
			CopyArray(outputByteBuffer, tempList);
			list.addAll(Arrays.asList(tempList));
            
			list.addAll(Arrays.asList(new Byte[]{0x1d, 0x77, 0x02}));                 //for 1D Code39 Barcode
			list.addAll(Arrays.asList(new Byte[]{0x1d, 0x68, 0x64}));                 //for 1D Code39 Barcode
			list.addAll(Arrays.asList(new Byte[]{0x1d, 0x48, 0x01}));                 //for 1D Code39 Barcode
			list.addAll(Arrays.asList(new Byte[]{0x1d, 0x6b, 0x41, 0x0b, 0x30, 0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39, 0x30}));           //for 1D Code39 Barcode
			
			outputByteBuffer = ("\n").getBytes();
			tempList = new Byte[outputByteBuffer.length];
			CopyArray(outputByteBuffer, tempList);
			list.addAll(Arrays.asList(tempList));
			
			list.addAll(Arrays.asList(new Byte[]{0x1d, 0x42, 0x01}));                 //Specify White-Black Invert
            
			outputByteBuffer = ("Refunds and Exchanges\n").getBytes();
			tempList = new Byte[outputByteBuffer.length];
			CopyArray(outputByteBuffer, tempList);
			list.addAll(Arrays.asList(tempList));
            
			list.addAll(Arrays.asList(new Byte[]{0x1d, 0x42, 0x00}));                 //Cancel White-Black Invert

			outputByteBuffer = ("Within ").getBytes();
			tempList = new Byte[outputByteBuffer.length];
			CopyArray(outputByteBuffer, tempList);
			list.addAll(Arrays.asList(tempList));
            
			list.addAll(Arrays.asList(new Byte[]{0x1b, 0x2d, 0x01}));                 //Specify Underline Printing
			
			outputByteBuffer = ("30 days").getBytes();
			tempList = new Byte[outputByteBuffer.length];
			CopyArray(outputByteBuffer, tempList);
			list.addAll(Arrays.asList(tempList));
			
			list.addAll(Arrays.asList(new Byte[]{0x1b, 0x2d, 0x00}));                 //Cancel Underline Printing
			
			outputByteBuffer = (" with receipt\n" +
                                "And tags attached\n" +
                                "------------- Card Holder's Signature ---------- \n\n\n" +
                                "------------------------------------------------ \n" +
                                "Thank you for buying Star!\n" +
                                "Scan QR code to visit our site!\n").getBytes();
			tempList = new Byte[outputByteBuffer.length];
			CopyArray(outputByteBuffer, tempList);
			list.addAll(Arrays.asList(tempList));
            
			list.addAll(Arrays.asList(new Byte[]{0x1d, 0x5a, 0x02}));                 //Cancel Underline Printing
			
			byte[] qrcodeByteBuffer = new byte[]{0x1d, 0x5a, 0x02, 0x1b, 0x5a,
					                             0x00, 0x51, 0x04, 0x1C, 0x00,
					                             0x68, 0x74, 0x74, 0x70, 0x3a,
					                             0x2f, 0x2f, 0x77, 0x77, 0x77,
					                             0x2e, 0x53, 0x74, 0x61, 0x72,
					                             0x4d, 0x69, 0x63, 0x72, 0x6f,
					                             0x6e, 0x69, 0x63, 0x73, 0x2e,
					                             0x63, 0x6f, 0x6d};
			
			tempList = new Byte[qrcodeByteBuffer.length];
			CopyArray(qrcodeByteBuffer, tempList);
			list.addAll(Arrays.asList(tempList));       //QR Code (View QR 2D Barcode code for better explanation)

			outputByteBuffer = ("\n\n\n\n").getBytes();
			tempList = new Byte[outputByteBuffer.length];
			CopyArray(outputByteBuffer, tempList);
			list.addAll(Arrays.asList(tempList));



		} else if (strPrintArea.equals("4inch (112mm)")) {

            Byte[] tempList;
			byte[] outputByteBuffer = null;
			
			list.addAll(Arrays.asList(new Byte[]{0x1d, 0x57, 0x40, 0x32}));           //Page Area Setting     <GS> <W> nL nH  (nL = 64, nH = 2)
			
			list.addAll(Arrays.asList(new Byte[]{0x1b, 0x61, 0x01}));                 //Center Justification  <ESC> a n       (0 Left, 1 Center, 2 Right)
			
//            outputByteBuffer = ("[Print Stored Logo Below]\n\n").getBytes();
//            port.writePort(outputByteBuffer, 0, outputByteBuffer.length);
//			
//            list.addAll(Arrays.asList(new Byte[]{0x1b, 0x66, 0x00}));                 //Stored Logo Printing  <ESC> f n       (n = Store Logo # = 0 or 1 or 2 etc.)
			
			outputByteBuffer = ("\nStar Clothing Boutique\n" +
					            "123 Star Road\n" +
					            "City, State 12345\n\n").getBytes();
			tempList = new Byte[outputByteBuffer.length];
			CopyArray(outputByteBuffer, tempList);
			list.addAll(Arrays.asList(tempList));
			
			list.addAll(Arrays.asList(new Byte[]{0x1b, 0x61, 0x00}));                 // Left Alignment
			
			list.addAll(Arrays.asList(new Byte[]{0x1b, 0x44, 0x02, 0x1b, 0x34, 0x00}));     //Setting Horizontal Tab
                                   
			outputByteBuffer = ("Date: MM/DD/YYYY ").getBytes();
			tempList = new Byte[outputByteBuffer.length];
			CopyArray(outputByteBuffer, tempList);
			list.addAll(Arrays.asList(tempList));
			
		    list.addAll(Arrays.asList(new Byte[]{0x09}));                             // Left Alignment"
		    
		    outputByteBuffer = ("Time: HH:MM PM\n" +
		                        "--------------------------------------------------------------------- \n").getBytes();
			tempList = new Byte[outputByteBuffer.length];
			CopyArray(outputByteBuffer, tempList);
			list.addAll(Arrays.asList(tempList));
			
			list.addAll(Arrays.asList(new Byte[]{0x1b, 0x45, 0x01}));                 //Set Emphasized Printing ON
			
			outputByteBuffer = ("SALE\n").getBytes();
			tempList = new Byte[outputByteBuffer.length];
			CopyArray(outputByteBuffer, tempList);
			list.addAll(Arrays.asList(tempList));
            
            list.addAll(Arrays.asList(new Byte[]{0x1b, 0x45, 0x00}));                 //Set Emphasized Printing OFF (same command as on)
			
			outputByteBuffer = ("300678566 " + "\u0009" + "  PLAIN T-SHIRT" + "\u0009" + "         10.99\n" +
					            "300692003 " + "\u0009" + "  BLACK DENIM" + "\u0009" + "         29.99\n" +
                                "300651148 " + "\u0009" + "  BLUE DENIM" + "\u0009" + "         29.99\n" +
                                "300642980 " + "\u0009" + "  STRIPED DRESS" + "\u0009" + "         49.99\n" +
                                "300638471 " + "\u0009" + "  BLACK BOOTS" + "\u0009" + "         35.99\n\n" +
                                "Subtotal " + "\u0009" + "" + "\u0009" + "        156.95" +"\n"+
                                "Tax " + "\u0009" + "" + "\u0009" + "" + "          0.00" +"\n"+
                                "--------------------------------------------------------------------- \n" +
                                "Total\u0009").getBytes();
			tempList = new Byte[outputByteBuffer.length];
			CopyArray(outputByteBuffer, tempList);
			list.addAll(Arrays.asList(tempList));

			list.addAll(Arrays.asList(new Byte[]{0x1d, 0x21, 0x11}));                 //Width and Height Character Expansion  <GS>  !  n
			
			outputByteBuffer = ("\u0009$156.95\n").getBytes();
			tempList = new Byte[outputByteBuffer.length];
			CopyArray(outputByteBuffer, tempList);
			list.addAll(Arrays.asList(tempList));
            
            list.addAll(Arrays.asList(new Byte[]{0x1d, 0x21, 0x00}));                 //Cancel Expansion - Reference Star Portable Printer Programming Manual
            
            
			outputByteBuffer = ("--------------------------------------------------------------------- \n" +
					            "Charge\n" + "$156.95\n" +
                                "Visa XXXX-XXXX-XXXX-0123\n").getBytes();
			tempList = new Byte[outputByteBuffer.length];
			CopyArray(outputByteBuffer, tempList);
			list.addAll(Arrays.asList(tempList));
            
			list.addAll(Arrays.asList(new Byte[]{0x1d, 0x77, 0x02}));                 //for 1D Code39 Barcode
			list.addAll(Arrays.asList(new Byte[]{0x1d, 0x68, 0x64}));                 //for 1D Code39 Barcode
			list.addAll(Arrays.asList(new Byte[]{0x1d, 0x48, 0x01}));                 //for 1D Code39 Barcode
			list.addAll(Arrays.asList(new Byte[]{0x1d, 0x6b, 0x41, 0x0b, 0x30, 0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39, 0x30}));           //for 1D Code39 Barcode
			
			outputByteBuffer = ("\n").getBytes();
			tempList = new Byte[outputByteBuffer.length];
			CopyArray(outputByteBuffer, tempList);
			list.addAll(Arrays.asList(tempList));
			
			list.addAll(Arrays.asList(new Byte[]{0x1d, 0x42, 0x01}));                 //Specify White-Black Invert
            
			outputByteBuffer = ("Refunds and Exchanges\n").getBytes();
			tempList = new Byte[outputByteBuffer.length];
			CopyArray(outputByteBuffer, tempList);
			list.addAll(Arrays.asList(tempList));
            
			list.addAll(Arrays.asList(new Byte[]{0x1d, 0x42, 0x00}));                 //Cancel White-Black Invert

			outputByteBuffer = ("Within ").getBytes();
			tempList = new Byte[outputByteBuffer.length];
			CopyArray(outputByteBuffer, tempList);
			list.addAll(Arrays.asList(tempList));
            
			list.addAll(Arrays.asList(new Byte[]{0x1b, 0x2d, 0x01}));                 //Specify Underline Printing
			
			outputByteBuffer = ("30 days").getBytes();
			tempList = new Byte[outputByteBuffer.length];
			CopyArray(outputByteBuffer, tempList);
			list.addAll(Arrays.asList(tempList));
			
			list.addAll(Arrays.asList(new Byte[]{0x1b, 0x2d, 0x00}));                 //Cancel Underline Printing
			
			outputByteBuffer = (" with receipt\n" +
                                "And tags attached\n" +
                                "----------------------- Card Holder's Signature --------------------- \n\n\n" +
                                "--------------------------------------------------------------------- \n" +
                                "Thank you for buying Star!\n" +
                                "Scan QR code to visit our site!\n").getBytes();
			tempList = new Byte[outputByteBuffer.length];
			CopyArray(outputByteBuffer, tempList);
			list.addAll(Arrays.asList(tempList));
            
			list.addAll(Arrays.asList(new Byte[]{0x1d, 0x5a, 0x02}));                 //Cancel Underline Printing
			
			byte[] qrcodeByteBuffer = new byte[]{0x1d, 0x5a, 0x02, 0x1b, 0x5a,
					                             0x00, 0x51, 0x04, 0x1C, 0x00,
					                             0x68, 0x74, 0x74, 0x70, 0x3a,
					                             0x2f, 0x2f, 0x77, 0x77, 0x77,
					                             0x2e, 0x53, 0x74, 0x61, 0x72,
					                             0x4d, 0x69, 0x63, 0x72, 0x6f,
					                             0x6e, 0x69, 0x63, 0x73, 0x2e,
					                             0x63, 0x6f, 0x6d};
			
			tempList = new Byte[qrcodeByteBuffer.length];
			CopyArray(qrcodeByteBuffer, tempList);
			list.addAll(Arrays.asList(tempList));       //QR Code (View QR 2D Barcode code for better explanation)

			outputByteBuffer = ("\n\n\n\n").getBytes();
			tempList = new Byte[outputByteBuffer.length];
			CopyArray(outputByteBuffer, tempList);
			list.addAll(Arrays.asList(tempList));
            
//			sendCommand(context, portName, portSettings, list);
		}
    	
    	return list;
    }
	/**
	 * This function shows how to print the receipt data of a thermal POS printer.
	 * @param context - Activity for displaying messages to the user
	 * @param portName - Port name to use for communication. This should be (TCP:<IPAddress>)
	 * @param portSettings - Should be blank
	 * @param commandType - Command type to use for printing. This should be ("Line" or "Raster")
	 * @param res - The resources object containing the image data. ( e.g.) getResources())
	 * @param strPrintArea - Printable area size, This should be ("3inch (78mm)" or "4inch (112mm)")
	 */
	public static void PrintSampleReceipt(Context context, String portName, String portSettings, String commandType, Resources res, String strPrintArea)
	{	
		if (commandType == "Line")
		{
			if (strPrintArea.equals("3inch (78mm)"))
			{
	            byte[] data;
	            ArrayList<Byte> list = new ArrayList<Byte>();

	            Byte[] tempList;
	    		
	            list.addAll(Arrays.asList(new Byte[]{0x1b, 0x1d, 0x61, 0x01}));    // Alignment (center)

//	            data = "[If loaded.. Logo1 goes here]\r\n".getBytes();
//	            tempList = new Byte[data.length];
//	            CopyArray(data, tempList);
//	            list.addAll(Arrays.asList(tempList));
//
//	            list.addAll(Arrays.asList(new Byte[]{0x1b, 0x1c, 0x70, 0x01, 0x00, '\r', '\n'}));  //Stored Logo Printing

	            data = "\nStar Clothing Boutique\r\n".getBytes();
	            tempList = new Byte[data.length];
	            CopyArray(data, tempList);
	            list.addAll(Arrays.asList(tempList));

	            data = "123 Star Road\r\nCity, State 12345\r\n\r\n".getBytes();
	            tempList = new Byte[data.length];
	            CopyArray(data, tempList);
	            list.addAll(Arrays.asList(tempList));

	            list.addAll(Arrays.asList(new Byte[]{0x1b, 0x1d, 0x61, 0x00})); // Alignment

	            list.addAll(Arrays.asList(new Byte[]{0x1b, 0x44, 0x02, 0x10, 0x22, 0x00})); //Set horizontal tab

	            data = "Date: MM/DD/YYYY".getBytes();
	            tempList = new Byte[data.length];
	            CopyArray(data, tempList);
	            list.addAll(Arrays.asList(tempList));

	            list.addAll(Arrays.asList(new Byte[]{' ', 0x09, ' '}));   //Moving Horizontal Tab

	            data = "Time:HH:MM PM\r\n------------------------------------------------\r\n\r\n".getBytes();
	            tempList = new Byte[data.length];
	            CopyArray(data, tempList);
	            list.addAll(Arrays.asList(tempList));

	            list.addAll(Arrays.asList(new Byte[]{0x1b, 0x45})); // bold

	            data = "SALE \r\n".getBytes();
	            tempList = new Byte[data.length];
	            CopyArray(data, tempList);
	            list.addAll(Arrays.asList(tempList));

	            list.addAll(Arrays.asList(new Byte[]{0x1b, 0x46})); // bolf off

	            data = "SKU ".getBytes();
	            tempList = new Byte[data.length];
	            CopyArray(data, tempList);
	            list.addAll(Arrays.asList(tempList));

	            list.addAll(Arrays.asList(new Byte[]{0x09}));

	            // Notice that we use a unicode representation because that is how Java expresses these bytes as double byte unicode
	            // This will TAB to the next horizontal position
	            data = "  Description   \u0009         Total\r\n".getBytes();
	            tempList = new Byte[data.length];
	            CopyArray(data, tempList);
	            list.addAll(Arrays.asList(tempList));

	            data = "300678566 \u0009  PLAIN T-SHIRT\u0009         10.99\r\n".getBytes();
	            tempList = new Byte[data.length];
	            CopyArray(data, tempList);
	            list.addAll(Arrays.asList(tempList));

	            data = "300692003 \u0009  BLACK DENIM\u0009         29.99\r\n".getBytes();
	            tempList = new Byte[data.length];
	            CopyArray(data, tempList);
	            list.addAll(Arrays.asList(tempList));

	            data = "300651148 \u0009  BLUE DENIM\u0009         29.99\r\n".getBytes();
	            tempList = new Byte[data.length];
	            CopyArray(data, tempList);
	            list.addAll(Arrays.asList(tempList));

	            data = "300642980 \u0009  STRIPED DRESS\u0009         49.99\r\n".getBytes();
	            tempList = new Byte[data.length];
	            CopyArray(data, tempList);
	            list.addAll(Arrays.asList(tempList));

	            data = "300638471 \u0009  BLACK BOOTS\u0009         35.99\r\n\r\n".getBytes();
	            tempList = new Byte[data.length];
	            CopyArray(data, tempList);
	            list.addAll(Arrays.asList(tempList));

	            data = "Subtotal \u0009\u0009        156.95\r\n".getBytes();
	            tempList = new Byte[data.length];
	            CopyArray(data, tempList);
	            list.addAll(Arrays.asList(tempList));

	            data = "Tax \u0009\u0009          0.00\r\n".getBytes();
	            tempList = new Byte[data.length];
	            CopyArray(data, tempList);
	            list.addAll(Arrays.asList(tempList));

	            data = "------------------------------------------------\r\n".getBytes();
	            tempList = new Byte[data.length];
	            CopyArray(data, tempList);
	            list.addAll(Arrays.asList(tempList));

	            data = "Total".getBytes();
	            tempList = new Byte[data.length];
	            CopyArray(data, tempList);
	            list.addAll(Arrays.asList(tempList));

	            // Character expansion
	            list.addAll(Arrays.asList(new Byte[]{0x06, 0x09, 0x1b, 0x69, 0x01, 0x01}));

	            data = "        $156.95\r\n".getBytes();
	            tempList = new Byte[data.length];
	            CopyArray(data, tempList);
	            list.addAll(Arrays.asList(tempList));

	            list.addAll(Arrays.asList(new Byte[]{0x1b, 0x69, 0x00, 0x00}));  //Cancel Character Expansion

	            data = "------------------------------------------------\r\n\r\n".getBytes();
	            tempList = new Byte[data.length];
	            CopyArray(data, tempList);
	            list.addAll(Arrays.asList(tempList));

	            data = "Charge\r\n159.95\r\n".getBytes();
	            tempList = new Byte[data.length];
	            CopyArray(data, tempList);
	            list.addAll(Arrays.asList(tempList));

	            data = "Visa XXXX-XXXX-XXXX-0123\r\n\r\n".getBytes();
	            tempList = new Byte[data.length];
	            CopyArray(data, tempList);
	            list.addAll(Arrays.asList(tempList));

	            data = "\u001b\u0034Refunds and Exchanges\u001b\u0035\r\n".getBytes();  //Specify/Cancel White/Black Invert
	            tempList = new Byte[data.length];
	            CopyArray(data, tempList);
	            list.addAll(Arrays.asList(tempList));

	            data = ("Within " + "\u001b\u002d\u0001" + "30 days\u001b\u002d\u0000" + " with receipt\r\n").getBytes();  //Specify/Cancel Underline Printing
	            tempList = new Byte[data.length];
	            CopyArray(data, tempList);
	            list.addAll(Arrays.asList(tempList));

	            data = ("And tags attached\r\n\r\n").getBytes();
	            tempList = new Byte[data.length];
	            CopyArray(data, tempList);
	            list.addAll(Arrays.asList(tempList));

	            //1D barcode example 
	            list.addAll(Arrays.asList(new Byte[]{0x1b, 0x1d, 0x61, 0x01}));
	            list.addAll(Arrays.asList(new Byte[]{0x1b, 0x62, 0x06, 0x02, 0x02}));

	            data = (" 12ab34cd56\u001e\r\n").getBytes();
	            tempList = new Byte[data.length];
	            CopyArray(data, tempList);
	            list.addAll(Arrays.asList(tempList));

	            list.addAll(Arrays.asList(new Byte[]{0x1b, 0x64, 0x02}));    // Cut

	            
	    		sendCommand(context, portName, portSettings, list);
			}
			else if (strPrintArea.equals("4inch (112mm)"))
			{
	            byte[] data;
	            ArrayList<Byte> list = new ArrayList<Byte>();

	            Byte[] tempList;
	    		
	            list.addAll(Arrays.asList(new Byte[]{0x1b, 0x1d, 0x61, 0x01}));    // Alignment (center)

//	            data = "[If loaded.. Logo1 goes here]\r\n".getBytes();
//	            tempList = new Byte[data.length];
//	            CopyArray(data, tempList);
//	            list.addAll(Arrays.asList(tempList));
//	            list.addAll(Arrays.asList(new Byte[]{0x1b, 0x1c, 0x70, 0x01, 0x00, '\r', '\n'}));  //Stored Logo Printing

	            data = "\nStar Clothing Boutique\r\n".getBytes();
	            tempList = new Byte[data.length];
	            CopyArray(data, tempList);
	            list.addAll(Arrays.asList(tempList));

	            data = "123 Star Road\r\nCity, State 12345\r\n\r\n".getBytes();
	            tempList = new Byte[data.length];
	            CopyArray(data, tempList);
	            list.addAll(Arrays.asList(tempList));

	            list.addAll(Arrays.asList(new Byte[]{0x1b, 0x1d, 0x61, 0x00})); // Alignment

	            list.addAll(Arrays.asList(new Byte[]{0x1b, 0x44, 0x02, 0x10, 0x22, 0x00})); //Set horizontal tab

	            data = "Date: MM/DD/YYYY     \u0009               \u0009       Time:HH:MM PM\r\n".getBytes();
	            tempList = new Byte[data.length];
	            CopyArray(data, tempList);
	            list.addAll(Arrays.asList(tempList));

	            data = "---------------------------------------------------------------------\r\n\r\n".getBytes();
	            tempList = new Byte[data.length];
	            CopyArray(data, tempList);
	            list.addAll(Arrays.asList(tempList));

	            list.addAll(Arrays.asList(new Byte[]{0x1b, 0x45})); // bold

	            data = "SALE \r\n".getBytes();
	            tempList = new Byte[data.length];
	            CopyArray(data, tempList);
	            list.addAll(Arrays.asList(tempList));

	            list.addAll(Arrays.asList(new Byte[]{0x1b, 0x46})); // bolf off

	            data = "SKU ".getBytes();
	            tempList = new Byte[data.length];
	            CopyArray(data, tempList);
	            list.addAll(Arrays.asList(tempList));

	            list.addAll(Arrays.asList(new Byte[]{0x09}));

	            // Notice that we use a unicode representation because that is how Java expresses these bytes as double byte unicode
	            // This will TAB to the next horizontal position
	            data = "            Description         \u0009\u0009\u0009                Total\r\n".getBytes();
	            tempList = new Byte[data.length];
	            CopyArray(data, tempList);
	            list.addAll(Arrays.asList(tempList));

	            data = "300678566      \u0009            PLAIN T-SHIRT\u0009                       10.99\r\n".getBytes();
	            tempList = new Byte[data.length];
	            CopyArray(data, tempList);
	            list.addAll(Arrays.asList(tempList));

	            data = "300692003      \u0009            BLACK DENIM\u0009                         29.99\r\n".getBytes();
	            tempList = new Byte[data.length];
	            CopyArray(data, tempList);
	            list.addAll(Arrays.asList(tempList));

	            data = "300651148      \u0009            BLUE DENIM\u0009                          29.99\r\n".getBytes();
	            tempList = new Byte[data.length];
	            CopyArray(data, tempList);
	            list.addAll(Arrays.asList(tempList));

	            data = "300642980      \u0009            STRIPED DRESS\u0009                       49.99\r\n".getBytes();
	            tempList = new Byte[data.length];
	            CopyArray(data, tempList);
	            list.addAll(Arrays.asList(tempList));

	            data = "300638471      \u0009            BLACK BOOTS\u0009                         35.99\r\n\r\n".getBytes();
	            tempList = new Byte[data.length];
	            CopyArray(data, tempList);
	            list.addAll(Arrays.asList(tempList));

	            data = "Subtotal       \u0009                       \u0009                        156.95\r\n".getBytes();
	            tempList = new Byte[data.length];
	            CopyArray(data, tempList);
	            list.addAll(Arrays.asList(tempList));

	            data = "Tax            \u0009                       \u0009                          0.00\r\n".getBytes();
	            tempList = new Byte[data.length];
	            CopyArray(data, tempList);
	            list.addAll(Arrays.asList(tempList));

	            data = "---------------------------------------------------------------------\r\n".getBytes();
	            tempList = new Byte[data.length];
	            CopyArray(data, tempList);
	            list.addAll(Arrays.asList(tempList));

	            data = "Total".getBytes();
	            tempList = new Byte[data.length];
	            CopyArray(data, tempList);
	            list.addAll(Arrays.asList(tempList));

	            // Character expansion
	            list.addAll(Arrays.asList(new Byte[]{0x06, 0x09, 0x1b, 0x69, 0x01, 0x01}));

	            data = "\u0009         $156.95\r\n".getBytes();
	            tempList = new Byte[data.length];
	            CopyArray(data, tempList);
	            list.addAll(Arrays.asList(tempList));

	            list.addAll(Arrays.asList(new Byte[]{0x1b, 0x69, 0x00, 0x00}));  //Cancel Character Expansion

	            data = "---------------------------------------------------------------------\r\n\r\n".getBytes();
	            tempList = new Byte[data.length];
	            CopyArray(data, tempList);
	            list.addAll(Arrays.asList(tempList));

	            data = "Charge\r\n159.95\r\n".getBytes();
	            tempList = new Byte[data.length];
	            CopyArray(data, tempList);
	            list.addAll(Arrays.asList(tempList));

	            data = "Visa XXXX-XXXX-XXXX-0123\r\n\r\n".getBytes();
	            tempList = new Byte[data.length];
	            CopyArray(data, tempList);
	            list.addAll(Arrays.asList(tempList));

	            data = "\u001b\u0034Refunds and Exchanges\u001b\u0035\r\n".getBytes();  //Specify/Cancel White/Black Invert
	            tempList = new Byte[data.length];
	            CopyArray(data, tempList);
	            list.addAll(Arrays.asList(tempList));

	            data = ("Within " + "\u001b\u002d\u0001" + "30 days\u001b\u002d\u0000" + " with receipt\r\n").getBytes();  //Specify/Cancel Underline Printing
	            tempList = new Byte[data.length];
	            CopyArray(data, tempList);
	            list.addAll(Arrays.asList(tempList));

	            data = ("And tags attached\r\n\r\n").getBytes();
	            tempList = new Byte[data.length];
	            CopyArray(data, tempList);
	            list.addAll(Arrays.asList(tempList));

	            //1D barcode example 
	            list.addAll(Arrays.asList(new Byte[]{0x1b, 0x1d, 0x61, 0x01}));
	            list.addAll(Arrays.asList(new Byte[]{0x1b, 0x62, 0x06, 0x02, 0x02}));

	            data = (" 12ab34cd56\u001e\r\n").getBytes();
	            tempList = new Byte[data.length];
	            CopyArray(data, tempList);
	            list.addAll(Arrays.asList(tempList));

	            list.addAll(Arrays.asList(new Byte[]{0x1b, 0x64, 0x02}));    // Cut

	    		sendCommand(context, portName, portSettings, list);
			}
		} else if (commandType == "Raster") {	}
	}
	
    private static void CopyArray(byte[] srcArray, Byte[] cpyArray) {
    	for (int index = 0; index < cpyArray.length; index++) {
    		cpyArray[index] = srcArray[index];
    	}
    }
	/**
	 * This function checks the status of the printer
	 * @param context - Activity for displaying messages to the user
	 * @param portName - Port name to use for communication. This should be (TCP:<IPAddress>)
	 * @param portSettings - Should be blank
	 * @param sensorActiveHigh - boolean variable to tell the sensor active of CashDrawer which is High  
	 */
	public static void CheckStatus(Context context, String portName, String portSettings, boolean sensorActiveHigh)
	{
		StarIOPort port = null;
		try 
    	{
			/*
				using StarIOPort3.1.jar (support USB Port)
				Android OS Version: upper 2.2
			*/
			port = StarIOPort.getPort(portName, portSettings, 10000, context);
			/* 
				using StarIOPort.jar
				Android OS Version: under 2.1
				port = StarIOPort.getPort(portName, portSettings, 10000);
			*/
			
			try
			{
				Thread.sleep(500);
			}
			catch(InterruptedException e) {}
			
			StarPrinterStatus status = port.retreiveStatus();
			
			if(status.offline == false)
			{
				String message = "Printer is Online";
				
				
				Builder dialog = new AlertDialog.Builder(context);
	    		dialog.setNegativeButton("Ok", null);
	    		AlertDialog alert = dialog.create();
	    		alert.setTitle("Printer");
	    		alert.setMessage(message);
	    		alert.setCancelable(false);
	    		alert.show();
			}
			else
			{
				String message = "Printer is Offline";
				
				if(status.receiptPaperEmpty == true)
				{
					message += "\nPaper is Empty";
				}
				
				if(status.coverOpen == true)
				{
					message += "\nCover is Open";
				}
								
				Builder dialog = new AlertDialog.Builder(context);
	    		dialog.setNegativeButton("Ok", null);
	    		AlertDialog alert = dialog.create();
	    		alert.setTitle("Printer");
	    		alert.setMessage(message);
	    		alert.show();
			}
			

		}
    	catch (StarIOPortException e)
    	{
    		Builder dialog = new AlertDialog.Builder(context);
    		dialog.setNegativeButton("Ok", null);
    		AlertDialog alert = dialog.create();
    		alert.setTitle("Failure");
    		alert.setMessage("Failed to connect to printer");
    		alert.show();
    	}
		finally
		{
			if(port != null)
			{
				try {
					StarIOPort.releasePort(port);
				} catch (StarIOPortException e) {}
			}
		}
	}
	
	/**
	 * This function checks the status of the drawer
	 * @param context - Activity for displaying messages to the user
	 * @param portName - Port name to use for communication. This should be (TCP:<IPAddress>)
	 * @param portSettings - Should be blank 
	 */
	public static void CheckDrawerStatus(Context context, String portName, String portSettings)
	{
		StarIOPort port = null;
		try 
		{
			/*
				using StarIOPort3.1.jar (support USB Port)
				Android OS Version: upper 2.2
			*/
			port = StarIOPort.getPort(portName, portSettings, 10000, context);
			/* 
				using StarIOPort.jar
				Android OS Version: under 2.1
				port = StarIOPort.getPort(portName, portSettings, 10000);
			*/
			
			try
			{
				Thread.sleep(500);
			}
			catch(InterruptedException e) {}
			
			StarPrinterStatus status = port.retreiveStatus();
			
			if(status.offline == false)
			{
				String message;
				
				if(status.compulsionSwitch == false)
				{
					message = "Cash Drawer: Close";
				}
				else
				{
					message = "Cash Drawer: Open";
				}
				
				Builder dialog = new AlertDialog.Builder(context);
	    		dialog.setNegativeButton("Ok", null);
	    		AlertDialog alert = dialog.create();
	    		alert.setTitle("Drawer");
	    		alert.setMessage(message);
	    		alert.show();
			}
			else
			{
				String message;
							
				if(status.compulsionSwitch == false)
				{
				    message = "Cash Drawer: Close";
				}
				else
				{
				    message = "Cash Drawer: Open";
				}
				
				Builder dialog = new AlertDialog.Builder(context);
	    		dialog.setNegativeButton("Ok", null);
	    		AlertDialog alert = dialog.create();
	    		alert.setTitle("Drawer");
	    		alert.setMessage(message);
	    		alert.show();
			}
			
	
		}
		catch (StarIOPortException e)
		{
			Builder dialog = new AlertDialog.Builder(context);
			dialog.setNegativeButton("Ok", null);
			AlertDialog alert = dialog.create();
			alert.setTitle("Failure");
			alert.setMessage("Failed to connect to drawer");
			alert.show();
		}
		finally
		{
			if(port != null)
			{
				try {
					StarIOPort.releasePort(port);
				} catch (StarIOPortException e) {}
			}
		}
	}
}

class PrintRecieptThread extends Thread{

	private StarIOPort port = null;
	private String portSettings = "";
	private String portName = "";
	private Context me;
	private byte[] commandToSendToPrinter = null;

	public PrintRecieptThread(Context context, String portName, String portSettings, byte[] commandToSendToPrinter){
    	this.portName = portName;
    	this.portSettings = portSettings; 
    	this.me = context;
    	this.commandToSendToPrinter = commandToSendToPrinter;
	}
	
	public void run(){
		
		
	}
		
	public int startPrint(){
		
		int result = 0;
		
    	try {
    		port = StarIOPort.getPort(portName, portSettings, 10000, me);
    		
			try
			{
				Thread.sleep(100);
			}
			catch (InterruptedException e) { }
			
			StarPrinterStatus status = port.beginCheckedBlock();
		
			if (true == status.offline)
			{
				result = -1;
				String message = "A printer is offline";
				printhandler.obtainMessage(2, message).sendToTarget();
			}			
			

			port.writePort(commandToSendToPrinter, 0, commandToSendToPrinter.length);
			
			
			port.setEndCheckedBlockTimeoutMillis(30000);//Change the timeout time of endCheckedBlock method.
			status = port.endCheckedBlock();

			if (true == status.coverOpen)
			{			
				result = -1;
				String message = "Printer cover is open";
				printhandler.obtainMessage(2, message).sendToTarget();
			}
			else if (true == status.receiptPaperEmpty)
			{
				result = -1;
				String message = "Receipt paper is empty";
				printhandler.obtainMessage(2, message).sendToTarget();
			}
			else if (true == status.offline)
			{				
				result = -1;
				String message = "Printer is offline";
				printhandler.obtainMessage(2, message).sendToTarget();
			}
		} catch ( StarIOPortException e) {
			printhandler.obtainMessage(2, e.getMessage()).sendToTarget();
			result = -1;
		}			
    	finally
    	{
			if (port != null)
			{
				try
				{
					StarIOPort.releasePort(port);
				}
				catch (StarIOPortException e) { result = -1;}
			}
    	}    	
    	return result;
	}
	
	private final Handler printhandler = new Handler() {
		public void handleMessage(Message message) {
			try {
				switch(message.what)
				{
					case 1 :			
	            		Builder dialog = new AlertDialog.Builder(me);
	            		dialog.setNegativeButton("Ok", null);
	            		dialog.setTitle("Failure");
	            		dialog.setMessage("Service discovery failed.");
	            		AlertDialog alert = dialog.create();
	            		alert.show();					
						break;
					case 2 :
	            		Builder dialog2 = new AlertDialog.Builder(me);
	            		dialog2.setNegativeButton("Ok", null);
	            		dialog2.setTitle("Failure");
	            		dialog2.setMessage(message.obj.toString());
	            		AlertDialog alert2 = dialog2.create();
	            		alert2.show();
						break;
				}
			}
			catch (Exception e)
			{
			}
		}
	};
}

 
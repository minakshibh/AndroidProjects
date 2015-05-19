package com.pos.retail;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.IntentCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.example.posapplication.R;
/*import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.lowagie.text.pdf.PdfCell;
*/
import com.pos.util.PoSDatabase;
import com.pos.util.PosConstants;
import com.pos.util.ProductReal;
import com.pos.util.ScreenReceiver;

public class RetunReceipt extends Activity
{
	private static final String LOG_TAG = "GeneratePDF";
	 
	 private EditText preparedBy;
	 private File pdfFile;
	 private String filename = "SampleReturn.pdf";
	 private String filepath = "ZoomKaraoke";
	 ProgressDialog progress_dialog;
//	 private BaseFont bfBold;
	 Context mcontext;
	 BigDecimal totalDiscount,totalTax,totalPayAble,subTotalBD;
	 ArrayList<ProductReal> productList;
	 private Button pdfbtn;
	 ProgressDialog dialog;
	 String TransactionId;
	 String naira ;
	 long curentTime=0;
	 long elapTime = 0;
	@SuppressWarnings("unchecked")
	public void onCreate(Bundle b)
	{
		super.onCreate(b);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.receiptgenerate);
		
		mcontext = this;
		pdfbtn = (Button) findViewById(R.id.pdf);
		pdfbtn.setVisibility(View.GONE);
		
		naira = getResources().getString(R.string.naira);
		Intent in=getIntent();
		TransactionId = in.getStringExtra("TransactionId");
		productList = (ArrayList<ProductReal>)in.getSerializableExtra("listofproduct");
		pdfFile = new File(Environment.getExternalStorageDirectory()
		         +File.separator
		         +filepath //folder name
		         +File.separator);
//		         +filename); //file name
		pdfFile.mkdirs();
		pdfFile=new File(Environment.getExternalStorageDirectory()
		         +File.separator
		         +filepath //folder name
		         +File.separator
		         +filename); //file name
		
	    progress_dialog = new ProgressDialog(this);
	    progress_dialog.setMessage("Please wait..");
	    progress_dialog.setCancelable(false);
	    progress_dialog.show();
		new GeneratePdf().execute();
	}
	
	/*private void generatePDF(String personName){
		 
		  //create a new document
		  Document document = new Document();
		 
		  try {
		    
		   PdfWriter docWriter = PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
		   document.open();
		 
		    
		   PdfContentByte cb = docWriter.getDirectContent();
		   //initialize fonts for text printing
		   initializeFonts();
		 
		   Font font = FontFactory.getFont("assets/arial.ttf",BaseFont.IDENTITY_H);
		   //the company logo is stored in the assets which is read only
		   //get the logo and print on the document
//		   InputStream inputStream = getAssets().open("a.png");
//		   Bitmap bmp = BitmapFactory.decodeStream(inputStream);
//		   ByteArrayOutputStream stream = new ByteArrayOutputStream();
//		   bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
//		   Image companyLogo = Image.getInstance(stream.toByteArray());
//		   companyLogo.setAbsolutePosition(25,700);
//		   companyLogo.scalePercent(25);
//		   document.add(companyLogo); 
//		 
		   //creating a sample invoice with some customer data
		   
		   
		  System.out.println( document.leftMargin()+"  "+document.getPageSize().getHeight());
		 
		  
		  PdfPTable table = new PdfPTable(5);
		  
		  float pageheight = document.getPageSize().getHeight();
		  float pagewidth = document.getPageSize().getWidth();
				  
		  
		  PdfPCell cell = new PdfPCell(new Phrase("Receipt"));
		   cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		   cell.setBackgroundColor(new BaseColor(Color.GRAY));
		   
		   cell.setMinimumHeight(40);
		   cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		   cell.disableBorderSide(PdfCell.LEFT);
		   cell.disableBorderSide(PdfCell.RIGHT);
		   cell.setColspan(5);
		   table.addCell(cell);
		  
		   table.completeRow();
		  
//		   createHeadingsSize(cb,pagewidth/2,pageheight-50,"Receipt",20);
		   
		   
		   
		   cell = new PdfPCell(new Phrase("Transaction ID "+TransactionId));
		   cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		   
		   
		   cell.setMinimumHeight(30);
		   cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		   cell.disableBorderSide(PdfCell.LEFT);
		   cell.disableBorderSide(PdfCell.RIGHT);
		   cell.disableBorderSide(PdfCell.BOTTOM);
		   cell.setColspan(2);
		   table.addCell(cell);
		  
		   
		   
//		   createHeadings(cb,document.leftMargin(),pageheight-100,"Transaction ID "+TransactionId);
//		   
		   
		   
		   SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy_hh:mm:ss");
		   String currentDateandTime = sdf.format(new Date());
		   
		   
		   
		   cell = new PdfPCell(new Phrase("Date_Time "+currentDateandTime));
		   cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		  
		   
		   cell.setMinimumHeight(30);
		   cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		   cell.disableBorderSide(PdfCell.LEFT);
		   cell.disableBorderSide(PdfCell.RIGHT);
		   cell.disableBorderSide(PdfCell.TOP);
		   cell.disableBorderSide(PdfCell.BOTTOM);
		   cell.setColspan(3);
		   table.addCell(cell);
		  
		   table.completeRow();
		   
		   
//		   createHeadings(cb,document.leftMargin()+200,pageheight-100,"Date_Time "+currentDateandTime);
		   
		   
//		   createHeadings(cb,document.leftMargin()+70,pageheight-100,"12312312");
		   
		   cell = new PdfPCell(new Phrase("POS ID "+"1"));
		   cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		  
		   
		   cell.setMinimumHeight(30);
		   cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		   cell.disableBorderSide(PdfCell.LEFT);
		   cell.disableBorderSide(PdfCell.RIGHT);
		   cell.disableBorderSide(PdfCell.TOP);
		   cell.disableBorderSide(PdfCell.BOTTOM);
		   cell.setColspan(5);
		   table.addCell(cell);
		  
		   table.completeRow();
		   
		   
//		   createHeadings(cb,document.leftMargin(),pageheight-120,"POS ID "+"1");
		  
//		   createHeadings(cb,document.leftMargin()+50,pageheight-110,"1");
		   
		   
		   cell = new PdfPCell(new Phrase("Store ID "+"1"));
		   cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		  
		   
		   cell.setMinimumHeight(30);
		   cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		   cell.disableBorderSide(PdfCell.LEFT);
		   cell.disableBorderSide(PdfCell.RIGHT);
		   cell.disableBorderSide(PdfCell.TOP);
		   cell.disableBorderSide(PdfCell.BOTTOM);
		   cell.setColspan(5);
		   table.addCell(cell);
		  
		   table.completeRow();
		   
		  
		  
		   
		   Font font1 = FontFactory.getFont("assets/arial.ttf",BaseFont.IDENTITY_H); 
		   font1.setSize(13);
		   
		   cell = new PdfPCell(new Phrase("Here you can find all kind of goods from Europe and all other continents",font1));
		   cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		  
		   
		   cell.setMinimumHeight(50);
		   cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		   cell.disableBorderSide(PdfCell.LEFT);
		   cell.disableBorderSide(PdfCell.RIGHT);
		   cell.setColspan(5);
		   table.addCell(cell);
		  
		   table.completeRow();
		   
		
		   
		   
		   DecimalFormat df = new DecimalFormat("0.00");
		   float x=document.leftMargin();
		   float y= pageheight-190;
		   	totalDiscount = BigDecimal.valueOf(0);
			 totalTax= BigDecimal.valueOf(0);
			 subTotalBD=BigDecimal.valueOf(0);
			
		   for(int i=0; i < productList.size(); i++ )
		   {
			   int j=1;
			  
			   ProductReal product=productList.get(i);
			   y -=25;
			  
			   
			   
			   cell = new PdfPCell(new Phrase("Article "+product.getProductName()));
			   cell.setHorizontalAlignment(Element.ALIGN_LEFT);
			  
			   
			   cell.setMinimumHeight(30);
			   cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			   cell.disableBorderSide(PdfCell.LEFT);
			   cell.disableBorderSide(PdfCell.RIGHT);
			   cell.disableBorderSide(PdfCell.BOTTOM);
			   cell.setColspan(2);
			   table.addCell(cell);
			  
			 
			   
			 
			   
			   int remainingqunatity= product.getInitialQuantity()-product.getSelectedQuatity();
			   
			   BigDecimal subTotal = product.getPrice().multiply(BigDecimal.valueOf(remainingqunatity));
			   
			   
			   cell = new PdfPCell(new Phrase(naira+" "+subTotal));
			   cell.setHorizontalAlignment(Element.ALIGN_LEFT);
			  
			   
			   cell.setMinimumHeight(30);
			   cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			   cell.disableBorderSide(PdfCell.LEFT);
			   cell.disableBorderSide(PdfCell.RIGHT);
			   cell.disableBorderSide(PdfPCell.BOTTOM);
			   table.addCell(cell);
			  
			  
			
			   
			   
			   cell = new PdfPCell(new Phrase("Remaining qunatity "+remainingqunatity));
			   cell.setHorizontalAlignment(Element.ALIGN_LEFT);
			  
			   
			   cell.setMinimumHeight(30);
			   cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			   cell.disableBorderSide(PdfCell.LEFT);
			   cell.disableBorderSide(PdfCell.RIGHT);
			   cell.disableBorderSide(PdfCell.BOTTOM);
			   cell.setColspan(2);
			   table.addCell(cell);
			   
			   
		
			   
			   table.completeRow();
			   
			   cell = new PdfPCell(new Phrase("Return "+product.getItemId()));
			   cell.setHorizontalAlignment(Element.ALIGN_LEFT);
			  
			   
			   cell.setMinimumHeight(30);
			   cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			   cell.disableBorderSide(PdfCell.LEFT);
			   cell.disableBorderSide(PdfCell.RIGHT);
			   cell.disableBorderSide(PdfCell.TOP);
			   cell.setColspan(2);
			   table.addCell(cell);
			   
			   
			   
			  
			   
			   
			   j +=150;
			   
			   BigDecimal returnAmount = product.getPrice().multiply(BigDecimal.valueOf(product.getSelectedQuatity()));
			   
			   subTotalBD = subTotalBD.add(returnAmount);
			
			   
			   cell = new PdfPCell(new Phrase(naira + "-"+ returnAmount,font));
			   cell.setHorizontalAlignment(Element.ALIGN_LEFT);
			  
			   
			   cell.setMinimumHeight(30);
			   cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			   cell.disableBorderSide(PdfCell.LEFT);
			   cell.disableBorderSide(PdfCell.RIGHT);
			   cell.disableBorderSide(PdfCell.TOP);
			   table.addCell(cell);
			   
			   
//			   createHeadings(cb,x+j,y, naira + "-"+ returnAmount);
			   
			   j +=75;
			   
			   
			   cell = new PdfPCell(new Phrase("Return quntity   "+ product.getSelectedQuatity()));
			   cell.setHorizontalAlignment(Element.ALIGN_LEFT);
			  
			   
			   cell.setMinimumHeight(30);
			   cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			   cell.disableBorderSide(PdfCell.LEFT);
			   cell.disableBorderSide(PdfCell.RIGHT);
			   cell.disableBorderSide(PdfCell.TOP);
			   cell.setColspan(2);
			   table.addCell(cell);
			   
			   
//			   createHeadings(cb,x+j,y, "Return quntity   "+ product.getSelectedQuatity());
			   
			   totalDiscount = totalDiscount.add(product.getDiscount().multiply(BigDecimal.valueOf(product.getSelectedQuatity())) );
			   totalTax = totalTax.add(product.getTax().multiply(BigDecimal.valueOf(product.getSelectedQuatity())));
//			   subTotalBD=subTotalBD.add(product.getTotalPrice());
			   
			   y -=5;
			   
			  
			
			   
		   }
		   y -=25; 
		   
		  
		   cell = new PdfPCell(new Phrase("Discount   "+" ₦ "+totalDiscount,font));
		   cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		  
		   cell.setPadding(20);
		   cell.setMinimumHeight(40);
		   cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		   cell.disableBorderSide(PdfCell.LEFT);
		   cell.disableBorderSide(PdfCell.RIGHT);
		   cell.setColspan(5);
		   table.addCell(cell);
		   table.completeRow();
		   

		 
		   
		   cell = new PdfPCell(new Phrase("VAT  "+" ₦ "+totalTax,font));
		   cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		  
		   cell.setPadding(20);
		   cell.setMinimumHeight(40);
		   cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		   cell.disableBorderSide(PdfCell.LEFT);
		   cell.disableBorderSide(PdfCell.RIGHT);
		   cell.setColspan(5);
		   table.addCell(cell);
		   table.completeRow();
		   

		 
		   subTotalBD = subTotalBD.subtract(totalDiscount);
		   
		  
		   
		   cell = new PdfPCell(new Phrase("Total  "+" ₦ "+subTotalBD,font));
		   cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		  
		   cell.setPadding(20);
		   cell.setMinimumHeight(40);
		   cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		   cell.disableBorderSide(PdfCell.LEFT);
		   cell.disableBorderSide(PdfCell.RIGHT);
		   cell.setColspan(5);
		   table.addCell(cell);
		   table.completeRow();
		   
		   cell = new PdfPCell(new Phrase());
		   cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		  cell.setRowspan(3);
		  cell.setPadding(20);
		   cell.setMinimumHeight(40);
		   cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		   cell.disableBorderSide(PdfCell.LEFT);
		   cell.disableBorderSide(PdfCell.RIGHT);
		   cell.setColspan(5);
		   table.addCell(cell);
		
		  font1 = FontFactory.getFont("assets/arial.ttf",BaseFont.IDENTITY_H); 
		   font1.setStyle(Font.BOLD);
		   
		   
		   cell = new PdfPCell(new Phrase("Amount due     "+" ₦ "+subTotalBD,font1));
		   cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		  cell.setBackgroundColor(new BaseColor(Color.GRAY));
		   cell.setPadding(20);
		   cell.setMinimumHeight(40);
		   cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		   cell.disableBorderSide(PdfCell.LEFT);
		   cell.disableBorderSide(PdfCell.RIGHT);
		   cell.setColspan(5);
		   table.addCell(cell);
		   table.completeRow();
		   
		   
		 document.add(table);
		  
		    
		   document.close();
		  } 
		  catch(Exception e){
			  System.out.println("Error  kjkjdlkdlkkkkkkkkkkkkkkkkk");
		   e.printStackTrace();
		  }
		 
		  
		  progress_dialog.dismiss();
		  doPrint();
//		 
		  
		
		 
		 }*/
	public void doPrint()
	{
//		showDialog("No Application Available to View PDF \n Do you want to download ?");
//		File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +"/"+ filename);
		
		
		
		
		/*Intent target = new Intent(Intent.ACTION_VIEW);
		target.setDataAndType(Uri.fromFile(pdfFile),"application/pdf");
		target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		
		try
		{
			startActivity(target);
		
		}
		catch(ActivityNotFoundException e)
		{
			showDialog("No Application Available to View PDF \n Do you want to download ?");
		}
		 */
	}
	
	/* private void createHeadings(PdfContentByte cb, float x, float y, String text){
		 
		  cb.beginText();
		  cb.setFontAndSize(bfBold, 10);
		  cb.setTextMatrix(x,y);
		  cb.showText(text.trim());
		
		  
		 
		  cb.endText(); 
		 
		 }
	 private void createHeadingsSize(PdfContentByte cb, float x, float y, String text,float size){
		 
		  cb.beginText();
		  cb.setFontAndSize(bfBold, size);
		  cb.setTextMatrix(x,y);
		  cb.showText(text.trim());
	
		
		cb.endText();
		 }
		 */
		 private static boolean isExternalStorageReadOnly() {  
		  String extStorageState = Environment.getExternalStorageState();  
		  if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)) {  
		   return true;  
		  }  
		  return false;  
		 }  
		 
		 private static boolean isExternalStorageAvailable() {  
		  String extStorageState = Environment.getExternalStorageState();  
		  if (Environment.MEDIA_MOUNTED.equals(extStorageState)) {  
		   return true;  
		  }  
		  return false;  
		 }  
		 
		 /*private void initializeFonts(){
		 
		 
		 try {
		   bfBold = BaseFont.createFont(BaseFont.HELVETICA_BOLD, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
		   
		  } catch (DocumentException e) {
		   e.printStackTrace();
		  } catch (IOException e) {
		   e.printStackTrace();
		  }
		 }*/
		 
		 @SuppressWarnings("deprecation")
		public void showDialog(String str)
		 {
			 
			 final AlertDialog alert=new AlertDialog.Builder(mcontext).create();
			 
			 alert.setMessage(str);
			 
			
			 alert.setButton2("OK", new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					alert.dismiss();
				}
			});
			 alert.setButton("Yes", new DialogInterface.OnClickListener() {
					
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						alert.dismiss();
						
						Intent i = new Intent(android.content.Intent.ACTION_VIEW);
						i.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.infraware.office.link"));
						startActivity(i);
						
						
					}
				});
			 
			 alert.show();
			 
		 }
		class GeneratePdf extends AsyncTask<String, String, String>
		{

			@Override
		    protected void onPreExecute() {
		    
				super.onPreExecute();
		    	dialog=new ProgressDialog(mcontext);
		    	
		    	dialog.setMessage("Creating receipt please wait ..");
		    	dialog.setCanceledOnTouchOutside(false);
		    	dialog.show();
			}
			@Override
			protected String doInBackground(String... params) {
				// TODO Auto-generated method stub
				
//				generatePDF("");
				
				return null;
			}
			@Override
		    protected void onPostExecute(String str) {
		    	
		    	dialog.dismiss();
		    	
		    	////commented by Minakshi
		    	/*Intent in =new Intent(mcontext,PrinterTypeActivity.class);
				in.putExtra("TransactionId", TransactionId);
			
				startActivity(in);*/
		    	
			}
		}
		public void onBackPressed()
		{
			Log.e("hfhhf", "fhhfhf");
			Intent intent = new Intent(mcontext.getApplicationContext(), ItemReturnsActivity.class);
//			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			ComponentName cn = intent.getComponent();
			Intent mainIntent = IntentCompat.makeRestartActivityTask(cn);
			
			mcontext.startActivity(mainIntent);
			((Activity)mcontext).finish();
	    	
	    	
		}
		public void onPause()
		{
			super.onPause();
			
			if(!ScreenReceiver.wasScreenOn)
				curentTime =System.currentTimeMillis();
			
		}
		@Override
		public void onResume(){
			super.onResume();
			
			if(ScreenReceiver.wasScreenOn)
			{
				elapTime = System.currentTimeMillis();
				
				elapTime = (elapTime-curentTime);
				if(elapTime>50000 && elapTime<System.currentTimeMillis() || PosConstants.time_out )
				{
					
					PosConstants.time_out = true;
						finish();
				}
				Log.e("", ""+(elapTime));
			}
		}
		
}
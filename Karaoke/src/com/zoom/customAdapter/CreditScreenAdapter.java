package com.zoom.customAdapter;

import java.io.InputStream;
import java.util.ArrayList;
import javax.xml.parsers.SAXParserFactory;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import com.zoom.karaoke.CreditActivity;
import com.zoom.karaoke.R;
import com.zoom.dataController.SingleTon;
import com.zoom.inappbilling.util.IabHelper;
import com.zoom.inappbilling.util.IabResult;
import com.zoom.inappbilling.util.Purchase;
import com.zoom.models.InAppModels;
import com.zoom.xmlResponseHandler.CreditXmlHandler;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class CreditScreenAdapter extends ArrayAdapter<InAppModels>{

	private ArrayList<InAppModels>product=new java.util.ArrayList<InAppModels>();
	private Activity mcontext;
	private LayoutInflater inflator;
	private IabHelper mHelper;
	public static final int RC_REQUEST = 10001;
	private  String TAG = "ZoomKaraoke";
	private String CREDIT_URL="http://www.zoomkaraokeapp.co.uk/service.asmx/AddCredits";
	private ProgressDialog pDialog;

	public CreditScreenAdapter(Activity context, int resource,ArrayList<InAppModels> product) {
		super(context, resource, product);
		this.product=product;
		mcontext=context;
		inflator=((Activity)mcontext).getLayoutInflater();
	}

	public View getView(final int position,View convertView,ViewGroup parent){


		if(convertView==null)
			convertView=inflator.inflate(R.layout.creditscreen_customadapter, null);

		TextView purchaseName=(TextView)convertView.findViewById(R.id.purchaseName);
		TextView price=(TextView)convertView.findViewById(R.id.price);
		TextView creditBuyNow=(TextView)convertView.findViewById(R.id.creditBuyNow);

		purchaseName.setText(product.get(position).getName());
		price.setText(product.get(position).getPrice());
		creditBuyNow.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				String payload = "";
				mHelper=CreditActivity.mHelper;
				mHelper.launchPurchaseFlow(mcontext, product.get(position).getSkuName(), RC_REQUEST,mPurchaseFinishedListener, payload);

			}
		});
		return convertView;
	}	
	// Enables or disables the "please wait" screen.
	void setWaitScreen(boolean set) {
		mcontext.findViewById(R.id.creditbackground).setVisibility(set ? View.GONE : View.VISIBLE);
		mcontext.findViewById(R.id.waiting_screen).setVisibility(set ? View.VISIBLE : View.GONE);
	}

	// Callback for when a purchase is finished
	IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
		public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
			Log.d(TAG, "Purchase finished: " + result + ", purchase: " + purchase);

			// if we were disposed of in the meantime, quit.
			if (mHelper == null) return;

			if (result.isFailure()) {
				complain("Error purchasing: " + result);
				setWaitScreen(false);
				return;
			}
			if (!verifyDeveloperPayload(purchase)) {
				complain("Error purchasing. Authenticity verification failed.");
				setWaitScreen(false);
				return;
			}          

			Log.d(TAG, "Purchase successful.");
			pDialog = new ProgressDialog(mcontext);
			pDialog.setTitle("Loading");
			pDialog.setMessage("Please wait...");
			pDialog.setCancelable(false);
			pDialog.show();
			mHelper.consumeAsync(purchase, mConsumeFinishedListener);
		}
	};


	/** Verifies the developer payload of a purchase. */
	boolean verifyDeveloperPayload(Purchase p) {
		String payload = p.getDeveloperPayload();

		return true;
	}

	void complain(String message) {
		Log.e(TAG, "**** Zoom karoake Error : " + message);
		alert("Error: " + message);
	}

	void alert(String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(mcontext);	
		builder.setMessage(message);
		builder.setNeutralButton("OK", null);
		Log.d(TAG, "Showing alert dialog: " + message);
		builder.create().show();
	}
	
	 // Called when consumption is complete
    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
        public void onConsumeFinished(Purchase purchase, IabResult result) {
            Log.d(TAG, "Consumption finished. Purchase: " + purchase + ", result: " + result);           
            
            // if we were disposed of in the meantime, quit.
            if (mHelper == null) return;

            // We know this is the "gas" sku because it's the only one we consume,
            // so we don't check which sku was consumed. If you have more than one
            // sku, you probably should check...
            if (result.isSuccess()) {
                // successfully consumed, so we apply the effects of the item in our
                // game world's logic, which in our case means filling the gas tank a bit
                Log.d(TAG, "Consumption successful. Provisioning.");
                
            	if (purchase.getSku().equals(CreditActivity.SKU_THREE_SONGS)) {    			
    				Log.d(TAG, "Purchase 3 song.");	    				
    				new AsyncRequestCredit("3").execute();				

    			}
    			else if (purchase.getSku().equals(CreditActivity.SKU_TEN_SONGS)) {    			
    				Log.d(TAG, "Purchase 10 song.");				
    				new AsyncRequestCredit("10").execute();				

    			}
    			else if (purchase.getSku().equals(CreditActivity.SKU_TWENTYFIVE_SONGS)) {    				
    				Log.d(TAG, "Purchase 25 song.");				
    				new AsyncRequestCredit("25").execute();				

    			}
    			else if (purchase.getSku().equals(CreditActivity.SKU_FIFTY_SONGS)) {    		
    				Log.d(TAG, "Purchase 50 song.");				
    				new AsyncRequestCredit("50").execute();				

    			}
    			else if(purchase.getSku().equals(CreditActivity.SKU_HUNDRED_SONGS)) {    				
    				Log.d(TAG, "Purchase 100 song.");				
    				new AsyncRequestCredit("100").execute();				

    			}

    			else{

    				Toast.makeText(mcontext, "Sorry we did something worng", 1).show();
    			}
             
            }
            else {
                complain("Error while consuming: " + result);
            }          
            setWaitScreen(false);
            Log.d(TAG, "End consumption flow.");
        }
    };

	public class AsyncRequestCredit extends AsyncTask<String, Void, String> {

		private  String mCredits;		
		public AsyncRequestCredit(String Credits){

			mCredits=Credits;
		}

		@Override
		protected void onPreExecute() {
			
			super.onPreExecute();			
		}

		@Override
		protected String doInBackground(String... urls) {	

			InputStream inputStream = null;
			String result = "";
			try {
				HttpClient httpclient = new DefaultHttpClient();
				String url=CREDIT_URL+"?user_email="+SingleTon.getInstance().getUserEmailID()
						+"&Credit="+mCredits;
				HttpResponse httpResponse = httpclient.execute(new HttpGet(url));
				inputStream = httpResponse.getEntity().getContent();	
				if(inputStream != null)
					result = convertInputStreamToString(inputStream);
				else
					result = "Did not work!";
				
			} catch (Exception e) {
				Log.d("InputStream", e.getLocalizedMessage());
			}

			return result;
		}

		@Override
		protected void onPostExecute(String result) {

			super.onPostExecute(result);	
			pDialog.dismiss();
			SingleTon.getInstance().setUserCredit(result);			
			String a=SingleTon.getInstance().getUserCredit();
			CreditActivity.creditSongs.setText(result+" "+"Credits");
		}
	}

	public String convertInputStreamToString(InputStream inputStream) {	
		String Result="";
		try {		
			XMLReader xmlReader = SAXParserFactory.newInstance().newSAXParser().getXMLReader();		
			CreditXmlHandler saxHandler = new CreditXmlHandler(); 
			xmlReader.setContentHandler(saxHandler);
			InputSource ins= new InputSource(inputStream);
			ins.setEncoding("UTF-8");
			xmlReader.parse(ins);
			Result = saxHandler.getTotalCredits();

		} catch (Exception ex) {
			Log.e("XML", "SAXXMLParser:  failed"+ex.getMessage());
		}
		return Result;
	}

}

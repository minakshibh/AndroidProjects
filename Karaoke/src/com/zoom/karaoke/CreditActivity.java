package com.zoom.karaoke;


import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.SAXParserFactory;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences.Editor;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;


import com.zoom.karaoke.R;
import com.zoom.karaoke.SplashScreen.AsyncRequestCredit;

import com.zoom.authentication.LoginActivity;
import com.zoom.customAdapter.CreditScreenAdapter;
import com.zoom.dataController.SingleTon;
import com.zoom.inappbilling.util.IabHelper;
import com.zoom.inappbilling.util.IabResult;
import com.zoom.inappbilling.util.Inventory;
import com.zoom.models.InAppModels;
import com.zoom.xmlResponseHandler.CreditXmlHandler;


public class CreditActivity extends Activity {

	private ListView listInAppPurchaseCredits;
	private CreditScreenAdapter adapter;
	private ArrayList<InAppModels> products = new ArrayList<InAppModels>();
	private ArrayList<String> additionalSkuList;
	private ImageButton back;
	private  String TAG = "Zoom-Karaoke";
	private InAppModels mAppModels;
	private String product;	
	public static TextView creditSongs;	
	private String CREDIT_URL="http://www.zoomkaraokeapp.co.uk/service.asmx/AddCredits";
	public static String SKU_THREE_SONGS = "paid_songs_3";
	public static String SKU_TEN_SONGS = "paid_songs_10";
	public static String SKU_TWENTYFIVE_SONGS = "k_paid_song_25";
	public static String SKU_FIFTY_SONGS = "k_paid_song_50";
	public static String SKU_HUNDRED_SONGS = "k_paid_song_100";

	public static IabHelper mHelper;
	public static final int RC_REQUEST = 10001;
	private ProgressDialog progressDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.creditscreen);	

		initaliseVariable();
		initialiseListener();

		String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAtBbTp/kHkgkz2BMiETq3gAvflXYMoFN+LyY51KLM4P8zwnz1L6S+X4iaMkhtVOKTcJFsnR/A3PHemG/wPu8s9jVVtAkcPrEE+0K0s+Z6OydYpXI98wvUPOagwMYtiWMJxiiAywQhR6IbAOkGxZZvDDYxaajAKthfZIhYI+ZpenuxN+9jwBCAYyXPb4vF2/CXmdkneyxRNGQBcqSZ/xX5s2F3lFsgho+hDvSV/74Ba6qsupAiQGEp94VhAW53V/zfR+WFeSZ/pH/e9R+1IUSpRyc+YRdUirq6+nZOeQWM5Uk+1V/2W4QgactqKbY2s0V5PiTRtXWoDSmZj+cVyDhP5QIDAQAB";

		if (getPackageName().startsWith("com.example")) {
			throw new RuntimeException("Please change the sample's package name! See README.");
		}

		// Create the helper, passing it our context and the public key to
		// verify signatures with
		Log.d(TAG, "Creating IAB helper.");
		mHelper = new IabHelper(this, base64EncodedPublicKey);

		// enable debug logging (for a production application, you should set
		// this to false).
		mHelper.enableDebugLogging(true);

		// Start setup. This is asynchronous and the specified listener
		// will be called once setup completes.
		Log.d(TAG, "Starting setup.");
		mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
			public void onIabSetupFinished(IabResult result) {
				Log.d(TAG, "Setup finished.");

				if (!result.isSuccess()) {
					// Oh noes, there was a problem.
					complain("Problem setting up in-app billing: " + result);
					return;
				}

				// Have we been disposed of in the meantime? If so, quit.
				if (mHelper == null)
					return;

				// IAB is fully set up. Now, let's get an inventory of stuff we
				// own.
				Log.d(TAG, "Setup successful. Querying inventory.");

				additionalSkuList = new ArrayList<String>();
				additionalSkuList.add(SKU_THREE_SONGS);
				additionalSkuList.add(SKU_TEN_SONGS);
				additionalSkuList.add(SKU_TWENTYFIVE_SONGS);
				additionalSkuList.add(SKU_FIFTY_SONGS);
				additionalSkuList.add(SKU_HUNDRED_SONGS);
				mHelper.queryInventoryAsync(true,additionalSkuList, mGotInventoryListener);

			}
		});
	}

	// Listener that's called when we finish querying the items and
	// subscriptions we own
	IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
		public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
			Log.d(TAG, "Query inventory finished.");

			// Have we been disposed of in the meantime? If so, quit.
			if (mHelper == null)
				return;

			// Is it a failure?
			if (result.isFailure()) {
				progressDialog.dismiss();
				if (inventory.hasPurchase("android.test.purchase")) {
					mHelper.consumeAsync(inventory.getPurchase("android.test.purchase"),null);
					}
				complain("Failed to query inventory: " + result);
				return;
			}

			Log.d(TAG, "Query inventory was successful.");

			/*
			 * Check for items we own. Notice that for each purchase, we check
			 * the developer payload to see if it's correct! See
			 * verifyDeveloperPayload().
			 */


			for (int i = 0; i < additionalSkuList.size(); i++) {
				mAppModels = new InAppModels();
				product = inventory.getSkuDetails(additionalSkuList.get(i)).getTitle();
				product = product.substring(0, product.lastIndexOf("("));
				mAppModels.setName(product);
				mAppModels.setPrice(inventory.getSkuDetails(additionalSkuList.get(i)).getPrice());
				mAppModels.setSkuName(additionalSkuList.get(i));
				products.add(mAppModels);

			}
			Log.d(TAG, "Initial inventory query finished; enabling main UI.");
			setAdapter();
			
		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d(TAG, "onActivityResult(" + requestCode + "," + resultCode + "," + data);
		if (mHelper == null) return;

		// Pass on the activity result to the helper for handling
		if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
			// not handled, so handle it ourselves (here's where you'd
			// perform any handling of activity results not related to in-app
			// billing...
			super.onActivityResult(requestCode, resultCode, data);
		}
		else {
			Log.d(TAG, "onActivityResult handled by IABUtil.");
		}
	}
	// We're being destroyed. It's important to dispose of the helper here!
	@Override
	public void onDestroy() {
		super.onDestroy();

		// very important:
		Log.d(TAG, "Destroying helper.");
		if (mHelper != null) {
			mHelper.dispose();
			mHelper = null;
		}
	}

	void complain(String message) {
		Log.e(TAG, "**** Zoom karoake Error : " + message);
		alert("Error: " + message);
	}

	void alert(String message) {
		AlertDialog.Builder bld = new AlertDialog.Builder(this);
		bld.setMessage(message);
		bld.setNeutralButton("OK", null);
		Log.d(TAG, "Showing alert dialog: " + message);
		bld.create().show();
	}
	
	protected void setAdapter() {

		adapter = new CreditScreenAdapter(CreditActivity.this, 0, products);
		listInAppPurchaseCredits.setAdapter(adapter);
		new AsyncRequestCredit().execute();
	}

	private void initialiseListener() {

		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

			
					
					Intent intent=new Intent(CreditActivity.this,AvailableSong.class);
					startActivity(intent);
					finish();
						
					
			

			}
		});

	}

	private void initaliseVariable() {

		creditSongs=(TextView)findViewById(R.id.creditSongs);
		listInAppPurchaseCredits = (ListView) findViewById(R.id.listInAppPurchaseCredits);
		back=(ImageButton)findViewById(R.id.creditBack);
		progressDialog = new ProgressDialog(CreditActivity.this);
		progressDialog.setTitle("Loading");
		progressDialog.setMessage("Please wait...");
		progressDialog.setCancelable(false);
		progressDialog.show();

	}
	
public class AsyncRequestCredit extends AsyncTask<String, Void, String> {

		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... urls) {	

			InputStream inputStream = null;
			String result = "";
			try {
				HttpClient httpclient = new DefaultHttpClient();
				String url=CREDIT_URL+"?user_email="+SingleTon.getInstance().getUserEmailID()+"&Credit=0";
				HttpResponse httpResponse = httpclient.execute(new HttpGet(url));
				inputStream = httpResponse.getEntity().getContent();	
				if(inputStream != null)
					result = convertInputStreamToString(inputStream);
				else
					result = "Did not work!";

				Thread.sleep(2000);
				
			} catch (Exception e) {
				Log.d("InputStream", e.getLocalizedMessage());
			}

			return result;
		}

		@Override
		protected void onPostExecute(String result) {
		
			super.onPostExecute(result);
			progressDialog.dismiss();
			SingleTon.getInstance().setUserCredit(result);			
			String a=SingleTon.getInstance().getUserCredit();
			creditSongs.setText(result+" "+"Credits");		
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
	
public void onBackPressed() {
		
		Intent intent=new Intent(CreditActivity.this,AvailableSong.class);
		startActivity(intent);
		finish();
			
		
	}
	
}

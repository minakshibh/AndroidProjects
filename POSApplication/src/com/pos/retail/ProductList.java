package com.pos.retail;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.posapplication.R;
import com.pos.retail.LoginScreen.CurrencySetting;
import com.pos.util.ConnectionDetector;
import com.pos.util.PoSDatabase;
import com.pos.util.PosConstants;
import com.pos.util.ProductReal;
import com.pos.util.ScreenReceiver;

public class ProductList extends Activity{

	LinearLayout ll;
	LayoutInflater inflator;
	Context mcontext;
	ConnectionDetector detector;
	ProgressDialog dialog;
	String producturl,Method_Name="GetProductList", naira;
	ListView productListView;
	EditText search;
	SharedPreferences loginPref;
	ArrayList<ProductReal> productList=new ArrayList<ProductReal>();
	ArrayList<ProductReal> searchList=new ArrayList<ProductReal>();
	long curentTime = 0;
	  long elapTime = 0;
	  SharedPreferences posPref;
		Editor posEditor;
	
	public void onCreate(Bundle b)
	{
		super.onCreate(b);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.productlayout);
		
		mcontext=this;
		naira=getResources().getString(R.string.naira);
		search=(EditText)findViewById(R.id.search);
		
		search.setImeOptions(EditorInfo.IME_ACTION_DONE);
		search.addTextChangedListener(watcher);
		
		loginPref=getSharedPreferences("LoginPref", MODE_PRIVATE);
		
		productListView=(ListView)findViewById(R.id.listview);
		
		producturl=getResources().getString(R.string.liveurl)+"/"+Method_Name;
		detector=new ConnectionDetector(mcontext);
		
		
		posPref = getSharedPreferences("pos", MODE_PRIVATE);
		posEditor = posPref.edit();	
	    	
//		new SyncProductListing().execute();
		PoSDatabase db=new PoSDatabase(mcontext);
		productList=db.getAllProducts();
		CustomAdapter adapter=new CustomAdapter(mcontext, android.R.layout.simple_list_item_1, productList);
    	productListView.setAdapter(adapter);
    	
		productListView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				
				ProductReal product;
				if(search.getText().toString().trim().equals(""))
					product = productList.get(arg2);
				else
					product = searchList.get(arg2);
				
				if(product.getTotalQuantity()<=0){
					AlertDialog.Builder alert = new AlertDialog.Builder(ProductList.this);
					alert.setTitle("Product Out Of Stock");
					alert.setMessage("This product is currently Out of Stock.");
					alert.setPositiveButton("Ok", null);
					alert.show();
				}else{
					product.setSelectedQuatity(1);
					
					AddProducts.product = product;
					finish();			
				}
			}
		});
		
	}
	/*class SyncProductListing extends AsyncTask<String, String, String>
	{
		int result=1;
//		ArrayList<ProductReal> productList =null;
		@Override
	    protected void onPreExecute() {
	    
			super.onPreExecute();
	    	dialog=new ProgressDialog(mcontext);
	    	
	    	dialog.setMessage("Loading products");
	    	dialog.setCanceledOnTouchOutside(false);
	    	dialog.show();
		}
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
		
			try
			{
			
			int timestamp=posPref.getInt("producttimestamp", -1);	
			InputStream is = null;
			DefaultHttpClient httpClient = new DefaultHttpClient();
	        HttpPost requestLogin = new HttpPost(producturl);
	         List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
	         nameValuePairs.add(new BasicNameValuePair("timestamp",Integer.toString(-1)));
	        
	   	     requestLogin.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		     HttpResponse response = httpClient.execute(requestLogin);
		     HttpEntity entity = response.getEntity();
		     is= entity.getContent();
			
		     productList= com.pos.util.SAXXMLParser.parseProductDetail(is);
		     Map<String, String> map=com.pos.util.SAXXMLParser.map;

		     System.out.println(map);
		     result=Integer.parseInt(map.get("result"));
		     timestamp=Integer.parseInt(map.get("timestamp"));
		     
		     if(result==0)
		     {
		    	 posEditor.putInt("producttimestamp", timestamp).commit();
		     }
		     Log.e("gggg", ""+productList);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			
			return null;
		}
		
		
		@Override
	    protected void onPostExecute(String str) {
	    	
	    	dialog.dismiss();
	    	
	    	
	    	if(result==0 && productList !=null)
		     {
		    	 
	    		PoSDatabase db =new PoSDatabase(mcontext);
	    		
	    		db.deleteProducts();
		    	 for(int i=0;i<productList.size();i++)
		    	 {
		    		 
		    		 ProductReal product=productList.get(i);
		    		 PoSDatabase db1=new PoSDatabase(mcontext);
		    		 
		    		 if(product.isDeleted())
		    		 {
//		    			 db.deleteProduct(product.getPid());
//		    			 productList.remove(i);
		    		 }
		    		 else
		    		 {
		    			 db1.addProduct(product);
		    		 }
		    		 
		    	 }
		    	PoSDatabase db2=new PoSDatabase(mcontext);
				productList=db2.getAllProducts();
				CustomAdapter adapter=new CustomAdapter(mcontext, android.R.layout.simple_list_item_1, productList);
		    	productListView.setAdapter(adapter);
		     }
	    	
//	    	new CurrencySetting().execute();
	    	
		}
	}*/
	class CustomAdapter extends ArrayAdapter<ProductReal>
	{
		
		ArrayList<ProductReal> list;
		public CustomAdapter(Context context, int textViewResourceId, ArrayList<ProductReal> objects) {
			super(context,textViewResourceId,objects);
			
			list=objects;
			
		}
		public View getView(int position,View convertView,ViewGroup parent)
		{
			if(convertView==null)
				convertView=((Activity)mcontext).getLayoutInflater().inflate(R.layout.itemlayout, null);
			
			
			TextView pid = (TextView)convertView.findViewById(R.id.pid);
			TextView itemId = (TextView)convertView.findViewById(R.id.pItemId);
			TextView pname = (TextView)convertView.findViewById(R.id.pname);
			TextView pprice=(TextView)convertView.findViewById(R.id.pprice);
			TextView tax=(TextView)convertView.findViewById(R.id.tax);
			
			TextView txtDelete = (TextView)convertView.findViewById(R.id.delete);
			TextView txtEdit = (TextView) convertView.findViewById(R.id.edit);
			
			txtDelete.setVisibility(View.GONE);
			txtEdit.setVisibility(View.GONE);
			
			TextView discount=(TextView)convertView.findViewById(R.id.discount);
			TextView qunatity=(TextView)convertView.findViewById(R.id.quantity);
			TextView txt = (TextView) convertView.findViewById(R.id.labelQuntity);
			
			ProductReal product=list.get(position);
			
			pid.setText(""+product.getPid());
			pname.setText(""+product.getProductName());
			pprice.setText(naira +" "+product.getPrice().toString());
			tax.setText(naira +" "+ product.getTax().toString());
			
			itemId.setText(""+product.getItemId());
			
			discount.setText(naira +" "+product.getDiscount().toString());
			if(product.getTotalQuantity()>0)
				qunatity.setText(""+product.getTotalQuantity());
			else{
				qunatity.setText("Out Of Stock");
				txt.setText("");
			}
			return convertView;
		}
	}
	@Override
	  public void onConfigurationChanged (Configuration newConfig) {
	    super.onConfigurationChanged(newConfig);
	    
	}
	TextWatcher watcher=new TextWatcher() {
		
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			// TODO Auto-generated method stub
			
		}
		
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub
			
		}
		
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
			
			String str=s.toString();
			
			
			searchList.clear();
			for(int i=0;i<productList.size();i++)
			{
				
				ProductReal product=productList.get(i);
				
				
				if(product.getProductName().toLowerCase().startsWith(str.toLowerCase()) || product.getItemId().toLowerCase().startsWith(str.toLowerCase()))
				{
					searchList.add(product);
				}
				
			}
			CustomAdapter adapter=new CustomAdapter(mcontext, android.R.layout.simple_list_item_1, searchList);
	    	productListView.setAdapter(adapter);
	    	
		}
	};
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
			if(elapTime>PosConstants.TIMEOUT_IN_MS && elapTime<System.currentTimeMillis() || PosConstants.time_out )
			{
				
				PosConstants.time_out = true;
				Intent in =new Intent(mcontext,BreakActivity.class);
				startActivity(in);
			}
			Log.e("", ""+(elapTime));
		}
	}
}

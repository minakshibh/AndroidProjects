package com.zira.creditcards;

import java.util.ArrayList;
import com.twentyfourseven.zira.R;
import com.zira.modal.CreditCard;
import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AddCreditCardCustomAdapter extends ArrayAdapter<CreditCard>{

	private ArrayList<CreditCard>cardNumber=new java.util.ArrayList<CreditCard>();
	private Activity mcontext;
	private LayoutInflater inflator;


	public AddCreditCardCustomAdapter(Activity context, int resource,ArrayList<CreditCard> cardNumber) {
		super(context, resource, cardNumber);
		this.cardNumber=cardNumber;
		mcontext=context;
		inflator=((Activity)mcontext).getLayoutInflater();
	}

	public View getView(final int position,View convertView,ViewGroup parent){


		if(convertView==null)
			convertView=inflator.inflate(R.layout.creditcard_customadapter, null);

		TextView CardNumber=(TextView)convertView.findViewById(R.id.textView_cardNumber);
		String getCardNumber=cardNumber.get(position).getCardNumber();
		int totallenghtOfCardNumber=getCardNumber.length();
		int startlength=totallenghtOfCardNumber-4;
		try {
			if(getCardNumber.length()==0)
			{
				CardNumber.setText("No Credit Card Number");
			}else{
			CardNumber.setText("*"+getCardNumber.substring(startlength, totallenghtOfCardNumber));}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		ImageView imageDefault=(ImageView)convertView.findViewById(R.id.image_IsDefault);
		boolean isDefault=cardNumber.get(position).getIsDefault();
		Log.d("tag", "Default::"+isDefault);
		if(isDefault){
		}else{
			imageDefault.setVisibility(View.GONE);
		}

		return convertView;
	}	

}

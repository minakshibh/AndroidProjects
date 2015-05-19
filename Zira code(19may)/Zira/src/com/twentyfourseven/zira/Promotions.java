package com.twentyfourseven.zira;

import java.util.ArrayList;

import com.twentyfourseven.zira.R;
import com.zira.creditcards.ListOfCreditCardsActivity;
import com.zira.modal.PromoCode;
import com.zira.modal.User;
import com.zira.registration.AddCreditInfo;
import com.zira.util.SingleTon;
import com.zira.util.Util;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class Promotions extends Activity{
ListView listPromoCode;
private User mUserModel;
ArrayList<String> promoCodeArrayList = new ArrayList<String>();
@Override
protected void onCreate(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	super.onCreate(savedInstanceState);
	requestWindowFeature(Window.FEATURE_NO_TITLE);
	setContentView(R.layout.promotions);
	intailizeVariables();
	addPromoCodeInList();
	setAdapterToList();
}
public void intailizeVariables()
{
	mUserModel = SingleTon.getInstance().getUser();
	listPromoCode=(ListView)findViewById(R.id.listView_Promotions);
}
private void addPromoCodeInList() {
	for(PromoCode u:mUserModel.getPromoCodeList()){
		promoCodeArrayList.add(u.getPromoCode());
	}
}
// assign Adapter to List
private void setAdapterToList() {
	if(promoCodeArrayList.size()>0){
	listPromoCode.setVisibility(View.VISIBLE);	
	ArrayAdapter<String> adtPromoCode = new ArrayAdapter<String>(Promotions.this, android.R.layout.simple_list_item_1,promoCodeArrayList);
	listPromoCode.setAdapter(adtPromoCode);
	}
	else {
		AlertDialog.Builder alert = new AlertDialog.Builder(Promotions.this);
		alert.setTitle("Zira24/7");
		alert.setMessage("No Promo Code is available.");
		alert.setPositiveButton("ok", new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface arg0, int arg1) {
				
				finish();
			}
		});
		alert.show();
		//Util.alertMessage(Promotions.this, "No Promo Code is available.");
	}
}
}

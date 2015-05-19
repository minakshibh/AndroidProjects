package com.pos.retail;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.example.posapplication.R;

public class ChooseTrxOption extends PreferenceActivity{
	
	private Button btnBack;
	
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState)
	{

		requestWindowFeature(Window.FEATURE_NO_TITLE);
	    super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.choose_trx_settings);
		setContentView(R.layout.setting);
		
		btnBack = (Button)findViewById(R.id.back);
		
		btnBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();		
			}
		});
		
		
		Preference trxPref = (Preference) findPreference("trx");
		trxPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {			
			@Override
			public boolean onPreferenceClick(Preference arg0) {
				Intent intent = new Intent(ChooseTrxOption.this, TrxSettings.class);
				intent.putExtra("trigger", "trx");
				startActivity(intent);
				return false;
			}
		});
		
		Preference masterPref = (Preference) findPreference("master");
		masterPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {			
			@Override
			public boolean onPreferenceClick(Preference arg0) {
				Intent intent = new Intent(ChooseTrxOption.this, TrxSettings.class);
				intent.putExtra("trigger", "master");
				startActivity(intent);
				return false;
			}
		});
	}
}

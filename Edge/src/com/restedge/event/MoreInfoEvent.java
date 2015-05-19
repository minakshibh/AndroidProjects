package com.restedge.event;





import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;

import com.restedge.utelite.R;

public class MoreInfoEvent extends Activity {

	TextView readmore;
	ImageButton btnBack;
	Intent in;
	String description;
	public void onCreate(Bundle b)
	{
		super.onCreate(b);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.eventmoreinfo);
		
		readmore=(TextView)findViewById(R.id.readmore);
		btnBack=(ImageButton)findViewById(R.id.backbtn);
		in=getIntent();
		
		btnBack.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
				
			}
		});
		description=in.getStringExtra("eventDesc");
		readmore.setText(description);
		
	}
	
}

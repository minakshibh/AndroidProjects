package com.twentyfourseven.zira;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;

public class ShareScreen extends Activity implements OnClickListener{
	ImageView imgFb,imgTwitter,imgPintrest,imgInstagram,
	imgStar1,imgStar2,imgStar3,imgStar4,imgStar5;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ride_with_confidence_screen);
		initailizeVraiable();
	}
private void initailizeVraiable()
{
	imgFb=(ImageView)findViewById(R.id.img_facebook);
	imgTwitter=(ImageView)findViewById(R.id.img_twitter);
	imgPintrest=(ImageView)findViewById(R.id.img_Pinterest);
	imgInstagram=(ImageView)findViewById(R.id.img_instagram);
	imgStar1=(ImageView)findViewById(R.id.rating_star1);
	imgStar2=(ImageView)findViewById(R.id.rating_star2);
	imgStar3=(ImageView)findViewById(R.id.rating_star3);
	imgStar4=(ImageView)findViewById(R.id.rating_star4);
	imgStar5=(ImageView)findViewById(R.id.rating_star5);
	
	imgFb.setOnClickListener(this);
	imgTwitter.setOnClickListener(this);
	imgPintrest.setOnClickListener(this);
	imgInstagram.setOnClickListener(this);
	
}
@Override
public void onClick(View v) {
	// TODO Auto-generated method stub
	switch (v.getId()) {
	case R.id.img_facebook:
		Intent browserIntentForFacebook = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/"));
		startActivity(browserIntentForFacebook);
		break;
		
	case R.id.img_twitter:
		Intent browserIntentForTwitter = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/?lang=en"));
		startActivity(browserIntentForTwitter);
		break;
		
	case R.id.img_Pinterest:
		Intent browserIntentForPinterset = new Intent(Intent.ACTION_VIEW, Uri.parse("https://in.pinterest.com/"));
		startActivity(browserIntentForPinterset);
		break;
		
	case R.id.img_instagram:
		Intent browserIntentForInstagram = new Intent(Intent.ACTION_VIEW, Uri.parse("http://instagram.com/"));
		startActivity(browserIntentForInstagram);
		break;
		
	default:
		break;
	}
}
}

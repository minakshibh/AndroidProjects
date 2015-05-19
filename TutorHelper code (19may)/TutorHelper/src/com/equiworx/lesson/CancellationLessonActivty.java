package com.equiworx.lesson;

import com.equiworx.tutorhelper.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

public class CancellationLessonActivty extends Activity{

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_cancellationlesson);
		
		
		intializelayout();
		fetchTuterList();
		setOnClickListners();

	}

	private void setOnClickListners() {
		// TODO Auto-generated method stub
		
	}

	private void fetchTuterList() {
		// TODO Auto-generated method stub
		
	}

	private void intializelayout() {
		// TODO Auto-generated method stub
		
	}
}

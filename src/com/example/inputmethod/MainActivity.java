package com.example.inputmethod;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity {
	private InputMethodResponseScrollView responseLayout;
	private View e, eBtn;
	private View lastEt, lastBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		responseLayout = (InputMethodResponseScrollView) findViewById(R.id.responseLayout);
		lastEt = findViewById(R.id.lastEt);
		lastBtn = findViewById(R.id.lastBtn);
		responseLayout.mapNextView(lastEt, lastBtn);
		
		e = findViewById(R.id.e);
		eBtn = findViewById(R.id.eBtn);
		responseLayout.mapNextView(e, eBtn);
	}
}

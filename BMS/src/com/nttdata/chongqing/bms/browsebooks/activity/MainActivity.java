package com.nttdata.chongqing.bms.browsebooks.activity;

import com.nttdata.chongqing.bms.R;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.app.Activity;
import android.content.Intent;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_browsebooks_test);
		Button button1 = (Button) findViewById(R.id.button1);
		
		button1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent int1 = new Intent();
				int1.putExtra("path", android.os.Environment.getExternalStorageDirectory().toString());
				int1.putExtra("fileName", "20140225");
				int1.setClass(MainActivity.this, BrowseBooksActivity.class);
				startActivity(int1);
				finish();
			}
		});
	}

}

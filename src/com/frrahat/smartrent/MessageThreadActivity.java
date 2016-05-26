package com.frrahat.smartrent;

import java.util.HashMap;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.frrahat.smartrent.utils.DatabaseHandler;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class MessageThreadActivity extends Activity {
	
	private TextView headingTextView;
	
	private String clientType;
	private HashMap<String, Integer> driverIDMap;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_thread);
		
		Intent intent=getIntent();
		String locationString=intent.getStringExtra("locationString");
		clientType=intent.getStringExtra("clientType");
		
		getActionBar().setTitle("Destination : "+locationString);
		
		headingTextView=(TextView) findViewById(R.id.textView_threadTop);	
		
		if("passenger".equals(clientType)){
			headingTextView.setText("Waiting for the drivers to respond");
		}else{
			headingTextView.setText("Send enqueries to the passenger.");
		}
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.thread, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}

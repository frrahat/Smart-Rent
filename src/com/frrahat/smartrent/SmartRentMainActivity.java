package com.frrahat.smartrent;

import com.frrahat.smartrent.utils.DatabaseHandler;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class SmartRentMainActivity extends Activity {
	
	Button btnGetCurrentLocation;
	Button btnHireATaxi;
	Button btnIfDriver;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_smart_rent_main);
		
		DatabaseHandler.initialize(getApplicationContext());
		
		initializeComponents();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.smart_rent_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		/*if (id == R.id.action_settings) {
			return true;
		}*/
		return super.onOptionsItemSelected(item);
	}
	

	private void initializeComponents() {
		btnGetCurrentLocation=(Button) findViewById(R.id.buttonSeeCurrentLocation);
		btnHireATaxi=(Button) findViewById(R.id.buttonHireATaxi);
		btnIfDriver=(Button) findViewById(R.id.buttonIfDriver);
		
		btnHireATaxi.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(SmartRentMainActivity.this, DestinationInputActivity.class);
				startActivity(intent);
			}
		});
		
		btnIfDriver.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(SmartRentMainActivity.this, DriverInfoUpdateActivity.class);
				startActivity(intent);
			}
		});
	}
}

package com.frrahat.smartrent;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.frrahat.smartrent.utils.DatabaseHandler;
import com.frrahat.smartrent.utils.Driver;
import com.frrahat.smartrent.utils.FileHandler;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class DriverInfoUpdateActivity extends Activity {
	
	TextView driverIDtextView;
	EditText carNumberEditText;
	EditText phoneNumberEditText;
	Switch advertiseLocationSwitch;
	
	Button btnUpdate;
	
	Driver driver;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_driver_info_update);
		
		driverIDtextView=(TextView) findViewById(R.id.textViewDriverID);
		carNumberEditText=(EditText) findViewById(R.id.editTextCarNum);
		phoneNumberEditText=(EditText) findViewById(R.id.editTextPhoneNum);
		advertiseLocationSwitch=(Switch) findViewById(R.id.switchAdvertiseLocation);
		
		btnUpdate=(Button) findViewById(R.id.buttonUpdate);
	    
		setFieldsFromLocal();
		
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
		btnUpdate.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				updateDriverInfo();
				if(driver.isAdvertisingLocation()){
					Intent intent=new Intent(DriverInfoUpdateActivity.this, RequestListActivity.class);
					//sending driver ID with intent
					intent.putExtra("driverID", driver.getDriverID());
					startActivity(intent);
				}else{
					finish();//exiting this activity
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.driver_info_update, menu);
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
		if (id == R.id.action_resetLocalDriverInfo){
			resetLocalDriverInfo();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void updateDriverInfo(){
		Editable carNumber=carNumberEditText.getText();
		if(carNumber==null || carNumber.toString().length()==0){
			showToast("Insert Car Number");
			return;
		}
		
		Editable phoneNumber=phoneNumberEditText.getText();
		if(phoneNumber==null || phoneNumber.toString().length()==0){
			showToast("Insert Phone Number");
			return;
		}
		String driverID=null;
		if(driver!=null){
			driverID=driver.getDriverID();
		}
		driver=new Driver(driverID, carNumber.toString(),
				phoneNumber.toString(), advertiseLocationSwitch.isChecked());
		
		updateDatabase();
		saveDriverInfoLocally();
	}
	
	private void showToast(String message){
		Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
	}
	
	public void updateDriverIDtextView(){
		driverIDtextView.setText(driver.getDriverID().substring(1));
	}
	
	private void setFieldsFromLocal(){
		driver=FileHandler.getDriverFromLocalStorage(getApplicationContext());
		driverIDtextView.setText((driver.getDriverID()==null?"Not set yet":
			driver.getDriverID().substring(1)));
		
		carNumberEditText.setText(driver.getCarNumber());
		if(driver.getCarNumber()!=null)
			carNumberEditText.setSelection(driver.getCarNumber().length());
		
		phoneNumberEditText.setText(driver.getPhoneNumber());
		if(driver.getPhoneNumber()!=null)
			phoneNumberEditText.setSelection(driver.getPhoneNumber().length());
		
		advertiseLocationSwitch.setChecked(driver.isAdvertisingLocation());
	}
	
	private void saveDriverInfoLocally(){
		FileHandler.saveDriverToLocalStorage(getApplicationContext(), driver);
	}
	
	private void updateDatabase(){
		if(driver.getDriverID()==null){// new driver
			String driverID=DatabaseHandler.getNewID(DatabaseHandler.getDriversRef(getApplicationContext()),"driverID",driver);
			driver.setDriverID(driverID);
			updateDriverIDtextView();
		}else{
			Query queryRef=DatabaseHandler.getDriversRef(getApplicationContext()).orderByChild("driverID").equalTo(driver.getDriverID());
			
			queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
				
				@Override
				public void onDataChange(DataSnapshot snapshot) {
					Firebase driverRef=snapshot.child(driver.getDriverID()).getRef();
					driverRef.setValue(driver);
				}
				
				@Override
				public void onCancelled(FirebaseError arg0) {
					// TODO Auto-generated method stub
					
				}
			});
		}
	}
	
	private void resetLocalDriverInfo(){
		driver=new Driver();
		FileHandler.saveDriverToLocalStorage(getApplicationContext(), driver);
		
		setFieldsFromLocal();
	}
}

package com.frrahat.smartrent;

import com.frrahat.smartrent.utils.DatabaseHandler;
import com.frrahat.smartrent.utils.FileHandler;
import com.frrahat.smartrent.utils.Message;
import com.frrahat.smartrent.utils.MessageTypes;
import com.frrahat.smartrent.utils.Passenger;
import com.frrahat.smartrent.utils.TaxiRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class DestinationInputActivity extends Activity 
	implements
	ConnectionCallbacks,
	OnConnectionFailedListener{
	
	EditText addressEditText;
	Button btnPointDestInMap;
	Button btnSend;
	
	Passenger passenger;
	
	private final int pointDestReqCode=123;
	
	private GoogleApiClient mGoogleApiClient;
	
	private LatLng sourceLocation;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_destination_input);
		
		addressEditText=(EditText) findViewById(R.id.editTextAddress);
		btnPointDestInMap=(Button) findViewById(R.id.buttonPointDestInMap);
		btnSend=(Button) findViewById(R.id.buttonSend);
		
		btnPointDestInMap.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				pointDestInMap();
			}
		});
		
		btnSend.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				performActionOnSendButtonClick();
			}
		});
		
		mGoogleApiClient = new GoogleApiClient.Builder(this)
				.addApi(LocationServices.API)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.build();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		mGoogleApiClient.connect();
	}

	@Override
	public void onPause() {
		super.onPause();
		mGoogleApiClient.disconnect();
	}
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.destination_input, menu);
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
		if (id == R.id.action_resetLocalPassengerInfo) {
			resetLocalPassengerInfo();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	
	private void showToast(String message){
		Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
	}
	
	private void pointDestInMap() {
		Intent intent=new Intent(DestinationInputActivity.this, LocationInMapActivity.class);
		startActivityForResult(intent, pointDestReqCode);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		//super.onActivityResult(requestCode, resultCode, data);
		if(requestCode==pointDestReqCode && resultCode==RESULT_OK){
			LatLng destLatLng=new LatLng(data.getDoubleExtra("latitude", 0),
					data.getDoubleExtra("longitude", 0));
			
			if(destLatLng.latitude ==0 && destLatLng.longitude==0)
				return;
			
			addressEditText.setText("LatLng=("+Double.toString(destLatLng.latitude)+","+
			Double.toString(destLatLng.longitude)+")");
		}
	}
	
	private void performActionOnSendButtonClick(){
		passenger = FileHandler.getPassengerFromLocalStorage(getApplicationContext());
		
		//setup passenger ID
		if(passenger.getPassengerID()==null){//new ID
			passenger.setPassengerID(
					DatabaseHandler.getNewID(DatabaseHandler.getPassengersRef(getApplicationContext()),
							"passengerID", passenger));
			
			FileHandler.savePassengerToLocalStorage(getApplicationContext(), passenger);
		}else{
			//TODO
		}
		
		TaxiRequest taxiRequest=new TaxiRequest(addressEditText.getText().toString(), passenger.getPassengerID(), System.currentTimeMillis());
		//pushing new request
		String requestID=DatabaseHandler.getNewID(DatabaseHandler.getRequestsRef(getApplicationContext()), "requestID", taxiRequest);
		
		//MessageThread thread=new MessageThread(requestID, System.currentTimeMillis(), true);
		//pushing new thread
		//String threadID=DatabaseHandler.getNewID(DatabaseHandler.getThreadsRef(), "threadID", thread);
		
		showToast("Sending Request...");
		
		
    	String sLatitude="0";
    	String sLongitude="0";
    	
    	if(!updateLocation())
    		return;
    	
    	if(sourceLocation!=null){
    		sLatitude=Double.toString(sourceLocation.latitude);
    		sLongitude=Double.toString(sourceLocation.longitude);
    	}
		//pushing a request message
		String locationString=addressEditText.getText().toString();

		Message pointLocationMessage =new Message(requestID, MessageTypes.LatLng, sLatitude+","+sLongitude+",0,0", 
				passenger.getPassengerID(), System.currentTimeMillis());
		if(locationString.startsWith("LatLng=(")){
			boolean isValidPointLocation;
			String parts[]=locationString.substring(8, locationString.length()-1).split(",");
			
			try{
				Double.parseDouble(parts[0]);
				Double.parseDouble(parts[1]);
				isValidPointLocation=true;
			}catch(NumberFormatException | ArrayIndexOutOfBoundsException e){
				isValidPointLocation=false;
			}
			
			if(isValidPointLocation){
				pointLocationMessage.setMessageData(sLatitude+","+sLongitude+","+parts[0]+","+parts[1]);
				
			}
		}
		DatabaseHandler.getNewID(DatabaseHandler.getMessagesRef(getApplicationContext()), null, pointLocationMessage);
		Message message =new Message(requestID, MessageTypes.TEXT, "Anyone ready to take me to "+addressEditText.getText(), 
					passenger.getPassengerID(), System.currentTimeMillis());
		DatabaseHandler.getNewID(DatabaseHandler.getMessagesRef(getApplicationContext()), null, message);
		
		//advance to Message Thread
		Intent intent=new Intent(DestinationInputActivity.this, MessageThreadActivity.class);
		intent.putExtra("locationString", locationString);
		intent.putExtra("clientType", "passenger");
		intent.putExtra("requestID", requestID);
		intent.putExtra("clientID", passenger.getPassengerID());
		//intent.putExtra("threadID", threadID);
				
		startActivity(intent);
	}
	
	private void resetLocalPassengerInfo(){
		passenger=new Passenger();
		FileHandler.savePassengerToLocalStorage(getApplicationContext(), passenger);
		
		showToast("Deleted");
	}

	
	
	
	
	
	
	
	
	
    
	
	
	
	

	private boolean updateLocation(){
		Location location=LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
		if(location!=null){
			sourceLocation=new LatLng(location.getLatitude(), location.getLongitude());
			return true;
		}
		else{
			showToast("Couldn't get your current location. Make sure location is enabled on the device");
			return false;
		}
	}
    
	@Override
    public void onConnectionSuspended(int cause) {
        // Do nothing
    }

    /**
     * Implementation of {@link OnConnectionFailedListener}.
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Toast.makeText(getApplicationContext(), "Conection Failure", Toast.LENGTH_SHORT).show();
    }

	/* (non-Javadoc)
	 * @see com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks#onConnected(android.os.Bundle)
	 */
	@Override
	public void onConnected(Bundle arg0) {
		//updateLocation();
	}
}

package com.frrahat.smartrent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.frrahat.smartrent.utils.DatabaseHandler;
import com.frrahat.smartrent.utils.TaxiRequest;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class RequestListActivity extends Activity {
	
	private String driverID;
	
	private TextView requestInfoTextView;
	private ListView requestListView;
	private BaseAdapter adapter;
	
	private ArrayList<TaxiRequest> requestList;
	private HashMap<String, Integer> passengerIDMap;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_request_list);
		
		driverID=getIntent().getStringExtra("driverID");
		
		requestInfoTextView = (TextView) findViewById(R.id.textViewRequestInfo);
		requestListView=(ListView) findViewById(R.id.listViewRequests);
		
		requestList=new ArrayList<TaxiRequest>();
		passengerIDMap=new HashMap<String, Integer>();
		
		adapter = new BaseAdapter() {

			LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			@SuppressLint({ "InflateParams", "SimpleDateFormat" })
			@Override
			public View getView(int position, View view, ViewGroup parent) {
				if (view == null) {
					view = layoutInflater.inflate(R.layout.request_list_item,
							null);
				}
				LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.linearLayout_reqListItem);
				TextView textView1 = (TextView) view.findViewById(R.id.text1);
				TextView textView2 = (TextView) view.findViewById(R.id.text2);
				TextView textView3 = (TextView) view.findViewById(R.id.text3);
				TextView textView4 = (TextView) view.findViewById(R.id.text4);
				
				TaxiRequest request=requestList.get(requestList.size()-position-1);
				textView1.setText(Integer.toString(position + 1) + ")  From Passenger - "+passengerIDMap.get(request.getPassengerID()));
				textView2.setText("Destination : "+request.getLocationString());
				
				SimpleDateFormat sdf = new SimpleDateFormat("dd MMM,yyyy hh:mm a");    
				Date resultdate = new Date(request.getRequestTime());				
				textView3.setText("Time : "+sdf.format(resultdate));

				if(request.getIsAccepted()){//deactivated
					textView4.setTextColor(Color.RED);
					textView4.setText("•Not available");
				}
				else{
					textView4.setText("•Available");
				}
				return view;
			}

			@Override
			public long getItemId(int position) {
				return position + 1;
			}

			@Override
			public Object getItem(int position) {
				return requestList.get(requestList.size()-position-1);
			}

			@Override
			public int getCount() {
				return requestList.size();
			}
		};

		requestListView.setAdapter(adapter);
		
		
		requestListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent=new Intent(RequestListActivity.this, MessageThreadActivity.class);
				TaxiRequest request=requestList.get(requestList.size()-position-1);
				intent.putExtra("locationString", request.getLocationString());
				intent.putExtra("clientType", "driver");
				intent.putExtra("requestID", request.getRequestID());
				intent.putExtra("clientID", driverID);
				
				startActivity(intent);
			}
		});
		
		
		
		
		
		
		
		
		
		//===========================================================
		DatabaseHandler.getRequestsRef(getApplicationContext()).addChildEventListener(new ChildEventListener() {
			
			@Override
			public void onChildRemoved(DataSnapshot arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onChildMoved(DataSnapshot arg0, String arg1) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onChildChanged(DataSnapshot arg0, String arg1) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onChildAdded(DataSnapshot snapshot, String previousChildKey) {
				TaxiRequest newRequest = snapshot.getValue(TaxiRequest.class);
				requestList.add(newRequest);
				Integer key=passengerIDMap.get(newRequest.getPassengerID());
				if(key==null){
					passengerIDMap.put(newRequest.getPassengerID(), passengerIDMap.size()+1);
				}
				adapter.notifyDataSetChanged();
			}
			
			@Override
			public void onCancelled(FirebaseError arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.request_list, menu);
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
}

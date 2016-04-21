package com.frrahat.smartrent;

import java.util.ArrayList;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.frrahat.smartrent.utils.DatabaseHandler;
import com.frrahat.smartrent.utils.TaxiRequest;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class RequestListActivity extends Activity {
	
	private TextView requestInfoTextView;
	private ListView requestListView;
	private BaseAdapter adapter;
	
	private ArrayList<TaxiRequest> requests;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_request_list);
		
		requestInfoTextView = (TextView) findViewById(R.id.textViewRequestInfo);
		requestListView=(ListView) findViewById(R.id.listViewRequests);
		
		requests=new ArrayList<TaxiRequest>();
		
		adapter = new BaseAdapter() {

			LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			@SuppressLint("InflateParams")
			@Override
			public View getView(int position, View view, ViewGroup parent) {
				if (view == null) {
					view = layoutInflater.inflate(R.layout.request_list_item,
							null);
				}
				TextView textView1 = (TextView) view.findViewById(R.id.text1);
				TextView textView2 = (TextView) view.findViewById(R.id.text2);
				
				TaxiRequest request=requests.get(requests.size()-position-1);
				textView1.setText(Integer.toString(position + 1) + ")  "+request.getPassengerID());
				textView2.setText(request.getLocationString());

				return view;
			}

			@Override
			public long getItemId(int position) {
				return position + 1;
			}

			@Override
			public Object getItem(int position) {
				return requests.get(requests.size()-position-1);
			}

			@Override
			public int getCount() {
				return requests.size();
			}
		};

		requestListView.setAdapter(adapter);
		
		
		
		
		
		
		
		
		
		
		
		
		//===========================================================
		DatabaseHandler.getRequestsRef().addChildEventListener(new ChildEventListener() {
			
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
				requests.add(newRequest);
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
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}

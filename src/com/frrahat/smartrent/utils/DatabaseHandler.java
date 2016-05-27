package com.frrahat.smartrent.utils;

import com.firebase.client.Firebase;

import android.content.Context;

public class DatabaseHandler {
	private static String superRootRefUrl="https://sizzling-inferno-8477.firebaseio.com";
	
	private static Firebase rootRef;
	
	public static void initialize(Context context){
		//initialize firebase lib
		Firebase.setAndroidContext(context);
		
		rootRef=new Firebase(superRootRefUrl).child("smartRent");
	}
	
	public static Firebase getRootRef(Context context){
		if(rootRef==null)
			initialize(context);
		return rootRef;
	}
	
	public static Firebase getDriversRef(Context context){
		if(rootRef==null)
			initialize(context);
		return rootRef.child("Drivers");
	}
	
	public static Firebase getPassengersRef(Context context){
		if(rootRef==null)
			initialize(context);
		return rootRef.child("Passengers");
	}
	
	public static Firebase getRequestsRef(Context context){
		if(rootRef==null)
			initialize(context);
		return rootRef.child("Requests");
	}
	
	public static Firebase getThreadsRef(Context context){
		if(rootRef==null)
			initialize(context);
		return rootRef.child("Threads");
	}
	
	public static Firebase getMessagesRef(Context context){
		if(rootRef==null)
			initialize(context);
		return rootRef.child("Messages");
	}
	
	public static String getNewID(Firebase ref,String IDfieldString,Object object){
		Firebase newRef=ref.push();
		newRef.setValue(object);
		
		String ID=newRef.getKey();
		if(IDfieldString!=null){
			newRef.child(IDfieldString).setValue(ID);
		}
		
		return ID;
	}
}

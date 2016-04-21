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
	
	public static Firebase getRootRef(){
		return rootRef;
	}
	
	public static Firebase getDriversRef(){
		return rootRef.child("Drivers");
	}
	
	public static Firebase getPassengersRef(){
		return rootRef.child("Passengers");
	}
	
	public static Firebase getRequestsRef(){
		return rootRef.child("Requests");
	}
	
	public static Firebase getThreadsRef(){
		return rootRef.child("Threads");
	}
	
	public static String getNewID(Firebase ref,String IDfieldString,Object object){
		Firebase newRef=ref.push();
		newRef.setValue(object);
		
		String ID=newRef.getKey();
		newRef.child(IDfieldString).setValue(ID);
		
		return ID;
	}
}

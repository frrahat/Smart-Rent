package com.frrahat.smartrent.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import android.content.Context;
import android.content.ContextWrapper;
import android.os.Environment;

/**
 * @author Rahat
 * @since Apr 21, 2016
 */
public class FileHandler {
	public static final String StorageFolderName=".com.frrahat.smartrent";
	public static final String DriverInfoFileName=".driverInfo.ser";
	public static final String PassengerInfoFileName=".passengerInfo.ser";
	
	
	public static File getInfoFile(Context context, String infoFileName){
		File storageDir;
		String state=Environment.getExternalStorageState();
		// has writable external  storage
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			storageDir = new File(Environment.getExternalStorageDirectory(),
					StorageFolderName);
		} else {
			ContextWrapper contextWrapper = new ContextWrapper(
					context.getApplicationContext());
			storageDir = contextWrapper.getDir(StorageFolderName,
					Context.MODE_PRIVATE);
		}
		
		if(!storageDir.exists()){
			storageDir.mkdirs();
		}
		File infoFile = new File(storageDir,infoFileName);
		
		return infoFile;
	}
	
	public static Driver getDriverFromLocalStorage(Context context){
		File infoFile = getInfoFile(context, DriverInfoFileName);
		Driver driver=new Driver();
		if (infoFile != null && infoFile.exists()) {
			try {
				FileInputStream inStream = new FileInputStream(infoFile);
				ObjectInputStream objectInStream = new ObjectInputStream(inStream);
				driver = (Driver) objectInStream.readObject();
				objectInStream.close();
			}catch(IOException | ClassNotFoundException e){
				e.printStackTrace();
			}

		}
		return driver;
	}
	
	public static Passenger getPassengerFromLocalStorage(Context context){
		File infoFile = getInfoFile(context, PassengerInfoFileName);
		Passenger passenger=new Passenger();
		if (infoFile != null && infoFile.exists()) {
			try {
				FileInputStream inStream = new FileInputStream(infoFile);
				ObjectInputStream objectInStream = new ObjectInputStream(inStream);
				passenger = (Passenger) objectInStream.readObject();
				objectInStream.close();
			}catch(IOException | ClassNotFoundException e){
				e.printStackTrace();
			}

		}
		return passenger;
	}
	
	public static void saveDriverToLocalStorage(Context context, Driver driver){
		File infoFile = getInfoFile(context, DriverInfoFileName);
		
		try {
			FileOutputStream outStream=new FileOutputStream(infoFile);
			ObjectOutputStream objectOutStream=new ObjectOutputStream(outStream);
			objectOutStream.writeObject(driver);
			objectOutStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void savePassengerToLocalStorage(Context context, Passenger passenger){
		File infoFile = getInfoFile(context, PassengerInfoFileName);
		
		try {
			FileOutputStream outStream=new FileOutputStream(infoFile);
			ObjectOutputStream objectOutStream=new ObjectOutputStream(outStream);
			objectOutStream.writeObject(passenger);
			objectOutStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

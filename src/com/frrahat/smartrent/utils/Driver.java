package com.frrahat.smartrent.utils;

import java.io.Serializable;

import android.location.Location;

/**
 * @author Rahat
 * 18-April-2016
 */
/**
 * @author Rahat
 * @since Apr 20, 2016
 */
public class Driver implements Serializable{
	private String driverID;
	private String carNumber;
	private String phoneNumber;
	private boolean isAdvertisingLocation;
	private Location currentLocation;
	private double rating;
	
	public Driver() {
		isAdvertisingLocation=true;
		rating=0.0;
	}

	public Driver(String carNumber, String phoneNumber,
			boolean isAdvertisingLocation) {
		this.carNumber = carNumber;
		this.phoneNumber = phoneNumber;
		this.isAdvertisingLocation = isAdvertisingLocation;
	}

	public String getDriverID() {
		return driverID;
	}

	public String getCarNumber() {
		return carNumber;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}
	
	public Location getCurrentLocation(){
		return currentLocation;
	}

	public double getRating() {
		return rating;
	}

	public boolean isAdvertisingLocation() {
		return isAdvertisingLocation;
	}

	public void setDriverID(String driverID) {
		this.driverID = driverID;
	}

	public void setCarNumber(String carNumber) {
		this.carNumber = carNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public void setAdvertisingLocation(boolean isAdvertisingLocation) {
		this.isAdvertisingLocation = isAdvertisingLocation;
	}
	
	public void setCurrentLocation(Location currentLocation){
		this.currentLocation = currentLocation;
	}
	
	public void setRating(double rating) {
		this.rating = rating;
	}
}

package com.frrahat.smartrent.utils;

import java.io.Serializable;

/**
 * @author Rahat
 * @since Apr 21, 2016
 */
public class Passenger implements Serializable{
	private String passengerID;
	
	public Passenger() {
	}

	public String getPassengerID() {
		return passengerID;
	}

	public void setPassengerID(String passengerID) {
		this.passengerID = passengerID;
	}
}

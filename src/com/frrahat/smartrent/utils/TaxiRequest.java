package com.frrahat.smartrent.utils;

/**
 * @author Rahat
 * @since Apr 21, 2016
 */
/**
 * @author Rahat
 * @since Apr 20, 2016
 */
public class TaxiRequest {
	private String requestID;
	private String locationString;
	private String passengerID;
	private long requestTime;
	private double fare;
	private boolean isAccepted;
	private int acceptedDriverID;
	
	public TaxiRequest() {
	}

	public TaxiRequest(String locationString, String passengerID, long requestTime) {
		this.locationString = locationString;
		this.passengerID = passengerID;
		this.requestTime=requestTime;
	}

	public String getLocationString() {
		return locationString;
	}

	public String getPassengerID() {
		return passengerID;
	}
	
	
	public String getRequestID() {
		return requestID;
	}
	
	
	public long getRequestTime() {
		return requestTime;
	}
	
	
	public double getFare() {
		return fare;
	}
	
	
	public int getAcceptedDriverID() {
		return acceptedDriverID;
	}

	public void setAcceptedDriverID(int acceptedDriverID) {
		this.acceptedDriverID = acceptedDriverID;
	}

	public boolean isAccepted() {
		return isAccepted;
	}

	public void setLocationString(String locationString) {
		this.locationString = locationString;
	}

	public void setPassengerID(String passengerID) {
		this.passengerID = passengerID;
	}
	
	public void setRequestID(String requestID) {
		this.requestID = requestID;
	}
	
	public void setRequestTime(long requestTime) {
		this.requestTime = requestTime;
	}
	
	public void setFare(double fare) {
		this.fare = fare;
	}

	public void setAccepted(boolean isAccepted) {
		this.isAccepted = isAccepted;
	}

}

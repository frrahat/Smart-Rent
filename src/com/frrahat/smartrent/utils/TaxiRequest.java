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
	private Long requestTime;
	private Double fare;
	private Boolean isAccepted;
	private String acceptedDriverID;
	
	public TaxiRequest() {
	}

	public TaxiRequest(String locationString, String passengerID, long requestTime) {
		this.locationString = locationString;
		this.passengerID = passengerID;
		this.requestTime=requestTime;
		
		this.isAccepted=false;
		this.fare=0.0;
		this.acceptedDriverID="";
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
	
	
	public Long getRequestTime() {
		return requestTime;
	}
	
	
	public Double getFare() {
		return fare;
	}
	
	
	public String getAcceptedDriverID() {
		return acceptedDriverID;
	}

	public void setAcceptedDriverID(String acceptedDriverID) {
		this.acceptedDriverID = acceptedDriverID;
	}

	public Boolean getIsAccepted() {
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
	
	public void setRequestTime(Long requestTime) {
		this.requestTime = requestTime;
	}
	
	public void setFare(Double fare) {
		this.fare = fare;
	}

	public void setIsAccepted(Boolean isAccepted) {
		this.isAccepted = isAccepted;
	}
	
}

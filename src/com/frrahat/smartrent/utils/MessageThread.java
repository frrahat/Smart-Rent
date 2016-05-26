package com.frrahat.smartrent.utils;

/**
 * @author Rahat
 * @since Apr 20, 2016
 */
public class MessageThread {
	private String threadID;
	private String requestID;
	private long startTime;
	private boolean isActive;
	
	public MessageThread() {
	}
	
	

	public MessageThread(String requestID, long startTime, boolean isActive) {
		this.threadID = threadID;
		this.requestID = requestID;
		this.startTime = startTime;
		this.isActive = isActive;
	}



	public String getThreadID() {
		return threadID;
	}

	public long getStartTime() {
		return startTime;
	}
	
	
	public String getRequestID() {
		return requestID;
	}

	public void setRequestID(String requestID) {
		this.requestID = requestID;
	}



	public boolean isActive() {
		return isActive;
	}

	public void setThreadID(String threadID) {
		this.threadID = threadID;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
	
	
}

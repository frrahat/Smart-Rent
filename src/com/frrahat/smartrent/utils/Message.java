package com.frrahat.smartrent.utils;

/**
 * @author Rahat
 * @since Apr 20, 2016
 */
public class Message {
	private String requestID;
	private int messageType;
	private String messageData;
	private String authorID;
	private long time;
	
	public Message(){}
	
	public Message(String requestID,int messageType, String messageData, String authorID, long time) {
		this.requestID=requestID;
		this.messageType = messageType;
		this.messageData = messageData;
		this.authorID = authorID;
		this.time=time;
	}
	
	public int getMessageType() {
		return messageType;
	}
	public String getMessageData() {
		return messageData;
	}
	public String getAuthorID() {
		return authorID;
	}
	public long getTime() {
		return time;
	}

	public String getRequestID() {
		return requestID;
	}

	public void setRequestID(String requestID) {
		this.requestID = requestID;
	}

	public void setMessageType(int messageType) {
		this.messageType = messageType;
	}
	public void setMessageData(String messageData) {
		this.messageData = messageData;
	}
	public void setAuthorID(String authorID) {
		this.authorID = authorID;
	}
	public void setTime(long time) {
		this.time = time;
	}
	
	
}

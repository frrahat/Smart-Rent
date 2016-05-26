package com.frrahat.smartrent.utils;

/**
 * @author Rahat
 * @since Apr 20, 2016
 */
public class Message {
	private int threadID;
	private int messageType;
	private String messageData;
	private String author;
	private long time;
	
	
	public Message(int threadID,int messageType, String messageData, String author, long time) {
		this.threadID=threadID;
		this.messageType = messageType;
		this.messageData = messageData;
		this.author = author;
		this.time=time;
	}
	
	public int getMessageType() {
		return messageType;
	}
	public String getMessageData() {
		return messageData;
	}
	public String getAuthor() {
		return author;
	}
	public long getTime() {
		return time;
	}
	
	public int getThreadID() {
		return threadID;
	}

	public void setThreadID(int threadID) {
		this.threadID = threadID;
	}

	public void setMessageType(int messageType) {
		this.messageType = messageType;
	}
	public void setMessageData(String messageData) {
		this.messageData = messageData;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public void setTime(long time) {
		this.time = time;
	}
	
	
}

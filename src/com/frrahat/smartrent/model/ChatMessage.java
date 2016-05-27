package com.frrahat.smartrent.model;

/**
 * Created by madhur on 17/01/15.
 */
public class ChatMessage {

    private String messageText;
    private UserType userType;
    private Status messageStatus;
    private String author;
    
    public ChatMessage(){
    	
    }
    public ChatMessage(String messageText, UserType userType,
			Status messageStatus, String author, long messageTime) {
		this.messageText = messageText;
		this.userType = userType;
		this.messageStatus = messageStatus;
		this.author = author;
		this.messageTime = messageTime;
	}

	public long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }

    private long messageTime;

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public void setMessageStatus(Status messageStatus) {
        this.messageStatus = messageStatus;
    }

    public String getMessageText() {

        return messageText;
    }

    public UserType getUserType() {
        return userType;
    }

    public Status getMessageStatus() {
        return messageStatus;
    }

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}
    
    
}

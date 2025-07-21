package com.chat.model;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String chatId;
    private String senderId;
    private String recipientId;
    @Transient
    private String recipient_name;
    @Transient
    private boolean online;
    @Transient
    private int unreadCount;
    
	public ChatRoom(Long id, String chatId, String senderId, String recipientId) {
		super();
		this.id = id;
		this.chatId = chatId;
		this.senderId = senderId;
		this.recipientId = recipientId;
	}
	public ChatRoom(String chatId,String senderId, String recipientId) {
		super();
		this.chatId=chatId;
		this.senderId = senderId;
		this.recipientId = recipientId;
	}
    
	
	
    
}
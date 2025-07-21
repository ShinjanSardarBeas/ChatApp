package com.chat.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.chat.model.ChatMessage;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
 
    @Query("SELECT cm FROM ChatMessage cm WHERE cm.chatId = :chatId")
	 List<ChatMessage> findBySenderIdAndRecipientId(@Param("chatId") String chatId);
  
    @Query("SELECT cm FROM ChatMessage cm WHERE cm.chatId = :chatId")
	    List<ChatMessage> findByRecipientIdAndSenderId(@Param("chatId") String chatId);

    List<ChatMessage> findBySenderIdAndRecipientIdAndIsReadFalse(String senderId, String recipientId);

    @Query("SELECT m.senderId, COUNT(m) FROM ChatMessage m WHERE m.recipientId = :userId AND m.isRead = false GROUP BY m.senderId")
    List<Object[]> findUnreadCountsByUserId(@Param("userId") String userId);

	List<ChatMessage> findByRecipientIdAndIsReadTrueAndSeenTimestampIsNull(String recipientId);
	    
  
}

package com.chat.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.chat.model.ChatMessage;
import com.chat.model.ChatRoom;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {


    Optional<ChatRoom> findBySenderIdAndRecipientId(String senderId, String recipientId);
    
    @Query("SELECT cr FROM ChatRoom cr WHERE cr.senderId = :userId OR cr.recipientId = :userId ORDER BY cr.id DESC")
    List<ChatRoom> findRecentChats(@Param("userId") String userId);
}

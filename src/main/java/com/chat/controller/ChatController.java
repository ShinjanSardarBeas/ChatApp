package com.chat.controller;

import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.chat.model.ChatMessage;
import com.chat.model.ChatRoom;
import com.chat.model.MarkAsReadRequest;
import com.chat.repository.ChatMessageRepository;
import com.chat.service.ChatMessageService;


@RestController
@CrossOrigin(origins = "*")
public class ChatController {

	private final Logger logger = LogManager.getLogger(ChatController.class);
	
	@Autowired
	private SimpMessagingTemplate messagingTemplate;
	@Autowired
	private ChatMessageService chatMessageService;
	@Autowired
	 private  ChatMessageRepository chatMessageRepository;
	
    @MessageMapping("/chat")
    public void processMessage(@Payload ChatMessage chatMessage) {
        try {
            ChatMessage savedMessage = chatMessageService.save(chatMessage);
            logger.info("recipient Id "+chatMessage.getRecipientId());
            messagingTemplate.convertAndSendToUser(chatMessage.getRecipientId(), "/queue/messages", savedMessage);
          
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


	@GetMapping("/messages/{senderId}/{recipientId}")
	public List<ChatMessage> findChatMessages(@PathVariable String senderId, @PathVariable String recipientId) {
		return chatMessageService.findChatMessages(senderId, recipientId);
	}

	@GetMapping("/recent-chats/{senderId}")
	public  List<ChatRoom> getRecentChats(@PathVariable String senderId) {
		return chatMessageService.findRecentChats(senderId);
	}
	
    @PostMapping("/markAsRead")
    public ResponseEntity<?> markMessagesAsRead(@RequestBody MarkAsReadRequest request) {
        chatMessageService.markMessagesAsRead(request.getSenderId(), request.getRecipientId());
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/markAsSeen/{recipientId}")
    public ResponseEntity<?> markMessagesAsSeen(@PathVariable String recipientId) {
        chatMessageService.markMessagesAsSeen(recipientId);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/unread-count/{userId}")
    public ResponseEntity<Map<String, Integer>> getUnreadMessageCounts(@PathVariable("userId") String userId) {
    	logger.info("executing..unreadCount "+userId);
        Map<String, Integer> unreadCounts = chatMessageService.getUnreadMessageCounts(userId);
        return ResponseEntity.ok(unreadCounts);
    } 
    
	

}

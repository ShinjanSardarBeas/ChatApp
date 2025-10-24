package com.chat.controller;

import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.chat.model.ChatMessage;
import com.chat.model.ChatRoom;
import com.chat.model.MarkAsReadRequest;
import com.chat.repository.ChatMessageRepository;
import com.chat.service.ChatMessageService;


@RestController
@CrossOrigin(origins = "*")
public class ChatController {

	private final Logger logger = LogManager.getLogger(ChatController.class);
	private static final String APIGATEWAY_VALIDATE_TOKEN_URL = "http://192.168.1.183:8086/api/v1/validateToken";

	@Autowired
    private RestTemplate restTemplate;
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
    public ResponseEntity<?> markMessagesAsRead(@RequestHeader("Authorization") String token, @RequestBody MarkAsReadRequest request) {
    	try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", token);
            HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
            
            ResponseEntity<String> validationResponse = restTemplate.exchange(
                APIGATEWAY_VALIDATE_TOKEN_URL,
                HttpMethod.GET,
                requestEntity,
                String.class
            );
            
            if (validationResponse.getStatusCode() != HttpStatus.OK) {
                 return ResponseEntity.status(validationResponse.getStatusCode())
                                      .body(null);
            }
            
        } catch (HttpClientErrorException e) {
            System.err.println("External token validation failed with status: " + e.getStatusCode());
            return ResponseEntity.status(e.getStatusCode()).body(null);
            
        } catch (RestClientException e) {
            System.err.println("Error calling external validation service: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    	chatMessageService.markMessagesAsRead(request.getSenderId(), request.getRecipientId());
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/markAsSeen/{recipientId}")
    public ResponseEntity<?> markMessagesAsSeen(@RequestHeader("Authorization") String token, @PathVariable String recipientId) {
    	try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", token);
            HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
            
            ResponseEntity<String> validationResponse = restTemplate.exchange(
                APIGATEWAY_VALIDATE_TOKEN_URL,
                HttpMethod.GET,
                requestEntity,
                String.class
            );
            
            if (validationResponse.getStatusCode() != HttpStatus.OK) {
                 return ResponseEntity.status(validationResponse.getStatusCode())
                                      .body(null);
            }
            
        } catch (HttpClientErrorException e) {
            System.err.println("External token validation failed with status: " + e.getStatusCode());
            return ResponseEntity.status(e.getStatusCode()).body(null);
            
        } catch (RestClientException e) {
            System.err.println("Error calling external validation service: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
        chatMessageService.markMessagesAsSeen(recipientId);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/unread-count/{userId}")
    public ResponseEntity<Map<String, Integer>> getUnreadMessageCounts(@RequestHeader("Authorization") String token, @PathVariable("userId") String userId) {
    	logger.info("executing..unreadCount "+userId);
    	
    	try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", token);
            HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
            
            ResponseEntity<String> validationResponse = restTemplate.exchange(
                APIGATEWAY_VALIDATE_TOKEN_URL,
                HttpMethod.GET,
                requestEntity,
                String.class
            );
            
            if (validationResponse.getStatusCode() != HttpStatus.OK) {
                 return ResponseEntity.status(validationResponse.getStatusCode())
                                      .body(null);
            }
            
        } catch (HttpClientErrorException e) {
            System.err.println("External token validation failed with status: " + e.getStatusCode());
            return ResponseEntity.status(e.getStatusCode()).body(null);
            
        } catch (RestClientException e) {
            System.err.println("Error calling external validation service: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
        Map<String, Integer> unreadCounts = chatMessageService.getUnreadMessageCounts(userId);
        return ResponseEntity.ok(unreadCounts);
    } 
    
	

}

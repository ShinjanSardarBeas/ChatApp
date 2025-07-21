package com.chat.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.chat.model.ChatMessage;
import com.chat.model.ChatRoom;
import com.chat.model.User;
import com.chat.repository.ChatMessageRepository;
import com.chat.repository.ChatRoomRepository;
import com.chat.repository.UserRepository;

@Service
public class ChatMessageService {
    @Autowired
    private UserRepository userRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;

    public ChatMessageService(ChatMessageRepository chatMessageRepository, ChatRoomRepository chatRoomRepository) {
        this.chatMessageRepository = chatMessageRepository;
        this.chatRoomRepository = chatRoomRepository;
    }

    public ChatMessage save(ChatMessage chatMessage) {
    	chatMessage.setIsRead(false);
        chatMessage.setSender_name(userRepository.getSenderName(chatMessage.getSenderId()).getName());
        chatMessage.setRecipient_name(userRepository.getRecipientName(chatMessage.getRecipientId()).getName());
        chatRoomRepository.findBySenderIdAndRecipientId(chatMessage.getSenderId(), chatMessage.getRecipientId())
                .orElseGet(() -> chatRoomRepository.save(new ChatRoom(chatMessage.getChatId(),
                        chatMessage.getSenderId(), chatMessage.getRecipientId())));
        chatMessage.setTimestamp(new Timestamp(System.currentTimeMillis()));
       
        ChatMessage saved = chatMessageRepository.save(chatMessage);
        saved.setOnline(userRepository.getStatusBySenderId(Integer.parseInt(saved.getSenderId()) ).isOnline());
        
        return saved;
    }

    public List<ChatMessage> findChatMessages(String senderId, String recipientId) {
        List<ChatMessage> messagesFromSenderToRecipient = chatMessageRepository
                .findBySenderIdAndRecipientId(senderId + "_" + recipientId);
        List<ChatMessage> messagesFromRecipientToSender = chatMessageRepository
                .findByRecipientIdAndSenderId(recipientId + "_" + senderId);
        List<ChatMessage> allMessages = new ArrayList<>();
        allMessages.addAll(messagesFromSenderToRecipient);
        allMessages.addAll(messagesFromRecipientToSender);
        allMessages.sort(Comparator.comparing(ChatMessage::getTimestamp));
        return allMessages;
    }

    public List<ChatRoom> findRecentChats(String userId) {
        List<ChatRoom> recentChats = chatRoomRepository.findRecentChats(userId);
        recentChats.stream().forEach(chatRoom -> {
            if (chatRoom.getSenderId().equals(userId)) { //current Logged In User's sent msg to reci
            	 User user = userRepository.getRecipientName(chatRoom.getRecipientId());
                chatRoom.setRecipient_name(user!=null?user.getName():chatRoom.getRecipientId());
                chatRoom.setOnline(user!=null?user.isOnline():false);
            } else {// current user got msg from another user..
            	User user = userRepository.getRecipientName(chatRoom.getSenderId());
                chatRoom.setRecipient_name(user!=null?user.getName():chatRoom.getSenderId());
                String temp = chatRoom.getSenderId();
                chatRoom.setSenderId(chatRoom.getRecipientId());
                chatRoom.setRecipientId(temp);
                chatRoom.setOnline(user!=null?user.isOnline():false);

            }
        });

    
        // Step 3: Collect chats into a map with recipientId as key (distinct by recipientId)
        Map<String, ChatRoom> distinctChats = recentChats.stream()
                .collect(Collectors.toMap(ChatRoom::getRecipientId, chatRoom -> chatRoom, (existing, replacement) -> existing));
        
        List<ChatRoom> distinctChatsList = distinctChats.values().stream()
                .collect(Collectors.toList());

        return distinctChatsList;
    }

    public List<ChatMessage> markMessagesAsRead(String senderId, String recipientId) {
        List<ChatMessage> messages = chatMessageRepository.findBySenderIdAndRecipientIdAndIsReadFalse(senderId, recipientId);
        messages.forEach(message ->{ message.setIsRead(true);
        message.setSeenTimestamp(new Date());}); // Add this line

        
        return chatMessageRepository.saveAll(messages);
    }
    
    
    public void markMessagesAsSeen(String recipientId) {
        List<ChatMessage> messages = chatMessageRepository.findByRecipientIdAndIsReadTrueAndSeenTimestampIsNull(recipientId);
        messages.forEach(message -> message.setSeenTimestamp(new Date()));
        chatMessageRepository.saveAll(messages);
    }
    
    public Map<String, Integer> getUnreadMessageCounts(String userId) {
        List<Object[]> counts = chatMessageRepository.findUnreadCountsByUserId(userId);
        Map<String, Integer> unreadCounts = new HashMap<>();
        for (Object[] count : counts) {
            unreadCounts.put((String) count[0], ((Long) count[1]).intValue());
        }
        return unreadCounts;
    }

 
}

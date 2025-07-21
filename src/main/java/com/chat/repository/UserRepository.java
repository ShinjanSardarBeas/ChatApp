
package com.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.chat.model.User;

public interface UserRepository extends JpaRepository<User, Integer> {

	@Query("select u from User u where u.id=:senderId")
	User getSenderName(@Param("senderId") String senderId);

	@Query("select u from User u where u.id=:recipientId")
	User getRecipientName(@Param("recipientId") String recipientId);

	@Query("select u from User u where u.id=:senderId")
	User getStatusBySenderId(@Param("senderId") int senderId);

}

package com.chat.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class WebSocketExceptionHandler {
	
	
	  @ExceptionHandler(WebSocketException.class)
	    public ResponseEntity<String> handleWebSocketException(WebSocketException ex) {
	      
	        String errorMessage = "WebSocket Exception: " + ex.getMessage();
	        return new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
	    }

}

package com.everest.chatserver.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.sockjs.client.RestTemplateXhrTransport;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import com.everest.chatserver.model.Message;




@Controller
public class ChatController {
	 @Autowired
	    private SimpMessageSendingOperations messagingTemplate;
	 
	   @MessageMapping("/chat.sendMessage")
	    @SendTo("/topic/public")
	    public Message sendMessage(@Payload Message chatMessage) {
		   List<Transport> transports = Arrays.asList(
					new WebSocketTransport(new StandardWebSocketClient()),
					new RestTemplateXhrTransport(new RestTemplate()));
		   SockJsClient sockJsClient = new SockJsClient(transports);
	        return chatMessage;
	    }

	    @MessageMapping("/chat.addUser")
	    @SendTo("/topic/public")
	    public Message addUser(@Payload Message chatMessage,
	                               SimpMessageHeaderAccessor headerAccessor) {
	        // Add username in web socket session
	        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
	        return chatMessage;
	    }
	    @MessageMapping("/personalchat")
	    public void sendSpecific(@Payload Message msg) throws Exception {
	       // Message out = new Message(msg.getSender(), msg.getContent(), new SimpleDateFormat("HH:mm").format(new Date()));
	    	
	        messagingTemplate.convertAndSendToUser(msg.getReceiver(), "/personal", msg);
	    }

}

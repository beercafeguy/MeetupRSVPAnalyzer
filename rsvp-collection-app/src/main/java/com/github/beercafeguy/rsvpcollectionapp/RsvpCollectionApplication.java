package com.github.beercafeguy.rsvpcollectionapp;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import java.net.URI;

@SpringBootApplication
public class RsvpCollectionApplication {

    private static final String MEETUP_RSVP_END_POINT="ws://stream.meetup.com/2/rsvps";
	public static void main(String[] args) {
		SpringApplication.run(RsvpCollectionApplication.class, args);
	}

	@Bean
    public ApplicationRunner initializeConnection(RsvpsWebSocketHandler rsvpsWebSocketHandler){
	    return args ->{
            WebSocketClient client=new StandardWebSocketClient();
            client.doHandshake(rsvpsWebSocketHandler,MEETUP_RSVP_END_POINT);
        };
    }
}


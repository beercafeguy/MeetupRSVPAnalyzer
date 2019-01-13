package org.github.beercafeguy.distribution.api;

import org.github.beercafeguy.distribution.model.MeetupRSVP;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Flux;

@RestController
public class RsvpsController {

	private final ReactiveMongoTemplate reactiveMongoTemplate;

	public RsvpsController(ReactiveMongoTemplate reactiveMongoTemplate) {
		this.reactiveMongoTemplate = reactiveMongoTemplate;
	}
	
	@GetMapping(value="/getRsvps",produces=MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<MeetupRSVP> getRsvps(){
		return reactiveMongoTemplate.tail(new Query(), MeetupRSVP.class).share();
	}
}

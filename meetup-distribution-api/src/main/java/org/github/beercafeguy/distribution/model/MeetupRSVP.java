package org.github.beercafeguy.distribution.model;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="rsvps_with_guests")
public class MeetupRSVP {

	private Venue venue;
    private String visibility;
    private String response;
    private Integer guests;
    private Member member;
    private Integer rsvpId;
    private Long mtime;
    private Event event;
    private Group group;
	public Venue getVenue() {
		return venue;
	}
	public void setVenue(Venue venue) {
		this.venue = venue;
	}
	public String getVisibility() {
		return visibility;
	}
	public void setVisibility(String visibility) {
		this.visibility = visibility;
	}
	public String getResponse() {
		return response;
	}
	public void setResponse(String response) {
		this.response = response;
	}
	public Integer getGuests() {
		return guests;
	}
	public void setGuests(Integer guests) {
		this.guests = guests;
	}
	public Member getMember() {
		return member;
	}
	public void setMember(Member member) {
		this.member = member;
	}
	public Integer getRsvpId() {
		return rsvpId;
	}
	public void setRsvpId(Integer rsvpId) {
		this.rsvpId = rsvpId;
	}
	public Long getMtime() {
		return mtime;
	}
	public void setMtime(Long mtime) {
		this.mtime = mtime;
	}
	public Event getEvent() {
		return event;
	}
	public void setEvent(Event event) {
		this.event = event;
	}
	public Group getGroup() {
		return group;
	}
	public void setGroup(Group group) {
		this.group = group;
	}
    
    
}

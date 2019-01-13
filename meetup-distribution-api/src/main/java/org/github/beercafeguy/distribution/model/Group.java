package org.github.beercafeguy.distribution.model;

import java.util.List;

public class Group {

	private List<GroupTopic> groupTopics = null;    
    private String groupCity;    
    private String groupCountry;    
    private Integer groupId;    
    private String groupName;    
    private Double groupLon;    
    private String groupUrlname;    
    private String groupState;    
    private Double groupLat;
	
	public List<GroupTopic> getGroupTopics() {
		return groupTopics;
	}
	public void setGroupTopics(List<GroupTopic> groupTopics) {
		this.groupTopics = groupTopics;
	}
	public String getGroupCity() {
		return groupCity;
	}
	public void setGroupCity(String groupCity) {
		this.groupCity = groupCity;
	}
	public String getGroupCountry() {
		return groupCountry;
	}
	public void setGroupCountry(String groupCountry) {
		this.groupCountry = groupCountry;
	}
	public Integer getGroupId() {
		return groupId;
	}
	public void setGroupId(Integer groupId) {
		this.groupId = groupId;
	}
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public Double getGroupLon() {
		return groupLon;
	}
	public void setGroupLon(Double groupLon) {
		this.groupLon = groupLon;
	}
	public String getGroupUrlname() {
		return groupUrlname;
	}
	public void setGroupUrlname(String groupUrlname) {
		this.groupUrlname = groupUrlname;
	}
	public String getGroupState() {
		return groupState;
	}
	public void setGroupState(String groupState) {
		this.groupState = groupState;
	}
	public Double getGroupLat() {
		return groupLat;
	}
	public void setGroupLat(Double groupLat) {
		this.groupLat = groupLat;
	}
    
    
}

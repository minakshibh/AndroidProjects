package com.restedge.util;

import java.io.Serializable;

public class Event implements Serializable {

	int eventId,VenueId,distance;
	public int getDistance() {
		return distance;
	}
	public void setDistance(int distance) {
		this.distance = distance;
	}
	String EventName,EventDesc,EventImageUrl,StartDate,EndDate,Latitude,Longitude,VenueName,CityName;
	public String getCityName() {
		return CityName;
	}
	public void setCityName(String cityName) {
		CityName = cityName;
	}
	public String getVenueName() {
		return VenueName;
	}
	public void setVenueName(String venueName) {
		VenueName = venueName;
	}
	public String getEventImageUrl() {
		return EventImageUrl;
	}
	public void setEventImageUrl(String eventImageUrl) {
		EventImageUrl = eventImageUrl;
	}
	public int getEventId() {
		return eventId;
	}
	public void setEventId(int eventId) {
		this.eventId = eventId;
	}
	public int getVenueId() {
		return VenueId;
	}
	public void setVenueId(int venueId) {
		VenueId = venueId;
	}
	public String getEventName() {
		return EventName;
	}
	public void setEventName(String eventName) {
		EventName = eventName;
	}
	public String getEventDesc() {
		return EventDesc;
	}
	public void setEventDesc(String eventDesc) {
		EventDesc = eventDesc;
	}
	
	public String getStartDate() {
		return StartDate;
	}
	public void setStartDate(String startDate) {
		StartDate = startDate;
	}
	public String getEndDate() {
		return EndDate;
	}
	public void setEndDate(String endDate) {
		EndDate = endDate;
	}
	public String getLatitude() {
		return Latitude;
	}
	public void setLatitude(String latitude) {
		Latitude = latitude;
	}
	public String getLongitude() {
		return Longitude;
	}
	public void setLongitude(String longitude) {
		Longitude = longitude;
	}
}

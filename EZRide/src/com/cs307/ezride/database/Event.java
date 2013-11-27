package com.cs307.ezride.database;

public class Event {
	private int id, google_id;
	private String title, details, start, end;
	
	public int getId() {
		return this.id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int getGoogleId() {
		return this.google_id;
	}
	
	public void setGoogleId(int google_id) {
		this.google_id = google_id;
	}
	
	public String getTitle() {
		return this.title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getDetails() {
		return this.details;
	}
	
	public void setDetails(String details) {
		this.details = details;
	}
	
	public String getStart() {
		return this.start;
	}
	
	public void setStart(String start) {
		this.start = start;
	}
	
	public String getEnd() {
		return this.end;
	}
	
	public void setEnd(String end) {
		this.end = end;
	}
	
	@Override
	public String toString() {
		return Integer.toString(id) + " " + title + " " + details + " " + start + " " + end;
	}
}

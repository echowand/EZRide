package com.cs307.ezride.database;

public class Group {
	private int id;
	private String name, description, datecreated;
	
	public int getId() {
		return this.id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getDescription() {
		return this.description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getDateCreated() {
		return this.datecreated;
	}
	
	public void setDateCreated(String datecreated) {
		this.datecreated = datecreated;
	}
	
	@Override
	public String toString() {
		return Integer.toString(id) + " " + name + " " + description + " " + datecreated + "\n";
	}
}

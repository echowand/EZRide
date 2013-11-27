package com.cs307.ezride.database;

public class User {
	private int id;
	private String google_id, realname, email, phone, address, bio, avatarUrl;
	
	public int getId() {
		return this.id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getGoogleId() {
		return this.google_id;
	}
	
	public void setGoogleId(String google_id) {
		this.google_id = google_id;
	}
	
	public String getRealname() {
		return this.realname;
	}
	
	public void setRealname(String realname) {
		this.realname = realname;
	}
	
	public String getEmail() {
		return this.email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getPhone() {
		return this.phone;
	}
	
	public void setPhone(String phone) {
		this.phone = phone;
	}
	
	public String getAddress() {
		return this.address;
	}
	
	public void setAddress(String address) {
		this.address = address;
	}
	
	public String getBio() {
		return this.bio;
	}
	
	public void setBio(String bio) {
		this.bio = bio;
	}
	
	public String getAvatarUrl() {
		return this.avatarUrl;
	}
	
	public void setAvatarUrl(String avatarUrl) {
		this.avatarUrl = avatarUrl;
	}
	
	@Override
	public String toString() {
		return Integer.toString(id) + " " + google_id + " " + realname + " " + email + " " + phone + " " + address + " " + bio;
	}
}

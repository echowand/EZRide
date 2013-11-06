package com.cs307.ezride;

public class User {
	private int id;
	private String username, password, realname, email, phone, address, bio;
	
	public int getId() {
		return this.id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getUsername() {
		return this.username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getPassword() {
		return this.password;
	}
	
	public void setPassword(String password) {
		this.password = password;
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
	
	@Override
	public String toString() {
		return Integer.toString(id) + " " + username + " " + password + " " + realname + " " + email + " " + phone + " " + address + " " + bio;
	}
}

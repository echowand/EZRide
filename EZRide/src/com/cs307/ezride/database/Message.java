package com.cs307.ezride.database;

public class Message {
	private int id, userid, groupid;
	private String content, posttime;
	
	public int getId() {
		return this.id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int getUserId() {
		return this.userid;
	}
	
	public void setUserId(int userid) {
		this.userid = userid;
	}
	
	public int getGroupId() {
		return this.groupid;
	}
	
	public void setGroupId(int groupid) {
		this.groupid = groupid;
	}
	
	public String getContent() {
		return this.content;
	}
	
	public void setContent(String content) {
		this.content = content;
	}
	
	public String getPostTime() {
		return this.posttime;
	}
	
	public void setPostTime(String posttime) {
		this.posttime = posttime;
	}
}

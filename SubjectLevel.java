//Brid Delap - SubjectLevel object

package com.example.project;

public class SubjectLevel {
	private String subId;
	private String levelId;
	
	public SubjectLevel() {
		// TODO Auto-generated constructor stub
	}
	
	public SubjectLevel(String subId, String levelId) {
		this.subId=subId;
		this.levelId=levelId;
	}

	
	public void setSubId(String subId) {
		this.subId=subId;
	}
	
	public void setLapId(String levelId) {
		this.levelId=levelId;
	}
	

	public String getSubId() {
		return this.subId;
	}
	
	public String getLevelId() {
		return this.levelId;
	}

}

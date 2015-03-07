//Brid Delap - Subject object

package com.example.project;

public class Subject {
	private String subId ;
	private String subName;
	private int subStartYear ;
	private int subEndYear;

	public Subject() {
	}
	
	public Subject(String subId) {
		this.subId=subId;
	}
	
	public Subject(String subId, String subName, int subStartYear) {
		this.subId=subId;
		this.subName=subName;
		this.subStartYear=subStartYear;
	}
	
	public Subject(String subId, String subName, int subStartYear, int subEndYear) {
		this.subId=subId;
		this.subName=subName;
		this.subStartYear=subStartYear;
		this.subEndYear=subEndYear;
	}

	public void setSubId (String subId) {
		this.subId=subId;
	}
	public void setSubName (String subName) {
		this.subName=subName;
	}
	
	public void setStartYear (int subStartYear) {
		this.subStartYear=subStartYear;
	}
	
	public void setEndYear (int subEndYear) {
		this.subEndYear=subEndYear;
	}
	
	public String getSubId() {
		return this.subId;
	}

	public String getSubName() {
		return this.subName;
	}
	
	public int getSubStartYear() {
		return this.subStartYear;
	}
	
	public int getSubEndYear() {
		return this.subEndYear;
			
	}
}

//Brid Delap - PaperHistory object

package com.example.project;

public class PaperHistory {
	private String subId;
	private String papId;
	private String levelId;
	private int yearId;
	private String papType ;
	private String dateHistory;

	public PaperHistory() {
		// TODO Auto-generated constructor stub
	}
	
	public PaperHistory(String subId, String papId, String papType) {
		this.subId=subId;
		this.papId=papId;
		this.yearId=yearId;
		this.papType=papType;
	}

	
	public PaperHistory(String subId, String papId, String levelId, int yearId, String papType, String dateHistory) {
		this.subId=subId;
		this.papId=papId;
		this.levelId=levelId;
		this.yearId=yearId;
		this.papType=papType;
		this.dateHistory=dateHistory;
	}
	
	public void setSubId(String subId) {
		this.subId=subId;
	}
	
	public void setPapId(String papId) {
		this.papId=papId;
	}
	
	public void setLevelId(String levelId) {
		this.levelId=levelId;
	}

		public void setYearId(int yearId) {
		this.yearId=yearId;
	}
		
	public void setPapType(String papType) {
		this.papType=papType;
	}

	
	public void setDateHistory(String dateHistory) {
		this.dateHistory=dateHistory;
	}

	public String getSubId() {
		return this.subId;
	}
	
	public String getPapId() {
		return this.papId;
	}

	public String getLevelId() {
		return this.levelId;
	}

	public int getYearId() {
		return this.yearId;
	}

	public String getPapType() {
		return this.papType;
	}

	public String getDateHistory() {
		return this.dateHistory;
	}
}

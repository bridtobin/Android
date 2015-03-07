//Brid Delap - Paper object
package com.example.project;

public class Paper {
	private String papId ;
	private String papName ;
	private String levelId;
	public Paper() {
	}
	
	public Paper(String papId) {
		this.papId=papId;
	}
	
	public Paper(String papId, String papName, String levelId) {
		this.papId=papId;
		this.papName=papName;
		this.levelId=levelId;
	}
	
	public void setPapId (String papId) {
		this.papId=papId;
	}
	
	public void setPapName(String papName) {
		this.papName=papName;
	}
	
	public void setLevelId(String levelId) {
		this.levelId=levelId;
	}
	
	
		
	public String getPapId() {
		return this.papId;
	}
	
	
	public String getPapName() {
		return this.papName ;
	}
	
	public String getLevelId() {
		return this.levelId;
	}
	
	

}

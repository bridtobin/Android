//Brid Delap
//Level object 
package com.example.project;

public class Level {
	private String levelId ;
	private String levelName;

	public Level() {
	}
	
	public Level(String levlId) {
		this.levelId=levelId;
	}
	
	public Level(String levelId, String levelName) {
		this.levelId=levelId;
		this.levelName=levelName;
	}
	
	

	public void setLevelId (String levelId) {
		this.levelId=levelId;
	}
	public void setLevelName (String levelName) {
		this.levelName=levelName;
	}
	
	
	public String getLevelId() {
		return this.levelId;
	}

	public String getLevelName() {
		return this.levelName;
	}
}

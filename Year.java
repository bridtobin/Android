//Brid Delap - Year object

package com.example.project;

public class Year {
	private int yearId ;

	public Year() {
	}
	
	public Year(int year) {
		this.yearId = year;
	}
	
	public void setYear(int yearId) {
		this.yearId=yearId;
	}
	public int getYear() {
		return this.yearId;
	}
	
	public String toString() {
		return "" + this.yearId;
		
	}

}

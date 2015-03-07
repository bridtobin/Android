//Brid Delap - SubjectPaper object

package com.example.project;

public class SubjectPaper {
	private String subId;
	private String papId;
	private int subPapStartYear;
	private int subPapEndYear;
	private String subPapType;

	public SubjectPaper() {
		// TODO Auto-generated constructor stub
	}
	
	public SubjectPaper(String subId, String papId) {
		this.subId=subId;
		this.papId=papId;
	}

	
	public SubjectPaper(String subId, String papId, int subPapStartYear, int subPapEndYear, String subPapType) {
		this.subId=subId;
		this.papId=papId;
		this.subPapStartYear=subPapStartYear;
		this.subPapEndYear=subPapEndYear;
		this.subPapType=subPapType;
	}
	
	public void setSubId(String subId) {
		this.subId=subId;
	}
	
	public void setPapId(String papId) {
		this.papId=papId;
	}
	
	public void setSubPapStartYear(int subPapStartYear) {
		this.subPapStartYear=subPapStartYear;
	}

	public void setSubPapEndYear(int subPapEndYear) {
		this.subPapEndYear=subPapEndYear;
	}

	public void setSubPapType(String subPapType) {
		this.subPapType=subPapType;
	}

	public String getSubId() {
		return this.subId;
	}
	
	public String getPapId() {
		return this.papId;
	}
	
	public int getSubPapStartYear() {
		return this.subPapStartYear;
	}

	public int getSubPapEndYear() {
		return this.subPapEndYear;
	}
	public String getSubPapType() {
		return this.subPapType;
	}
}

//Brid Delap - PDF object

package com.example.project;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class PDF {
	private String subPapType ;
	private int yearId ;
	private String subPapId ;
	private String subId;
	private String papId;
	private String levelId;
	public PDF() {
	}
	
	public PDF(String subPapType, int yearId, String subPapId, String subId, String papId, String levelId  ) {
		this.subPapType=subPapType;
		this.yearId=yearId;
		this.subPapId=subPapId;
		this.subId=subId;
		this.papId=papId;
		this.levelId=levelId;
	}
	
	// Method to create new intent (to view pdf).  Paper also written to paperHistory table
	public Intent viewPDF(DatabaseOperations db, Context context) {
			String basePdfUrl;
			if(this.subPapType.equals("E")) {
				basePdfUrl = "http://www.examinations.ie/archive/exampapers/";
			} else {
				basePdfUrl = "http://www.examinations.ie/archive/markingschemes/";
			}
			String myPdfUrl = basePdfUrl + this.yearId + "/" + this.subPapId + ".pdf" ; 
			Log.d(myPdfUrl,myPdfUrl);
			//Write this to history file
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
			Date date = new Date();
			//DatabaseOperations db = DatabaseOperations.getInstance(getApplicationContext());
			PaperHistory paperHistory = new PaperHistory(this.subId, this.papId, this.levelId, this.yearId, 
					                    this.subPapType, dateFormat.format(date)) ;
			db.updateRecord(paperHistory, "A");
			Intent intent = new Intent(Intent.ACTION_VIEW);
			if(canDisplayPdf(context)){
				intent.setData(Uri.parse(myPdfUrl));
				intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
				intent.addFlags(Intent.FLAG_FROM_BACKGROUND);
			} else {
				intent=null;
			}
			return intent;
		}
	
	
	//Method to check if a pdf viewer is available on device
	public static boolean canDisplayPdf(Context context) {
		String MIME_TYPE_PDF = "application/pdf";
	    PackageManager packageManager = context.getPackageManager();
	    Intent testIntent = new Intent(Intent.ACTION_VIEW);
	    testIntent.setType(MIME_TYPE_PDF);
	    if (packageManager.queryIntentActivities(testIntent, PackageManager.MATCH_DEFAULT_ONLY).size() > 0) {
	        return true;
	    } else {
	        return false;
	    }
	}

}

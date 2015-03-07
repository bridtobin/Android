//Brid Delap 
//This is an SQLiteOpenHelper used to create, update and retrieve information from all tables in the database as well 
//as creating the database itself.
package com.example.project;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.example.project.TableData.TableInfo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseOperations extends SQLiteOpenHelper {
	public static final int databaseVersion=12;
	private static DatabaseOperations mInstance = null;

	private DatabaseOperations(Context context) {
		super(context, TableInfo.DATABASE_NAME, null, databaseVersion) ;

	}
	
	public static DatabaseOperations getInstance(Context ctx) {

	    // Use the application context, which will ensure that you 
	    // don't accidentally leak an Activity's context.
	    if (mInstance == null) {
	      mInstance = new DatabaseOperations(ctx.getApplicationContext());
	    }
	    return mInstance;
	  }

	@Override
	//Create tables
	public void onCreate(SQLiteDatabase sdb) {
		sdb.execSQL(TableInfo.CREATE_TABLE_YEAR);
		Log.d("Database operations","Year table created") ;
		sdb.execSQL(TableInfo.CREATE_TABLE_SUBJECT);
		Log.d("Database operations","Subject table created") ;
		sdb.execSQL(TableInfo.CREATE_TABLE_PAPER);
		Log.d("Database operations","Paper table created") ;
		sdb.execSQL(TableInfo.CREATE_TABLE_LEVEL);
		Log.d("Database operations","Level table created") ;
		sdb.execSQL(TableInfo.CREATE_TABLE_SUB_LEVEL);
		Log.d("Database operations","Subject Level table created") ;
		sdb.execSQL(TableInfo.CREATE_TABLE_SUB_PAP);
		Log.d("Database operations","Subject Paper table created") ;
		sdb.execSQL(TableInfo.CREATE_TABLE_HISTORY) ;
		Log.d("Database operations","History table created") ;
		//Create triggers to ensure referential integrity
       sdb.execSQL(TableInfo.CREATE_INSERT_TRIGGER_SUB_PAP) ;
       sdb.execSQL(TableInfo.CREATE_INSERT_TRIGGER_SUB_LEVEL) ;
       sdb.execSQL(TableInfo.CREATE_INSERT_TRIGGER_PAPER);
       updateDatabase(sdb);
	}

	@Override
	//Firstly drop tables and then call onCreate
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	        // on upgrade drop older tables
		    db.execSQL("DROP TABLE IF EXISTS " + TableInfo.TABLE_HISTORY);
			db.execSQL("DROP TABLE IF EXISTS " + TableInfo.TABLE_LEVEL);
	        db.execSQL("DROP TABLE IF EXISTS " + TableInfo.TABLE_SUB_LEVEL);
			db.execSQL("DROP TABLE IF EXISTS " + TableInfo.TABLE_SUB_PAP);
	        db.execSQL("DROP TABLE IF EXISTS " + TableInfo.TABLE_SUB);
	        db.execSQL("DROP TABLE IF EXISTS " + TableInfo.TABLE_PAP);
	        db.execSQL("DROP TABLE IF EXISTS " + TableInfo.TABLE_YEAR);
	        
	        // create new tables
	        onCreate(db);
	    }
	
	//update year table
	public void updateRecord(Year year, String typeOfUpdate, SQLiteDatabase sdb) 
 {
		//Year can be just added or deleted.  As it is only one field it cannot be modified.

		long k;
		switch(typeOfUpdate) {
		case "A" :
			ContentValues values = new ContentValues() ;
			values.put(TableInfo.COL_YEAR_ID, year.getYear());
			k=sdb.insert(TableInfo.TABLE_YEAR, null, values) ;
			Log.d("DatabaseOperations","Year " + year +" written to table with an id of " + k);
			break;
		case "D" :
			String whereClause=TableInfo.COL_YEAR_ID+" LIKE ? " ;
			String [] whereArgs={String.valueOf(year.getYear())} ;
			k=sdb.delete(TableInfo.TABLE_YEAR, whereClause, whereArgs);
			Log.d("DatabaseOperations","Year " + year.getYear() +" deleted ");
			break;
		}
	}
	//update paperHistory table
	public void updateRecord(PaperHistory paperHistory, String typeOfUpdate) {
	    SQLiteDatabase sdb=this.getReadableDatabase();
		ContentValues values=new ContentValues() ;
		String whereClause=TableInfo.COL_SUB_ID + " LIKE ? AND " + TableInfo.COL_PAP_ID + " LIKE ? AND " +
		                   TableInfo.COL_YEAR_ID + " = ? AND " + TableInfo.COL_SUB_PAP_TYPE + " LIKE ? ";
		String [] whereArgs={String.valueOf(paperHistory.getSubId()), String.valueOf(paperHistory.getPapId()), 
				             String.valueOf(paperHistory.getYearId()), String.valueOf(paperHistory.getPapType())} ;
		long k ;
		//Check if the paper already exists in history.  If it does, type of update becomes "M" otherwise "A"
		Cursor cursorHistory=getHistoryInformation(paperHistory.getYearId(), paperHistory.getSubId(), paperHistory.getPapId(),
				paperHistory.getPapType());
		if (cursorHistory.getCount() !=0){
			typeOfUpdate="M" ;
		} else {
			typeOfUpdate="A" ;
		}
		if(typeOfUpdate=="A" || typeOfUpdate=="M") {
			values.put(TableInfo.COL_LEVEL_ID, paperHistory.getLevelId()) ;
			values.put(TableInfo.COL_DATE_HISTORY,paperHistory.getDateHistory());
		}
		switch(typeOfUpdate) {
		case "A" :
			values.put(TableInfo.COL_SUB_ID, paperHistory.getSubId());
			values.put(TableInfo.COL_PAP_ID, paperHistory.getPapId());
			values.put(TableInfo.COL_YEAR_ID, paperHistory.getYearId());
			values.put(TableInfo.COL_SUB_PAP_TYPE, paperHistory.getPapType());
			k = sdb.insert(TableInfo.TABLE_HISTORY, null, values) ;
			Log.d("DatabaseOperations","Paper History " + paperHistory.getSubId() + " " + paperHistory.getPapId() + " " +
					paperHistory.getYearId() + " " + paperHistory.getLevelId() + " " + paperHistory.getPapType() + " "  
					+ paperHistory.getDateHistory()+ " written to table with an id of " + k);
			break;
		case "M" :
			k=sdb.update(TableInfo.TABLE_HISTORY, values, whereClause, whereArgs);
			break;
		case "D" :
			k=sdb.delete(TableInfo.TABLE_HISTORY, whereClause, whereArgs);
			break;
		}
	}
	
	//update paper table
public void updateRecord(Paper paper, String typeOfUpdate, SQLiteDatabase sdb) {
		ContentValues values = new ContentValues() ;
		String whereClause=TableInfo.COL_PAP_ID + " LIKE ? " ;
		String [] whereArgs={String.valueOf(paper.getPapId())} ;
		long k ;
		if(typeOfUpdate=="A" || typeOfUpdate=="M") {
			values.put(TableInfo.COL_PAP_NAME, paper.getPapName()) ;
			values.put(TableInfo.COL_LEVEL_ID, paper.getLevelId()) ;
		}
		switch(typeOfUpdate) {
		case "A" :
			values.put(TableInfo.COL_PAP_ID, paper.getPapId());
			k = sdb.insert(TableInfo.TABLE_PAP, null, values) ;
			Log.d("DatabaseOperations","Paper " + paper.getPapName() +" written to table with an id of " + k);
			break;
		case "M" :
			k=sdb.update(TableInfo.TABLE_PAP, values, whereClause, whereArgs);
			break;
		case "D" :
			k=sdb.delete(TableInfo.TABLE_PAP, whereClause, whereArgs);
			break;
		}
	}
	
	//update subject table
	public void updateRecord(Subject subject, String typeOfUpdate, SQLiteDatabase sdb) {
		ContentValues values=new ContentValues() ;
		String whereClause=TableInfo.COL_SUB_ID + " LIKE ? " ;
		String [] whereArgs={String.valueOf(subject.getSubId())} ;
		long k ;
		if(typeOfUpdate=="A" || typeOfUpdate=="M") {
			values.put(TableInfo.COL_SUB_NAME, subject.getSubName()) ;
			values.put(TableInfo.COL_SUB_START_YEAR, subject.getSubStartYear()) ;
			values.put(TableInfo.COL_SUB_END_YEAR, subject.getSubEndYear());
		}
		switch(typeOfUpdate) {
		case "A" :
			values.put(TableInfo.COL_SUB_ID, subject.getSubId());
			k = sdb.insert(TableInfo.TABLE_SUB, null, values) ;
			Log.d("DatabaseOperations","Subject " + subject.getSubName() +" written to table with an id of " + k);
			break;
		case "M" :
			k=sdb.update(TableInfo.TABLE_SUB, values, whereClause, whereArgs);
			break;
		case "D" :
			k=sdb.delete(TableInfo.TABLE_SUB, whereClause, whereArgs);
			break;
		}
	}
	
	//update level table
	public void updateRecord(Level level, String typeOfUpdate, SQLiteDatabase sdb) {
		ContentValues values=new ContentValues() ;
		String whereClause=TableInfo.COL_LEVEL_ID + " LIKE ? " ;
		String [] whereArgs={String.valueOf(level.getLevelId())} ;
		long k ;
		if(typeOfUpdate=="A" || typeOfUpdate=="M") {
			values.put(TableInfo.COL_LEVEL_NAME, level.getLevelName()) ;
		}
		switch(typeOfUpdate) {
		case "A" :
			values.put(TableInfo.COL_LEVEL_ID, level.getLevelId());
			k = sdb.insert(TableInfo.TABLE_LEVEL, null, values) ;
			Log.d("DatabaseOperations","Level " + level.getLevelName() +" written to table with an id of " + k);
			break;
		case "M" :
			k=sdb.update(TableInfo.TABLE_LEVEL, values, whereClause, whereArgs);
			break;
		case "D" :
			k=sdb.delete(TableInfo.TABLE_LEVEL, whereClause, whereArgs);
			break;
		}
	}
	
	//update subjectPaper table
	public void updateRecord(SubjectPaper subjectPaper, String typeOfUpdate, SQLiteDatabase sdb) {
		ContentValues values=new ContentValues() ;
		String whereClause=TableInfo.COL_SUB_ID + " LIKE ? AND " + TableInfo.COL_PAP_ID + " LIKE ? " ;
		String [] whereArgs={String.valueOf(subjectPaper.getSubId()), String.valueOf(subjectPaper.getPapId())} ;
		long k ;
		if(typeOfUpdate=="A" || typeOfUpdate=="M") {
			values.put(TableInfo.COL_SUB_PAP_START_YEAR, subjectPaper.getSubPapStartYear()) ;
			values.put(TableInfo.COL_SUB_PAP_END_YEAR, subjectPaper.getSubPapEndYear());
			values.put(TableInfo.COL_SUB_PAP_TYPE,subjectPaper.getSubPapType());
		}
		switch(typeOfUpdate) {
		case "A" :
			values.put(TableInfo.COL_SUB_ID, subjectPaper.getSubId());
			values.put(TableInfo.COL_PAP_ID, subjectPaper.getPapId());
			k = sdb.insert(TableInfo.TABLE_SUB_PAP, null, values) ;
			Log.d("DatabaseOperations","Subject Paper " + subjectPaper.getSubId() + " " + subjectPaper.getPapId() + " written to table with an id of " + k);
			break;
		case "M" :
			k=sdb.update(TableInfo.TABLE_SUB_PAP, values, whereClause, whereArgs);
			break;
		case "D" :
			k=sdb.delete(TableInfo.TABLE_SUB_PAP, whereClause, whereArgs);
			break;
		}
	}
	
	//update subjectLevel table
	public void updateRecord(SubjectLevel subjectLevel, String typeOfUpdate, SQLiteDatabase sdb) {
		ContentValues values=new ContentValues() ;
		String whereClause=TableInfo.COL_SUB_ID + " LIKE ? AND " + TableInfo.COL_LEVEL_ID + " LIKE ? " ;
		String [] whereArgs={String.valueOf(subjectLevel.getSubId()), String.valueOf(subjectLevel.getLevelId())} ;
		long k ;
		switch(typeOfUpdate) {
		case "A" :
			values.put(TableInfo.COL_SUB_ID, subjectLevel.getSubId());
			values.put(TableInfo.COL_LEVEL_ID, subjectLevel.getLevelId());
			k = sdb.insert(TableInfo.TABLE_SUB_LEVEL, null, values) ;
			Log.d("DatabaseOperations","Subject Level " + subjectLevel.getSubId() + " " + subjectLevel.getLevelId() + " written to table with an id of " + k);
			break;
		case "M" :
			k=sdb.update(TableInfo.TABLE_SUB_LEVEL, values, whereClause, whereArgs);
			break;
		case "D" :
			k=sdb.delete(TableInfo.TABLE_SUB_LEVEL, whereClause, whereArgs);
			break;
		}
	}
	
	//get information from year table
	public Cursor getYearInformation() {
	    SQLiteDatabase sdb=this.getReadableDatabase();
		String [] columns = {TableInfo.CURSOR_ID, TableInfo.COL_YEAR_ID} ;
		Cursor cr = sdb.query(TableInfo.TABLE_YEAR, columns, null, null, null, null, null)	;
 		return cr;
	}
	
	//get information for paper table
	public Cursor getPaperInformation() {
		SQLiteDatabase sdb=this.getReadableDatabase();
		String [] columns = {TableInfo.CURSOR_ID, TableInfo.COL_PAP_ID, TableInfo.COL_PAP_NAME} ;
		Cursor cr = sdb.query(TableInfo.TABLE_PAP, columns, null, null, null, null, null)	;
		return cr;
	}
	
	//get information from subject table
	public Cursor getSubjectInformation(int year) {
		SQLiteDatabase sdb=this.getReadableDatabase();
		String [] columns = {TableInfo.CURSOR_ID, TableInfo.COL_SUB_ID, TableInfo.COL_SUB_NAME, TableInfo.COL_SUB_START_YEAR, TableInfo.COL_SUB_END_YEAR} ;
		String whereClause=TableInfo.COL_SUB_START_YEAR + " <= ? AND (" + TableInfo.COL_SUB_END_YEAR + " >= ? " +
				" OR " + TableInfo.COL_SUB_END_YEAR + " == ?)" ; 
		String [] whereArgs={String.valueOf(year),String.valueOf(year),String.valueOf(0)} ;
		Cursor cr = sdb.query(TableInfo.TABLE_SUB, columns, whereClause, whereArgs, null, null, TableInfo.COL_SUB_NAME, null)	;
		return cr;
	}

	//get information from level table
	public Cursor getLevelInformation(String subject) {
		SQLiteDatabase sdb=this.getReadableDatabase();
		//We need a raw query here as we must join two tables
		String rawQuery = "SELECT " +
		TableInfo.TABLE_SUB_LEVEL + "." + TableInfo.CURSOR_ID + "," +
		TableInfo.TABLE_SUB_LEVEL + "." + TableInfo.COL_LEVEL_ID + "," +
		TableInfo.TABLE_SUB_LEVEL + "." + TableInfo.COL_SUB_ID + "," +
		TableInfo.TABLE_LEVEL + "." + TableInfo.COL_LEVEL_NAME  +
		" FROM " + TableInfo.TABLE_SUB_LEVEL + " INNER JOIN " + TableInfo.TABLE_LEVEL + " ON " +
		TableInfo.TABLE_SUB_LEVEL + "." + TableInfo.COL_LEVEL_ID + " = " +
		TableInfo.TABLE_LEVEL + "." + TableInfo.COL_LEVEL_ID +
		" WHERE " + TableInfo.TABLE_SUB_LEVEL + "." + TableInfo.COL_SUB_ID + " LIKE ? " ;
		Cursor cr = sdb.rawQuery(rawQuery, new String [] {subject})	;
		return cr;
	}
	
	//get information from paperHistory table based on year subject paper and paper type. Firstly calls getHistoryRawQuery 
	//which creates the first part of the select query string
	public Cursor getHistoryInformation(int year, String subject, String paper, String papType ) {
		SQLiteDatabase sdb=this.getReadableDatabase();
		String rawQuery = getHistoryRawQuery() ;
		// we need a raw query here as we must join two tables
		rawQuery = rawQuery + " WHERE " + 
		 TableInfo.TABLE_HISTORY + "." + TableInfo.COL_SUB_ID + " LIKE ? AND " +
		 TableInfo.TABLE_HISTORY + "." + TableInfo.COL_PAP_ID + " LIKE ? AND " +
		 TableInfo.TABLE_HISTORY + "." + TableInfo.COL_YEAR_ID + " LIKE ? AND " +
		 TableInfo.TABLE_HISTORY + "." + TableInfo.COL_SUB_PAP_TYPE + " LIKE ? " ;
		
		Cursor cr = sdb.rawQuery(rawQuery, new String [] {subject, paper, String.valueOf(year),papType})	;
		return cr;
	}
	
	//get all information from paperHistory table. Firstly calls getHistoryRawQuery which creates the first
	//part of the select query string
	public Cursor getHistoryInformation(String subject) {
		SQLiteDatabase sdb=this.getReadableDatabase();
		String rawQuery = getHistoryRawQuery() ;
		rawQuery = rawQuery + " WHERE " +
		TableInfo.TABLE_HISTORY + "." + TableInfo.COL_SUB_ID + " LIKE ? " ;
//		rawQuery=rawQuery + " ORDER BY " + 
//		TableInfo.TABLE_SUB + "." + TableInfo.COL_SUB_NAME ;
//		Cursor cr= sdb.rawQuery(rawQuery,null);
		Cursor cr= sdb.rawQuery(rawQuery,new String[] {subject});
		return cr;
	}
	
	//get information from subject table
	public Cursor getSubjectPaperInformation(int year, String subject, String level, String subPapType ) {
		SQLiteDatabase sdb=this.getReadableDatabase();
		// we need a raw query here as we must join two tables
		String rawQuery = "SELECT " + 
		TableInfo.TABLE_SUB_PAP + "." + TableInfo.CURSOR_ID + ", " +
		TableInfo.TABLE_SUB_PAP + "." + TableInfo.COL_SUB_ID + ", " +
		TableInfo.TABLE_SUB_PAP + "." + TableInfo.COL_PAP_ID + ", " +
		TableInfo.TABLE_PAP + "." + TableInfo.COL_PAP_NAME + ", " +
		TableInfo.TABLE_PAP + "." + TableInfo.COL_LEVEL_ID  + " FROM " +
		TableInfo.TABLE_SUB_PAP + " INNER JOIN " + TableInfo.TABLE_PAP + " ON " +
		TableInfo.TABLE_SUB_PAP + "." + TableInfo.COL_PAP_ID + " = " +
		TableInfo.TABLE_PAP + "." + TableInfo.COL_PAP_ID + 
		" WHERE " +  TableInfo.TABLE_SUB_PAP + "." + TableInfo.COL_SUB_ID + " LIKE ? AND " +
		TableInfo.COL_SUB_PAP_START_YEAR + " <= ? AND (" + TableInfo.COL_SUB_PAP_END_YEAR + " >= ? " +
		" OR " + TableInfo.COL_SUB_PAP_END_YEAR + " == ?) AND " +
		TableInfo.TABLE_PAP + "." + TableInfo.COL_LEVEL_ID + " LIKE ? "  ;
		if(subPapType=="E") {
			rawQuery = rawQuery + " AND (" + TableInfo.TABLE_SUB_PAP + "." + TableInfo.COL_SUB_PAP_TYPE + "='A' OR " +
					TableInfo.TABLE_SUB_PAP + "." + TableInfo.COL_SUB_PAP_TYPE + "='E')";
		} else {
			rawQuery = rawQuery + " AND (" + TableInfo.TABLE_SUB_PAP + "." + TableInfo.COL_SUB_PAP_TYPE + "='A' OR " +
					TableInfo.TABLE_SUB_PAP + "." + TableInfo.COL_SUB_PAP_TYPE + "='M')";
		}
		Cursor cr = sdb.rawQuery(rawQuery, new String [] {subject,String.valueOf(year),String.valueOf(year),String.valueOf(0),level})	;
		return cr;
	}

	//First part of query to select information from paperHistory.  Called from the two getHistoryInformation methods
	public String getHistoryRawQuery() {
		String rawQuery = "SELECT " + 
		TableInfo.TABLE_HISTORY + "." + TableInfo.CURSOR_ID + ", " +
		TableInfo.TABLE_HISTORY + "." + TableInfo.COL_SUB_ID + ", " +
		TableInfo.TABLE_SUB + "." + TableInfo.COL_SUB_NAME + ", " +
		TableInfo.TABLE_HISTORY + "." + TableInfo.COL_PAP_ID + ", " +
		TableInfo.TABLE_PAP + "." + TableInfo.COL_PAP_NAME + ", " +
		TableInfo.TABLE_HISTORY + "." + TableInfo.COL_YEAR_ID + ", "  + 
		TableInfo.TABLE_HISTORY + "." + TableInfo.COL_LEVEL_ID  + ", " +
		TableInfo.TABLE_HISTORY + "." + TableInfo.COL_DATE_HISTORY + "," +
		TableInfo.TABLE_HISTORY + "." + TableInfo.COL_SUB_PAP_TYPE +
		" FROM " + 	TableInfo.TABLE_HISTORY +
		" INNER JOIN " + TableInfo.TABLE_PAP + " ON " +
		TableInfo.TABLE_HISTORY + "." + TableInfo.COL_PAP_ID + " = " +
		TableInfo.TABLE_PAP + "." + TableInfo.COL_PAP_ID +
		" INNER JOIN " + TableInfo.TABLE_SUB + " ON " +
		TableInfo.TABLE_HISTORY + "." + TableInfo.COL_SUB_ID + " = " +
		TableInfo.TABLE_SUB + "." + TableInfo.COL_SUB_ID ;
		return rawQuery;
	}

	//Add records to database
	public void updateDatabase(SQLiteDatabase sdb)  {
	  //Enter the years
      updateRecord(new Year(2007), "A", sdb);
	  updateRecord(new Year(2008), "A", sdb);
	  updateRecord(new Year(2009), "A", sdb);
	  updateRecord(new Year(2010), "A", sdb);
	  updateRecord(new Year(2011), "A", sdb);
	  updateRecord(new Year(2012), "A", sdb); 
	  updateRecord(new Year(2013),"A", sdb);
	  updateRecord(new Year(2014), "A", sdb);
	  //Enter the three levels
	  updateRecord(new Level("H","Higher"), "A", sdb) ;
	  updateRecord(new Level("O","Ordinary"), "A", sdb) ;
	  updateRecord(new Level("F","Foundation"), "A", sdb) ;
	  //Enter the papers
	  updateRecord(new Paper("ALP000EV","Higher Level","H" ), "A", sdb) ;
	  updateRecord(new Paper("GLP000EV","Ordinary Level","O" ), "A", sdb) ;
	  updateRecord(new Paper("BLP000EV","Foundation Level","F" ), "A", sdb) ;
	  updateRecord(new Paper("ALP012EV","Life Sketching Higher Level","H"), "A", sdb) ;
	  updateRecord(new Paper("GLP012EV","Life Sketching Ordinary Level","O"), "A", sdb) ;
	  updateRecord(new Paper("ALP009EV","Imaginative Composition Higher Level","H" ), "A", sdb) ;
	  updateRecord(new Paper("GLP009EV","Imaginative Composition Ordinary Level","O" ), "A", sdb) ;
	  updateRecord(new Paper("ALP011EV","Craftwork Higher Level","H"), "A", sdb) ;
	  updateRecord(new Paper("GLP011EV","Craftwork Ordinary Level","O"), "A", sdb) ;
	  updateRecord(new Paper("ALP010EV","Design Higher Level","H"), "A", sdb) ;
	  updateRecord(new Paper("GLP010EV","Design Ordinary Level","O"), "A", sdb) ;
	  updateRecord(new Paper("ALP028EV","Drawing Higher Level","H"), "A", sdb) ;
	  updateRecord(new Paper("GLP028EV","Drawing Ordinary Level","O"), "A", sdb) ;
	  updateRecord(new Paper("ALP013EV","History & Appreciation Higher Level","H"), "A", sdb) ;
	  updateRecord(new Paper("GLP013EV","History & Appreciation Ordinary Level","O"), "A", sdb) ;
	  updateRecord(new Paper("ALPC00EV","Source Paper Higher Level","H"), "A", sdb) ;
	  updateRecord(new Paper("GLPC00EV","Source Paper Ordinary Level","O"), "A", sdb) ;
	  updateRecord(new Paper("ALP004EV","Picture/Illustration Higher Level","H"), "A", sdb) ;
	  updateRecord(new Paper("GLP004EV","Picture/Illustration Ordinary Level","O"), "A", sdb) ;
	  updateRecord(new Paper("CLP029EV","Project Common Level","H"), "A", sdb) ;
	  updateRecord(new Paper("CLP029EV","Project Common Level","O"), "A", sdb) ;
	  updateRecord(new Paper("CLP018EV","Practical Common Level","H"), "A", sdb) ;
	  updateRecord(new Paper("CLP018EV","Practical Common Level","O"), "A", sdb) ;
	  updateRecord(new Paper("ALP014EV","Section A Higher Level","H"), "A", sdb) ;
	  updateRecord(new Paper("GLP014EV","Section A Ordinary Level","O"), "A", sdb) ;
	  updateRecord(new Paper("ALP015EV","Section B Higher Level","H"), "A", sdb) ;
	  updateRecord(new Paper("GLP015EV","Section B Ordinary Level","O"), "A", sdb) ;
	  updateRecord(new Paper("ALPA00EV","Aural Higher Level","H"), "A", sdb) ;
	  updateRecord(new Paper("GLPA00EV","Aural Ordinary Level","O"), "A", sdb) ;
	  updateRecord(new Paper("CLP003EV","Map","H"), "A", sdb) ;
	  updateRecord(new Paper("CLP003EV","Map","O"), "A", sdb) ;
	  updateRecord(new Paper("CLP004EV","Picture","H"), "A", sdb) ;
	  updateRecord(new Paper("CLP004EV","Picture","O"), "A", sdb) ;
	  updateRecord(new Paper("CLPC00EV","Source","H"), "A", sdb) ;
	  updateRecord(new Paper("CLPC00EV","Source","O"), "A", sdb) ;
	  updateRecord(new Paper("ALP000IV","Irish Marking Scheme Higher Level","H"), "A", sdb) ;
	  updateRecord(new Paper("GLP000IV","Irish Marking Scheme Ordinary Level","H"), "A", sdb) ;
	  updateRecord(new Paper("ALP100IV","Irish Paper 1 Higher Level","H"), "A", sdb) ;
	  updateRecord(new Paper("GLP100IV","Irish Paper 1 Ordinary Level","O"), "A", sdb) ;
	  updateRecord(new Paper("ALP200IV","Irish Paper 2 Higher Level","H"), "A", sdb) ;
	  updateRecord(new Paper("GLP200IV","Irish Paper 2 Ordinary Level","O"), "A", sdb) ;
	  updateRecord(new Paper("BLP000IV","Irish Foundation Paper","F"), "A", sdb) ;
	  updateRecord(new Paper("ALPA00IV","Irish Aural Paper Higher Level","H"), "A", sdb) ;
	  updateRecord(new Paper("GLPA00IV","Irish Aural Paper Ordinary Level","O"), "A", sdb) ;
	  updateRecord(new Paper("BLPA00IV","Irish Aural Paper Foundation Level","F"), "A", sdb) ;
	  updateRecord(new Paper("ALP130EV","Paper 1 Project Higher Level","H"), "A", sdb) ;
	  updateRecord(new Paper("GLP130EV","Paper 1 Project Ordinary Level","O"), "A", sdb) ;
	  updateRecord(new Paper("BLP130EV","Paper 1 Project Foundation Level","F"), "A", sdb) ;
	  updateRecord(new Paper("ALP230EV","Paper 2 Project Higher Level","H"), "A", sdb) ;
	  updateRecord(new Paper("GLP230EV","Paper 2 Project Ordinary Level","O"), "A", sdb) ;
	  updateRecord(new Paper("BLP230EV","Paper 2 Project Foundation Level","F"), "A", sdb) ;
	  updateRecord(new Paper("ALP100EV","Paper 1 Higher Level","H"), "A", sdb) ;
	  updateRecord(new Paper("GLP100EV","Paper 1 Ordinary Level","O"), "A", sdb) ;
	  updateRecord(new Paper("BLP100EV","Paper 1 Foundation Level","F"), "A", sdb) ;
	  updateRecord(new Paper("ALP200EV","Paper 2 Higher Level","H"), "A", sdb) ;
	  updateRecord(new Paper("GLP200EV","Paper 2 Ordinary Level","O"), "A", sdb) ;
	  updateRecord(new Paper("BLP200EV","Paper 2 Foundation Level","F"), "A", sdb) ;
	  updateRecord(new Paper("ALP006EV","Composing Higher Level","H"), "A", sdb) ;
	  updateRecord(new Paper("GLP006EV","Composing Ordinary Level","O"), "A", sdb) ;
	  updateRecord(new Paper("ALP008EV","Listening Higher Level","H"), "A", sdb) ;
	  updateRecord(new Paper("GLP008EV","Listening Ordinary Level","O"), "A", sdb) ;
	  updateRecord(new Paper("ALP007EV","Elective Higher Level","H"), "A", sdb) ;
	  updateRecord(new Paper("ALPU00EV","Unprepared Test Higher Level","H"), "A", sdb) ;
	  updateRecord(new Paper("GLPU00EV","Unprepared Test Ordinary Level","O"), "A", sdb) ;
	  //enter subject records
	  updateRecord(new Subject("LC032","Accounting",2007), "A", sdb);
	  updateRecord(new Subject("LC026","Agricultural Economics",2007), "A", sdb);
	  updateRecord(new Subject("LC024","Agricultural Science",2007), "A", sdb);
	  updateRecord(new Subject("LC007","Ancient Greek",2008), "A", sdb);
	  updateRecord(new Subject("LC020","Applied Mathematics",2007), "A", sdb);
	  updateRecord(new Subject("LC059","Arabic",2008), "A", sdb);
	  updateRecord(new Subject("LC014","Art",2007), "A", sdb);
	  updateRecord(new Subject("LC025","Biology",2007), "A", sdb);
	  updateRecord(new Subject("LC559","Bulgarian",2007), "A", sdb);
	  updateRecord(new Subject("LC033","Business",2007), "A", sdb);
	  updateRecord(new Subject("LC022","Chemistry",2007), "A", sdb);
	  updateRecord(new Subject("LC008","Classical Studies",2007), "A", sdb);
	  updateRecord(new Subject("LC029","Construction Studies",2007), "A", sdb);
	  updateRecord(new Subject("LC567","Croatian",2014), "A", sdb);
	  updateRecord(new Subject("LC547","Czech",2007), "A", sdb);
	  updateRecord(new Subject("LC038","Danish",2007), "A", sdb);
	  updateRecord(new Subject("LC017","Dutch",2007), "A", sdb);
	  updateRecord(new Subject("LC034","Economics",2007), "A", sdb);
	  updateRecord(new Subject("LC027","Engineering",2007), "A", sdb);
	  updateRecord(new Subject("LC562","Design & Communications Graphics",2009), "A", sdb);
	  updateRecord(new Subject("LC552","Estonian",2007), "A", sdb);
	  updateRecord(new Subject("LC049","Finnish",2007), "A", sdb);
	  updateRecord(new Subject("LC010","French",2007), "A", sdb);
	  updateRecord(new Subject("LC005","Geography",2007), "A", sdb);
	  updateRecord(new Subject("LC011","German",2007), "A", sdb);
	  updateRecord(new Subject("LC009","Hebrew Studies",2007), "A", sdb);
	  updateRecord(new Subject("LC096","History",2007), "A", sdb);
	  updateRecord(new Subject("LC098","Home Economics S&S",2007), "A", sdb);
	  updateRecord(new Subject("LC551","Hungarian",2007), "A", sdb);
	  updateRecord(new Subject("LC001","Irish",2007), "A", sdb);
	  updateRecord(new Subject("LC013","Italian",2007), "A", sdb);
	  updateRecord(new Subject("LC058","Japanese",2007), "A", sdb);
	  updateRecord(new Subject("LC549","Latvian",2007), "A", sdb);
	  updateRecord(new Subject("LC550","Lithuanian",2007), "A", sdb);
	  updateRecord(new Subject("LC003","Mathematics",2007), "A", sdb);
	  updateRecord(new Subject("LC019","Modern Greek",2007), "A", sdb);
	  updateRecord(new Subject("LC067","Music",2007), "A", sdb);
	  updateRecord(new Subject("LC021","Physics",2007), "A", sdb);
	  updateRecord(new Subject("LC023","Physics & Chemistry",2007), "A", sdb);
	  updateRecord(new Subject("LC548","Polish",2007), "A", sdb);
	  updateRecord(new Subject("LC018","Portugese",2007), "A", sdb);
	  updateRecord(new Subject("LC223","Religious Education",2007), "A", sdb);
	  updateRecord(new Subject("LC553","Romanian",2007), "A", sdb);
	  updateRecord(new Subject("LC099","Russian",2007), "A", sdb);
	  updateRecord(new Subject("LC554","Slovaky",2007), "A", sdb);
	  updateRecord(new Subject("LC012","Spanish",2007), "A", sdb);
	  updateRecord(new Subject("LC039","Swedish",2007), "A", sdb);
	  updateRecord(new Subject("LC065","Technology",2009), "A", sdb);
	  updateRecord(new Subject("LC002","English",2007), "A", sdb);
	  updateRecord(new Subject("LC006","Latin",2007), "A", sdb);
	  //Enter Subject Papers
	  updateRecord(new SubjectPaper("LC032","ALP000EV",2007,0,"A"), "A", sdb);
	  updateRecord(new SubjectPaper("LC032","GLP000EV",2007,0,"A"), "A", sdb);
	  updateRecord(new SubjectPaper("LC026","ALP000EV",2007,0,"A"), "A", sdb);
	  updateRecord(new SubjectPaper("LC026","GLP000EV",2007,0,"A"), "A", sdb);
	  updateRecord(new SubjectPaper("LC024","ALP000EV",2007,0,"A"), "A", sdb);
	  updateRecord(new SubjectPaper("LC024","GLP000EV",2007,0,"A"), "A", sdb);
	  updateRecord(new SubjectPaper("LC007","ALP000EV",2008,0,"A"), "A", sdb);
	  updateRecord(new SubjectPaper("LC007","GLP000EV",2008,0,"A"), "A", sdb);
	  updateRecord(new SubjectPaper("LC020","ALP000EV",2007,0,"A"), "A", sdb);
	  updateRecord(new SubjectPaper("LC020","GLP000EV",2007,0,"A"), "A", sdb);
	  updateRecord(new SubjectPaper("LC059","ALP000EV",2008,0,"A"), "A", sdb);
	  updateRecord(new SubjectPaper("LC059","GLP000EV",2008,0,"A"), "A", sdb);
	  updateRecord(new SubjectPaper("LC014","ALP000EV",2007,2010,"A"), "A", sdb);
	  updateRecord(new SubjectPaper("LC014","GLP000EV",2007,2010,"A"), "A", sdb);
	  updateRecord(new SubjectPaper("LC014","ALP012EV",2007,0,"E"), "A", sdb);
	  updateRecord(new SubjectPaper("LC014","GLP012EV",2007,0,"E"), "A", sdb);
	  updateRecord(new SubjectPaper("LC014","ALP009EV",2007,0,"E"), "A", sdb);
	  updateRecord(new SubjectPaper("LC014","GLP009EV",2007,0,"E"), "A", sdb);
	  updateRecord(new SubjectPaper("LC014","ALP011EV",2007,0,"E"), "A", sdb);
	  updateRecord(new SubjectPaper("LC014","GLP011EV",2007,0,"E"), "A", sdb);
	  updateRecord(new SubjectPaper("LC014","ALP010EV",2007,0,"E"), "A", sdb);
	  updateRecord(new SubjectPaper("LC014","GLP010EV",2007,0,"E"), "A", sdb);
	  updateRecord(new SubjectPaper("LC014","ALP028EV",2007,0,"E"), "A", sdb);
	  updateRecord(new SubjectPaper("LC014","GLP028EV",2007,0,"E"), "A", sdb);
	  updateRecord(new SubjectPaper("LC014","ALP013EV",2007,0,"E"), "A", sdb);
	  updateRecord(new SubjectPaper("LC014","GLP013EV",2007,0,"E"), "A", sdb);
	  updateRecord(new SubjectPaper("LC014","ALPC00EV",2011,0,"E"), "A", sdb);
	  updateRecord(new SubjectPaper("LC014","GLPC00EV",2011,0,"E"), "A", sdb);
	  updateRecord(new SubjectPaper("LC025","ALP000EV",2007,0,"A"), "A", sdb);
	  updateRecord(new SubjectPaper("LC025","GLP000EV",2007,0,"A"), "A", sdb);
	  updateRecord(new SubjectPaper("LC559","ALP000EV",2007,0,"A"), "A", sdb);
	  updateRecord(new SubjectPaper("LC559","GLP000EV",2007,0,"A"), "A", sdb);
	  updateRecord(new SubjectPaper("LC033","ALP000EV",2007,0,"A"), "A", sdb);
	  updateRecord(new SubjectPaper("LC033","GLP000EV",2007,0,"A"), "A", sdb);
	  updateRecord(new SubjectPaper("LC022","ALP000EV",2007,0,"A"), "A", sdb);
	  updateRecord(new SubjectPaper("LC022","GLP000EV",2007,0,"A"), "A", sdb);
	  updateRecord(new SubjectPaper("LC008","ALP000EV",2007,0,"A"), "A", sdb);
	  updateRecord(new SubjectPaper("LC008","GLP000EV",2007,0,"A"), "A", sdb);
	  updateRecord(new SubjectPaper("LC008","ALP004EV",2007,0,"E"), "A", sdb);
	  updateRecord(new SubjectPaper("LC008","GLP004EV",2007,0,"E"), "A", sdb);
	  updateRecord(new SubjectPaper("LC029","ALP000EV",2007,0,"A"), "A", sdb);
	  updateRecord(new SubjectPaper("LC029","GLP000EV",2007,0,"A"), "A", sdb);
	  updateRecord(new SubjectPaper("LC029","CLP029EV",2007,2010,"E"), "A", sdb);
	  updateRecord(new SubjectPaper("LC029","CLP018EV",2011,2013,"E"), "A", sdb);
	  updateRecord(new SubjectPaper("LC567","ALP000EV",2014,0,"A"), "A", sdb);
	  updateRecord(new SubjectPaper("LC547","ALP000EV",2007,0,"A"), "A", sdb);
	  updateRecord(new SubjectPaper("LC038","ALP000EV",2007,0,"A"), "A", sdb);
	  updateRecord(new SubjectPaper("LC017","ALP000EV",2007,0,"A"), "A", sdb);
	  updateRecord(new SubjectPaper("LC034","ALP000EV",2007,0,"A"), "A", sdb);
	  updateRecord(new SubjectPaper("LC034","GLP000EV",2007,0,"A"), "A", sdb);
	  updateRecord(new SubjectPaper("LC027","ALP000EV",2007,0,"A"), "A", sdb);
	  updateRecord(new SubjectPaper("LC027","GLP000EV",2007,0,"A"), "A", sdb);
	  updateRecord(new SubjectPaper("LC562","ALP000EV",2009,2011,"A"), "A", sdb);
	  updateRecord(new SubjectPaper("LC562","GLP000EV",2009,2011,"A"), "A", sdb);
	  updateRecord(new SubjectPaper("LC562","ALP014EV",2009,0,"E"), "A", sdb);
	  updateRecord(new SubjectPaper("LC562","GLP014EV",2009,0,"E"), "A", sdb);
	  updateRecord(new SubjectPaper("LC562","ALP015EV",2012,0,"E"), "A", sdb);
	  updateRecord(new SubjectPaper("LC562","GLP015EV",2012,0,"E"), "A", sdb);
	  updateRecord(new SubjectPaper("LC552","ALP000EV",2007,0,"A"), "A", sdb);
	  updateRecord(new SubjectPaper("LC049","ALP000EV",2007,0,"A"), "A", sdb);
	  updateRecord(new SubjectPaper("LC010","ALP000EV",2007,0,"A"), "A", sdb);
	  updateRecord(new SubjectPaper("LC010","GLP000EV",2007,0,"A"), "A", sdb);
	  updateRecord(new SubjectPaper("LC010","ALPA00EV",2007,0,"E"), "A", sdb);
	  updateRecord(new SubjectPaper("LC010","GLPA00EV",2007,0,"E"), "A", sdb);
	  updateRecord(new SubjectPaper("LC005","ALP000EV",2007,0,"E"), "A", sdb);
	  updateRecord(new SubjectPaper("LC005","GLP000EV",2007,0,"E"), "A", sdb);
	  updateRecord(new SubjectPaper("LC005","CLP003EV",2011,0,"E"), "A", sdb);
	  updateRecord(new SubjectPaper("LC005","CLP004EV",2011,0,"E"), "A", sdb);
	  updateRecord(new SubjectPaper("LC005","CLPC00EV",2012,0,"E"), "A", sdb);
	  updateRecord(new SubjectPaper("LC011","ALP000EV",2007,0,"A"), "A", sdb);
	  updateRecord(new SubjectPaper("LC011","GLP000EV",2007,0,"A"), "A", sdb);
	  updateRecord(new SubjectPaper("LC011","ALPA00EV",2007,0,"E"), "A", sdb);
	  updateRecord(new SubjectPaper("LC011","GLPA00EV",2007,0,"E"), "A", sdb);
	  updateRecord(new SubjectPaper("LC009","ALP000EV",2007,0,"A"), "A", sdb);
	  updateRecord(new SubjectPaper("LC009","GLP000EV",2007,0,"A"), "A", sdb);
	  updateRecord(new SubjectPaper("LC096","ALP000EV",2007,0,"A"), "A", sdb);
	  updateRecord(new SubjectPaper("LC096","GLP000EV",2007,0,"A"), "A", sdb);
	  updateRecord(new SubjectPaper("LC098","ALP000EV",2007,0,"A"), "A", sdb);
	  updateRecord(new SubjectPaper("LC098","GLP000EV",2007,0,"A"), "A", sdb);
	  updateRecord(new SubjectPaper("LC551","ALP000EV",2007,0,"A"), "A", sdb);	  

	  updateRecord(new SubjectPaper("LC001","ALP000IV",2007,0,"M"), "A", sdb);
	  updateRecord(new SubjectPaper("LC001","GLP000IV",2007,0,"M"), "A", sdb);

	  updateRecord(new SubjectPaper("LC001","ALP100IV",2007,0,"E"), "A", sdb);
	  updateRecord(new SubjectPaper("LC001","GLP100IV",2007,0,"E"), "A", sdb);
	  updateRecord(new SubjectPaper("LC001","ALP200IV",2007,0,"E"), "A", sdb);
	  updateRecord(new SubjectPaper("LC001","GLP200IV",2007,0,"E"), "A", sdb);
	  updateRecord(new SubjectPaper("LC001","BLP000IV",2007,0,"A"), "A", sdb);
	  updateRecord(new SubjectPaper("LC001","ALPA00IV",2007,2011,"E"), "A", sdb);
	  updateRecord(new SubjectPaper("LC001","GLPA00IV",2007,2011,"E"), "A", sdb);
	  updateRecord(new SubjectPaper("LC001","BLPA00IV",2007,2011,"E"), "A", sdb);
	  updateRecord(new SubjectPaper("LC013","ALP000EV",2007,0,"A"), "A", sdb);
	  updateRecord(new SubjectPaper("LC013","GLP000EV",2007,0,"A"), "A", sdb);
	  updateRecord(new SubjectPaper("LC013","ALPA00EV",2007,0,"E"), "A", sdb);
	  updateRecord(new SubjectPaper("LC013","GLPA00EV",2007,0,"E"), "A", sdb);
	  updateRecord(new SubjectPaper("LC058","ALP000EV",2007,0,"A"), "A", sdb);
	  updateRecord(new SubjectPaper("LC058","GLP000EV",2007,0,"A"), "A", sdb);
	  updateRecord(new SubjectPaper("LC058","ALPA00EV",2007,0,"E"), "A", sdb);
	  updateRecord(new SubjectPaper("LC058","GLPA00EV",2007,0,"E"), "A", sdb);
	  updateRecord(new SubjectPaper("LC006","ALP000EV",2007,0,"A"), "A", sdb);
	  updateRecord(new SubjectPaper("LC006","GLP000EV",2007,0,"A"), "A", sdb);
	  updateRecord(new SubjectPaper("LC549","ALP000EV",2007,0,"A"), "A", sdb);
	  updateRecord(new SubjectPaper("LC550","ALP000EV",2007,0,"A"), "A", sdb);
	  
	  updateRecord(new SubjectPaper("LC003","ALP000EV",2007,0,"M"), "A", sdb);
	  updateRecord(new SubjectPaper("LC003","GLP000EV",2007,0,"M"), "A", sdb);
	  updateRecord(new SubjectPaper("LC003","BLP000EV",2007,0,"M"), "A", sdb);

	  
	  updateRecord(new SubjectPaper("LC003","BLP130EV",2011,2014,"E"), "A", sdb);
	  updateRecord(new SubjectPaper("LC003","GLP130EV",2011,2014,"E"), "A", sdb);
	  updateRecord(new SubjectPaper("LC003","ALP130EV",2011,2014,"E"), "A", sdb);
	  updateRecord(new SubjectPaper("LC003","BLP230EV",2010,2014,"E"), "A", sdb);
	  updateRecord(new SubjectPaper("LC003","ALP230EV",2010,2014,"E"), "A", sdb);
	  updateRecord(new SubjectPaper("LC003","GLP230EV",2010,2014,"E"), "A", sdb);
	  updateRecord(new SubjectPaper("LC003","ALP100EV",2007,2013,"E"), "A", sdb);
	  updateRecord(new SubjectPaper("LC003","GLP100EV",2007,2013,"E"), "A", sdb);
	  updateRecord(new SubjectPaper("LC003","BLP100EV",2007,2013,"E"), "A", sdb);
	  updateRecord(new SubjectPaper("LC003","ALP200EV",2007,2013,"E"), "A", sdb);
	  updateRecord(new SubjectPaper("LC003","GLP200EV",2007,2013,"E"), "A", sdb);
	  updateRecord(new SubjectPaper("LC003","BLP200EV",2007,2013,"E"), "A", sdb);
	  updateRecord(new SubjectPaper("LC019","ALP000EV",2007,0,"A"), "A", sdb);
	  updateRecord(new SubjectPaper("LC067","ALP000EV",2007,0,"M"), "A", sdb);
	  updateRecord(new SubjectPaper("LC067","GLP000EV",2007,0,"M"), "A", sdb);
	  updateRecord(new SubjectPaper("LC067","ALP006EV",2007,0,"E"), "A", sdb);
	  updateRecord(new SubjectPaper("LC067","GLP006EV",2007,0,"E"), "A", sdb);
	  updateRecord(new SubjectPaper("LC067","ALP008EV",2007,0,"E"), "A", sdb);
	  updateRecord(new SubjectPaper("LC067","GLP008EV",2007,0,"E"), "A", sdb);
	  updateRecord(new SubjectPaper("LC067","ALP007EV",2007,0,"E"), "A", sdb);
	  updateRecord(new SubjectPaper("LC067","ALPU00EV",2013,0,"E"), "A", sdb);
	  updateRecord(new SubjectPaper("LC067","GLPU00EV",2013,0,"E"), "A", sdb);
	  updateRecord(new SubjectPaper("LC021","ALP000EV",2007,0,"A"), "A", sdb);
	  updateRecord(new SubjectPaper("LC021","GLP000EV",2007,0,"A"), "A", sdb);
	  updateRecord(new SubjectPaper("LC023","ALP000EV",2007,0,"A"), "A", sdb);
	  updateRecord(new SubjectPaper("LC023","GLP000EV",2007,0,"A"), "A", sdb);
	  updateRecord(new SubjectPaper("LC548","ALP000EV",2007,0,"A"), "A", sdb);
	  updateRecord(new SubjectPaper("LC018","ALP000EV",2007,0,"A"), "A", sdb);
	  updateRecord(new SubjectPaper("LC223","ALP000EV",2007,0,"A"), "A", sdb);
	  updateRecord(new SubjectPaper("LC223","GLP000EV",2007,0,"A"), "A", sdb);
	  updateRecord(new SubjectPaper("LC553","ALP000EV",2007,0,"A"), "A", sdb);
	  updateRecord(new SubjectPaper("LC099","ALP000EV",2007,0,"A"), "A", sdb);
	  updateRecord(new SubjectPaper("LC099","GLP000EV",2007,0,"A"), "A", sdb);
	  updateRecord(new SubjectPaper("LC099","ALPA00EV",2007,0,"E"), "A", sdb);
	  updateRecord(new SubjectPaper("LC099","GLPA00EV",2007,0,"E"), "A", sdb);
	  updateRecord(new SubjectPaper("LC554","ALP000EV",2007,0,"A"), "A", sdb);
	  updateRecord(new SubjectPaper("LC012","ALP000EV",2007,0,"A"), "A", sdb);
	  updateRecord(new SubjectPaper("LC012","GLP000EV",2007,0,"A"), "A", sdb);
	  updateRecord(new SubjectPaper("LC012","ALPA00EV",2007,0,"E"), "A", sdb);
	  updateRecord(new SubjectPaper("LC012","GLPA00EV",2007,0,"E"), "A", sdb);
	  updateRecord(new SubjectPaper("LC039","ALP000EV",2007,0,"A"), "A", sdb);
	  updateRecord(new SubjectPaper("LC065","ALP014EV",2009,0,"E"), "A", sdb);
	  updateRecord(new SubjectPaper("LC065","GLP014EV",2009,0,"E"), "A", sdb);
	  updateRecord(new SubjectPaper("LC065","ALP015EV",2009,0,"E"), "A", sdb);
	  updateRecord(new SubjectPaper("LC065","GLP015EV",2009,0,"E"), "A", sdb);
	  updateRecord(new SubjectPaper("LC002","ALP100EV",2007,0,"E"), "A", sdb);
	  updateRecord(new SubjectPaper("LC002","GLP100EV",2007,0,"E"), "A", sdb);
	  updateRecord(new SubjectPaper("LC002","ALP200EV",2007,0,"E"), "A", sdb);
	  updateRecord(new SubjectPaper("LC002","GLP200EV",2007,0,"E"), "A", sdb);
					  

	  //Enter subject levels
	  updateRecord(new SubjectLevel("LC032","H"), "A", sdb);
	  updateRecord(new SubjectLevel("LC032","O"), "A", sdb);
	  updateRecord(new SubjectLevel("LC026","H"), "A", sdb);
	  updateRecord(new SubjectLevel("LC026","O"), "A", sdb);
	  updateRecord(new SubjectLevel("LC024","H"), "A", sdb);
	  updateRecord(new SubjectLevel("LC024","O"), "A", sdb);
	  updateRecord(new SubjectLevel("LC007","H"), "A", sdb);
	  updateRecord(new SubjectLevel("LC007","O"), "A", sdb);
	  updateRecord(new SubjectLevel("LC020","H"), "A", sdb);
	  updateRecord(new SubjectLevel("LC020","O"), "A", sdb);
	  updateRecord(new SubjectLevel("LC059","H"), "A", sdb);
	  updateRecord(new SubjectLevel("LC059","O"), "A", sdb);
	  updateRecord(new SubjectLevel("LC014","H"), "A", sdb);
	  updateRecord(new SubjectLevel("LC014","O"), "A", sdb);
	  updateRecord(new SubjectLevel("LC025","H"), "A", sdb);
	  updateRecord(new SubjectLevel("LC025","O"), "A", sdb);
	  updateRecord(new SubjectLevel("LC559","H"), "A", sdb);
	  updateRecord(new SubjectLevel("LC033","H"), "A", sdb);
	  updateRecord(new SubjectLevel("LC033","O"), "A", sdb);
	  updateRecord(new SubjectLevel("LC022","H"), "A", sdb);
	  updateRecord(new SubjectLevel("LC022","O"), "A", sdb);
	  updateRecord(new SubjectLevel("LC008","H"), "A", sdb);
	  updateRecord(new SubjectLevel("LC008","O"), "A", sdb);
	  updateRecord(new SubjectLevel("LC029","H"), "A", sdb);
	  updateRecord(new SubjectLevel("LC029","O"), "A", sdb);
	  updateRecord(new SubjectLevel("LC567","H"), "A", sdb);
	  updateRecord(new SubjectLevel("LC547","H"), "A", sdb);
	  updateRecord(new SubjectLevel("LC038","H"), "A", sdb);
	  updateRecord(new SubjectLevel("LC017","H"), "A", sdb);
	  updateRecord(new SubjectLevel("LC034","H"), "A", sdb);
	  updateRecord(new SubjectLevel("LC034","O"), "A", sdb);
	  updateRecord(new SubjectLevel("LC027","H"), "A", sdb);
	  updateRecord(new SubjectLevel("LC027","O"), "A", sdb);
	  updateRecord(new SubjectLevel("LC562","H"), "A", sdb);
	  updateRecord(new SubjectLevel("LC562","O"), "A", sdb);
	  updateRecord(new SubjectLevel("LC552","H"), "A", sdb);
	  updateRecord(new SubjectLevel("LC049","H"), "A", sdb);
	  updateRecord(new SubjectLevel("LC010","H"), "A", sdb);
	  updateRecord(new SubjectLevel("LC010","O"), "A", sdb);
	  updateRecord(new SubjectLevel("LC005","H"), "A", sdb);
	  updateRecord(new SubjectLevel("LC005","O"), "A", sdb);
	  updateRecord(new SubjectLevel("LC011","H"), "A", sdb);
	  updateRecord(new SubjectLevel("LC011","O"), "A", sdb);
	  updateRecord(new SubjectLevel("LC009","H"), "A", sdb);
	  updateRecord(new SubjectLevel("LC009","O"), "A", sdb);
	  updateRecord(new SubjectLevel("LC096","H"), "A", sdb);
	  updateRecord(new SubjectLevel("LC096","O"), "A", sdb);
	  updateRecord(new SubjectLevel("LC098","H"), "A", sdb);
	  updateRecord(new SubjectLevel("LC098","O"), "A", sdb);
	  updateRecord(new SubjectLevel("LC551","H"), "A", sdb);
	  updateRecord(new SubjectLevel("LC001","H"), "A", sdb);
	  updateRecord(new SubjectLevel("LC001","O"), "A", sdb);
	  updateRecord(new SubjectLevel("LC001","F"), "A", sdb);
	  updateRecord(new SubjectLevel("LC013","H"), "A", sdb);
	  updateRecord(new SubjectLevel("LC013","O"), "A", sdb);
	  updateRecord(new SubjectLevel("LC058","H"), "A", sdb);
	  updateRecord(new SubjectLevel("LC058","O"), "A", sdb);
	  updateRecord(new SubjectLevel("LC549","H"), "A", sdb);
	  updateRecord(new SubjectLevel("LC550","H"), "A", sdb);
	  updateRecord(new SubjectLevel("LC003","H"), "A", sdb);
	  updateRecord(new SubjectLevel("LC003","O"), "A", sdb);
	  updateRecord(new SubjectLevel("LC003","F"), "A", sdb);
	  updateRecord(new SubjectLevel("LC019","H"), "A", sdb);
	  updateRecord(new SubjectLevel("LC019","O"), "A", sdb);
	  updateRecord(new SubjectLevel("LC067","H"), "A", sdb);
	  updateRecord(new SubjectLevel("LC067","O"), "A", sdb);
	  updateRecord(new SubjectLevel("LC021","H"), "A", sdb);
	  updateRecord(new SubjectLevel("LC021","O"), "A", sdb);
	  updateRecord(new SubjectLevel("LC023","H"), "A", sdb);
	  updateRecord(new SubjectLevel("LC023","O"), "A", sdb);
	  updateRecord(new SubjectLevel("LC548","H"), "A", sdb);
	  updateRecord(new SubjectLevel("LC018","H"), "A", sdb);
	  updateRecord(new SubjectLevel("LC223","H"), "A", sdb);
	  updateRecord(new SubjectLevel("LC223","O"), "A", sdb);
	  updateRecord(new SubjectLevel("LC553","H"), "A", sdb);
	  updateRecord(new SubjectLevel("LC099","H"), "A", sdb);
	  updateRecord(new SubjectLevel("LC099","O"), "A", sdb);
	  updateRecord(new SubjectLevel("LC554","H"), "A", sdb);
	  updateRecord(new SubjectLevel("LC012","H"), "A", sdb);
	  updateRecord(new SubjectLevel("LC012","O"), "A", sdb);
	  updateRecord(new SubjectLevel("LC039","H"), "A", sdb);
	  updateRecord(new SubjectLevel("LC065","H"), "A", sdb);
	  updateRecord(new SubjectLevel("LC065","O"), "A", sdb);
	  updateRecord(new SubjectLevel("LC002","H"), "A", sdb);
	  updateRecord(new SubjectLevel("LC002","O"), "A", sdb);
	  updateRecord(new SubjectLevel("LC006","H"), "A", sdb);
	  updateRecord(new SubjectLevel("LC006","O"), "A", sdb);
	}	
	
}

//Brid Delap - Program to hold strings for database name, table names and create statements.
package com.example.project;

import android.provider.BaseColumns;
import android.util.Log;

public class TableData {
	
	public TableData() {
		
	}
	
	public static abstract class TableInfo implements BaseColumns {
		public static final String DATABASE_NAME="leavingCert" ;
		public static final String TABLE_YEAR = "year" ;
		public static final String TABLE_LEVEL = "level" ;
		public static final String TABLE_SUB = "sub" ;
		public static final String TABLE_SUB_LEVEL = "subLevel";
		public static final String TABLE_PAP = "pap" ;
		public static final String TABLE_SUB_PAP = "subPap" ;
		public static final String TABLE_HISTORY = "paperHistory" ;
		//define column for _id field needed by cursor on all tables
		public static final String CURSOR_ID = "_id";
		//define column for year
		public static final String COL_YEAR_ID = "yearId" ;
		//define columns for subject ;
		public static final String COL_SUB_ID = "subId" ;
		public static final String COL_SUB_NAME = "subName" ;
		public static final String COL_SUB_START_YEAR = "subStartYear" ;
		public static final String COL_SUB_END_YEAR = "subEndYear" ;
		//define columns for level
		public static final String COL_LEVEL_ID = "levelId" ;
		public static final String COL_LEVEL_NAME = "levelName" ;
		//***table subLevel just contains COL_LEVEL_ID from the level table and COL_SUB_ID from the subject table
		//define columns for paper;
		public static final String COL_PAP_ID = "papId" ;
		public static final String COL_PAP_NAME = "papName" ;
		//define columns for subjectPaper
		//***The columns subId and papId will also be used in subjectPaper*****
		public static final String COL_SUB_PAP_TYPE = "papType" ;
		public static final String COL_SUB_PAP_START_YEAR = "subPapStartYear";
		public static final String COL_SUB_PAP_END_YEAR = "subPapEndYear" ;
		//define columns for paperHistory
		//***columns subId, papId, yearId, levelId and subPapType will also be used in paperHistory*****
		public static final String COL_DATE_HISTORY = "dateHistory" ;
		//define create statement for year
		public static final String CREATE_TABLE_YEAR =
				"CREATE TABLE " + TABLE_YEAR + "(" + CURSOR_ID + " INTEGER, " + COL_YEAR_ID + " INTEGER PRIMARY KEY " + ")" ; 
		public static final String CREATE_TABLE_SUBJECT =
				"CREATE TABLE " + TABLE_SUB + "(" + CURSOR_ID + " INTEGER, " + COL_SUB_ID + " TEXT PRIMARY KEY, " + 
				COL_SUB_NAME + " TEXT, " + COL_SUB_START_YEAR + " INTEGER, " + COL_SUB_END_YEAR + " INTEGER " + ")" ;
		public static final String CREATE_TABLE_LEVEL =
				"CREATE TABLE " + TABLE_LEVEL + "(" + CURSOR_ID + " INTEGER, " + COL_LEVEL_ID + " TEXT PRIMARY KEY, " + 
				COL_LEVEL_NAME + " TEXT)" ;
		public static final String CREATE_TABLE_PAPER =
				"CREATE TABLE " + TABLE_PAP + "("+ CURSOR_ID + " INTEGER, " + COL_PAP_ID + " TEXT, " +
				COL_PAP_NAME + " TEXT, " + 
				COL_LEVEL_ID + " TEXT, " +
				" PRIMARY KEY (" + COL_PAP_ID + ", " + COL_LEVEL_ID + "))" ;
		public static final String CREATE_TABLE_HISTORY =
				"CREATE TABLE " + TABLE_HISTORY + "(" + CURSOR_ID + " INTEGER, " + COL_SUB_ID + " TEXT, " +
				COL_PAP_ID + " TEXT, " + COL_YEAR_ID + " INTEGER, " + COL_LEVEL_ID + " TEXT, " + 
				COL_SUB_PAP_TYPE + " TEXT, " + COL_DATE_HISTORY + " TEXT, " +
				" PRIMARY KEY (" + COL_SUB_ID + ", " + COL_PAP_ID + ", " + COL_YEAR_ID + ", " + COL_SUB_PAP_TYPE +  "))" ;
		//TABLE_SUB_PAP has a composite primary key consisting of subject id and paper id
		public static final String CREATE_TABLE_SUB_LEVEL =
				"CREATE TABLE " + TABLE_SUB_LEVEL + "(" + CURSOR_ID + " INTEGER, " + COL_SUB_ID + " TEXT, " + COL_LEVEL_ID + " TEXT, " +
				" PRIMARY KEY (" + COL_SUB_ID + ", " + COL_LEVEL_ID + "))" ;
		public static final String CREATE_TABLE_SUB_PAP =
				"CREATE TABLE " + TABLE_SUB_PAP + "(" + CURSOR_ID + " INTEGER, " + COL_SUB_ID + " TEXT, " + COL_PAP_ID + " TEXT, " +
				COL_SUB_PAP_START_YEAR + " INTEGER, " + COL_SUB_PAP_END_YEAR + " INTEGER, " +
				COL_SUB_PAP_TYPE + " INTEGER, " +
				" PRIMARY KEY (" + COL_SUB_ID + ", " + COL_PAP_ID + "))" ;
		public static final String CREATE_INSERT_TRIGGER_SUB_PAP =
				"CREATE TRIGGER insertSubPap BEFORE INSERT ON " +
				TABLE_SUB_PAP +
				" FOR EACH ROW BEGIN " + 
				" SELECT RAISE(ROLLBACK, 'insert on table violates foreign key constraint') " +
				" WHERE  ((SELECT " + COL_SUB_ID + " FROM  " + TABLE_SUB + " WHERE " + COL_SUB_ID + " = NEW." + COL_SUB_ID + ") IS NULL " +
				" OR (SELECT " + COL_PAP_ID + " FROM  " + TABLE_PAP + " WHERE " + COL_PAP_ID + " = NEW." + COL_PAP_ID + ") IS NULL); " +
				"  END;" ;
		public static final String CREATE_INSERT_TRIGGER_SUB_LEVEL =
				"CREATE TRIGGER insertSubLevel BEFORE INSERT ON " +
				TABLE_SUB_LEVEL +
				" FOR EACH ROW BEGIN " + 
				" SELECT RAISE(ROLLBACK, 'insert on table violates foreign key constraint') " +
				" WHERE  ((SELECT " + COL_SUB_ID + " FROM  " + TABLE_SUB + " WHERE " + COL_SUB_ID + " = NEW." + COL_SUB_ID + ") IS NULL " +
				" OR (SELECT " + COL_LEVEL_ID + " FROM  " + TABLE_LEVEL + " WHERE " + COL_LEVEL_ID + " = NEW." + COL_LEVEL_ID + ") IS NULL); " +
				"  END;" ;
		public static final String CREATE_INSERT_TRIGGER_PAPER =
				"CREATE TRIGGER insertPaper BEFORE INSERT ON " +
				TABLE_PAP +
				" FOR EACH ROW BEGIN " + 
				" SELECT RAISE(ROLLBACK, 'insert on table violates foreign key constraint') " +
				" WHERE (SELECT " + COL_LEVEL_ID + " FROM  " + TABLE_LEVEL + " WHERE " + COL_LEVEL_ID + " = NEW." + COL_LEVEL_ID + ") IS NULL; " +
				"  END;" ;
	}
}

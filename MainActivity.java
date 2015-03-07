//Brid Delap 
//Main Activity which presents user with spinners to select year, subject, level.  If more than one paper exists at that level,
//then a fourth spinner is displayed listing those papers.
package com.example.project;


import com.example.project.TableData.TableInfo;
import android.support.v7.app.ActionBarActivity;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
 

public class MainActivity extends ActionBarActivity implements OnItemSelectedListener {
	Spinner yearSpinner ;
	int yearSelected;
	boolean restore=false;
	int subjectIndex;
	int levelIndex;
	int subPapIndex;
	Spinner subjectSpinner;
	String subjectSelected ;
	String levelSelected;
	String subPapSelected;
	String papSelected;
	Spinner paperSpinner;
	Spinner levelSpinner;
	Spinner subPapSpinner;
	String subPapType ;

	@Override

	protected void onCreate(Bundle savedInstanceState) {
		if(savedInstanceState==null) {
			restore=true;
			
		} else {
			restore=false;
		}
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	    RadioGroup radioMarkOrExam = (RadioGroup) findViewById(R.id.radioMarkOrExam);
	    //Set up listener to check if radio group is changed
	    radioMarkOrExam.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
	        public void onCheckedChanged(RadioGroup arg0, int id) {
	        	//If the radio group changes 
	        	fillYearSpinner();
	        }
	      });
		fillYearSpinner();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void viewHistory(View view) {
		Intent intent = new Intent(this,HistoryActivity.class) ;
		intent.putExtra("subjectId",subjectSelected);
		startActivity(intent);
	}
	
	public void viewPDF(View view) {
		DatabaseOperations db = DatabaseOperations.getInstance(getApplicationContext());
		PDF pdfView = new PDF(subPapType, yearSelected, subPapSelected, subjectSelected, papSelected, levelSelected );
	
		Intent intent = pdfView.viewPDF(db, this) ;
		if(intent==null) {
	   	    Toast.makeText(getApplicationContext(),
		      	      "You now need to download a suitable pdf reader to view this particular document", Toast.LENGTH_LONG)
		      	      .show();

		} else {
			startActivity(intent);
		}
	}
	

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}


	public void fillYearSpinner() {
		yearSpinner = (Spinner) findViewById(R.id.yearSpinner);
	      // Spinner click listener
		yearSpinner.setOnItemSelectedListener(this);
		int currentYear=0;
		int selectIndex = 0;
		Log.d("Count of spinner = ",yearSpinner.getCount() + " count");
 		if(yearSpinner.getCount()>0) {
			currentYear= ((Cursor) yearSpinner.getSelectedItem()).getInt(1);
 		}
		
		//DatabaseOperations db = new DatabaseOperations(getApplicationContext());
		DatabaseOperations db = DatabaseOperations.getInstance(getApplicationContext());

		Cursor cursorYear =  db.getYearInformation();
		if (cursorYear.getCount() !=0){
			  cursorYear.moveToFirst();
			  //If the year that was previously selected exists in the new cursor, then we want to select that year.
			  //We do this by looping through the cursor.
			  for (int i=0; i<cursorYear.getCount();i++) {
				  if(cursorYear.getInt(1)==(currentYear)) {
					  selectIndex = cursorYear.getPosition();
					  break;
				  } else {
					  cursorYear.moveToNext();
				  }
			  }
		  }
 
		
		
		
          android.support.v4.widget.SimpleCursorAdapter  adapter = new android.support.v4.widget.SimpleCursorAdapter(this, 
                  android.R.layout.simple_spinner_item,
                   cursorYear, 
                   new String[] {TableInfo.COL_YEAR_ID}, 
                   new int[] {android.R.id.text1}, 0);       
          adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
          yearSpinner.setAdapter(adapter); 
          yearSpinner.setSelection(selectIndex);
          adapter.notifyDataSetChanged();
        //  cursorYear.close();
  		//fillSubjectSpinner();
}

	public void fillSubjectSpinner() {
		int selectIndex = 0;
		//If the application has had to restoreInstanceState then we want the value of the index to be equal to the
		//value of the index taken from InstanceState
		if(restore==true) {
			selectIndex=subjectIndex;
		}
		subjectSpinner = (Spinner) findViewById(R.id.subjectSpinner);
  	 	subjectSpinner.setOnItemSelectedListener(this); 
 		String currentSubject="";
		Log.d("Count of spinner = ",subjectSpinner.getCount() + " count");
 		 
 		if(subjectSpinner.getCount()>0) {
			currentSubject= ((Cursor) subjectSpinner.getSelectedItem()).getString(1);
			Log.d("Current Subject",currentSubject + " current subject ");
 		}
		//get the current year selected as subjects can change depending on year
       	yearSelected = ((Cursor) yearSpinner.getSelectedItem()).getInt(1);
 		DatabaseOperations db = DatabaseOperations.getInstance(getApplicationContext());

       	Cursor cursorSubject =  db.getSubjectInformation(yearSelected);
		  
		  //loop through cursor to see if the previously selected item exists in the current cursor
		  if (cursorSubject.getCount() !=0){
			  cursorSubject.moveToFirst();
			  //If the subject that was previously selected exists in the new cursor, then we want to select that subject.
			  //We do this by looping through the cursor.
			  for (int i=0; i<cursorSubject.getCount();i++) {
				  if(cursorSubject.getString(1).equals(currentSubject)) {
					  selectIndex = cursorSubject.getPosition();
					  break;
				  } else {
					  //Log.d(cursorSubject.getString(1),currentSubject) ;
					  cursorSubject.moveToNext();
				  }
			  }
		  }
  //        android.widget.SimpleCursorAdapter  adapter = new android.widget.SimpleCursorAdapter(this, 
          android.support.v4.widget.SimpleCursorAdapter  adapter = new android.support.v4.widget.SimpleCursorAdapter(this, 
              android.R.layout.simple_spinner_item,
                 cursorSubject, 
                 new String[] {TableInfo.COL_SUB_NAME, TableInfo.COL_SUB_ID, TableInfo.COL_SUB_START_YEAR, TableInfo.COL_SUB_END_YEAR}, 
                 new int[] {android.R.id.text1}, 0);       
	      adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	      subjectSpinner.setAdapter(adapter);
	      subjectSpinner.setSelection(selectIndex);
          adapter.notifyDataSetChanged();
 
	}
	

	public void fillLevelSpinner() {
	  	levelSpinner = (Spinner) findViewById(R.id.levelSpinner);
			levelSpinner.setOnItemSelectedListener(this);
			String currentLevel="";
		int selectIndex = 0;
		//If the application has had to restoreInstanceState then we want the value of the index to be equal to the
		//value of the index taken from InstanceState
		if(restore==true) {
			selectIndex=levelIndex;
		}
		if(levelSpinner.getCount()>0) {
			currentLevel= ((Cursor) levelSpinner.getSelectedItem()).getString(1);
 		}
		//get the current subject selected as levels can change depending on subjects
        subjectSelected = ((Cursor) subjectSpinner.getSelectedItem()).getString(1);
 		DatabaseOperations db = DatabaseOperations.getInstance(getApplicationContext());
		  Cursor cursorLevel =  db.getLevelInformation(subjectSelected);
		  
		  //loop through cursor to see if the previously selected item exists in the current cursor
		  if (cursorLevel.getCount() !=0){
			  cursorLevel.moveToFirst();
			  //If the level that was previously selected exists in the new cursor, then we want to select that level.
			  //We do this by looping through the cursor.
			  for (int i=0; i<cursorLevel.getCount();i++) {
				  if(cursorLevel.getString(1).equals(currentLevel)) {
					  selectIndex = cursorLevel.getPosition();
					  break;
				  } else {
					  //Log.d(cursorSubject.getString(1),currentSubject) ;
					  cursorLevel.moveToNext();
				  }
			  }
		  }
//          android.widget.SimpleCursorAdapter  adapter = new android.widget.SimpleCursorAdapter(this, 
          android.support.v4.widget.SimpleCursorAdapter  adapter = new android.support.v4.widget.SimpleCursorAdapter(this, 
                android.R.layout.simple_spinner_item,
                 cursorLevel, 
                 new String[] {TableInfo.COL_LEVEL_NAME, TableInfo.COL_LEVEL_ID, TableInfo.COL_SUB_ID}, 
                 new int[] {android.R.id.text1}, 0);       
	      adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	      levelSpinner.setAdapter(adapter);  
	      levelSpinner.setSelection(selectIndex);
	      levelSelected = ((Cursor) levelSpinner.getSelectedItem()).getString(1);
          adapter.notifyDataSetChanged();

	}
	
	
	public void fillSubjectPaperSpinner() {
		RadioButton radioExam = (RadioButton) findViewById(R.id.exam);
		if(radioExam.isChecked()) {
			subPapType="E";
			Log.d("Exam is checked","Exam is checked");
		} else {
			subPapType="M";
			Log.d("Mark is checked","Mark is checked");

		}
	  	subPapSpinner = (Spinner) findViewById(R.id.subPapSpinner);
	    subPapSpinner.setOnItemSelectedListener(this);
		String currentSubPap="";
	
		int selectIndex = 0;

		if(restore==true) {
			if(subPapType=="E") {
				selectIndex=subPapIndex;
			}
		}
		levelSelected = ((Cursor) levelSpinner.getSelectedItem()).getString(1);
			
		
		if(subPapSpinner.getCount()>0) {
			currentSubPap= ((Cursor) subPapSpinner.getSelectedItem()).getString(1) + ((Cursor) subPapSpinner.getSelectedItem()).getString(2);
		}
		DatabaseOperations db = DatabaseOperations.getInstance(getApplicationContext());
		  Cursor cursorRelatedPapers =  db.getSubjectPaperInformation(yearSelected, subjectSelected, levelSelected,subPapType);
		  if(cursorRelatedPapers.getCount()>1) {
			  subPapSpinner.setVisibility(View.VISIBLE);
			  TextView labelPaper = (TextView) findViewById(R.id.labelPaper);
			  labelPaper.setVisibility(View.VISIBLE);
			  cursorRelatedPapers.moveToFirst();
			  //If the subjectPaper that was previously selected exists in the new cursor, then we want to select that subject.
			  //We do this by looping through the cursor.
			  for (int i=0; i<cursorRelatedPapers.getCount();i++) {
				  Log.d("Loop " + cursorRelatedPapers.getInt(0),"Previous" + currentSubPap);
				  if((cursorRelatedPapers.getString(1) + cursorRelatedPapers.getString(2)).equals(currentSubPap)) {
					  selectIndex = cursorRelatedPapers.getPosition();
					  break;
				  } else {
					  cursorRelatedPapers.moveToNext();
				  }
			  }
	          android.support.v4.widget.SimpleCursorAdapter  adapter = new android.support.v4.widget.SimpleCursorAdapter(this, 
		                android.R.layout.simple_spinner_item,
		                 cursorRelatedPapers, 
		      new String[] {TableInfo.COL_PAP_NAME,TableInfo.COL_SUB_ID, TableInfo.COL_PAP_ID},
		      new int[] {android.R.id.text1}, 0);       
			  adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			  subPapSpinner.setAdapter(adapter);  
			  Log.d(selectIndex + " is the value of the index",selectIndex + "index");
			  if(subPapSpinner.getCount()>selectIndex) {
				  subPapSpinner.setSelection(selectIndex);
			  } else {
				  subPapSpinner.setSelection(0);
			  }
			  papSelected = ((Cursor) subPapSpinner.getSelectedItem()).getString(2);
			  subPapSelected = ((Cursor) subPapSpinner.getSelectedItem()).getString(1) + ((Cursor) subPapSpinner.getSelectedItem()).getString(2);
	          adapter.notifyDataSetChanged();
			  
	  } else {
			  subPapSpinner.setVisibility(View.GONE);
			  TextView labelPaper = (TextView) findViewById(R.id.labelPaper);
			  labelPaper.setVisibility(View.GONE);
			  if(cursorRelatedPapers.getCount()==1) {
				  cursorRelatedPapers.moveToFirst();
				  papSelected =cursorRelatedPapers.getString(2);
				  subPapSelected = cursorRelatedPapers.getString(1) + cursorRelatedPapers.getString(2);
			  }
	  }
	}
	

    public void onItemSelected(AdapterView<?> parent, View view, 
            int position, long id) {
    	int currentSpinnerId = parent.getId();
    	switch (currentSpinnerId) 
        {         
        	case R.id.yearSpinner:
        		 fillSubjectSpinner();
                 break;              
             case R.id.subjectSpinner:
 	          	 fillLevelSpinner();
            	 break;    
             case R.id.levelSpinner:
            	 fillSubjectPaperSpinner();
               	 break;              
             case R.id.subPapSpinner:
   			  	 papSelected = ((Cursor) subPapSpinner.getSelectedItem()).getString(2);
            	 subPapSelected = ((Cursor) subPapSpinner.getSelectedItem()).getString(1) + ((Cursor) subPapSpinner.getSelectedItem()).getString(2);
           	  	break;
        }
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
	    }

  //If an activity is stopped such as changing the orientation of the device
  public void onSaveInstanceState(Bundle savedInstanceState) {
	  Log.d("In Save Instance State","In Save Instance State");
	  savedInstanceState.putInt("KEY_YEAR_INDEX", yearSpinner.getSelectedItemPosition());
	  savedInstanceState.putInt("KEY_SUBJECT_INDEX", subjectSpinner.getSelectedItemPosition());
	  savedInstanceState.putInt("KEY_LEVEL_INDEX", levelSpinner.getSelectedItemPosition());
	  savedInstanceState.putInt("KEY_SUB_PAP_INDEX", subPapSpinner.getSelectedItemPosition());
	  savedInstanceState.putInt("KEY_YEAR_SEL", yearSelected);
	  savedInstanceState.putString("KEY_SUBJECT_SEL", subjectSelected);
	  savedInstanceState.putString("KEY_LEVEL_SEL", levelSelected);
	  savedInstanceState.putString("KEY_SUB_PAP_TYPE", subPapType);
	  savedInstanceState.putString("KEY_LEVEL_SUB_PAP_SEL", subPapSelected);
    super.onSaveInstanceState(savedInstanceState);
 }


  //If an activity is restored (eg after changing the orientation of device)  
  public void onRestoreInstanceState(Bundle savedInstanceState) {
	//Load preferences on resume in case application crashes
	    super.onRestoreInstanceState(savedInstanceState);
	    if(savedInstanceState!=null) {
	    	subjectIndex = savedInstanceState.getInt("KEY_SUBJECT_INDEX") ;
	    	levelIndex=savedInstanceState.getInt("KEY_LEVEL_INDEX") ;
	    	subPapIndex=savedInstanceState.getInt("KEY_SUB_PAP_INDEX");
	    	yearSelected=savedInstanceState.getInt("KEY_YEAR_SEL");
	    	subjectSelected=savedInstanceState.getString("KEY_SUBJECT_SEL");
	    	levelSelected=savedInstanceState.getString("KEY_LEVEL_SEL");
	    	subPapSelected=savedInstanceState.getString("KEY_LEVEL_SUB_PAP_SEL");
	    	subPapType=savedInstanceState.getString("KEY_SUB_PAP_TYPE");
	    	restore=true;
	    } else {
	    	restore=false;
	    }
  }
    //}
    public void onPause() {
        super.onPause();
   }

    public void onResume() {
	  super.onResume();
    }

}

//Brid Delap.  Activity to display list of papers already viewed.
package com.example.project;


import com.example.project.TableData.TableInfo;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class HistoryActivity extends ActionBarActivity {
	
//comment
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		String subjectId="";
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_history);
		final ListView listview=(ListView)findViewById(R.id.listHistory);
		DatabaseOperations db = DatabaseOperations.getInstance(getApplicationContext());
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
		    subjectId = extras.getString("subjectId");
		}
       	final Cursor cursorHistory =  db.getHistoryInformation(subjectId) ;
        android.support.v4.widget.SimpleCursorAdapter  adapter = new android.support.v4.widget.SimpleCursorAdapter(this, 
        		R.layout.activity_listview,
                 cursorHistory, 
                 new String[] {TableInfo.COL_SUB_NAME, TableInfo.COL_PAP_NAME, TableInfo.COL_YEAR_ID,
        		TableInfo.COL_LEVEL_ID, TableInfo.COL_DATE_HISTORY, TableInfo.COL_SUB_PAP_TYPE,
        		TableInfo.COL_SUB_ID, TableInfo.COL_PAP_ID}, 
                 new int[] {R.id.subject, R.id.paper, R.id.year, R.id.level, R.id.date, R.id.papType}, 0);       
	     // adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	      listview.setAdapter(adapter);  
	      //listener - pdf document is displayed if an item is clicked in the listview
	      listview.setOnItemClickListener(new OnItemClickListener() {
	    	  @Override
	    	  public void onItemClick(AdapterView<?> parent, View view,
	    	    int position, long id) {
	    		  cursorHistory.moveToPosition(position);
	    		  String subPapType = cursorHistory.getString(8);
	    		  int yearId = cursorHistory.getInt(5) ;
	    		  String subId = cursorHistory.getString(1);
	    		  String papId = cursorHistory.getString(3);
	    		  String subPapId = subId + papId ;
	    		  String levelId = cursorHistory.getString(6);
	    		  Log.d(yearId + " " + subId + " " + papId+" " + subPapId +" "+ levelId + " "+subPapType,"cursor variables");
      		      PDF pdfView = new PDF(subPapType, yearId, subPapId, subId, papId, levelId) ;
      		      viewPDF(pdfView) ;
	    	  }
  	    	}); 
	      }

   public void viewPDF(PDF pdfView) {
		DatabaseOperations db = DatabaseOperations.getInstance(getApplicationContext());
		Intent intent = pdfView.viewPDF(db, this) ;
		if(intent==null) {
	   	    Toast.makeText(getApplicationContext(),
		      	      "You now need to download a suitable pdf reader to view this document", Toast.LENGTH_LONG)
		      	      .show();

		} else {
			startActivity(intent);
		}
	}
  	  
	   	
   

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.history, menu);
		return true;
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
}

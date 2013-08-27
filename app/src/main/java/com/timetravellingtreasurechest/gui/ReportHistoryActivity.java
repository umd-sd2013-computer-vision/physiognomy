package com.timetravellingtreasurechest.gui;

import android.app.Activity;
import android.app.AlertDialog;

import com.timetravellingtreasurechest.app.MainActivity;
import com.timetravellingtreasurechest.app.R;
import com.timetravellingtreasurechest.report.ReportData;
import com.timetravellingtreasurechest.services.ReportDataAdapter;
import com.timetravellingtreasurechest.services.ServiceServer;
import com.timetravellingtreasurechest.services.DatabaseService;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

public class ReportHistoryActivity extends ListActivity {
	
	private List<ReportData> reports = null;
	private SimpleDateFormat form = new SimpleDateFormat("MM/dd/yy HH:mm:ss");
	private DatabaseService db = new DatabaseService(ServiceServer.getAndroidContext());
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_report_history);
		getDir(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Physiognomy/Thumbs");
	}    
		
	private void getDir(String dirPath) {
		reports = new ArrayList<ReportData>();
		
		File f = new File(dirPath);
		File[] files = f.listFiles();
		
		// order files so most recent are nearest the top
		Arrays.sort(files, new Comparator<File>(){
		    public int compare(File f1, File f2) {
		        return Long.valueOf(f2.lastModified()).compareTo(f1.lastModified());
		    } });

		for(int i = 0; i < files.length; i++) {
			File file = files[i];
			if(!file.isHidden() && !file.isDirectory() && db.getReportText(file.getAbsolutePath()) != null)
				reports.add(db.getReportDataFromThumb(file.getAbsolutePath()));
		}
		
		ReportDataAdapter adapter = new ReportDataAdapter(this, R.layout.row, reports);
		
		ListView listView = getListView();
		listView.setAdapter(adapter);
		
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {
	        @Override
	        public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

	        	new AlertDialog.Builder(ReportHistoryActivity.this)
	        	.setTitle("Confirm Delete")
	        	.setMessage("Delete this report?")
	        	.setIcon(android.R.drawable.ic_dialog_alert)
	        	.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
	        	    public void onClick(DialogInterface dialog, int whichButton) {
	        	    	DatabaseService db = new DatabaseService(getBaseContext());
	            		db.deleteReport(reports.get(position).getImageUri().getPath());
	            		Intent intent = new Intent();
	            		intent.setClassName(ServiceServer.getAndroidContext(), "com.timetravellingtreasurechest.gui.ReportHistoryActivity");
	            		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	            		startActivity(intent);
	        	    }})
	        	 .setNegativeButton(android.R.string.no, null).show();
	        	
	            return true;
	        }
		});
	}
	
	protected void onListItemClick(ListView l, View v, int position, long id) {
		File file = new File(reports.get(position).getImageUri().getPath());
		
		MainActivity.latestReport = db.getReportDataFromThumb(reports.get(position).getThumbUri().getPath());
		Intent intent = new Intent();
		intent.setClassName(this, "com.timetravellingtreasurechest.gui.ManageReportActivity");
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}
}
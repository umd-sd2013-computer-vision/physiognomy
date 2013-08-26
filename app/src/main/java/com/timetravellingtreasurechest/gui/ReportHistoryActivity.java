package com.timetravellingtreasurechest.gui;

import android.app.Activity;

import com.timetravellingtreasurechest.app.MainActivity;
import com.timetravellingtreasurechest.app.R;
import com.timetravellingtreasurechest.report.ReportData;
import com.timetravellingtreasurechest.services.ServiceServer;
import com.timetravellingtreasurechest.services.DatabaseService;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ReportHistoryActivity extends ListActivity {
	
	private List<String> item = null;
	private List<String> path = null;
	private SimpleDateFormat form = new SimpleDateFormat("MM/dd/yy HH:mm:ss");
	private DatabaseService db = new DatabaseService(ServiceServer.getAndroidContext());
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_report_history);
		getDir(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Physiognomy/Thumbs");
	}    
		
	private void getDir(String dirPath) {
		item = new ArrayList<String>();
		path = new ArrayList<String>();
		
		File f = new File(dirPath);
		File[] files = f.listFiles();

		for(int i = 0; i < files.length; i++) {
			File file = files[i];
			if(!file.isHidden() && !file.isDirectory() && db.getReportText(file.getAbsolutePath()) != null) {
				item.add(form.format(new Date(file.lastModified())));
				path.add(file.getAbsolutePath());
			}
		}
	
		ArrayAdapter<String> fileList = new ArrayAdapter<String>(this, R.layout.row, item);
		setListAdapter(fileList);
	}
	
	protected void onListItemClick(ListView l, View v, int position, long id) {
		File file = new File(item.get(position));
		
		MainActivity.latestReport = db.getReportData(path.get(position));
		Intent myIntent = new Intent();
		myIntent.setClassName(this, "com.timetravellingtreasurechest.gui.ManageReportActivity");
		startActivity(myIntent);
	}

}
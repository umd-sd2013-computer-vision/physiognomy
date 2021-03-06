package com.timetravellingtreasurechest.gui;

import com.timetravellingtreasurechest.physiognomy.R;
import com.timetravellingtreasurechest.physiognomy.ReportData;
import com.timetravellingtreasurechest.share.IReportSharingService;
import com.timetravellingtreasurechest.share.ReportSharingService;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class ManageReportActivity extends Activity {
	
	// Dependencies
	ReportData usingReport;
	IReportSharingService reportSharingService = new ReportSharingService();
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_report);
        TextView reportText = (TextView)this.findViewById(R.id.reportText);
        usingReport = (ReportData) getIntent().getSerializableExtra(MainActivity.REPORT);
        reportText.setText(usingReport == null ? "Missing Report" : usingReport.getReportText());
        Button share = (Button)this.findViewById(R.id.Share);
        share.setOnClickListener(new OnClickListener() 
        {
                public void onClick(View v) 
                {
	                	Intent myIntent = new Intent();
	                	myIntent.setClassName(ManageReportActivity.this, "com.timetravellingtreasurechest.gui.ShareActivity");
	                	myIntent.putExtra(MainActivity.REPORT, usingReport);
	                	startActivity(myIntent);  
                }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
         getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
}

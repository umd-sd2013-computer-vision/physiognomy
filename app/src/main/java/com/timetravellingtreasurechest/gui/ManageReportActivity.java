package com.timetravellingtreasurechest.gui;

import com.timetravellingtreasurechest.app.MainActivity;
import com.timetravellingtreasurechest.app.R;
import com.timetravellingtreasurechest.report.ReportData;
import com.timetravellingtreasurechest.services.ImageConverter;
import com.timetravellingtreasurechest.share.IReportSharingService;
import com.timetravellingtreasurechest.share.ReportSharingService;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ManageReportActivity extends Activity {
	
	// Dependencies
	ReportData usingReport;
	IReportSharingService reportSharingService = new ReportSharingService();
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_manage_report);
        TextView reportText = (TextView)this.findViewById(R.id.reportText);
        usingReport = MainActivity.latestReport;//(ReportData) getIntent().getSerializableExtra(MainActivity.REPORT);
        reportText.setText(usingReport == null ? "Missing Report" : usingReport.getReportText());
        Button share = (Button)this.findViewById(R.id.Share);
        share.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
	            		Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
	                    sharingIntent.setType("image/jpeg");
	                    sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Physiognomy reading!");
	                    sharingIntent.putExtra(Intent.EXTRA_TEXT, MainActivity.latestReport.getReportText());
	                    sharingIntent.putExtra("sms_body", MainActivity.latestReport.getReportText());
	                    sharingIntent.putExtra(Intent.EXTRA_STREAM, MainActivity.latestReport.getImageUri());
	                    startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.share_using)));
//	                	Intent myIntent = new Intent();
//	                	myIntent.setClassName(ManageReportActivity.this, "com.timetravellingtreasurechest.gui.ShareActivity");
//	                	//myIntent.putExtra(MainActivity.REPORT, usingReport);
//	                	startActivity(myIntent);  
                }
        });
        
        ImageView reportImage = (ImageView) this.findViewById(R.id.reportImage);
        reportImage.setImageBitmap(MainActivity.latestReport.getBitmap());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
         getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
}

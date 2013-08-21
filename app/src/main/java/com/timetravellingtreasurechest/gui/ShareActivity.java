package com.timetravellingtreasurechest.gui;

import com.timetravellingtreasurechest.app.R;
import com.timetravellingtreasurechest.share.IReportSharingService;
import com.timetravellingtreasurechest.share.ReportSharingService;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ShareActivity extends Activity {

	// Dependencies
	IReportSharingService reportSharingService = new ReportSharingService();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_share);
		
		Button facebook = (Button)this.findViewById(R.id.Facebook);
        facebook.setOnClickListener(new OnClickListener() 
        {
                public void onClick(View v) 
                {
	                	reportSharingService.shareWithFacebook();
                }
        });
        Button text = (Button)this.findViewById(R.id.Text);
        text.setOnClickListener(new OnClickListener() 
        {
                public void onClick(View v) 
                {
	                	reportSharingService.shareWithText();
                }
        });
        Button email = (Button)this.findViewById(R.id.E);
        email.setOnClickListener(new OnClickListener() 
        {
                public void onClick(View v) 
                {
	                	reportSharingService.shareWithEmail();
                }
        });
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.share, menu);
		return true;
	}

}

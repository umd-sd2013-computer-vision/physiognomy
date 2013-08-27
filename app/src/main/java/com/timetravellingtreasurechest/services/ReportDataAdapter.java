package com.timetravellingtreasurechest.services;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.timetravellingtreasurechest.app.MainActivity;
import com.timetravellingtreasurechest.report.ReportData;

import com.timetravellingtreasurechest.app.R;
import android.app.Activity;
import android.content.Context;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ReportDataAdapter extends ArrayAdapter<ReportData> {
	
	private Context context;
	int layoutResourceId;
	List<ReportData> items;
	private SimpleDateFormat form = new SimpleDateFormat("MM/dd/yy HH:mm");

	public ReportDataAdapter(Context context, int layoutResourceId, List<ReportData> items) {
		super(context, layoutResourceId, items);
		this.layoutResourceId = layoutResourceId;
		this.context = context;
		this.items = items;
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		ReportDataHolder holder = null;
		
		if (row == null) {
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			row = inflater.inflate(layoutResourceId, parent, false);
			
			holder = new ReportDataHolder();
			holder.thumbnail = (ImageView) row.findViewById(R.id.reportThumb);
			holder.report = (TextView) row.findViewById(R.id.reportText);
			holder.date = (TextView) row.findViewById(R.id.reportDate);
			
			row.setTag(holder);
		} else {
			holder = (ReportDataHolder)row.getTag();
		}
		
		ReportData report = items.get(position);
		try {
			holder.thumbnail.setImageBitmap(MediaStore.Images.Media.getBitmap(context.getContentResolver(), report.getImageUri()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		holder.report.setText(report.getReportText());
		holder.date.setText(form.format(new Date((new File(report.getImageUri().getPath())).lastModified())));
		
		return row;
	}
	
	static class ReportDataHolder {
		ImageView thumbnail;
		TextView report;
		TextView date;
	}
}

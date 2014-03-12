package com.nttdata.chongqing.bms.main.util;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nttdata.chongqing.bms.R;

/**
 * 每一个自定义格子中的各个控件的控件bean
 * @author yuchen.wei
 *
 */
public class ViewHolder {
	public LinearLayout linearLayout1;
	public LinearLayout linearLayout2;
	public LinearLayout linearLayout3;
	public ImageView imageView1;
	public ImageView imageView2;
	public ImageView imageView3;
	public TextView textView1;
	public TextView textView2;
	public TextView textView3;
	public ImageButton button1;
	public ImageButton button2;
	public ImageButton button3;
	public ProgressBar download_prssbar1;
	public ProgressBar download_prssbar2;
	public ProgressBar download_prssbar3;
	public TextView download_result1;
	public TextView download_result2;
	public TextView download_result3;
	
	/**
	 * 获取控件id
	 * @param convertView
	 */
	public ViewHolder(View convertView){
		
		this.linearLayout1 = (LinearLayout) convertView.findViewById(R.id.listGridDownloadStatus1);
		this.imageView1 = (ImageView) convertView.findViewById(R.id.listGridImageView1);
		this.textView1 = (TextView) convertView.findViewById(R.id.listGridTextView1);
		this.button1 = (ImageButton) convertView.findViewById(R.id.listGridImageButton1);
		this.download_prssbar1 = (ProgressBar) convertView.findViewById(R.id.download_prssbar1); 
		this.download_result1 = (TextView) convertView.findViewById(R.id.download_result1);
		
		this.linearLayout2 = (LinearLayout) convertView.findViewById(R.id.listGridDownloadStatus2);
		this.imageView2 = (ImageView) convertView.findViewById(R.id.listGridImageView2);
		this.textView2 = (TextView) convertView.findViewById(R.id.listGridTextView2);
		this.button2 = (ImageButton) convertView.findViewById(R.id.listGridImageButton2);
		this.download_prssbar2 = (ProgressBar) convertView.findViewById(R.id.download_prssbar2); 
		this.download_result2 = (TextView) convertView.findViewById(R.id.download_result2);
		
		this.linearLayout3 = (LinearLayout) convertView.findViewById(R.id.listGridDownloadStatus3);
		this.imageView3 = (ImageView) convertView.findViewById(R.id.listGridImageView3);
		this.textView3 = (TextView) convertView.findViewById(R.id.listGridTextView3);
		this.button3 = (ImageButton) convertView.findViewById(R.id.listGridImageButton3);
		this.download_prssbar3 = (ProgressBar) convertView.findViewById(R.id.download_prssbar3); 
		this.download_result3 = (TextView) convertView.findViewById(R.id.download_result3);
	}
	
	// 将控件设为可见
	public void setVisibility(int column) {
		switch (column) {
		case 1:
			this.linearLayout1.setVisibility(View.VISIBLE);
			this.imageView1.setVisibility(View.VISIBLE);
			this.textView1.setVisibility(View.VISIBLE);
			this.button1.setVisibility(View.VISIBLE);
			break;
		case 2:
			this.linearLayout2.setVisibility(View.VISIBLE);
			this.imageView2.setVisibility(View.VISIBLE);
			this.textView2.setVisibility(View.VISIBLE);
			this.button2.setVisibility(View.VISIBLE);
			break;
		case 3:
			this.linearLayout3.setVisibility(View.VISIBLE);
			this.imageView3.setVisibility(View.VISIBLE);
			this.textView3.setVisibility(View.VISIBLE);
			this.button3.setVisibility(View.VISIBLE);
			break;
		}
	}
}

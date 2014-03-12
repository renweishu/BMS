package com.nttdata.chongqing.bms.newbookonline.entity;

import com.nttdata.chongqing.bms.main.entity.Book;

import android.widget.ProgressBar;
import android.widget.TextView;

public class DownLoadEntity {
	// 当前对应的下载进度条
	private ProgressBar progressbar;
	// 当前对应的下载文字反应
	private TextView textview;
	// 当前文件的网上下载路径
	private String downloadPath;
	// 当前文件的下载保存路径
	private String downloadSavePath;
	// 当前文件的下载保存名字
	private String downloadSaveFile;
	// 当前下载对象 
	private Book bookdowmloadItem;

	// 当前下载对象下载状态（未下载：0  下载中：1  已下载 2）
	private String downIngFlg="0";

	public String getDownIngFlg() {
		return downIngFlg;
	}
	public void setDownIngFlg(String downIngFlg) {
		this.downIngFlg = downIngFlg;
	}
	public ProgressBar getProgressbar() {
		return progressbar;
	}
	public void setProgressbar(ProgressBar progressbar) {
		this.progressbar = progressbar;
	}
	public TextView getTextview() {
		return textview;
	}
	public void setTextview(TextView textview) {
		this.textview = textview;
	}
	public String getDownloadPath() {
		return downloadPath;
	}
	public void setDownloadPath(String downloadPath) {
		this.downloadPath = downloadPath;
	}
	public String getDownloadSavePath() {
		return downloadSavePath;
	}
	public void setDownloadSavePath(String downloadSavePath) {
		this.downloadSavePath = downloadSavePath;
	}
	public String getDownloadSaveFile() {
		return downloadSaveFile;
	}
	public void setDownloadSaveFile(String downloadSaveFile) {
		this.downloadSaveFile = downloadSaveFile;
	}
	public Book getBookdowmloadItem() {
		return bookdowmloadItem;
	}
	public void setBookdowmloadItem(Book bookdowmloadItem) {
		this.bookdowmloadItem = bookdowmloadItem;
	}





}

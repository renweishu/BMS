package com.nttdata.chongqing.bms.main.entity;

import com.nttdata.chongqing.bms.newbookonline.entity.DownLoadEntity;

/**
 * Book的Bean类
 * @author yuchen.wei
 *
 */
public class Book {
	// 书ID
	private String bookId;
	// 书名
	private String bookName;
	// 简介
	private String bookInfo;
	// 封面URl地址
	private String thumbUrl;
	// 存放目录url地址
	private String saveFileUrl;
	// 下载状态
	private String downloadStatus;
	// 下载信息
	private DownLoadEntity downLoadEntity = new DownLoadEntity();

	public DownLoadEntity getDownLoadEntity() {
		return downLoadEntity;
	}
	public void setDownLoadEntity(DownLoadEntity downLoadEntity) {
		this.downLoadEntity = downLoadEntity;
	}
	public String getDownloadStatus() {
		return downloadStatus;
	}
	public void setDownloadStatus(String downloadStatus) {
		this.downloadStatus = downloadStatus;
	}
	public String getBookInfo() {
		return bookInfo;
	}
	public void setBookInfo(String bookInfo) {
		this.bookInfo = bookInfo;
	}
	public String getThumbUrl() {
		return thumbUrl;
	}
	public void setThumbUrl(String thumbUrl) {
		this.thumbUrl = thumbUrl;
	}
	public String getSaveFileUrl() {
		return saveFileUrl;
	}
	public void setSaveFileUrl(String saveFileUrl) {
		this.saveFileUrl = saveFileUrl;
	}

	public String getBookId() {
		return bookId;
	}
	public void setBookId(String bookId) {
		this.bookId = bookId;
	}
	public String getBookName() {
		return bookName;
	}
	public void setBookName(String bookName) {
		this.bookName = bookName;
	}

	// 用于List<Book>的contains方法用
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Book) {
			Book book = (Book) obj;
			return this.bookId.equals(book.bookId);
		} else {

			return super.equals(obj);
		}
	}
}

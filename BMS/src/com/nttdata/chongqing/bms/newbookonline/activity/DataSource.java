package com.nttdata.chongqing.bms.newbookonline.activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import com.nttdata.chongqing.bms.main.entity.Book;
import com.nttdata.chongqing.bms.main.util.XMLReaderByDOM;



public class DataSource {

	// 书ID
	public static final String KEY_ID = "bookId";
	// 书名
	public static final String KEY_TITLE = "bookName";
	// 作者
	public static final String KEY_AUTHOR = "author";

	// 简介
	public static final String KEY_INFOMATION = "bookInfo";

	// 缩略图
	public static final String KEY_THUMB_URL = "thumbUrl";

	// 期刊文件压缩包的地址
	public static final String KEY_ZIPFILE_URL = "zipfileUrl";

	/**
	 * 获取服务端图书信息
	 */
	public static ArrayList<Book> onlineBookInfo(String url) {
		ArrayList<Book> bookList = new ArrayList<Book>();
		
		
		// 从网络
		try {
			
			URL xmlUrl = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) xmlUrl
					.openConnection();
			conn.setConnectTimeout(30000);
			conn.setReadTimeout(30000);
			conn.setInstanceFollowRedirects(true);
			InputStream is = conn.getInputStream();
			XMLReaderByDOM xmlreaderbydom = new XMLReaderByDOM();
			xmlreaderbydom.read(is);
			bookList=xmlreaderbydom.getList();
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
		return bookList;
	}
	
	/**
	 * 获取本地端图书信息
	 */
	public static ArrayList<Book> localBookInfo(String url) {
		ArrayList<Book> bookList = new ArrayList<Book>();
		
		
		// 从本地
		try {
			File localfile = new File(url);
			FileInputStream ff= new FileInputStream(localfile);
			XMLReaderByDOM xmlreaderbydom = new XMLReaderByDOM();
			xmlreaderbydom.read(ff);
			bookList=xmlreaderbydom.getList();
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
		return bookList;
	}
	
	/**
	 *每次刷新添加数据 
	 **/
	public static ArrayList<Book> addRefresh (ArrayList<Book> bookList) {
		// 每次刷新添加新的数据
		Book b = new Book();
		b.setBookId("004");
		b.setBookInfo("简介:《留德十年》记述了季羡林1935至1945年抛家傍路赴德求学的经过。在赫赫有名的哥廷根大学，作者几经辗转选定印度学为主修方向，遂对其倾注热情与辛劳，最终获得博士学位，也由此奠定了毕生学术研究的深厚根基。原有若干种不同版本的单行本行世，这次则依据东方出版社1992年初版排定。另加《二战心影》一文，亦为作者对留德岁月的回忆。");
		b.setBookName("4.留德十年(季羡林作品珍藏本)(图文版)");
		b.setSaveFileUrl(null);
		b.setThumbUrl("http://t3.baidu.com/it/u=1016987827,1041294181&fm=23&gp=0.jpg");
		bookList.add(b);

		return bookList;
	}


}

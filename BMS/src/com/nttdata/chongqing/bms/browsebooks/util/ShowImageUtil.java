package com.nttdata.chongqing.bms.browsebooks.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xml.sax.SAXException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Xml;
import android.widget.ImageView;
import android.widget.TextView;

import com.nttdata.chongqing.bms.browsebooks.activity.BrowseBooksActivity;
import com.nttdata.chongqing.bms.browsebooks.entity.Magazine;

public class ShowImageUtil {

	BrowseBooksActivity browseBooksActivity;
	List<Magazine> magazineList;
	// 获取存放图片地址
	private File filePath;
	// 存放每个图片
	private List<String> filePathList;
	// 获取图片的数量
	private int fileCount;

	// 获取xml上的文件属性
	public void getMagazineName(String url, String fileName) throws IOException, SAXException {
		
		File getFileSource = new File(url,"/" + fileName + "/" + fileName + ".xml");
		FileInputStream  fins = new FileInputStream(getFileSource);
		InputStream is = fins;
		XmlContentHandler xmlMagazine = new XmlContentHandler();
		Xml.parse(is, Xml.Encoding.UTF_8, xmlMagazine); 
		magazineList = new ArrayList<Magazine>();
		magazineList = xmlMagazine.getMagazineList();
	}
	
	// 获取图片信息
	public void getList(String url, String fileName) throws IOException, SAXException {
		
		browseBooksActivity = new BrowseBooksActivity();
		getMagazineName(url, fileName);
		if (magazineList != null) {
			String fileMagazinePath;
			filePathList = new ArrayList<String>();
			for (Magazine magazine: magazineList) {
				fileMagazinePath = url.concat("/" + fileName + "/" + magazine.getName());
				filePathList.add(fileMagazinePath);
			}
	        if (filePathList == null) {
	        	return;
	        } else {
		        fileCount = filePathList.size();
	        }	
		}
	}
	
	// 设置图片源
	public void setImageView(ImageView imageView, String playWay, 
			String playWay_Rondom, int position, TextView txt_NowPage) {
		
		// 随机播放
        if (playWay == playWay_Rondom) {
        	long imagesCount = Math.round(Math.random()*(filePathList.size()-1) + 1);
        	Bitmap bmp = BitmapFactory.decodeFile(filePathList.get((int)imagesCount));
        	imageView.setImageBitmap(bmp);
        	txt_NowPage.setText(""+imagesCount);
        } else {
        	Bitmap bmp = BitmapFactory.decodeFile(filePathList.get(position));
        	imageView.setImageBitmap(bmp);
        	txt_NowPage.setText(""+(position+1));
        }
	}
	
	public File getFilePath() {
		return filePath;
	}
	
	public List<String> getFilePathList() {
		return filePathList;
	}
	
	public int getFileCount() {
		return fileCount;
	}
}

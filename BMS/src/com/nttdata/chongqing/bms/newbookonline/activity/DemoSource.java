package com.nttdata.chongqing.bms.newbookonline.activity;

import java.util.ArrayList;
import java.util.HashMap;

/*
 *现成代码的数据来源 
 */
public class DemoSource {

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

	/*
	 *存放数据的类 
	 */
	public static ArrayList<HashMap<String, String>> getSourceList () {

		// 存放图书信息的list
		ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> map1 = new HashMap<String, String>();
		map1.put(KEY_ID, "20140221");
		map1.put(KEY_TITLE, "1.留德十年(季羡林作品珍藏本)(图文版)");
		map1.put(KEY_AUTHOR, "季羡林 (作者)");
		map1.put(KEY_INFOMATION, "简介:《留德十年》记述了季羡林1935至1945年抛家傍路赴德求学的经过。在赫赫有名的哥廷根大学，作者几经辗转选定印度学为主修方向，遂对其倾注热情与辛劳，最终获得博士学位，也由此奠定了毕生学术研究的深厚根基。原有若干种不同版本的单行本行世，这次则依据东方出版社1992年初版排定。另加《二战心影》一文，亦为作者对留德岁月的回忆。");
		map1.put(KEY_THUMB_URL, "http://172.23.9.124:8080/BMS_SERVER/file/book/2014-02-21/1.jpg");
		map1.put(KEY_ZIPFILE_URL, "http://172.23.9.124:8080/BMS_SERVER/file/book/2014-02-21/2014-02-21.zip");
		songsList.add(map1);

		HashMap<String, String> map2 = new HashMap<String, String>();
		map2.put(KEY_ID, "20140222");
		map2.put(KEY_TITLE, "2.人生有何意义");
		map2.put(KEY_AUTHOR, "胡适 (作者)");
		map2.put(KEY_INFOMATION, "简介: 总之，生命本没有意义，你要能给他什么意义，他就有什么意义。与其终日冥想人生有何意义，不如试用此生作点有意义的事？……——胡适");
		map2.put(KEY_THUMB_URL, "http://172.23.9.124:8080/BMS_SERVER/file/book/2014-02-22/1.jpg");
		map2.put(KEY_ZIPFILE_URL, "http://172.23.9.124:8080/BMS_SERVER/file/book/2014-02-22/2014-02-22.zip");
		songsList.add(map2);


		HashMap<String, String> map3 = new HashMap<String, String>();
		map3.put(KEY_ID, "20140223");
		map3.put(KEY_TITLE, "3.古汉语常用字字典(第4版)");
		map3.put(KEY_AUTHOR, "岑麒祥 (作者)");
		map3.put(KEY_INFOMATION, "简介:《古汉语常用字字典(第4版)》由著名语言学家王力、岑麟祥、林焘、戴澧、唐作藩、蒋绍愚等十余位专家学者编写，是学习古汉语的必备工具书。");
		map3.put(KEY_THUMB_URL, "http://172.23.9.124:8080/BMS_SERVER/file/book/2014-02-23/1.jpg");
		map3.put(KEY_ZIPFILE_URL, "http://172.23.9.124:8080/BMS_SERVER/file/book/2014-02-23/2014-02-23.zip");
		songsList.add(map3);
		
		HashMap<String, String> map4 = new HashMap<String, String>();
		map4.put(KEY_ID, "20140224");
		map4.put(KEY_TITLE, "4.西游记");
		map4.put(KEY_AUTHOR, "吴承恩 (作者)");
		map4.put(KEY_INFOMATION, "简介:从前有一只天不怕地不怕的猴子，大闹天宫，被镇压后去西天取经了。");
		map4.put(KEY_THUMB_URL, "http://172.23.9.124:8080/BMS_SERVER/file/book/2014-02-24/1.jpg");
		map4.put(KEY_ZIPFILE_URL, "http://172.23.9.124:8080/BMS_SERVER/file/book/2014-02-24/2014-02-24.zip");
		songsList.add(map4);
		
		
		HashMap<String, String> map5 = new HashMap<String, String>();
		map5.put(KEY_ID, "20140225");
		map5.put(KEY_TITLE, "5.xxxxxx");
		map5.put(KEY_AUTHOR, "xxxxxxxx (作者)");
		map5.put(KEY_INFOMATION, "简介:从前有一只天不怕地不怕的猴子，大闹天宫，被镇压后去西天取经了。");
		map5.put(KEY_THUMB_URL, "http://172.23.9.124:8080/BMS_SERVER/file/book/2014-02-25/1.jpg");
		map5.put(KEY_ZIPFILE_URL, "http://172.23.9.124:8080/BMS_SERVER/file/book/2014-02-25/2014-02-25.zip");
		songsList.add(map5);
		
		return songsList;
	}
	
	/**
	 *每次刷新添加数据 
	 **/
	public static ArrayList<HashMap<String, String>> addRefresh (ArrayList<HashMap<String, String>> sourceList) {
		// 每次刷新添加新的数据
		HashMap<String, String> map4 = new HashMap<String, String>();
		map4.put(KEY_ID, "004");
		map4.put(KEY_TITLE, "4.留德十年(季羡林作品珍藏本)(图文版)");
		map4.put(KEY_AUTHOR, "季羡林 (作者)");
		map4.put(KEY_INFOMATION, "简介:《留德十年》记述了季羡林1935至1945年抛家傍路赴德求学的经过。在赫赫有名的哥廷根大学，作者几经辗转选定印度学为主修方向，遂对其倾注热情与辛劳，最终获得博士学位，也由此奠定了毕生学术研究的深厚根基。原有若干种不同版本的单行本行世，这次则依据东方出版社1992年初版排定。另加《二战心影》一文，亦为作者对留德岁月的回忆。");

		map4.put(KEY_THUMB_URL, "http://t3.baidu.com/it/u=1016987827,1041294181&fm=23&gp=0.jpg");
		sourceList.add(map4);
		return sourceList;
	}


}

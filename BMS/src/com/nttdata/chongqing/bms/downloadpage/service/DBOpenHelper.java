package com.nttdata.chongqing.bms.downloadpage.service;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author chao11.ma
 *
 */
public class DBOpenHelper extends SQLiteOpenHelper {
	private static final String DBNAME = "bmsonlinebook.db";
	private static final int VERSION = 1;
	// 调用父类构造器
	public DBOpenHelper(Context context) {
		super(context, DBNAME, null, VERSION);
	}
	// 当数据库首次创建时执行该方法，一般将创建表等初始化操作放在该方法中执行.
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE IF NOT EXISTS filedownlog (id integer primary key autoincrement, downpath varchar(100), threadid INTEGER, downlength INTEGER)");
		// 创建 存放 实时进度的表
		db.execSQL("CREATE TABLE IF NOT EXISTS filedownlog (id integer primary key autoincrement, downpath varchar(100), threadid INTEGER, downlength INTEGER)");
	}

	// 当打开数据库时传入的版本号与当前的版本号不同时会调用该方法
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS filedownlog");
		onCreate(db);
	}

}

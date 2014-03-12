package com.nttdata.chongqing.bms.newbookonline.util;

import java.io.File;

import android.content.Context;

/**
 * @author chao11.ma
 * 删除本地书的封面或者已下载的文件<br>
 *
 */
public class DeleLocalFile {
	private File cacheDir;
	public DeleLocalFile(Context context){

		//找寻SD卡 在下面创建一个bmsonlinebook的目录
		if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
			cacheDir=new File(android.os.Environment.getExternalStorageDirectory(),"BMSthumbUrl");
		else
			// getCacheDir()方法用于获取/data/data//cache目录
			cacheDir=context.getCacheDir();
		if(!cacheDir.exists())
			cacheDir.mkdirs();
	}

	/**
	 * 删除封面
	 * @param url
	 * @return
	 */
	public boolean deleteThumbUrl(String url) {
		String filename=String.valueOf(url.hashCode());
		File f = new File(cacheDir, filename);
		if (f.exists()) {
			f.delete();
		}
		return true;
	}


	/**
	 * 删除本地下载文件
	 * @param url
	 * @return
	 */
	public boolean deleteSaveFileUrl(String url) {
		File f = new File(url);
		if (f.exists()) {
			f.delete();
		}
		return true;
	}
}

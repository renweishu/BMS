package com.nttdata.chongqing.bms.newbookonline.util;

import java.io.File;
import android.content.Context;

/**
 * @author chao11.ma
 *
 */
public class FileCache {
    
    private File cacheDir;
    
    public FileCache(Context context){
        //找寻SD卡 在下面创建一个bmsonlinebook的目录
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
            cacheDir=new File(android.os.Environment.getExternalStorageDirectory(),"BMSthumbUrl");
        else
        	// getCacheDir()方法用于获取/data/data//cache目录
            cacheDir=context.getCacheDir();
        if(!cacheDir.exists())
            cacheDir.mkdirs();
    }
    
    public File getFile(String url){
        
        String filename=String.valueOf(url.hashCode());
        File f = new File(cacheDir, filename);
        return f;
        
    }
    
    public void clear(){
        File[] files=cacheDir.listFiles();
        if(files==null)
            return;
        for(File f:files)
            f.delete();
    }

}

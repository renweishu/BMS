package com.nttdata.chongqing.bms.newbookonline.util;

import java.lang.ref.SoftReference;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import android.graphics.Bitmap;

/**
 * @author chao11.ma
 *
 */
public class MemoryCache {
	// SoftReference是强引用，它保存的对象实例，除非JVM即将OutOfMemory，否则不会被GC回收。这个特性使得它特别适合设计对象Cache
    private Map<String, SoftReference<Bitmap>> cache=Collections.synchronizedMap(new HashMap<String, SoftReference<Bitmap>>());//霓ｯ蠑慕畑
    
    public Bitmap get(String id){
        if(!cache.containsKey(id))
            return null;
        SoftReference<Bitmap> ref=cache.get(id);
        return ref.get();
    }
    
    public void put(String id, Bitmap bitmap){
        cache.put(id, new SoftReference<Bitmap>(bitmap));
    }

    public void clear() {
        cache.clear();
    }
}
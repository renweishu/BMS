package com.nttdata.chongqing.bms.newbookonline.util;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nttdata.chongqing.bms.R;
import com.nttdata.chongqing.bms.main.entity.Book;

/**
 * @author chao11.ma
 *
 */
public class LazyAdapter extends BaseAdapter {
    
    private Activity activity;
    private ArrayList<Book> data;
    private static LayoutInflater inflater=null;
    public ImageLoader imageLoader; //用来下载图片的类，后面有介绍
    
    public LazyAdapter(Activity a, ArrayList<Book> d) {
        activity = a;
        data=d;
         // 对于一个没有被载入或者想要动态载入的界面, 都需要使用inflate来载入. 
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader=new ImageLoader(activity.getApplicationContext());
    }

    public int getCount() {
        return data.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }
    
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        if(convertView==null)
        	// 使用inflate方法来载入layout的xml
            vi = inflater.inflate(R.layout.bookonline_list_item, null);

        TextView title = (TextView)vi.findViewById(R.id.title); // 标题
        TextView information = (TextView)vi.findViewById(R.id.information); // 简介
        ImageView thumb_image=(ImageView)vi.findViewById(R.id.list_image); // 缩略图
        
        Book book = new Book();
        book = data.get(position);
        
        // 设置ListView的相关值
        title.setText(book.getBookName());
        information.setText(book.getBookInfo());
        String url = book.getThumbUrl();
        imageLoader.DisplayImage(url, thumb_image);
		
        return vi;
    }
     
}

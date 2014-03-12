package com.nttdata.chongqing.bms.main.activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.nttdata.chongqing.bms.R;
import com.nttdata.chongqing.bms.browsebooks.activity.BrowseBooksActivity;
import com.nttdata.chongqing.bms.downloadpage.service.DownloadReceiver;
import com.nttdata.chongqing.bms.downloadpage.util.DownloadProgressListener;
import com.nttdata.chongqing.bms.downloadpage.util.FileDownloader;
import com.nttdata.chongqing.bms.downloadpage.util.ZipUtil;
import com.nttdata.chongqing.bms.main.entity.Book;
import com.nttdata.chongqing.bms.main.util.DataHelper;
import com.nttdata.chongqing.bms.main.util.ViewHolder;
import com.nttdata.chongqing.bms.newbookonline.activity.DataSource;
import com.nttdata.chongqing.bms.newbookonline.activity.NewBookOnlineActivity;
import com.nttdata.chongqing.bms.newbookonline.entity.DownLoadEntity;
import com.nttdata.chongqing.bms.newbookonline.util.DeleLocalFile;
import com.nttdata.chongqing.bms.newbookonline.util.ImageLoader;
import com.nttdata.chongqing.bms.update.tool.UpdateManager;

public class MainActivity extends Activity implements OnClickListener{

	// 页面ListView
	private ListView listView;
	// ListView适配器
	private ListViewAdapter listViewAdapter;
	// 处理后的用于显示在画面上的最终书本一览List
	private ArrayList<Book> bookList4ListView = new ArrayList<Book>();
	// 入力文本资源
	private List<Map<String, Object>> inputText;
	// 入力图片资源
	private String [] inputImage;
	// 网上新书menu
	protected final static int MENU_NEWBOOKONLINE = Menu.FIRST;
	// 获取更新menu
	protected final static int MENU_UPDATEBOOKS = Menu.FIRST + 1;
	// 更新版本menu
	protected final static int MENU_UPDATEVERSION = Menu.FIRST + 2;
	// 无删除索引（没有需要删除的书）
	private final static int NO_BOOK_DELETE = -1;
	// 操作是“初始化”
	private final static int MODE_INIT = 1;
	// 操作是“从服务器获取最新并更新”
	private final static int MODE_UPDATE = 2;
	// 操作是“删除本地书本后并更新”
	private final static int MODE_DELETE = 3;
	// 下载状态是“已下载”
	private final static String DOWNLOADED = "1";
	// 服务器XML文件路径
//	private static String BOOKLIST_FROM_SERVER = "http://192.168.1.101:8080/BMS_SERVER/file/contains/books_server.xml";
	private static String BOOKLIST_FROM_SERVER = "http://172.23.9.124:8080/BMS_SERVER/file/contains/books_server.xml";
	// 本地XML存放路径，在"安卓手机/data/data/com.nttdata.chongqing.bms/files/BOOKLIST_FROM_LOCAL"
	private static String BOOKLIST_FROM_LOCAL = "books_local.xml";
	// 顶部广告图片资源位置
	private static String AD = "http://172.23.9.124:8080/BMS_SERVER/file/ad/image_ad1.png";

	/*==== chao11.ma 关于大文件的下载==============================================================================*/
	// 图片异步加载的对象
	public  ImageLoader imageLoader; 
	// 关于下载弹出框视图的各种元素
	private  View downloadView;
	private ImageView bookpageshowImage;
	private TextView download_title;
	private TextView download_information;
	private Button download_button;
	/*2014-03-04 chao11.ma*/
	// 监听下载进度
	Intent intent;
	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				//要通知的notification中进度条的id
				int notifityId=msg.arg1;
				int size = msg.getData().getInt("size");
				// 当前的下载对象
				DownLoadEntity downloadentity = bookList4ListView.get(notifityId).getDownLoadEntity();
				// 下载进度条
				ProgressBar download_prssbar =downloadentity.getProgressbar();
				// 下载文字框
				TextView download_result =downloadentity.getTextview();
				// 当前文件的网上下载路径
				String download_path=downloadentity.getDownloadPath();
				// 当前文件的下载保存路径
				String download_save_path=downloadentity.getDownloadSavePath();
				// 当前文件的下载保存名字
				String download_save_file=downloadentity.getDownloadSaveFile();
				// 当前下载对象 
				Book bookdowmload_item=downloadentity.getBookdowmloadItem();

				download_prssbar.setProgress(size);
				// 下载的进度条
				float result = 0f;
				int p=0;
				result = (float)download_prssbar.getProgress()/ (float)download_prssbar.getMax();
				p = (int)(result*100);
				// 主页面上的第一个进度条
				download_prssbar.setProgress(size);
				download_result.setText("下载中："+p+"%");

				intent.putExtra("pro", p);
				intent.putExtra("id", notifityId);
				MainActivity.this.sendBroadcast(intent);
				if(p==100){
					Toast.makeText(MainActivity.this, msg.getData().getString("fileName")+"下载完成！", 1).show();
					// 改变下载按钮的文字
					download_button.setVisibility(View.VISIBLE);
					download_button.setText("去阅读");
					download_prssbar.setVisibility(View.INVISIBLE);
					download_result.setVisibility(View.INVISIBLE);

					try {
						// 解压下载的zip包
						ZipUtil.unzip(download_save_file, download_save_path);
						// 删除下载压缩包					
						ZipUtil.deleteZip(download_save_file);
						// 往本地xml写入下载文件的信息
						String flodername=download_path.substring(download_path.lastIndexOf('/') + 1,download_path.lastIndexOf("."));

						//找到本地xml否包含当前下载ID更新状态
						for(Book localbook:bookList4ListView) {
							if (bookdowmload_item.getBookId().equals(localbook.getBookId())) {
								//直接追加
								// 更新本地存放目录
								localbook.setSaveFileUrl(download_save_path+"/"+flodername);
								// 把下载状态更新为已下载1
								localbook.setDownloadStatus("1");	
							}
						}
						DataHelper.save2LocalXML(bookList4ListView, getApplicationContext(), "books_local.xml");
						// 下载完成后 标记为下载完成
						downloadentity.setDownIngFlg("2");
						//TODO:下载完成后刷新杂志封面灰色状态为高亮
						// 若直接调用notifyDataSetChanged方法，则会造成两个文件同时下载的时候，一个文件下载完后，画面卡死，等全部文件下载完成后，才会刷新页面
//						listViewAdapter.notifyDataSetChanged();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				break;
			case -1:
				Toast.makeText(MainActivity.this, R.string.error, 1).show();
				break;
			}
		}    	
	};
	/*==== chao11.ma 关于大文件的下载==============================================================================*/

	/**
	 * 主页面初始化
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		intent=new Intent(this,DownloadReceiver.class);
		// 下载图片用
		imageLoader=new ImageLoader(this.getApplicationContext());

		// 获取主页面的ListView控件
		listView = (ListView) findViewById(R.id.listView);

		// 初始化广告View并添加到ListView的页眉
		View adView = adInit();
		listView.addHeaderView(adView);

		// 初始化ListView
		listViewInit();
	}

	@Override  
	/**
	 * 顶部菜单栏
	 * */
	public boolean onCreateOptionsMenu(Menu menu) {   

		super.onCreateOptionsMenu(menu);   
		menu.add(Menu.NONE, MENU_NEWBOOKONLINE, 0, "网上新书");
		menu.add(Menu.NONE, MENU_UPDATEBOOKS, 1, "获取最新");
		menu.add(Menu.NONE, MENU_UPDATEVERSION, 2, "更新版本");
		return true;   

	}

	/**
	 * 主页面右上角菜单栏
	 */
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		case MENU_NEWBOOKONLINE:

			// 前移往网上新书页面
			Toast.makeText(this, "前移往网上新书页面", Toast.LENGTH_SHORT).show();
			Intent intent = new Intent(this, NewBookOnlineActivity.class);
			startActivity(intent);

			break;
		case MENU_UPDATEBOOKS:

			// 获取服务器端最新书本一览并刷新页面
			listViewUpdate(NO_BOOK_DELETE, MODE_UPDATE);
			break;
		case MENU_UPDATEVERSION:

			// 前往更新版本
			//			Toast.makeText(this, "前往更新版本", Toast.LENGTH_SHORT).show();
			//			Intent intent2 = new Intent(this, UpdateVesionActivity.class);
			//			startActivity(intent2);	
			UpdateManager manager = new UpdateManager(this);
			// 检查软件更新
			manager.checkUpdate();
		}
		return true;
	}

	/**
	 * 主页面点击事件
	 */
	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		//TODO:点击广告图片发生的事件
		case R.id.adImageView:
			Toast.makeText(this, "你点击了广告", Toast.LENGTH_SHORT).show();
			break;
		}
	}

	/**
	 * 初始化ListView
	 * @param mode 数据更新模式
	 */
	private void listViewInit() {

		// 创建适配器，与ListView绑定
		listViewAdapter = new ListViewAdapter(this);

		// 更新数据
		dataUpdate(NO_BOOK_DELETE, MODE_INIT);

		// 将包含最新数据的适配器添加到ListView
		listView.setAdapter(listViewAdapter);

		// 让ListView每一行为一个单位的item不能获取焦点，焦点由子控件（每一行中的每一个小格）获取
		listView.setItemsCanFocus(false);
	}

	/**
	 * 顶部广告初始化
	 * @return 广告的View
	 */
	private View adInit() {

		// 获取广告View的XML布局文件
		View adView = LayoutInflater.from(MainActivity.this).inflate(R.layout.ad_view, null);

		// 获取布局内的图片控件
		ImageView adImageView = (ImageView) adView.findViewById(R.id.adImageView);

		// 设置广告图片资源
		ImageLoader imageLoader = new ImageLoader(this);
		imageLoader.DisplayImage(AD, adImageView);

		// 广告图片控件点击事件
		adImageView.setOnClickListener(this);
		return adView;
	}

	/**
	 * 刷新ListView
	 * @param listIndex
	 * @param mode
	 */
	private void listViewUpdate(int listIndex, int mode) {
		
		// 如果有正在下载的杂志，则不操作
		Boolean isDownLoad = false;
		for(Book book:bookList4ListView) {
			if(book.getDownLoadEntity().getDownIngFlg().equals("1")) {
				isDownLoad = true;
				break;
			}
		}
		if(!isDownLoad) {
			// 更新数据
			dataUpdate(listIndex, mode);
			// 动态刷新ListView，而不是刷新整个页面
			listViewAdapter.notifyDataSetChanged();
		} else {
			Toast.makeText(MainActivity.this, "请等待所有杂志下载完成", Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 数据更新
	 * @param listIndex 该数组索引只有书本删除时才会用到
	 * @param mode 判断是更新还是删除书本
	 */
	private void dataUpdate(int listIndex, int mode) {

		// 服务器书本一览List
		ArrayList<Book> bookListServer = new ArrayList<Book>();
		// 本地书本一览List
		ArrayList<Book> bookListLocal = new ArrayList<Book>();

		// 读取本地一览XML
		try {
			FileInputStream fileInputStream = openFileInput(BOOKLIST_FROM_LOCAL);
			bookListLocal = DataHelper.inputstream2List(fileInputStream);
		} catch (FileNotFoundException e) {
			bookListLocal = new ArrayList<Book>();
		}

		switch (mode) {
		case MODE_INIT:
			bookList4ListView = bookListLocal;
			break;

		case MODE_UPDATE:

			// 读取服务器端一览XML
			bookListServer = DataSource.onlineBookInfo(BOOKLIST_FROM_SERVER);

			// bookList表示与服务器端差分后的书本信息
			bookList4ListView = DataHelper.listDifference(bookListServer, bookListLocal);
			break;

		case MODE_DELETE:
			DeleLocalFile deleLocalFile = new DeleLocalFile(this);
			if(DOWNLOADED.equals(bookListLocal.get(listIndex).getDownloadStatus())) {
				// 删除bookListLocal中索引为listIndex的书的本地已下载封面文件和已下载书本文件
				deleLocalFile.deleteThumbUrl(bookListLocal.get(listIndex).getThumbUrl());
			} else {
				// 删除bookListLocal中索引为listIndex的书的本地已下载封面文件
				deleLocalFile.deleteThumbUrl(bookListLocal.get(listIndex).getThumbUrl());
				deleLocalFile.deleteSaveFileUrl(bookListLocal.get(listIndex).getSaveFileUrl());
			}
			bookListLocal.remove(listIndex);
			bookList4ListView = bookListLocal;
			break;
		}

		// 将bookList存到本地XML中(安卓手机/data/data/com.nttdata.chongqing.bms/files/books_local.xml)
		DataHelper.save2LocalXML(bookList4ListView, this, BOOKLIST_FROM_LOCAL);

		// 文本资源入力
		textInput();

		// 图片资源入力
		imageInput();
	}

	/**
	 * 文本资源入力
	 * @param bookList 入力书本List
	 */
	private void textInput() {
		inputText = new ArrayList<Map<String, Object>>();
		Map<String, Object> map;
		for(int i = 0 ;i < bookList4ListView.size()/3; i++){
			map = new HashMap<String, Object>();
			map.put("textView1", "《"+bookList4ListView.get(i*3).getBookName()+"》");
			map.put("textView2", "《"+bookList4ListView.get(i*3+1).getBookName()+"》");
			map.put("textView3", "《"+bookList4ListView.get(i*3+2).getBookName()+"》");
			inputText.add(map);
		}

		if(bookList4ListView.size()%3 == 1) {
			map = new HashMap<String, Object>();
			map.put("textView1", "《"+bookList4ListView.get(bookList4ListView.size()-1).getBookName()+"》");
			map.put("textView2", "");
			map.put("textView3", "");
			inputText.add(map);
		} else if (bookList4ListView.size()%3 == 2) {
			map = new HashMap<String, Object>();
			map.put("textView1", "《"+bookList4ListView.get(bookList4ListView.size()-2).getBookName()+"》");
			map.put("textView2", "《"+bookList4ListView.get(bookList4ListView.size()-1).getBookName()+"》");
			map.put("textView3", "");
			inputText.add(map);
		}
	}

	/**
	 * 图片资源入力
	 * @param inputImage 入力图片资源
	 */
	private void imageInput() {

		// 读取图片封面路径，显示到ImageView上
		int listSize = bookList4ListView.size() + (3-bookList4ListView.size()%3);
		inputImage = new String[listSize];
		for(int i = 0; i < bookList4ListView.size(); i++) {
			inputImage[i] = bookList4ListView.get(i).getThumbUrl();
		}
		for(int i = bookList4ListView.size(); i < listSize; i++) {
			inputImage[bookList4ListView.size()] = "none";
		}
	}

	/**
	 * 自定义适配器，绘制ListView时触发事件，绑定数据到控件
	 * @author yuchen.wei
	 *
	 */
	public class ListViewAdapter extends BaseAdapter {

		// 主程序界面句柄
		private Context context;

		public ListViewAdapter(Context context) {
			this.context = context;
		}

		@Override
		public int getCount() {
			return inputText.size();
		}

		@Override
		public Object getItem(int arg0) {
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		/**
		 * ListView在加载每一行时调用的绘制该行View的方法
		 */
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {

			//获取每一行的item布局文件
			convertView = LayoutInflater.from(context).inflate(R.layout.list_item_view, null);
			ViewHolder viewHolder = new ViewHolder(convertView);

			// 将每一个控件显示出来
			// 不等于最后一行时
			if (position != bookList4ListView.size()/3) {
				viewHolder.setVisibility(1);
				viewHolder.setVisibility(2);
				viewHolder.setVisibility(3);
			} else {

				//绘制最后一行时根据书本剩余个数绘制
				for( int i = 1; i <= bookList4ListView.size()%3; i++) {
					viewHolder.setVisibility(i);
				}
			}

			//将入力数据与控件绑定
			bindData(viewHolder, inputImage, position, 
					viewHolder.imageView1, viewHolder.textView1, viewHolder.button1, viewHolder.linearLayout1,
					viewHolder.download_prssbar1, viewHolder.download_result1,
					position*3, "textView1");

			bindData(viewHolder, inputImage, position, 
					viewHolder.imageView2, viewHolder.textView2, viewHolder.button2, viewHolder.linearLayout2,
					viewHolder.download_prssbar2, viewHolder.download_result2,
					position*3+1, "textView2");

			bindData(viewHolder, inputImage, position, 
					viewHolder.imageView3, viewHolder.textView3, viewHolder.button3, viewHolder.linearLayout3,
					viewHolder.download_prssbar3, viewHolder.download_result3,
					position*3+2, "textView3");

			return convertView;
		}

		/**
		 * 将控件与入力数据绑定
		 * @param holder 控件holder
		 * @param imageInput 图片资源入力
		 * @param position ListView每一行的行标,表明这是第几行的数据
		 * @param imageView 自定义格子xml布局文件中的imageView
		 * @param textView 自定义格子xml布局文件中的textView
		 * @param button 自定义格子xml布局文件中的button
		 * @param listIndex 数组索引
		 * @param text 文本资源
		 */
		private void bindData(ViewHolder holder, String [] imageUrl, final int position, 
				ImageView imageView, TextView textView, ImageButton button, LinearLayout linearLayout, 
				ProgressBar download_prssbar, TextView download_result,
				final int listIndex, final String text) {

			if(listIndex < bookList4ListView.size() ) {
				
				/*==== chao11.ma 关于大文件的下载==============================================================================*/
				// 当前点击的Item对象的进度条和文字条
				bookList4ListView.get(listIndex).getDownLoadEntity().setProgressbar(download_prssbar);
				bookList4ListView.get(listIndex).getDownLoadEntity().setTextview(download_result);
				/*==== chao11.ma 关于大文件的下载==============================================================================*/
				
				// 根据下载状态显示图片是否高亮
				if(DOWNLOADED.equals(bookList4ListView.get(listIndex).getDownloadStatus())) {
					linearLayout.setBackgroundColor(00000000);
				}
			}

			// 绑定图片资源
			ImageLoader imageLoader = new ImageLoader(context);
			imageLoader.DisplayImage(imageUrl[listIndex], imageView);

			// 绑定文本资源
			textView.setText((String) inputText.get(position).get(text));
			
			// 绑定图片点击事件
			imageView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					gridClick(v, listIndex);
				}
			});

			// 绑定按钮点击事件
			button.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					gridClick(v, listIndex);
				}
			});
		}

		/**
		 * 格子中图片和按钮的点击事件
		 * @param listIndex 精确定位用数组索引
		 * @param text Debug用
		 */
		private void gridClick(View v, final int listIndex) {
			switch (v.getId()) {
			case R.id.listGridImageView1:
			case R.id.listGridImageView2:
			case R.id.listGridImageView3:
				// 点击杂志封面，判断是弹出下载框还是直接去阅读
				popupOrRead(listIndex);
				break;
			case R.id.listGridImageButton1:
			case R.id.listGridImageButton2:
			case R.id.listGridImageButton3:
				// 点击按钮，弹出菜单
				showMenu(v, listIndex);
				break;
			}
		}

		/**
		 * 显示菜单
		 * @param listIndex 精确定位用数组索引
		 */
		private void showMenu(View v, int listIndex) {

			AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
			View menuView = LayoutInflater.from(context).inflate(R.layout.list_item_view_menu, null);
			bindMenuClick(menuView, listIndex, alertDialog);
			alertDialog.setView(menuView,0,0,0,0);
			alertDialog.show();

			Window window = alertDialog.getWindow();

			// 弹出菜单位于屏幕中央
			window.setGravity(Gravity.CENTER);

			// 弹出菜单的宽高
			WindowManager.LayoutParams layoutParams = window.getAttributes();
			layoutParams.width = DataHelper.dip2px(context, 180);
			layoutParams.height = DataHelper.dip2px(context, 100);

			// 弹出菜单后背景不变黑，0.0f表示完全不变黑，1.0f表示完全变黑
			layoutParams.dimAmount = 0.0f;

			window.setAttributes(layoutParams);
		}

		/**
		 * 菜单点击事件
		 * @param menuView
		 * @param listIndex
		 * @param alertDialog
		 */
		private void bindMenuClick(View menuView, final int listIndex, final AlertDialog alertDialog) {

			ImageButton menuButton1 = (ImageButton) menuView.findViewById(R.id.gridMenuButton1);
			menuButton1.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					// 前往索引为listIndex的书的阅读画面还是下载该书
					popupOrRead(listIndex);
					alertDialog.dismiss();
				}
			});

			ImageButton menuButton2 = (ImageButton) menuView.findViewById(R.id.gridMenuButton2);
			menuButton2.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					// 删除索引为listIndex的书
					listViewUpdate(listIndex, MODE_DELETE);
					alertDialog.dismiss();
				}
			});

			ImageButton menuButton3 = (ImageButton) menuView.findViewById(R.id.gridMenuButton3);
			menuButton3.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					// 返回
					alertDialog.dismiss();
				}
			});
		}
	}

	/*==== chao11.ma 关于大文件的下载==============================================================================*/
	/*
	 * 动态的插入 弹出框的视图
	 */
	protected Builder myBuilder(MainActivity dialogWindows) {

		final LayoutInflater inflater=this.getLayoutInflater();
		AlertDialog.Builder builder=new AlertDialog.Builder(dialogWindows);
		// 动态的插入 弹出框的视图
		downloadView=inflater.inflate(R.layout.downloadpop, null);
		bookpageshowImage=(ImageView)downloadView.findViewById(R.id.download_book);
		download_title = (TextView)downloadView.findViewById(R.id.download_title);
		download_information = (TextView)downloadView.findViewById(R.id.info_tv_right);
		download_button = (Button) downloadView.findViewById(R.id.download_button);
		return builder.setView(downloadView);
	}

	/**
	 * 对于UI控件的更新只能由主线程(UI线程)负责，如果在非UI线程更新UI控件，更新的结果不会反映在屏幕上，某些控件还会出错
	 * @author chao11.ma
	 * @param path 文件下载路径
	 * @param dir  手机本地保存目录
	 * @param softid 下载的进程ID
	 */
	private void download(final String path, final File dir,final int softid){
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					FileDownloader loader = new FileDownloader(MainActivity.this, path, dir, 1,softid);
					final FileDownloader loader2=loader;
					int length = loader.getFileSize();//获取文件的长度

					// 设置当前下载进度的最多刻度
					DownLoadEntity downloadentity= bookList4ListView.get(softid).getDownLoadEntity();
					downloadentity.getProgressbar().setMax(length);

					// listener 监听下载数量的变化,如果不需要了解实时下载的数量,可以设置为null
					loader.download(new DownloadProgressListener(){
						@Override
						// 由于在FileDownloader download的方法里已经获得了下载进度
						public void onDownloadSize(int size) {//可以实时得到文件下载的长度
							Message msg = new Message();
							// 在这儿获得下载文件的ID
							msg.what = 1;
							msg.arg1=loader2.notifityid;
							msg.getData().putInt("size", size);
							msg.getData().putString("file", loader2.getFileName());
							handler.sendMessage(msg);
						}});
				} catch (Exception e) {
					Message msg = new Message();
					msg.what = -1;
					msg.getData().putString("error", "下载失败");
					handler.sendMessage(msg);
				}
			}
		}).start();

	}
	/*==== chao11.ma 关于大文件的下载==============================================================================*/

	private void popupOrRead(final int listIndex) {
		/*==== chao11.ma 关于大文件的下载==============================================================================*/
		// 通过进程ID 判断当前对象是否正在下载，如正在下载，就不做任何操作
		// 进程ID
		final int softid = listIndex;
		// 1 代表下载中的状态
		if(bookList4ListView.get(softid).getDownLoadEntity().getDownIngFlg().equals("1")){
			Toast.makeText(MainActivity.this, "你点击的对象已经在下载了", Toast.LENGTH_SHORT).show();
		} else {

			DownLoadEntity downloadentity = bookList4ListView.get(listIndex).getDownLoadEntity();

			Book bookdowmload_item = bookList4ListView.get(listIndex);
			String download_path = bookdowmload_item.getSaveFileUrl();

			downloadentity.setBookdowmloadItem(bookdowmload_item);
			downloadentity.setDownloadPath(download_path);

			// 判断是否去下载
			boolean gotoDownloadFlg=true;
			// /storage/sdcard/BMSdownload/2014-02-21
			String path = bookdowmload_item.getSaveFileUrl();
			if (bookdowmload_item.getDownloadStatus().equals("1")) {
				Intent int1 = new Intent();
				// /storage/sdcard/BMSdownload/
				int1.putExtra("path", path.substring(0, path.lastIndexOf('/')));
				// 2014-02-21
				int1.putExtra("saveFolder", path.substring(path.lastIndexOf('/') + 1));
				// 判断是从哪个页面前移过去的
				int1.putExtra("page", "1");
				int1.setClass(MainActivity.this, BrowseBooksActivity.class);
				startActivity(int1);
				gotoDownloadFlg= false;
			}if (gotoDownloadFlg) {
				// 如果本地没有下载就开始下载
				// 2.本地不存在的情况，从服务端取地址下载 进行下面的操作
				// 获取弹出dialog框
				Builder builder=myBuilder(MainActivity.this);
				final AlertDialog dialog=builder.show();  
				//点击对话框外部取消对话框显示 的效果消失 false
				dialog.setCanceledOnTouchOutside(false);
				// 书的封面视图 的信息展示在下载页面上
				download_title.setText(bookdowmload_item.getBookName());
				download_information.setText(bookdowmload_item.getBookInfo());
				imageLoader.DisplayImage(bookdowmload_item.getThumbUrl(), bookpageshowImage);

				// 监听下载按钮的事件
				download_button.setOnClickListener(new OnClickListener() {		
					@Override
					public void onClick(View downloadView) {
						dialog.dismiss();
						//每次取最后一个追加进来的对象
						DownLoadEntity downloadentity =bookList4ListView.get(listIndex).getDownLoadEntity();
						String download_path = downloadentity.getDownloadPath();
						downloadentity.getProgressbar().setVisibility(View.VISIBLE);
						downloadentity.getTextview().setVisibility(View.VISIBLE);
						// 标记为下载中
						downloadentity.setDownIngFlg("1");
						// 进程ID
						int softid = listIndex;

						// 判断内存卡是否存在 如果不存在就写在手机内存里
						if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
							File dir = Environment.getExternalStorageDirectory();//文件保存目录
							// 下载的文件名
							String filename = download_path.substring(download_path.lastIndexOf('/') + 1);
							String download_save_path=dir.toString()+"/BMSdownload";
							String download_save_file =dir.toString()+"/BMSdownload/"+filename;
							downloadentity.setDownloadSaveFile(download_save_file);
							downloadentity.setDownloadSavePath(download_save_path);
							download(download_path, dir,softid);
						}else{
							// 内存卡不存在的情况下 获取/data/data/.../files目录
							Context context= getApplicationContext();
							// 取得程序安装路径下files
							File dir = context.getFilesDir();
							// 下载的文件名
							String filename = download_path.substring(download_path.lastIndexOf('/') + 1);
							String download_save_path=dir.toString()+"/BMSdownload";
							String download_save_file=dir.toString()+"/BMSdownload/"+filename;
							downloadentity.setDownloadSaveFile(download_save_file);
							downloadentity.setDownloadSavePath(download_save_path);
							download(download_path, dir,softid);
						}
					}
				});

				/*
				 * 让dialog 消失			
				 */
				ImageButton customviewtvimgCancel=(ImageButton)downloadView.findViewById(R.id.downloadBackHome);
				customviewtvimgCancel.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						// 如果没有点击下载按钮的情况下  clickList移除最新添加的那个对象
						// clickHashMap.remove(listIndex);
						dialog.dismiss();
					}
				});
			}
		}
		/*==== chao11.ma 关于大文件的下载==============================================================================*/
	}
}
package com.nttdata.chongqing.bms.newbookonline.activity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.nttdata.chongqing.bms.R;
import com.nttdata.chongqing.bms.browsebooks.activity.BrowseBooksActivity;
import com.nttdata.chongqing.bms.downloadpage.service.DownloadReceiver;
import com.nttdata.chongqing.bms.downloadpage.util.DownloadProgressListener;
import com.nttdata.chongqing.bms.downloadpage.util.FileDownloader;
import com.nttdata.chongqing.bms.downloadpage.util.ZipUtil;
import com.nttdata.chongqing.bms.main.activity.MainActivity;
import com.nttdata.chongqing.bms.main.entity.Book;
import com.nttdata.chongqing.bms.main.util.DataHelper;
import com.nttdata.chongqing.bms.newbookonline.entity.DownLoadEntity;
import com.nttdata.chongqing.bms.newbookonline.util.BookOnlineListView;
import com.nttdata.chongqing.bms.newbookonline.util.BookOnlineListView.OnRefreshListener;
import com.nttdata.chongqing.bms.newbookonline.util.ImageLoader;
import com.nttdata.chongqing.bms.newbookonline.util.LazyAdapter;

/**
 * @author chao11.ma
 * @version 1.0
 */
public class NewBookOnlineActivity extends Activity {

	// 自定义的listview
	BookOnlineListView listView;
	// 自定义的适配器
	private  LazyAdapter lazyadapter;
	// 图片异步加载的对象
	public  ImageLoader imageLoader; 
	// 服务器端的url地址
	public static String server_url = "http://172.23.9.124:8080/BMS_SERVER/file/contains/books_server.xml";

	/* 筛选与排行*/
	private static final String[] sorts={"排行","按时间最新排列","按时间最早排行","按热度排行","按下载量排行"}; 
	private static final String[] screenings={"筛选","男士包","女士包","女士鞋","男士鞋"};
	private Spinner spinner; 
	private Spinner spinner1; 
	private ArrayAdapter<String> sortsadapter;
	private ArrayAdapter<String> screeningsadapter;

	// 关于下载弹出框视图的各种元素
	private  View downloadView;
	private ImageView bookpageshowImage;
	private TextView download_title;
	private TextView download_information;
	private Button download_button;

	// 服务端存放图书信息list
	public static ArrayList<Book> bookList;
	// 手机本地已下载图书存放信息的list
	public static ArrayList<Book> localBookList;

	// invisible：不显示，但空出格子
	public static int invisible;
	// visible：正常显示
	public static int visible;
	// gone：不显示，且不空出格子
	public static int gone;

	/* Integer：线程ID  DownLoadEntity：对应一个下载对象*/
	private static  HashMap<Integer,DownLoadEntity> clickHashMap = new HashMap<Integer,DownLoadEntity>();

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
				DownLoadEntity downloadentity = clickHashMap.get(notifityId);
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
				NewBookOnlineActivity.this.sendBroadcast(intent);
				if(p==100){
					// 改变下载按钮的文字
					download_prssbar.setVisibility(gone);
					download_result.setText("下载完成");
					try {
						// 解压下载的zip包
						ZipUtil.unzip(download_save_file, download_save_path);
						// 删除下载压缩包					
						ZipUtil.deleteZip(download_save_file);
						// 往本地xml写入下载文件的信息
						String flodername=download_path.substring(download_path.lastIndexOf('/') + 1,download_path.lastIndexOf("."));

						//判断本地xml是否包含当前下载ID，如果有，则更新状态，如果没有，则直接追加
						ArrayList<String> listID = new ArrayList<String>();
						for(Book localbook:localBookList) {
							listID.add(localbook.getBookId());
							if (bookdowmload_item.getBookId().equals(localbook.getBookId())) {
								//直接追加
								// 更新本地存放目录
								localbook.setSaveFileUrl(download_save_path+"/"+flodername);
								// 把下载状态更新为已下载1
								localbook.setDownloadStatus("1");	
							}
						}
						if (!listID.contains(bookdowmload_item.getBookId())) {
							//直接追加
							// 更新本地存放目录
							bookdowmload_item.setSaveFileUrl(download_save_path+"/"+flodername);
							// 把下载状态更新为已下载1
							bookdowmload_item.setDownloadStatus("1");
							localBookList.add(bookdowmload_item);
						} 

						DataHelper.save2LocalXML(localBookList, getApplicationContext(), "books_local.xml");
						// 下载完成后 标记为下载完成
						downloadentity.setDownIngFlg("2");
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				break;
			case -1:
				Toast.makeText(NewBookOnlineActivity.this, R.string.error, Toast.LENGTH_SHORT).show();
				break;
			}
		}    	
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_newbookonline);
		// 监听后台下载进度的
		intent=new Intent(this,DownloadReceiver.class);
		// 下载图片用
		imageLoader=new ImageLoader(this.getApplicationContext());

		// 获取上方的actionbar;让这个icon可以点击。
		ActionBar actionBar = this.getActionBar();
		// 让这个icon可以点击显示不带左箭头
		actionBar.setHomeButtonEnabled(true); 

		// 从服务端取的图书列表
		bookList=DataSource.onlineBookInfo(server_url);
		//从本地　取得图书列表
		String local_url = getApplicationContext().getFilesDir().toString()+"/books_local.xml";
		localBookList =DataSource.localBookInfo(local_url);		

		listView = (BookOnlineListView) findViewById(R.id.listView);
		// 展示的下拉列表
		getData();

		spinner=(Spinner)findViewById(R.id.spinner); 
		spinner1=(Spinner)findViewById(R.id.spinner1);
		Toast.makeText(this, "欢迎进入!", Toast.LENGTH_SHORT).show();

		//将可选内容与ArrayAdapter连接 
		sortsadapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,sorts);
		screeningsadapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,screenings);
		//设置下拉列表风格 
		sortsadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); 
		screeningsadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); 
		//将adapter添加到spinner中 
		spinner.setAdapter(sortsadapter); 
		spinner1.setAdapter(screeningsadapter); 
		//添加Spinner事件监听 
		spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){ 
			@Override 
			public void onItemSelected(AdapterView<?> arg0, View arg1,int arg2, long arg3) { 
				// 拿来做判断用的
				String selectStr = sorts[arg2];
				if (!selectStr.equals("排行")) {
					// 动态删除list里的一条数据
					bookList.remove(0);
					getData();
					arg0.setVisibility(View.VISIBLE); 
				}
			} 

			@Override 
			public void onNothingSelected(AdapterView<?> arg0) { 
				// TODO Auto-generated method stub 
			} 
		}); 

		spinner1.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){ 
			@Override 
			public void onItemSelected(AdapterView<?> arg0, View arg1,int arg2, long arg3) { 
				// 拿来做判断用的
				String selectStr = screenings[arg2];
				if (!selectStr.equals("筛选")) {
					// 动态删除list里的一条数据
					bookList.remove(0);
					getData();
					arg0.setVisibility(View.VISIBLE); 
				} 
			} 

			@Override 
			public void onNothingSelected(AdapterView<?> arg0) { 
				// TODO Auto-generated method stub 
			} 
		}); 
	}

	/*
	 *重写菜单方法
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/**
	 * 主页菜单栏
	 */
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		// 这个AcionBar item 的ID为：android.R.id.home。可以用这个id来获取左上角点击动作:
		case android.R.id.home:

			// 前移往网上新书页面
			Toast.makeText(this, "返回到主界面", Toast.LENGTH_SHORT).show();
			Intent intent = new Intent(this, MainActivity.class);
			startActivity(intent);
			// NewBookOnlineActivity.this.finish();

			break;

		}
		return true;
	} 



	/**
	 * listview显示数据
	 * */
	public void getData() {
		// 
		lazyadapter = new LazyAdapter(this, bookList);
		listView.setAdapter(lazyadapter);
		// 调用MyListView中自定义接口
		listView.setonRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {

				new AsyncTask<Void, Void, Void>() {

					// 运行在UI线程中，在调用doInBackground()之前执行
					protected void onPostExecute(Void result) {
						lazyadapter.notifyDataSetChanged();
						listView.onRefreshComplete();
					}

					// 后台运行的方法，可以运行非UI线程，可以执行耗时的方法
					protected Void doInBackground(Void... params) {
						try {
							Thread.sleep(1000);
						} catch (Exception e) {
							e.printStackTrace();
						}
						// 每次刷新添加新的数据
						DataSource.addRefresh(bookList);
						return null;
					}

				}.execute();

			}
		});

		// 为单一列表行绑定单击事件
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// invisible：不显示，但空出格子
				invisible =View.INVISIBLE;
				// visible：正常显示
				visible=View.VISIBLE;
				// gone：不显示，且不空出格子
				gone=View.GONE;
				// 获取点击的书本信息
				Book bookdowmload_item = bookList.get(position-1);
				// 取每一本书的ID 作为当前下载任务的线程ID
				final int softid = Integer.parseInt(bookdowmload_item.getBookId());
				// 在存放下载任务的的map集合中通过线程ID判断是否有对应的下载任务
				if (!clickHashMap.containsKey(softid)){
					// 如果当前线程ID对应的下载任务不存在 ，就初始化一个下载任务
					DownLoadEntity downloadentity= new DownLoadEntity();
					// 获取当前点击的item对应的下载进度条 ProgressBar
					ProgressBar download_prssbar = (ProgressBar) view.findViewById(R.id.download_prssbar); 
					// 获取当前点击的item对应的反映进度的TextView
					TextView download_result=(TextView) view.findViewById(R.id.download_result);
					// 获取当前下载文件的URL地址
					String download_path = bookdowmload_item.getSaveFileUrl();
					// 把上述取得属性 赋值给下载任务实体类
					downloadentity.setProgressbar(download_prssbar);
					downloadentity.setTextview(download_result);
					downloadentity.setDownloadPath(download_path);
					downloadentity.setBookdowmloadItem(bookdowmload_item);
					// 追加当前点击的下载任务 到任务集合里
					clickHashMap.put(softid, downloadentity);
				}
				// 通过进程ID 判断当前下载任务是否正在下载（当前下载对象下载状态（未下载：0  下载中：1  已下载 2）），如正在下载，就不做任何操作
				if(clickHashMap.get(softid).getDownIngFlg().equals("1")){
					Toast.makeText(NewBookOnlineActivity.this, "你点击的对象已经在下载了", Toast.LENGTH_SHORT).show();
				} else {
					// 如果当前下载任务不是处于正在下载中状态便做如下操作
					// 首先获取该书的ID，然后在程序安装目录中获取books_local.xml文件中判断是否存在这个ID
					// 如果本地xml存在当前ID，在根据 downloadStatus 下载状态判断   1代表已经下载 直接跳转到阅读页面 0代表未下载，开始下载
					boolean gotoDownloadFlg=true;
					// 遍历本地图书list
					for(Book book:localBookList) {
						// /storage/sdcard/BMSdownload/2014-02-21
						String path = book.getSaveFileUrl();
						if (book.getBookId().equals(bookdowmload_item.getBookId()) && book.getDownloadStatus().equals("1")) {
							Intent int1 = new Intent();
							// /storage/sdcard/BMSdownload/
							int1.putExtra("path", path.substring(0, path.lastIndexOf('/')));
							// 2014-02-21
							int1.putExtra("saveFolder", path.substring(path.lastIndexOf('/') + 1));
							// 判断是从哪个页面前移过去的
							int1.putExtra("page", "2");
							int1.setClass(NewBookOnlineActivity.this, BrowseBooksActivity.class);
							startActivity(int1);
							gotoDownloadFlg= false;
							break;
						}
					}
					if (gotoDownloadFlg) {
						// 如果当前点击的书本本地没有下载就开始从服务端下载   首先弹出一个对话框
						Builder builder=myBuilder(NewBookOnlineActivity.this);
						final AlertDialog dialog=builder.show();  
						//点击对话框外部取消对话框显示 的效果消失 false
						dialog.setCanceledOnTouchOutside(false);
						// 设置对话框上的书名
						download_title.setText(bookdowmload_item.getBookName());
						// 设置对话框上的内容简介
						download_information.setText(bookdowmload_item.getBookInfo());
						// 设置对话框上的图书封面
						imageLoader.DisplayImage(bookdowmload_item.getThumbUrl(), bookpageshowImage);
						// 监听对话框上下载按钮的点击事件
						download_button.setOnClickListener(new OnClickListener() {		
							@Override
							public void onClick(View downloadView) {
								// 当点击下载按钮的时候 对话框消失
								dialog.dismiss();
								//从下载任务map集合中或者当前的下载任务对象
								DownLoadEntity downloadentity= clickHashMap.get(softid);
								// 显示当前下载任务的进度条
								downloadentity.getProgressbar().setVisibility(visible);
								// 显示当前下载任务的完成进度
								downloadentity.getTextview().setVisibility(visible);
								// 取得当前下载任务的下载地址
								String download_path= downloadentity.getDownloadPath();
								// 把当前下载任务标记为下载中的状态
								downloadentity.setDownIngFlg("1");
								// 判断SD卡是否存在 如果不存在就写在手机内存里
								if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
									File dir = Environment.getExternalStorageDirectory();//文件保存目录
									// 下载的文件名
									String filename = download_path.substring(download_path.lastIndexOf('/') + 1);
									String download_save_path=dir.toString()+"/BMSdownload";
									String download_save_file =dir.toString()+"/BMSdownload/"+filename;
									downloadentity.setDownloadSaveFile(download_save_file);
									downloadentity.setDownloadSavePath(download_save_path);
									// 开始下载任务
									download(download_path, dir,softid);
								}else{
									// SD卡不存在的情况下 获取手机内存/data/data/.../files目录
									Context context= getApplicationContext();
									// 取得程序安装路径下files
									File dir = context.getFilesDir();
									// 下载的文件名
									String filename = download_path.substring(download_path.lastIndexOf('/') + 1);
									String download_save_path=dir.toString()+"/BMSdownload";
									String download_save_file=dir.toString()+"/BMSdownload/"+filename;
									downloadentity.setDownloadSaveFile(download_save_file);
									downloadentity.setDownloadSavePath(download_save_path);
									// 开始下载任务
									download(download_path, dir,softid);
								}

							}
						});

						/*
						 * 监听对话框的取消按钮		
						 */
						ImageButton customviewtvimgCancel=(ImageButton)downloadView.findViewById(R.id.downloadBackHome);
						customviewtvimgCancel.setOnClickListener(new OnClickListener() {
							public void onClick(View v) {
								// 如果没有点击下载按钮的情况下，而是取消的情况下  就从任务集合里移除当前准备好的下载任务
								clickHashMap.remove(softid);
								dialog.dismiss();
							}
						});

					}
				}
			}
		});

	}


	/*
	 * 动态的插入 弹出框的视图
	 */
	protected Builder myBuilder(NewBookOnlineActivity dialogWindows) {

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
	 * @param softid 下载的线程ID
	 */
	private void download(final String path, final File dir,final int softid){
		// 在每一次调用下载的时候，都会单独新建一个下载线程
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					// 初始化一个文件下载下载器
					FileDownloader loader = new FileDownloader(NewBookOnlineActivity.this, path, dir, 1,softid);
					final FileDownloader loader2=loader;
					// 获取文件的大小
					int length = loader.getFileSize();
					// 获取当前下载任务
					DownLoadEntity downloadentity= clickHashMap.get(softid);
					// 设置当前下载任务进度条的最大刻度
					downloadentity.getProgressbar().setMax(length);

					// 调用下载，同时监听当前下载任务的下载进度
					loader.download(new DownloadProgressListener(){
						@Override
						// 由于在FileDownloader download的方法里已经获得了下载进度
						public void onDownloadSize(int size) {//可以实时得到文件下载的长度
							Message msg = new Message();
							// 在这儿获得下载文件的ID
							msg.what = 1;
							msg.arg1=loader2.notifityid;
							msg.getData().putInt("size", size);
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
}

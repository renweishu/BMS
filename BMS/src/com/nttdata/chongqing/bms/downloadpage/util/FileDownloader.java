package com.nttdata.chongqing.bms.downloadpage.util;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.nttdata.chongqing.bms.downloadpage.service.FileService;

import android.content.Context;
import android.util.Log;
/**
 * 文件下载器
 * @author chao11.ma
 */
public class FileDownloader {
	private static final String TAG = "FileDownloader";
	private Context context;
	private FileService fileService;
	public int notifityid;
	/* 已下载文件长度 */
	private int downloadSize = 0;
	/* 原始文件长度 */
	private int fileSize = 0;
	/*文件名*/
	private String fileName = "";
	/* 线程数 */
	private DownloadThread[] threads;
	/* 本地保存文件 */
	private File saveFile;
	/* 缓存各线程下载的长度*/ //ConcurrentHashMap允许多个修改操作并发进行
	private Map<Integer, Integer> data = new ConcurrentHashMap<Integer, Integer>();
	/* 每条线程下载的长度 */
	private int block;
	/* 下载路径  */
	private String downloadUrl;
	/**
	 * 获取线程数
	 */
	public int getThreadSize() {
		return threads.length;
	}
	/**
	 * 获取文件大小
	 * @return
	 */
	public int getFileSize() {
		return fileSize;
	}
	/**
	 * 获取文件名
	 * @return
	 */
	public String getFileName() {
		return fileName;
	}
	/**
	 * 累计已下载大小
	 * @param size
	 */
	protected synchronized void append(int size) {
		downloadSize += size;
	}
	/**
	 * 更新指定线程最后下载的位置
	 * @param threadId 线程id
	 * @param pos 最后下载的位置
	 */
	protected void update(int threadId, int pos) {
		this.data.put(threadId, pos);
	}
	/**
	 * 保存记录文件
	 */
	protected synchronized void saveLogFile() {
		this.fileService.update(this.downloadUrl, this.data);
	}
	/**
	 * 构建文件下载器
	 * @param downloadUrl 下载路径
	 * @param fileSaveDir SD卡的根目录 /手机内存目录
	 * @param threadNum 下载线程数 固定值  3
	 * @param notifityid 下载的文件id
	 */
	public FileDownloader(Context context, String downloadUrl, File rootfileSaveDir, int threadNum,int notifityid) {
		try {
			this.context = context;
			this.downloadUrl = downloadUrl;
			this.notifityid=notifityid;
			fileService = new FileService(this.context);
			URL url = new URL(this.downloadUrl);
			// 文件保存目录 sd/machao
			File fileSaveDir = new File (rootfileSaveDir,"/BMSdownload");

			if(!fileSaveDir.exists()) fileSaveDir.mkdirs();
			// 创建一个 包含三个线程的线程数组
			this.threads = new DownloadThread[threadNum];					
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5*1000);
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "image/gif, image/jpeg, image/pjpeg, image/pjpeg, application/x-shockwave-flash, application/xaml+xml, application/vnd.ms-xpsdocument, application/x-ms-xbap, application/x-ms-application, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, */*");
			conn.setRequestProperty("Accept-Language", "zh-CN");
			conn.setRequestProperty("Referer", downloadUrl); 
			conn.setRequestProperty("Charset", "UTF-8");
			conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.04506.30; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.connect();
			printResponseHeader(conn);
			if (conn.getResponseCode()==200) {
				this.fileSize = conn.getContentLength();//根据响应获取文件大小
				if (this.fileSize <= 0) throw new RuntimeException("Unkown file size ");
				// 获取要下载的文件名
				fileName = getFileName(conn);
				this.saveFile = new File(fileSaveDir, fileName);/* 保存文件 */
				//从本地sqlite数据库中  获取每条线程已经下载的文件长度  线程ID 和下载的进度
				Map<Integer, Integer> logdata = fileService.getData(downloadUrl);
				if(logdata.size()>0){
					for(Map.Entry<Integer, Integer> entry : logdata.entrySet())
						// 缓存各线程下载的长度  把它放在ConcurrentHashMap这个线程安全的map中
						data.put(entry.getKey(), entry.getValue());
				}
				// 这儿 分别定下 每条线程下载的文件长度 
				this.block = (this.fileSize % this.threads.length)==0? this.fileSize / this.threads.length : this.fileSize / this.threads.length + 1;
				// 缓存各线程的线程数 
				if(this.data.size()==this.threads.length){
					for (int i = 0; i < this.threads.length; i++) {
						this.downloadSize += this.data.get(i+1);
					}
					print("已经下载的长度"+ this.downloadSize);
				}				
			}else{
				throw new RuntimeException("server no response ");
			}
		} catch (Exception e) {
			print(e.toString());
			throw new RuntimeException("don't connection this url");
		}
	}
	/**
	 * 获取文件名
	 */
	private String getFileName(HttpURLConnection conn) {
		String filename = this.downloadUrl.substring(this.downloadUrl.lastIndexOf('/') + 1);
		if(filename==null || "".equals(filename.trim())){//如果获取不到文件名称
			for (int i = 0;; i++) {
				String mine = conn.getHeaderField(i);
				if (mine == null) break;
				// Content-disposition是MIME协议的扩展，由于多方面的安全性考虑没有被标准化，所以可能某些浏览器不支持，比如说IE4.01
				if("content-disposition".equals(conn.getHeaderFieldKey(i).toLowerCase())){
					Matcher m = Pattern.compile(".*filename=(.*)").matcher(mine.toLowerCase());
					if(m.find()) return m.group(1);
				}
			}
			// 全局唯一标识符,是指在一台机器上生成的数字，它保证对在同一时空中的所有机器都是唯一的
			filename = UUID.randomUUID()+ ".tmp";//默认取一个文件名
		}
		return filename;
	}

	/**
	 *  开始下载文件
	 * @param listener 监听下载数量的变化,如果不需要了解实时下载的数量,可以设置为null
	 * @return 已下载文件大小
	 * @throws Exception
	 */
	public int download(DownloadProgressListener listener) throws Exception{
		try {
			// 实现文件的多线程下载，即多线程下载一个文件时，将文件分成几块，每块用不同的线程进行下载。
			RandomAccessFile randOut = new RandomAccessFile(this.saveFile, "rw");
			if(this.fileSize>0) randOut.setLength(this.fileSize);
			randOut.close();
			URL url = new URL(this.downloadUrl);
			if(this.data.size() != this.threads.length){
				this.data.clear();
				for (int i = 0; i < this.threads.length; i++) {
					this.data.put(i+1, 0);
				}
			}
			for (int i = 0; i < this.threads.length; i++) {
				int downLength = this.data.get(i+1);
				if(downLength < this.block && this.downloadSize<this.fileSize){ //该线程未完成下载时,继续下载						
					this.threads[i] = new DownloadThread(this, url, this.saveFile, this.block, this.data.get(i+1), i+1);
					this.threads[i].setPriority(7);
					this.threads[i].start();
				}else{
					this.threads[i] = null;
				}
			}
			this.fileService.save(this.downloadUrl, this.data);
			boolean notFinish = true;//下载未完成
			while (notFinish) {// 循环判断是否下载完毕
				Thread.sleep(900);
				notFinish = false;//假定下载完成
				for (int i = 0; i < this.threads.length; i++){
					if (this.threads[i] != null && !this.threads[i].isFinish()) {
						notFinish = true;//下载没有完成
						if(this.threads[i].getDownLength() == -1){//如果下载失败,再重新下载
							this.threads[i] = new DownloadThread(this, url, this.saveFile, this.block, this.data.get(i+1), i+1);
							this.threads[i].setPriority(7);
							this.threads[i].start();
						}
					}
				}	
				// 这儿把下载进度 给予 监听器
				if(listener!=null) listener.onDownloadSize(this.downloadSize);
			}
			fileService.delete(this.downloadUrl);
		} catch (Exception e) {
			print(e.toString());
			throw new Exception("file download fail");
		}
		return this.downloadSize;
	}
	/**
	 * 获取Http响应头字段
	 * @param http
	 * @return
	 */
	public static Map<String, String> getHttpResponseHeader(HttpURLConnection http) {
		Map<String, String> header = new LinkedHashMap<String, String>();
		for (int i = 0;; i++) {
			String mine = http.getHeaderField(i);
			if (mine == null) break;
			header.put(http.getHeaderFieldKey(i), mine);
		}
		return header;
	}
	/**
	 * 打印Http头字段
	 * @param http
	 */
	public static void printResponseHeader(HttpURLConnection http){
		Map<String, String> header = getHttpResponseHeader(http);
		for(Map.Entry<String, String> entry : header.entrySet()){
			String key = entry.getKey()!=null ? entry.getKey()+ ":" : "";
			print(key+ entry.getValue());
		}
	}

	private static void print(String msg){
		Log.i(TAG, msg);
	}

	public static void main(String[] args) {

	}

}

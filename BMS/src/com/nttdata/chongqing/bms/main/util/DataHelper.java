package com.nttdata.chongqing.bms.main.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.util.ArrayList;

import org.xmlpull.v1.XmlSerializer;

import android.content.Context;
import android.util.Xml;

import com.nttdata.chongqing.bms.main.entity.Book;

public class DataHelper {

	/**
	 * 将对象数组串行化写成XML文件并保存
	 * 
	 * @param bookList
	 *            入力对象数组
	 */
	public static void save2LocalXML(ArrayList<Book> bookList, Context context,
			String savePath) {
		XmlSerializer xmlSerializer = Xml.newSerializer();
		StringWriter stringWriter = new StringWriter();
		try {
			xmlSerializer.setOutput(stringWriter);

			// <?xml version=”1.0″ encoding=”UTF-8″ standalone=”yes”?>
			xmlSerializer.startDocument("UTF-8", true);

			// <books>
			xmlSerializer.startTag("", "books");
			for (int i = 0; i < bookList.size(); i++) {

				// <book id="X">
				xmlSerializer.startTag("", "book");
				xmlSerializer.attribute("", "id",
						String.valueOf(bookList.get(i).getBookId()));

				// <bookId>X</bookId>
				xmlSerializer.startTag("", "bookId");
				xmlSerializer.text(String.valueOf(bookList.get(i).getBookId()));
				xmlSerializer.endTag("", "bookId");

				// <bookName>bookNameX</bookName>
				xmlSerializer.startTag("", "bookName");
				xmlSerializer.text(bookList.get(i).getBookName());
				xmlSerializer.endTag("", "bookName");

				// <bookInfo>bookInfoX</bookInfo>
				xmlSerializer.startTag("", "bookInfo");
				xmlSerializer.text(bookList.get(i).getBookInfo());
				xmlSerializer.endTag("", "bookInfo");

				// <thumbUrl>thumbUrlX</thumbUrl>
				xmlSerializer.startTag("", "thumbUrl");
				xmlSerializer.text(bookList.get(i).getThumbUrl());
				xmlSerializer.endTag("", "thumbUrl");

				// <saveFileUrl>saveFileUrlX</saveFileUrl>
				xmlSerializer.startTag("", "saveFileUrl");
				xmlSerializer.text(bookList.get(i).getSaveFileUrl());
				xmlSerializer.endTag("", "saveFileUrl");

				// <downloadStatus>Z</downloadStatus>
				xmlSerializer.startTag("", "downloadStatus");
				xmlSerializer.text(bookList.get(i).getDownloadStatus());
				xmlSerializer.endTag("", "downloadStatus");

				// </book>
				xmlSerializer.endTag("", "book");
			}

			// <books>
			xmlSerializer.endTag("", "books");
			xmlSerializer.endDocument();

		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		// 将串行化的stringWriter写入本地XML文件BOOKLIST_FROM_LOCAL，如果文件不存在，会自动创建
		try {
			OutputStream outputStream = context.openFileOutput(savePath,
					Context.MODE_PRIVATE);
			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
					outputStream);
			outputStreamWriter.write(stringWriter.toString());
			outputStreamWriter.close();
			outputStream.close();
		} catch (IOException e) {
			// TODO:IO异常
		}
	}

	/**
	 * 解析XML文件为Bean
	 * 
	 * @param inputStream
	 *            含XML路径的输入流
	 * @return 解析XML后生成的Bean数组
	 */
	public static ArrayList<Book> inputstream2List(InputStream inputStream) {
		XMLReaderByDOM reader = new XMLReaderByDOM();
		reader.read(inputStream);
		ArrayList<Book> bookList = reader.getList();
		return bookList;
	}

	/**
	 * 对两个List做差分
	 * 
	 * @param bookListServer
	 * @param bookListLocal
	 * @return
	 */
	public static ArrayList<Book> listDifference(
			ArrayList<Book> bookListServer, ArrayList<Book> bookListLocal) {

		// 如果本地为空，就直接返回服务器一览
		if (bookListLocal == null || bookListLocal.size() == 0) {
			return bookListServer;
		} else {

			for (int i = 0; i < bookListServer.size(); i++) {

				// 如果本地没有，但是服务器端有，就把这本书加到本地
				if (!bookListLocal.contains(bookListServer.get(i))) {
					bookListLocal.add(bookListServer.get(i));
				}
			}
			return bookListLocal;
		}
	}

	/**
	 * px与dp转换
	 * @param context 当前窗口句柄
	 * @param dpValue 入力dp数值
	 * @return 出力px数值
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}
}

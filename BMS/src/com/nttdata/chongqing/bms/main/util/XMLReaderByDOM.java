package com.nttdata.chongqing.bms.main.util;

import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.nttdata.chongqing.bms.main.entity.Book;

public class XMLReaderByDOM {
	
	private static String ROOT_TAG = "book";
	private static String CHILD_TAG1 = "bookId";
	private static String CHILD_TAG2 = "bookName";
	private static String CHILD_TAG3 = "bookInfo";
	private static String CHILD_TAG4 = "thumbUrl";
	private static String CHILD_TAG5 = "saveFileUrl";
	private static String CHILD_TAG6="downloadStatus";
	
	private Book book = null;
	private ArrayList<Book> bookList = new ArrayList<Book>();
	
	public ArrayList<Book> getList() {
		return bookList;
	}

	public void read(InputStream inputStream) {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(inputStream);
			
			// 获取根节点
			Element root = document.getDocumentElement();
			parse(root);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 分析XML文件各个标签结构和标签之间的内容
	 * @param element XML文件的根节点
	 */
	private void parse(Element element) {
		NodeList nodelist = element.getChildNodes();
		int size = nodelist.getLength();
		for (int i = 0; i < size; i++) {
			
			// 获取特定位置的node
			Node node = (Node) nodelist.item(i);
			
			/* getNodeName获取tagName，例如<book>thinking in android</book>这个Element的getNodeName返回book
			 * getNodeType返回当前节点的确切类型，如Element、Attr、Text等
			 * getNodeValue 返回节点内容，如果当前为Text节点，则返回文本内容；否则会返回null
			 * getTextContent 返回当前节点以及其子代节点的文本字符串，这些字符串会拼成一个字符串给用户返回。例如
			 * 对<book><name>thinking in android</name><price>12.23</price></book>调用此方法，则会返回“thinking in android12.23”
			 */
			String tagName = node.getNodeName();
			if (tagName.equals(ROOT_TAG) && node.getNodeType() == Document.ELEMENT_NODE) {
				book = new Book();
				if (node.getNodeType() == Document.ELEMENT_NODE) {
					parse((Element) node);
				}
				bookList.add(book);
			}
			if (tagName.equals(CHILD_TAG1)) {
				book.setBookId(node.getTextContent());
			}
			if (tagName.equals(CHILD_TAG2)) {
				book.setBookName(node.getTextContent());
			}
			if (tagName.equals(CHILD_TAG3)) {
				book.setBookInfo(node.getTextContent());
			}
			if (tagName.equals(CHILD_TAG4)) {
				book.setThumbUrl(node.getTextContent());
			}
			if (tagName.equals(CHILD_TAG5)) {
				book.setSaveFileUrl(node.getTextContent());
			}
			if (tagName.equals(CHILD_TAG6)) {
				book.setDownloadStatus(node.getTextContent());
			}
		}
	}
}

package com.nttdata.chongqing.bms.browsebooks.util;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.nttdata.chongqing.bms.browsebooks.entity.Magazine;

public class XmlContentHandler extends DefaultHandler {

	// 定义一个杂志类引用
	private Magazine magazine;
	// 此处将XML里的数据封装成Magazine类，magazineList用于装解析后的数据 
	private List<Magazine> magazineList;
	private StringBuffer buffer = new StringBuffer();
	
	public List<Magazine> getMagazineList() {
		return magazineList;
	}

	@Override
	public void startDocument() throws SAXException {
		magazineList = new ArrayList<Magazine>();
	}
	
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
	    if (localName.equals("picture")) {
	    	magazine = new Magazine();
	    }
	    super.startElement(uri, localName, qName, attributes);
	}
	 
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		buffer.append(ch, start, length);
		super.characters(ch, start, length);
	}
	
	 @Override
	 public void endElement(String uri, String localName, String qName) throws SAXException {

		 if (localName.equals("picture")) {
			 magazineList.add(magazine);
		 } else if (localName.equals("name")) {
			 magazine.setName(buffer.toString().trim());
			 buffer.setLength(0);
		 }
		 super.endElement(uri, localName, qName);
	 }
	 
	 @Override
	 public void endDocument() throws SAXException {
		 super.endDocument();
	 }
}

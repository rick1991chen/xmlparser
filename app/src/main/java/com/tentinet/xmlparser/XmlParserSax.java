package com.tentinet.xmlparser;

import android.util.Xml;

import org.w3c.dom.Document;
import org.xml.sax.Parser;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * @author rick
 * @Description 使用SAX方式解析xml文档
 * @date 2015/11/23
 * @Copyright: Copyright (c)${year} Shenzhen Tentinet Technology Co., Ltd. Inc. All rights reserved.
 */
public class XmlParserSax {
    public static void readFromSource(InputStream inputStream){
        DocumentBuilderFactory documentBuilderFactory= DocumentBuilderFactory.newInstance();

        try {
            DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();




            Document doc = builder.parse(inputStream);



        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public static void writeToFile(){

    }

}

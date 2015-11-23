package com.tentinet.xmlparser;

import android.util.Log;
import android.util.Xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParser;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;

/**
 * @author rick
 * @Description 使用dom方式解析xml文档
 * @date 2015/11/23
 * @Copyright: Copyright (c)${year} Shenzhen Tentinet Technology Co., Ltd. Inc. All rights reserved.
 */
public class XmlParserDom {
    /**
     * 用来存放解析结果的list
     */
    private static List sList = new ArrayList<>();
    /**
     * 用于打印log日志的tag
     */
    private static final String TAG = XmlParserDom.class.getSimpleName();

    /**
     * 静态方法完成xml文件的解析
     *
     * @version 1.0
     * @createTime 2015/11/23  9:51
     * @updateTime 2015/11/23  9:51
     * @createAuthor 陈思齐
     * @updateAuthor
     * @updateInfo
     */
    public static List<Map> readFromSource(InputStream is) {
        //获取文档创建者工厂
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();

        Element root = null;
        try {
        //获取文档生成的创建者
            DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();

            //通过创建者读取整个文档返回文档对象
            Document document = builder.parse(is);

            //解析文档对象,获取根节点
            root = document.getDocumentElement();

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (root == null) { //空文件,返回null
            return null;
        }

        //根节点下,一级子节点集合
        NodeList firstNodes = root.getChildNodes();

        if (firstNodes == null) { //空根节点,返回一个元素个数为0的list集合
            return sList;
        }
        Log.e(TAG, "从这里开始填充list集合");
        Map firstMap = new HashMap<>();
        //遍历一级子节点,每次循环填充一个list集合或者map集合
        for (int i = 0; i < firstNodes.getLength(); i++) {
            //获取一级子节点对象
            Node nodeFirst = firstNodes.item(i);
            if (nodeFirst != null && nodeFirst.getNodeType() == Node.ELEMENT_NODE) { //node 不为空 并且节点类型为元素节点

                if(nodeFirst.hasAttributes()){
                //判断是否有"属性"存在,存在即在这个节点信息保存后,紧接保存一个以"_$attrs"结尾的字符串作为键,属性作为map的

                }

                if(nodeFirst.getChildNodes().getLength() > 1) {//判断该节点是不是有子节点
                    String nodeName = nodeFirst.getNodeName();
                    //大于1说明是个有多个子节点的父节点,创建用于存放多个KV键值对map的list
                    List firstList = new ArrayList();
                    Map secondMap = new HashMap();
                    NodeList secondNodes = nodeFirst.getChildNodes();

                    //遍历二级子节点
                    for(int x = 0;x<secondNodes.getLength();x++){
                        Node nodeSecond = secondNodes.item(x);






                        firstList.add(); //一级list装map
                    }
                    firstMap.put(nodeName,firstList);//map的泛型为<String,List>
                } else {
                    //说明是单独的子标签,取出nodeName和nodeTextContent作为键值对存入map返回
                    String nodeName = nodeFirst.getNodeName();
                    String nodeText = nodeFirst.getTextContent();

                    firstMap.put(nodeName,nodeText);//map的泛型为<String,String>
                }
            }

            sList.add(firstMap);
        }
        Log.e(TAG, "list集合填充完毕");
        return sList;
    }
}



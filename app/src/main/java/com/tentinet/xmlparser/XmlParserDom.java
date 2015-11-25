package com.tentinet.xmlparser;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * @author rick
 * @Description 使用dom方式解析xml文档
 * @date 2015/11/23
 * @Copyright: Copyright (c)${year} Shenzhen Tentinet Technology Co., Ltd. Inc. All rights reserved.
 */
public class XmlParserDom {
    /**
     * 用来存放解析结果的Map
     */
    private static Map<String,Map> rootMap = new HashMap<>();

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
    public static Map<String,Map> readFromSource(InputStream is) {
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

        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }

        if (root == null) { //空文件,返回null
            return null;
        }

        Map<String,Map> primaryNodesMap = new HashMap<>();

        //根节点下,一级子节点集合
        NodeList primaryNodes = root.getChildNodes();
        rootMap.put(root.getNodeName(), primaryNodesMap); //root

        if (primaryNodes == null) { //空根节点,返回一个元素个数为0的map集合
            return rootMap;
        }

 //       System.out.println("primaryNodes.getLength(): "+primaryNodes.getLength());
        //遍历一级子节点,每次循环填充一个list集合或者map集合
        for (int i = 0; i < primaryNodes.getLength(); i++) {
            Node primaryNode = primaryNodes.item(i);            //获取一级子节点

            if (primaryNode != null && primaryNode.getNodeType() == Node.ELEMENT_NODE) { //node 不为空 元素节点
                //获取节点名 student
                String primaryNodeName = primaryNode.getNodeName();

                if (primaryNode.getChildNodes().getLength() > 1) { //有子节点

                    Map <String,Object> secondaryNodesMap = new HashMap<>();
                    NodeList secondaryNodes = primaryNode.getChildNodes();

                    for(int j = 0;j<secondaryNodes.getLength();j++) {
                        Node secondaryNode = secondaryNodes.item(j);

                        if(secondaryNode != null && secondaryNode.getNodeType() == Node.ELEMENT_NODE){
                            //獲取二級節點的名字
                            String secondaryNodeName = secondaryNode.getNodeName();

                            if(secondaryNode.getChildNodes().getLength() > 1){ //有子節點

                                Map<String,String> thirdlyNodesMap = new HashMap<>();
                                NodeList thirdlyNodes = secondaryNode.getChildNodes();

                                for(int k = 0;k<thirdlyNodes.getLength();k++){
                                    Node thirdlyNode = thirdlyNodes.item(k);

                                    if(thirdlyNode != null && thirdlyNode.getNodeType() == Node.ELEMENT_NODE){
                                        String thirdlyNodeName = thirdlyNode.getNodeName();
                                        String thirdlyNodeText = thirdlyNode.getTextContent();
                                        thirdlyNodesMap.put(thirdlyNodeName,thirdlyNodeText);
                                    }
                                }
                                secondaryNodesMap.put(secondaryNodeName,thirdlyNodesMap);
                            } else { //無三級子節點
                                //獲取二級節點中的文本內容
                                String secondaryNodeText = secondaryNode.getTextContent();

                                secondaryNodesMap.put(secondaryNodeName, secondaryNodeText);
                            }

                            //二級節點的屬性添加
                            if (secondaryNode.hasAttributes()) {
                                Map<String, String> secondaryNodeAttrMap = new HashMap<>();
                                //判断是否有"属性"存在,存在即在这个节点信息保存后,紧接保存一个以"_$attrs"结尾的字符串作为键,值为map的键值对

                                NamedNodeMap attributes = primaryNode.getAttributes();
                                for (int x = 0; x < attributes.getLength(); x++) {
                                    secondaryNodeAttrMap.put(attributes.item(x).getNodeName(), attributes.item(x).getTextContent());
                                }
                                //保存属性,以key中独特的结尾确定
                                secondaryNodesMap.put(secondaryNodeName + "_$attrs", secondaryNodeAttrMap); //map 泛型为<String,map>
                            }
                        }
                    }

                    primaryNodesMap.put(primaryNodeName, secondaryNodesMap);
                } else {
                    //肯定有子節點
                }
                //解析完成一个节点后,判断该节点有没有属性,有则添加
                if (primaryNode.hasAttributes()) {
                    Map<String, String> primaryNodeAttrMap = new HashMap<>();
                    //判断是否有"属性"存在,存在即在这个节点信息保存后,紧接保存一个以"_$attrs"结尾的字符串作为键,值为map的键值对

                    NamedNodeMap attributes = primaryNode.getAttributes();
                    for (int x = 0; x < attributes.getLength(); x++) {
                        primaryNodeAttrMap.put(attributes.item(x).getNodeName(), attributes.item(x).getTextContent());
                    }
                    //保存属性,以key中独特的结尾确定
                    primaryNodesMap.put(primaryNodeName + "_$attrs", primaryNodeAttrMap); //map 泛型为<String,map>
                }

            }
        }
        return rootMap;
    }


    /**
     * 将一个装载XML文件的集合中的数据导出为XML文件
     *
     * @version 1.0
     * @createTime 2015/11/24  11:24
     * @updateTime 2015/11/24  11:24
     * @createAuthor 陈思齐
     * @updateAuthor
     * @updateInfo
     *
     */
    public static File writeXMLToFile(List list){

        return null;
    }



}



package com.tentinet.xmlparser;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * @author rick
 * @Description 使用dom方式解析xml文档, 并暴露公共方法获取数据
 * @date 2015/11/23
 * @Copyright: Copyright (c)2015 Shenzhen Tentinet Technology Co., Ltd. Inc. All rights reserved.
 */
public class XmlParserDom {
    /**
     * 用来存放解析结果的Map
     */
    private static Map<String, Map> rootMap = new LinkedHashMap<>();

    /**
     * xml文件的解析
     *
     * @param XMLStr xml文件字符串
     * @return 包含了整个文档信息的Map集合
     *
     * @version 1.0
     * @createTime 2015/11/23  9:51
     * @updateTime 2015/11/23  9:51
     * @createAuthor 陈思齐
     * @updateAuthor
     * @updateInfo
     */
    public static Map<String, Map> resolveXMLFromSource(String XMLStr) {

        StringReader sr = new StringReader(XMLStr);
        InputSource is = new InputSource(sr);

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

        Map<String, Object> primaryNodesMap = new LinkedHashMap<>();

        //根节点下,一级子节点集合
        NodeList primaryNodes = root.getChildNodes();
        // rootMap.put(root.getNodeName(), primaryNodesMap); //root

        if (primaryNodes == null) { //空根节点,返回一个元素个数为0的map集合
            return rootMap;
        }

        //遍历一级子节点,每次循环填充一个map集合
        for (int i = 0; i < primaryNodes.getLength(); i++) {
            Node primaryNode = primaryNodes.item(i);

            if (primaryNode != null && primaryNode.getNodeType() == Node.ELEMENT_NODE) { //node 不为空 元素节点
                //获取节点名 student
                String primaryNodeName = primaryNode.getNodeName();

                boolean isPrimaryNodeHasAttr = primaryNode.hasAttributes();

                if (primaryNode.getChildNodes().getLength() > 1) { //有子节点

                    Map<String, Object> secondaryNodesMap = new LinkedHashMap<>();
                    NodeList secondaryNodes = primaryNode.getChildNodes();

                    /**在添加二级子节点前添加一级子节点的属性*/
                    if (isPrimaryNodeHasAttr) { //在添加子节点集合前添加属性
                        addAttrMap(secondaryNodesMap, primaryNode, primaryNodeName);
                    }

                    for (int j = 0; j < secondaryNodes.getLength(); j++) {
                        Node secondaryNode = secondaryNodes.item(j);

                        if (secondaryNode != null && secondaryNode.getNodeType() == Node.ELEMENT_NODE) {
                            //獲取二級節點的名字
                            String secondaryNodeName = secondaryNode.getNodeName();
                            boolean isSecondaryNodeHasAttr = secondaryNode.hasAttributes();

                            if (secondaryNode.getChildNodes().getLength() > 1) { //有子節點

                                Map<String, String> thirdlyNodesMap = new LinkedHashMap<>();
                                NodeList thirdlyNodes = secondaryNode.getChildNodes();

                                /**在添加三级子节点前添加二级子节点的属性*/
                                if (isSecondaryNodeHasAttr) {
                                    addAttrMap(thirdlyNodesMap, secondaryNode, secondaryNodeName);
                                }

                                //循环保存简单节点
                                for (int k = 0; k < thirdlyNodes.getLength(); k++) {
                                    Node thirdlyNode = thirdlyNodes.item(k);

                                    if (thirdlyNode != null && thirdlyNode.getNodeType() == Node.ELEMENT_NODE) {

                                        String thirdlyNodeName = thirdlyNode.getNodeName();
                                        String thirdlyNodeText = thirdlyNode.getTextContent();
                                        thirdlyNodesMap.put(thirdlyNodeName, thirdlyNodeText);
                                    }
                                }
                                secondaryNodesMap.put(secondaryNodeName, thirdlyNodesMap);
                            } else { //無三級子節點
                                //獲取二級節點中的文本內容
                                String secondaryNodeText = secondaryNode.getTextContent();

                                secondaryNodesMap.put(secondaryNodeName, secondaryNodeText);
                            }

                        }
                        primaryNodesMap.put(primaryNodeName, secondaryNodesMap);
                    }

                } else { //没有子节点

                    String textContent = primaryNode.getTextContent();
                    primaryNodesMap.put(primaryNodeName, textContent);
                }
            }
            rootMap.put(root.getNodeName(), primaryNodesMap);
        }
        return rootMap;
    }

    /**
     * 封装的方法,如果节点有属性,添加进子节点集合,以节点名 + "_$attrs"作为键,属性封装的map集合作为值
     *
     * @param nodesMap 当前节点的子节点集合
     * @param node     当前节点
     * @param nodeName 当前节点名
     * @version 1.0
     * @createTime 2015/11/25  10:48
     * @updateTime 2015/11/25  10:48
     * @createAuthor 陈思齐
     * @updateAuthor
     * @updateInfo
     */
    private static void addAttrMap(Map nodesMap, Node node, String nodeName) {

        Map<String, String> nodeAttrMap = new LinkedHashMap<>();

        NamedNodeMap attributes = node.getAttributes();

        for (int x = 0; x < attributes.getLength(); x++) {
            nodeAttrMap.put(attributes.item(x).getNodeName(), attributes.item(x).getTextContent());
        }
        //保存属性,以key中独特的结尾确定
        nodesMap.put(nodeName + "_$attrs", nodeAttrMap); //map 泛型为<String,Map>
    }


    /**
     * 将一个装载XML文件信息的Map导出为String,适配根节点下,一级子节点有属性或者为简单节点
     * 或者带有二级子节点,无属性,为简单节点
     *
     * @param map 封装了xml数据的map
     * @return XML文件中的字符串
     * @version 1.0
     * @createTime 2015/11/24  11:24
     * @updateTime 2015/11/24  11:24
     * @createAuthor 陈思齐
     * @updateAuthor
     * @updateInfo
     */
    public static String XMLToString(Map map) {

        //获取用来拼接xml文件的StringBuffer
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>"); //文件头

        if (map == null) {
            throw new NullPointerException("传入的数据源为空");
        }

        Iterator rootIt = map.entrySet().iterator();
        //第一轮遍历,获取根节点名称及根节点
        while (rootIt.hasNext()) {
            Map.Entry rootEntry = (Map.Entry) rootIt.next();

            String rootName = (String) rootEntry.getKey();
            Map primaryNodesMap = (Map) rootEntry.getValue();

            stringBuffer.append("<" + rootName + ">");

            Iterator primaryNodesIter = primaryNodesMap.entrySet().iterator();
            //遍历primaryMap,value为子节点集合
            while (primaryNodesIter.hasNext()) {
                Map.Entry primaryEntry = (Map.Entry) primaryNodesIter.next();

                String primaryNodeName = (String) primaryEntry.getKey();

                stringBuffer.append("<" + primaryNodeName);

                Object object = primaryEntry.getValue();

                //判断值的类型:1.Map 其中是子节点 2.String 其中包含的是当前节点文本
                if (object instanceof Map) {
                    Map secondaryMap = (Map) object;
                    Iterator secondaryIter = secondaryMap.entrySet().iterator();

                    while (secondaryIter.hasNext()) { //从map key值的结尾判断该map是否存放属性
                        Map.Entry secondaryEntry = (Map.Entry) secondaryIter.next();
                        String secondaryNodeName = (String) secondaryEntry.getKey();
                        boolean isAttribute = secondaryNodeName.endsWith("_$attrs");

                        if (isAttribute) { //添加属性
                            appendAttrsToStringBuffer(stringBuffer, (Map) secondaryEntry.getValue());
                        } else {
                            break;
                        }
                    }
                    stringBuffer.append(">");

                    while (secondaryIter.hasNext()) { //从map key值的结尾判断该map是否存放属性
                        Map.Entry secondaryEntry = (Map.Entry) secondaryIter.next();
                        String secondaryNodeName = (String) secondaryEntry.getKey();
                        boolean isAttribute = secondaryNodeName.endsWith("_$attrs");
                        if (isAttribute) {
                            continue;
                        }

                        if (!isAttribute) { //添加普通节点
                            String secondaryNodeText = (String) secondaryEntry.getValue();
                            stringBuffer.append("<" + secondaryNodeName + ">" + secondaryNodeText + "</" + secondaryNodeName + ">");
                        }
                    }
                } else { //primary节点是基本节点

                    stringBuffer.append(">");

                    String textContent = (String) object;
                    stringBuffer.append(textContent);

                }
                stringBuffer.append("</" + primaryNodeName + ">");
            }
            stringBuffer.append("</" + rootName + ">");

        }
        return stringBuffer.toString();

    }


    /**
     * 将节点集合中的普通节点添加进stringbuffer
     *
     * @param stringBuffer 添加字符串容器
     * @version 1.0
     * @createTime 2015/11/25  15:40
     * @updateTime 2015/11/25  15:40
     * @createAuthor 陈思齐
     * @updateAuthor
     * @updateInfo
     */
    private static void appendNodesToStringBuffer(StringBuffer stringBuffer, Map nodesMap) {

        Iterator Iter = nodesMap.entrySet().iterator();
        while (Iter.hasNext()) {
            Map.Entry entry1 = (Map.Entry) Iter.next();
            String nodeName = (String) entry1.getKey();
            String nodeText = (String) entry1.getValue();
            stringBuffer.append("<" + nodeName + ">" + nodeText + "</" + nodeName + ">");
        }
    }

    /**
     * 将节点属性的map中的键值添加到StringBuffer中
     *
     * @param stringBuffer 存数据的sb
     * @param nodeAttrMap  保存节点属性的map集合
     * @version 1.0
     * @createTime 2015/11/25  15:23
     * @updateTime 2015/11/25  15:23
     * @createAuthor 陈思齐
     * @updateAuthor
     * @updateInfo
     */
    private static void appendAttrsToStringBuffer(StringBuffer stringBuffer, Map nodeAttrMap) {

        Iterator attrIter = nodeAttrMap.entrySet().iterator();
        while (attrIter.hasNext()) {
            Map.Entry attrs = (Map.Entry) attrIter.next();
            String attrName = (String) attrs.getKey();
            String attrValue = (String) attrs.getValue();
            stringBuffer.append(" " + attrName + "=" + "\"" + attrValue + "\"");
        }
    }

    /**
     * 根据提供的参数返回数据
     *
     * @param requestDataName 请求的数据的名称,限制为根节点下第一级节点
     * @return 搭载数据的Map
     * @version 1.0
     * @createTime 2015/11/25  17:41
     * @updateTime 2015/11/25  17:41
     * @createAuthor 陈思齐
     * @updateAuthor
     * @updateInfo
     */
    public static Map requestData(String requestDataName, Map map) {

        Map resultMap = null;
        if (map == null) {
            return null;
        }

        //迭代查找对应的元素
        Iterator iterator = map.entrySet().iterator();
        Object object = null;
        while (iterator.hasNext()) {
            Map.Entry entry = (Entry) iterator.next();
            String key = (String) entry.getKey(); //root
            if (requestDataName.equalsIgnoreCase(key)) {
                object = entry.getValue();
                break;
            }
            Map medialMap = (Map) entry.getValue();

            Iterator it = medialMap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry resultEntry = (Entry) it.next();
                String request = (String) resultEntry.getKey();
                if (requestDataName.equalsIgnoreCase(request)) {
                    object = resultEntry.getValue();
                    break;
                }
            }
        }

        if (object instanceof Map) {
            resultMap = (Map) object;
            return map;
        } else if (object instanceof String) {
            resultMap = new HashMap();
            resultMap.put(requestDataName, (String) object);
        }
        return resultMap; //所有元素总集合
    }
}



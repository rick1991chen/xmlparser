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
 * @author Rick
 * @Description 使用dom方式解析xml文档, 并暴露公共方法获取数据
 * @date 2015/11/23
 * @Copyright: Copyright (c)2015 Shenzhen Tentinet Technology Co., Ltd. Inc. All
 * rights reserved.
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
     * @version 1.0
     * @createTime 2015/11/23 9:51
     * @updateTime 2015/11/23 9:51
     * @createAuthor 陈思齐
     * @updateAuthor
     * @updateInfo
     */
    public static Map<String, Map> resolveXML(String XMLStr) {
        StringReader sr = new StringReader(XMLStr);
        InputSource is = new InputSource(sr);

        // 获取文档创建者工厂
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
                .newInstance();

        Element root = null;
        try {
            // 获取文档生成的创建者
            DocumentBuilder builder = documentBuilderFactory
                    .newDocumentBuilder();

            // 通过创建者读取整个文档返回文档对象
            Document document = builder.parse(is);

            // 解析文档对象,获取根节点
            root = document.getDocumentElement();

        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }

        if (root == null) { // 空文件,返回null
            return null;
        }

        NodeList primaryChildNodes = root.getChildNodes();

        Map primaryNodesMap = new LinkedHashMap();

        for (int x = 0; x < primaryChildNodes.getLength(); x++) {// 遍历根节点下的子节点

            Node primaryNode = primaryChildNodes.item(x);

            if (primaryNode != null
                    && primaryNode.getNodeType() == Node.ELEMENT_NODE) {
                recursionAddNodes(primaryNode, primaryNodesMap);
            }

            rootMap.put(root.getNodeName(), primaryNodesMap);
        }

        return rootMap;
    }

    /**
     * 递归方法来操作节点的添加
     *
     * @param node     需要的节点
     * @param nodesMap 节点所在的集合
     */
    private static void recursionAddNodes(Node node, Map nodesMap) {

        LinkedHashMap nodeMap = new LinkedHashMap();

        if (node.hasAttributes()) { // 递归方法首先获取节点属性
            NamedNodeMap attributes = node.getAttributes();
            Map attrMap = new LinkedHashMap();
            for (int x = 0; x < attributes.getLength(); x++) {
                Node item = attributes.item(x);
                attrMap.put(item.getNodeName(), item.getNodeValue());
            }
            nodeMap.put(node.getNodeName() + "_$attrs", attrMap);// 首先放入属性集合
        }

        // 接着判断有没有子节点
        if (node.getChildNodes().getLength() > 1) { // 获取子节点
            NodeList childNodes = node.getChildNodes();
            LinkedHashMap subNodesMap = new LinkedHashMap();
            for (int y = 0; y < childNodes.getLength(); y++) {
                Node subNode = childNodes.item(y); // 轮循获取子节点
                if (subNode != null
                        && subNode.getNodeType() == Node.ELEMENT_NODE) {

                    recursionAddNodes(subNode, subNodesMap);
                }

            }
            nodeMap.put(node.getNodeName(), subNodesMap);
        } else { // 没有字节点,只有文本内容
            String nodeText = node.getTextContent();
            nodeMap.put(node.getNodeName(), nodeText);
        }

        nodesMap.put(node.getNodeName(), nodeMap);
    }

    /**
     * 封装的方法,如果节点有属性,添加进子节点集合,以节点名 + "_$attrs"作为键,属性封装的map集合作为值
     *
     * @param nodesMap 当前节点的子节点集合
     * @param node     当前节点
     * @version 1.0
     * @createTime 2015/11/25 10:48
     * @updateTime 2015/11/25 10:48
     * @createAuthor 陈思齐
     * @updateAuthor
     * @updateInfo
     */
    private static void addAttrMap(Map nodesMap, Node node) {

        Map<String, String> nodeAttrMap = new LinkedHashMap<>();

        NamedNodeMap attributes = node.getAttributes();

        for (int x = 0; x < attributes.getLength(); x++) {
            nodeAttrMap.put(attributes.item(x).getNodeName(), attributes
                    .item(x).getTextContent());
        }
        // 保存属性,以key中独特的结尾确定
        nodesMap.put(node.getNodeName() + "_$attrs", nodeAttrMap); // map
        // 泛型为<String,Map>
    }

    /**
     * 将一个装载XML文件信息的Map导出为String,适配根节点下,一级子节点有属性或者为简单节点 或者带有二级子节点,无属性,为简单节点
     *
     * @param map      封装了xml数据的map
     * @return XML文件中的字符串
     * @version 1.0
     * @createTime 2015/11/24 11:24
     * @updateTime 2015/11/24 11:24
     * @createAuthor 陈思齐
     * @updateAuthor
     * @updateInfo
     */
    public static String XML2String(Map map) {

        // 获取用来拼接xml文件的StringBuffer
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>"); // 文件头

        if (map == null) {
            throw new NullPointerException("传入的数据源为空");
        }

        Iterator rootIt = map.entrySet().iterator();
        // 第一轮遍历,获取根节点名称及根节点
        while (rootIt.hasNext()) {
            Entry rootEntry = (Entry) rootIt.next();

            String rootName = (String) rootEntry.getKey();

            Map primaryNodesMap = (Map) rootEntry.getValue();

            stringBuffer.append("<" + rootName);

            // 添加属性
            Iterator attrIter = primaryNodesMap.entrySet().iterator();
            // 循环用于添加属性
            while (attrIter.hasNext()) {
                Entry secEntry = (Entry) attrIter.next();
                String attrName = (String) secEntry.getKey();
                Object object = secEntry.getValue();

                if (attrName.endsWith("_$attrs")) {
                    Map nodeAttrMap = (Map) object;
                    appendAttrsToStringBuffer(stringBuffer, nodeAttrMap);
                } else {
                    break;
                }
            }
            stringBuffer.append(">"); // 属性的后半括

            // 添加子节点
            Iterator nodeIter = primaryNodesMap.entrySet().iterator();
            while (nodeIter.hasNext()) {
                Entry nodeEntry = (Entry) nodeIter.next();
                String nodeName = (String) nodeEntry.getKey();
                if (nodeName.endsWith("_$attrs")) {
                    continue;
                } else { // 添加子节点
                    Object object = nodeEntry.getValue();

                    if (object instanceof Map) { // 该节点有子节点

                        // 此处用来添加子节点的方法,接收参数stringbuffer,map
                        recursionAppendString(stringBuffer, nodeName,
                                (Map) object);

                    } else { // 该节点是简单节点
                        stringBuffer.append(">");
                        String textContent = (String) object;
                        stringBuffer.append(textContent);
                    }
                }
            }
            stringBuffer.append("</" + rootName + ">");
        }
        return stringBuffer.toString();
    }

    /**
     * 递归添加stringbuffer
     *
     * @param stringBuffer 添加字符串的stringbuffer
     * @param object       节点集合
     * @version 1.0
     * @createTime 2015/11/30 14:54
     * @updateTime 2015/11/30 14:54
     * @createAuthor 陈思齐
     * @updateAuthor
     * @updateInfo
     */
    private static void recursionAppendString(StringBuffer stringBuffer,
                                              String upperNodeName, Map object) {

        Iterator iterator = object.entrySet().iterator();
        stringBuffer.append("<" + upperNodeName);
        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();

            String nodeName = (String) entry.getKey();
            Object obj = entry.getValue();
            boolean isAttr = nodeName.endsWith("_$attrs");

            if (isAttr) {
                Map nodeAttrMap = (Map) obj;
                appendAttrsToStringBuffer(stringBuffer, nodeAttrMap);
            } else {
                if (obj instanceof Map) {
                    Map valueMap = (Map) obj;

                    // 先添加属性
                    Iterator attrIter = valueMap.entrySet().iterator();
                    while (attrIter.hasNext()) {
                        Entry secEntry = (Entry) attrIter.next();
                        String key = (String) secEntry.getKey();
                        if (key.endsWith("_$attrs")) {
                            appendAttrsToStringBuffer(stringBuffer,
                                    (Map) secEntry.getValue());
                        } else {
                            break;
                        }

                    }
                    stringBuffer.append(">");

                    // 在添加子节点
                    Iterator nodeIter = valueMap.entrySet().iterator();
                    while (nodeIter.hasNext()) {
                        Entry secEntry = (Entry) nodeIter.next();
                        String key = (String) secEntry.getKey();
                        if (key.endsWith("_$attrs")) {
                            continue;
                        } else {
                            Object secObj = secEntry.getValue();

                            if (secObj instanceof Map) {

                                recursionAppendString(stringBuffer, key,
                                        (Map) secObj);
                            } else { // 简单节点

                                String textC = (String) secObj;
                                stringBuffer.append("<" + key + ">" + textC
                                        + "</" + key + ">");
                            }
                        }
                    }

                } else { // 为简单节点
                    stringBuffer.append(">");
                    String text = (String) obj;
                    stringBuffer.append(text);
                }
                stringBuffer.append("</" + nodeName + ">");
            }
        }
    }

    /**
     * 将节点集合中的普通节点添加进stringbuffer
     *
     * @param stringBuffer 添加字符串容器
     * @version 1.0
     * @createTime 2015/11/25 15:40
     * @updateTime 2015/11/25 15:40
     * @createAuthor 陈思齐
     * @updateAuthor
     * @updateInfo
     */
    private static void appendNodesToStringBuffer(StringBuffer stringBuffer,
                                                  Map nodesMap) {

        Iterator Iter = nodesMap.entrySet().iterator();
        while (Iter.hasNext()) {
            Entry entry1 = (Entry) Iter.next();
            String nodeName = (String) entry1.getKey();
            String nodeText = (String) entry1.getValue();
            stringBuffer.append("<" + nodeName + ">" + nodeText + "</"
                    + nodeName + ">");
        }
    }

    /**
     * 将节点属性的map中的键值添加到StringBuffer中
     *
     * @param stringBuffer 存数据的sb
     * @param nodeAttrMap  保存节点属性的map集合
     * @version 1.0
     * @createTime 2015/11/25 15:23
     * @updateTime 2015/11/25 15:23
     * @createAuthor Rick
     * @updateAuthor
     * @updateInfo
     */
    private static void appendAttrsToStringBuffer(StringBuffer stringBuffer,
                                                  Map nodeAttrMap) {

        Iterator attrIter = nodeAttrMap.entrySet().iterator();
        while (attrIter.hasNext()) {
            Entry attrs = (Entry) attrIter.next();
            String attrName = (String) attrs.getKey();
            String attrValue = (String) attrs.getValue();
            stringBuffer.append(" " + attrName + "=" + "\"" + attrValue + "\"");
        }
    }

    /**
     * 根据提供的节点名返回数据
     *
     * @param requestString 请求的数据的名称,限制为根节点下第一级节点
     * @param map           解析XML文件产生的Map集合
     * @return 搭载数据的Map
     * @version 1.0
     * @createTime 2015/11/25 17:41
     * @updateTime 2015/11/30 11:41
     * @createAuthor Rick
     * @updateAuthor Rick
     * @updateInfo
     */
    public static Map requestData(String requestString, Map map) {

        Map resultMap = null; //

        if (map == null) {
            return null;
        }

        Iterator iter = map.entrySet().iterator();

        while (iter.hasNext()) {
            Entry me = (Entry) iter.next();
            String key = (String) me.getKey();

            if (requestString.equalsIgnoreCase(key)) {
                resultMap = (Map) me.getValue();
                break;
            }
            Object obj = me.getValue();

            if (obj instanceof Map) {

                Map subMap = (Map) obj;
                resultMap = recursionFindNode(requestString, subMap);
            }

        }

        return resultMap; // 所有元素总集合
    }

    /**
     * 通过递归方式查找对应节点的信息
     *
     * @param requestString 查找节点的节点名
     * @param map           节点的值集合
     * @return 查询结果集合
     * @version 1.0
     * @createTime 2015/11/30  16:48
     * @updateTime 2015/11/30  16:48
     * @createAuthor 陈思齐
     * @updateAuthor
     * @updateInfo
     */
    private static Map recursionFindNode(String requestString, Map map) {

        Map resultMap = null;
        // 迭代查找对应的元素
        Iterator iterator = map.entrySet().iterator();

        while (iterator.hasNext()) {

            Entry entry = (Entry) iterator.next();

            String key = (String) entry.getKey(); // root
            if (requestString.equalsIgnoreCase(key)) {
                resultMap = (Map) entry.getValue();
                return resultMap;
            }

            // 执行到此代表 : 查询结束没结果

            Object obj = entry.getValue();
            if (obj instanceof String) {
                continue;
            }
            // 此处不加判断,会查询到属性集合的键值对中,而属性键值对是<String,String>泛型
            Map medialMap = (Map) obj;
            // 递归继续查询
            resultMap = recursionFindNode(requestString, medialMap);
        }
        return resultMap;
    }
}

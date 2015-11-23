package com.tentinet.xmlparser;

import android.provider.DocumentsContract;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.util.HashMap;

/**
 * @author 陈思齐
 * @Description 使用pull方法解析xml文档.需要知道解析结果实体bean的情况
 * @date 2015/11/23
 * @Copyright: Copyright (c)${year} Shenzhen Tentinet Technology Co., Ltd. Inc. All rights reserved.
 */
public class XmlParserPull {

    public static HashMap<String,String> readFromStream(InputStream is){

        try {
            //获取制造工厂
            XmlPullParserFactory xpf = XmlPullParserFactory.newInstance();

            //获取pullparser
            XmlPullParser xmlPullParser = xpf.newPullParser();

            //设置输入流
            xmlPullParser.setInput(is,"utf-8");

            int eventType = xmlPullParser.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT){ //如果文档没有结束

                String nodeName = xmlPullParser.getName();

                switch (nodeName){
                    case "123":
                    break;

                }

            }


        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }



        return null;
    }
}

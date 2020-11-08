package com.example.qianggou.tmall;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.format.DateParser;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.URLUtil;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;


/**
 * 项目名称：qianggou
 * 类名称：Test
 * 类描述：
 */
public class Test {

    public static void main(String[] args) throws Exception {
        String s = "100种让你鸡皮疙瘩的方法,href=https://www.bilibili.com/video/BV1xA411e7MB?from=search&seid=10000720196433447407";
        System.out.println(s.substring(0, s.indexOf(",href=")));
    }
}

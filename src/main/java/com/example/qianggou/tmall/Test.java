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

        CloseableHttpClient httpClient= HttpClientBuilder.create().build();

        HttpGet httpGet=new HttpGet("https://r3---sn-i3b7knsd.googlevideo.com/videoplayback?expire=1604658726&ei=xtGkX7_XKqeE1d8PyJaciA0&ip=112.164.94.40&id=o-AMd_0Zm2yiDfjq_2ll4x2XhJf_CLJ3jPJD8mZdJtcLk8&itag=22&source=youtube&requiressl=yes&vprv=1&mime=video%2Fmp4&ratebypass=yes&dur=725.971&lmt=1604405366521415&fvip=3&beids=9466586&c=WEB&txp=5432434&sparams=expire%2Cei%2Cip%2Cid%2Citag%2Csource%2Crequiressl%2Cvprv%2Cmime%2Cratebypass%2Cdur%2Clmt&sig=AOq0QJ8wRQIgM0HCBIJQxg2pR5amTLjf4-lvVKQXnITvGMsbTFb5CZwCIQC2qLgK_Mkfwt4Zziu0tHtQGQ5hS0-jEXospIDFYwKHVw%3D%3D&title=%5BChinese+ASMR+%E4%B8%AD%E6%96%87%5D%E4%B9%9F%E8%AE%B8%E8%BF%99%E6%98%AFYoutube%E6%9C%80%E9%AB%98%E7%9A%84%E7%A6%8F%E5%88%A9%E7%B3%BB%E5%88%97%C2%B74K+Relax++Treatment+of+insomnia&cms_redirect=yes&mh=Wl&mip=119.28.4.115&mm=31&mn=sn-i3b7knsd&ms=au&mt=1604641600&mv=m&mvi=3&pl=23&lsparams=mh,mip,mm,mn,ms,mv,mvi,pl&lsig=AG3C_xAwRAIgLWyyR-VdItPTLssJXIgCeH8tZKRIPktMLUevQxgOe98CICClIwGxjmluzgNUIUC6Zs9Z02YgPgd6rYT90HFeohDS");
        httpGet.addHeader("Connection","keep-alive");
        httpGet.addHeader("Upgrade-Insecure-Requests","1");
        httpGet.addHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.183 Safari/537.36");
        httpGet.addHeader("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
        httpGet.addHeader("Accept-Encoding","gzip, deflate, br");
        CloseableHttpResponse response=httpClient.execute(httpGet);
        HttpEntity httpEntity=response.getEntity();
        System.out.println(Arrays.toString(response.getHeaders("Content-Type")));
        InputStream inputStream=httpEntity.getContent();
        FileOutputStream fs=new FileOutputStream("C:\\Users\\T480\\Desktop\\1.mp4");
        int byteRead;
        byte[] buffer=new byte[1024];
        while ((byteRead=inputStream.read(buffer))!=-1){
            fs.write(buffer,0,byteRead);
        }
        inputStream.close();
        fs.close();
    }
}

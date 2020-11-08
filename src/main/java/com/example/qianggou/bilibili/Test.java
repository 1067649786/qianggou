package com.example.qianggou.bilibili;

import cn.hutool.core.io.FileUtil;
import cn.hutool.http.HttpUtil;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 项目名称：qianggou
 * 类名称：Test
 * 类描述：
 */
public class Test {

    public static void main(String[] args) {
        String urlString = "http://upos-sz-mirrorhw.bilivideo.com/upgcxcode/22/27/148552722/148552722-1-208.mp4?e=ig8euxZM2rNcNbhjnwdVhwdlhzT3hwdVhoNvNC8BqJIzNbfqXBvEuENvNC8aNEVEtEvE9IMvXBvE2ENvNCImNEVEIj0Y2J_aug859r1qXg8gNEVE5XREto8z5JZC2X2gkX5L5F1eTX1jkXlsTXHeux_f2o859IB_&ua=tvproj&deadline=1604746944&gen=playurl&nbs=1&oi=1866714693&os=hwbv&trid=2c614000bd6d48aeb8af7e9167de73b4&uipk=5&upsig=6f04c771d264adc95e19ed49bcf3e7c1&uparams=e,ua,deadline,gen,nbs,oi,os,trid,uipk&mid=0?yijianjiexi=app.bilibili.com";
        HttpURLConnection connection = null;
        InputStream inputStream = null;
        RandomAccessFile randomAccessFile = null;
        try {
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Range", "bytes=0-");
            connection.connect();
            inputStream = connection.getInputStream();
            long downloaded = 0;
            long fileSize = connection.getContentLength();
            String fileName = url.getFile();
            fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
            randomAccessFile = new RandomAccessFile(new File("C:\\Users\\10676\\Desktop\\test.mp4"), "rw");
            while (downloaded < fileSize) {
                byte[] buffer = null;
                if (fileSize - downloaded > 1000000) {
                    buffer = new byte[1000000];
                } else {
                    buffer = new byte[100000];
                }
                int read = -1;
                int currentDownload = 0;
                long startTime = System.currentTimeMillis();
                while (currentDownload < buffer.length) {
                    read = inputStream.read();
                    buffer[currentDownload++] = (byte) read;
                }
                long endTime = System.currentTimeMillis();
                double speed = 0.0;
                if (endTime - startTime > 0) {
                    speed = currentDownload / 1024.0 / ((double) (endTime - startTime) / 1000);
                }
                randomAccessFile.write(buffer);
                downloaded += currentDownload;
                randomAccessFile.seek(downloaded);
                System.out.printf("下载速度:%.2f%%,下载速度:%.1fkb/s(%.1fM/s)%n", downloaded * 1.0 / fileSize * 10000 / 100, speed, speed / 1000);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                assert connection != null;
                connection.disconnect();
                assert inputStream != null;
                inputStream.close();
                assert randomAccessFile != null;
                randomAccessFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

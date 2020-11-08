package com.example.qianggou.bilibili;

import cn.hutool.core.io.FileUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * 项目名称：qianggou
 * 类名称：Download
 * 类描述：
 */
public class SeleniumDownload {

    private static final String URL = "https://search.bilibili.com/all?keyword=";

    private static final String PARSE_URL = "https://xbeibeix.com/api/bilibili/";

    private static final int LOOP_TIME = 50;

    private static final int MAX_BUFFER_SIZE = 1024000;

    private static final String CHROME_DRIVER_PATH = "C:\\Program Files (x86)\\Google\\Chrome\\Application\\chromedriver.exe";

    public static void main(String[] args) throws InterruptedException, IOException {
        System.setProperty("webdriver.chrome.driver", CHROME_DRIVER_PATH);
        WebDriver driver = new ChromeDriver();
        driver.get(URL + "哄睡");
        //getUrl(driver);
        parseUrl(driver);
        driver.close();
    }

    private static void parseUrl(WebDriver driver) throws IOException, InterruptedException {
        File readFile = new File("C:\\Users\\10676\\Desktop\\bilibiliUrl.txt");
        File writFile = new File("C:\\Users\\10676\\Desktop\\bilibiliParseUrl.txt");
        FileWriter fileWriter = new FileWriter(writFile);
        BufferedWriter bw = new BufferedWriter(fileWriter);
        List<String> contents = FileUtil.readUtf8Lines(readFile);
        System.out.println("总共读取到" + contents.size() + "条链接");
        int count = 0;

        driver.get(PARSE_URL);
        //Thread.sleep(1000*160);
        for (String content : contents) {
            String title = content.substring(0, content.indexOf(",href="));
            String url = content.substring(content.indexOf(",href=") + 6);
            Thread.sleep(1500);
            while (true) {
                if (isJudgingElement(driver, By.xpath("//input[@name='bilibiliurl0815']"))) {
                    break;
                }
            }
            WebElement input = driver.findElement(By.xpath("//input[@name='bilibiliurl0815']"));
            input.sendKeys(Keys.CONTROL, "a");
            input.sendKeys(url);
            Thread.sleep(500);
            while (true) {
                if (isJudgingElement(driver, By.id("button-1"))) {
                    break;
                }
            }
            driver.findElement(By.id("button-1")).click();
            Thread.sleep(500);
            boolean isTimeOut = false;
            long startTime = System.currentTimeMillis();
            while (true) {
                if (isJudgingElement(driver, By.id("mp4-url2"))) {
                    break;
                }
                if (System.currentTimeMillis() - startTime / 1000 > 30) {
                    isTimeOut = true;
                    break;
                }
            }
            if (isTimeOut) {
                System.out.println("fail:" + url);
                continue;
            }
            //String parseUrl=driver.findElement(By.linkText("下载视频")).getAttribute("href");
            String parseUrl = driver.findElement(By.id("mp4-url2")).getText();
            //System.out.println("parseUrl:"+parseUrl);
            String parseContent = title + ",href=" + parseUrl;
            List<String> parseContents = new ArrayList<>();
            parseContents.add(parseContent);
            FileUtil.writeUtf8Lines(parseContents, writFile);
            //bw.write(parseContent);
            //bw.newLine();
            count++;
            if (count % 50 == 0) {
                bw.flush();
                System.out.println("已解析" + count + "条");
            }
        }
        bw.flush();
        bw.close();
    }

    private static void getUrl(WebDriver driver) throws InterruptedException, IOException {
        File file = new File("C:\\Users\\10676\\Desktop\\bilibiliUrl.txt");
        FileWriter fileWriter = new FileWriter(file);
        BufferedWriter bw = new BufferedWriter(fileWriter);
        int count = 0;

        while (true) {
            if (isJudgingElement(driver, By.linkText("最多点击"))) {
                break;
            }
        }
        driver.findElement(By.linkText("最多点击")).click();
        Thread.sleep(500);

        for (int i = 0; i < LOOP_TIME; i++) {
            List<WebElement> webElements = driver.findElements(By.xpath("//li/a[@target='_blank' and @class='img-anchor']"));
            for (WebElement webElement : webElements) {
                String href = webElement.getAttribute("href");
                String title = webElement.getAttribute("title");
                if (title.contains("德叔") || title.contains("哥") || title.contains("男")) {
                    continue;
                }
                String content = title + ",href=" + href;
                bw.write(content);
                bw.newLine();
                count++;
            }
            bw.flush();
            System.out.println("获取到" + count + "条链接");
            long startTime = System.currentTimeMillis();
            boolean isTimeOut = false;
            while (true) {
                if (isJudgingElement(driver, By.xpath("//li[@class='page-item next']/button"))) {
                    break;
                }
                if (System.currentTimeMillis() - startTime / 1000 > 10) {
                    isTimeOut = true;
                    break;
                }
            }
            if (!isTimeOut) {
                driver.findElement(By.xpath("//li[@class='page-item next']/button")).click();
            }
            Thread.sleep(1000);
        }
        bw.close();
    }

    private static void download(String urlString, String filePath) {
        HttpURLConnection connection = null;
        InputStream inputStream = null;
        RandomAccessFile randomAccessFile = null;
        try {
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Range", "bytes=0-");
            connection.connect();
            inputStream = connection.getInputStream();
            int downloaded = 0;
            int fileSize = connection.getContentLength();
            randomAccessFile = new RandomAccessFile(new File(filePath), "rw");
            while (downloaded < fileSize) {
                byte[] buffer = new byte[MAX_BUFFER_SIZE];
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
                System.out.printf("下载进度:%.2f%%,下载速度:%.1fkb/s(%.1fM/s)%n", downloaded * 1.0 / fileSize * 10240 / 100, speed, speed / 1024);
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

    private static boolean isJudgingElement(WebDriver webDriver, By by) {
        try {
            webDriver.findElement(by);
            return true;
        } catch (Exception e) {
            //System.out.println("不存在此元素");
            return false;
        }
    }

}

package com.example.qianggou.youtube;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.LineHandler;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.PrimitiveIterator;
import java.util.stream.Collectors;

/**
 * @Description
 * @Author ygy
 * @Date 2020/11/6
 */
public class SeleniumDownload {

    /**
     * youtube搜索后页面url
     */
    private static final String YOUTUBE_URL="https://www.youtube.com/results?search_query=%E4%B8%AD%E6%96%87asmr";

    private static final String DOWNLOAD_URL = "https://www.youtubeconverter.io/en6";
    /**
     * chromedriver路径
     */
    private static final String CHROME_DRIVER_PATH = "C:\\Program Files (x86)\\Google\\Chrome\\Application\\chromedriver.exe";
    /**
     * url存储路径
     */
    private static final String URL_FILE_PATH = "C:\\Users\\10676\\Desktop\\url.txt";

    private static final String FAIL_FILE_PATH = "C:\\Users\\10676\\Desktop\\failurl.txt";
    /**
     * 爬取最大数量
     */
    private static final int COUNT = 603;
    /**
     * 爬取起始值
     */
    private static final int START = 91;

    public static void main(String[] args) throws IOException, InterruptedException {
        System.setProperty("webdriver.chrome.driver", CHROME_DRIVER_PATH);
        WebDriver driver = new ChromeDriver();
        driver.get(YOUTUBE_URL);
        Thread.sleep(1000 * 60);
        //getUrl(driver);
        download(driver);
        Thread.sleep(1000);
        driver.close();
    }

    private static void download(WebDriver driver) throws InterruptedException, IOException {
        List<String> urls=FileUtil.readUtf8Lines(URL_FILE_PATH);
        System.out.println("urls.size()="+urls.size());
        driver.get(DOWNLOAD_URL);
        List<String> failList=new ArrayList<>();
        for (int i=START;i<urls.size();i++){
            if (urls.get(i)==null || "".equals(urls.get(i))){
                continue;
            }
            while (true){
                if (isJudgingElement(driver,By.id("ytUrl"))){
                    break;
                }
            }
            Thread.sleep(100);
            driver.findElement(By.id("ytUrl")).sendKeys(Keys.CONTROL,"a");
            Thread.sleep(1000);
            driver.findElement(By.id("ytUrl")).sendKeys(urls.get(i));
            System.out.println(urls.get(i));
            Thread.sleep(1000);
            driver.findElement(By.id("ytUrl")).sendKeys(Keys.ENTER);
            //driver.findElement(By.id("convertBtn")).click();
            Thread.sleep(1000);
            while (true){
                if (isJudgingElement(driver,By.xpath("//td/a[@type='button']"))){
                    break;
                }
            }
            Thread.sleep(100);
            List<WebElement> webElements=driver.findElements(By.xpath("//td/a[@type='button']"));
            List<WebElement> webElementList=webElements.stream().filter(webElement->webElement.getAttribute("onclick").contains("720p")).collect(Collectors.toList());
            if (webElementList.isEmpty()){
                webElementList=webElements.stream().filter(webElement->webElement.getAttribute("onclick").contains("360p")).collect(Collectors.toList());
                if (webElementList.isEmpty()){
                    webElementList=webElements.stream().filter(webElement->webElement.getAttribute("onclick").contains("240p")).collect(Collectors.toList());
                }
            }
            if (!webElementList.isEmpty()){
                webElementList.get(0).click();
            } else {
                failList.add(urls.get(i));
                System.out.println("fail:" + urls.get(i));
                continue;
            }
            while (true) {
                if (isJudgingElement(driver, By.linkText("Convert next"))) {
                    break;
                }
            }
            if (!isJudgingElement(driver, By.linkText("Download"))) {
                failList.add(urls.get(i));
                driver.navigate().back();
                driver.navigate().refresh();
                Thread.sleep(5000);
                if (i > 0 && i % 5 == 0) {
                    Thread.sleep(1200 * 60 * 4);
                }
                continue;
            }
            Thread.sleep(100);
            String downloadHref = driver.findElement(By.linkText("Download")).getAttribute("href");
            driver.navigate().to(downloadHref);
            Thread.sleep(1000);
            driver.navigate().back();
            driver.navigate().refresh();
            Thread.sleep(5000);
            if (i > 0 && i % 5 == 0) {
                Thread.sleep(1200 * 60 * 4);
            }
        }
        FileUtil.writeUtf8Lines(failList,FAIL_FILE_PATH);
    }

    private static void getUrl(WebDriver driver) throws IOException {
        File file=new File(URL_FILE_PATH);
        FileWriter fileWriter=new FileWriter(file);
        BufferedWriter bw= new BufferedWriter(fileWriter);
        try {
            List<WebElement> webElements=driver.findElements(By.xpath("//a[@id='video-title']"));
            //模拟滚轮一直向下滑动加载
            Actions actions=new Actions(driver);
            while(webElements.size()<=COUNT){
                actions.sendKeys(Keys.DOWN).perform();
                Thread.sleep(50);
                webElements=driver.findElements(By.xpath("//a[@id='video-title']"));
//                if (webElements.size()%100==0){
//                    System.out.println(webElements.size());
//                }
                System.out.println("页面视频数量："+webElements.size());
            }

            for (int i=START;i<webElements.size();i++){
                String href=webElements.get(i).getAttribute("href");
                System.out.println(href);
                bw.write(href);
                bw.newLine();
            }

            System.out.println("总过读取到"+webElements.size()+"条");
            System.out.println("写入"+(webElements.size()-START)+"条");
            bw.close();
        } catch (Exception e){
            System.out.println("没有找到符合条件的标签");
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

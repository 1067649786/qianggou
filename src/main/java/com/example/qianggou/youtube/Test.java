package com.example.qianggou.youtube;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RandomUtil;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description
 * @Author ygy
 * @Date 2020/11/6
 */
public class Test {

    /**
     * youtube搜索后页面url
     */
    private static final String YOUTUBE_URL="https://www.youtube.com/results?search_query=%E4%B8%AD%E6%96%87asmr";

    private static final String DOWNLOAD_URL="https://www.youtubeconverter.io/en6";
    /**
     * chromedriver路径
     */
    private static final String CHROME_DRIVER_PATH = "C:\\Users\\T480\\Desktop\\chromedriver.exe";
    /**
     * url存储路径
     */
    private static final String URL_FILE_PATH="C:\\Users\\T480\\Desktop\\url.txt";
    /**
     * 爬取最大数量
     */
    private static final int COUNT=100;
    /**
     * 爬取起始值
     */
    private static final int START=0;

    public static void main(String[] args) throws InterruptedException {
        System.setProperty("webdriver.chrome.driver", CHROME_DRIVER_PATH);
        WebDriver driver = new ChromeDriver();
        driver.get(YOUTUBE_URL);
        download(driver);
        driver.close();
    }

    private static void download(WebDriver driver) throws InterruptedException {
        List<String> urls= FileUtil.readUtf8Lines(URL_FILE_PATH);
        System.out.println("urls.size()="+urls.size());
        driver.get(DOWNLOAD_URL);
        for (int i=START;i<urls.size();i++){
            if (urls.get(i)==null || "".equals(urls.get(i))){
                continue;
            }
            while (true){
                if (isJudgingElement(driver, By.id("ytUrl"))){
                    break;
                }
            }
            driver.findElement(By.id("ytUrl")).sendKeys(urls.get(i));
            driver.findElement(By.id("convertBtn")).click();
            Thread.sleep(5000);
            while (true){
                if (isJudgingElement(driver,By.linkText("Download"))){
                    break;
                }
            }
            List<WebElement> webElements=driver.findElements(By.linkText("Download"));
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
                continue;
            }
            while (true){
                if (isJudgingElement(driver,By.linkText("Convert next"))){
                    break;
                }
            }
            String downloadHref=driver.findElement(By.linkText("Download")).getAttribute("href");
            driver.navigate().to(downloadHref);
            Thread.sleep(1000);
            driver.navigate().back();
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

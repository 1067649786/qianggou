package com.example.qianggou.tmall;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import lombok.extern.slf4j.Slf4j;
import lombok.extern.slf4j.XSlf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.net.URL;
import java.time.Instant;
import java.util.Date;

/**
 * 项目名称：qianggou
 * 类名称：Tmall
 * 类描述：
 */
public class Tmall {

    private static final String URL = "https://detail.tmall.com/item.htm?spm=a220m.1000858.1000725.29.520d38b6GsMjUr&id=556895458653&skuId=4492710685738&areaId=510100&user_id=3174816146&cat_id=2&is_b=1&rn=37d7601777c403f3d246c766e96d360e";
    private static final String START_TIME = "2020-11-06 20:00:00";
    private static final String CHROME_DRIVER_PATH = "C:\\Program Files (x86)\\Google\\Chrome\\Application\\chromedriver.exe";

    private static void login(WebDriver driver) throws InterruptedException {
        driver.findElement(By.linkText("请登录"));
        System.out.println("=========跳转到登录页===========");
        Thread.sleep(15000);
        System.out.println("=========登录成功自动跳转到商品页面=========");
    }

    private static void buy(WebDriver driver) throws InterruptedException {
        long startTime=System.currentTimeMillis();
        while (true) {
            int random= RandomUtil.randomInt(15,20);
            long nowTime=System.currentTimeMillis();
            long targetTime=DateUtil.parse(START_TIME).getTime();
            if (targetTime-nowTime>1000*60*5 && (nowTime-startTime)%(1000*60*random)==0){
                driver.navigate().refresh();
            }
            if (targetTime-nowTime<=500){
                System.out.println("==========刷新" + DateUtil.now() + "============");
                break;
            }
        }
        driver.navigate().refresh();

        while (true) {
            if (isJudgingElement(driver, By.linkText("立即购买"))) {
                driver.findElement(By.linkText("立即购买")).click();
                break;
            }
        }
        while (true) {
            if (isJudgingElement(driver, By.linkText("提交订单"))) {
                driver.findElement(By.linkText("提交订单")).click();
                break;
            }
        }
        System.out.println("========下订单完成============");
    }

    private static boolean isJudgingElement(WebDriver webDriver, By by) {
        try {
            webDriver.findElement(by);
            return true;
        } catch (Exception e) {
            System.out.println("不存在此元素");
            return false;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        System.setProperty("webdriver.chrome.driver", CHROME_DRIVER_PATH);
        WebDriver driver = new ChromeDriver();

        driver.get(URL);
        Thread.sleep(200);
        login(driver);
        Thread.sleep(10000);
        buy(driver);

        Thread.sleep(10000);

        driver.close();
    }
}

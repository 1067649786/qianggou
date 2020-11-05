package com.example.qianggou.tmall;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.format.DateParser;

import java.text.DateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;


/**
 * 项目名称：qianggou
 * 类名称：Test
 * 类描述：
 */
public class Test {

    public static void main(String[] args) {
        Date now = DateUtil.parseDateTime(DateUtil.now());
        System.out.println(now);
        Date target = DateUtil.parse("2020-11-05 22:16:00");
        System.out.println(target);
        System.out.println(DateUtil.between(now, target, DateUnit.MS));
    }
}

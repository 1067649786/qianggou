package com.example.qianggou.youtube;

import cn.hutool.core.io.FileUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @Description
 * @Author ygy
 * @Date 2020/11/9
 */
public class YoutubeDlDownload {

    public static void main(String[] args) {
        String youtubeDlPath="C:\\Users\\T480\\Desktop\\youtube-dl.exe";
        String savePath="D:\\asmr\\youtube\\%(title)s.%(ext)s";
        List<String> urlList=readUrlTxt("C:\\Users\\T480\\Desktop\\youtubeUrl.txt");
        int start=0;
        int end=604;
        for (int i=start;i<Math.min(end,urlList.size());i++){
            if ("".equals(urlList.get(i)) || urlList.get(i)==null){
                continue;
            }
            List<String> command=new ArrayList<>();
            command.add(youtubeDlPath);
            command.add("-o");
            command.add(savePath);
            command.add("--proxy");
            command.add("127.0.0.1:1080");
            command.add(urlList.get(i));
            exeCmd(command);
        }
        //String commandStr="D:\\ffmpeg\\bin\\ffmpeg.exe -h";
        //exeCmd(commandStr);
    }

    private static List<String> readUrlTxt(String path){
        return FileUtil.readUtf8Lines(path);
    }

    private static void exeCmd(List<String> command){
        ProcessBuilder builder = new ProcessBuilder();
        builder.command(command);
        try {
            builder.inheritIO().start().waitFor();
            System.out.println("--------------音视频下载完成--------------");
        } catch (InterruptedException | IOException e) {
            System.err.println("音视频下载失败！");
            e.printStackTrace();
        }
    }

    private static void exeCmd(String commandStr){
        BufferedReader br=null;
        try{
            Process p=Runtime.getRuntime().exec(commandStr);
            br=new BufferedReader(new InputStreamReader(p.getInputStream(), StandardCharsets.UTF_8));
            String line=null;
            StringBuilder sb=new StringBuilder();
            while ((line=br.readLine())!=null){
                //sb.append(line).append("\n");
                System.out.println(line);
            }
            //System.out.println(sb.toString());
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            try {
                assert br != null;
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

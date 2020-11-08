package com.example.qianggou.bilibili;

import cn.hutool.core.io.FileUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 项目名称：qianggou
 * 类名称：BiliVideoDownload
 * 类描述：
 */
public class BiliVideoDownload {

    //private static final String VIDEO_URL="https://www.bilibili.com/video/BV12D4y197Jx";

    /**
     * ffmpeg位置
     */
    private static final String FFMPEG_PATH = "D:\\baidu\\BaiduNetdiskDownload\\ffmpeg\\bin\\ffmpeg.exe";

    private static String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.163 Safari/537.36";

    private static String SAVE_PATH = "D:\\baidu\\BaiduNetdiskDownload\\asmr\\未发\\bilibili";

    private static int START = 50;

    private static int END = 100;

    /**
     * 重试次数
     */
    private static int RETRY_TIME = 10;

    private static List<String> failList = new ArrayList<>();

    public static void main(String[] args) {
        htmlParser();
    }

    /**
     * 解析html获取相关信息
     */
    private static void htmlParser() {
        File readFile = new File("C:\\Users\\10676\\Desktop\\bilibiliUrl.txt");
        List<String> contents = FileUtil.readUtf8Lines(readFile);

        for (int i = START; i < (Math.min(contents.size(), END)); i++) {
            if ("".equals(contents.get(i)) || contents.get(i) == null) continue;
            String url = contents.get(i).substring(contents.get(i).indexOf(",href=") + 6);
            HttpResponse res = HttpRequest.get(url).timeout(2000).execute();
            String html = res.body();
            Document document = Jsoup.parse(html);
            Element title = document.getElementsByTag("title").first();
            VideoInfo videoInfo = new VideoInfo();
            //视频名称
            videoInfo.videoName = title.text();
            //截取视频信息
            Pattern pattern = Pattern.compile("(?<=<script>window.__playinfo__=).*?(?=</script>)");
            Matcher matcher = pattern.matcher(html);
            if (matcher.find()) {
                videoInfo.videoInfo = new JSONObject(matcher.group());
            } else {
                System.out.println("未匹配到视频信息，退出程序!");
                return;
            }
            getVideoInfo(videoInfo, url);
        }
        //FileUtil.writeUtf8Lines(failList,"C:\\Users\\10676\\Desktop\\failurl.txt");
        FileUtil.appendUtf8Lines(failList, "C:\\Users\\10676\\Desktop\\failurl.txt");
    }

    /**
     * 解析视频和音频的具体信息
     */
    private static void getVideoInfo(VideoInfo video, String url) {
        try {
            int temp = 0;
            //获取视频的基本信息
            JSONObject videoInfo = video.videoInfo;
            JSONArray videoInfoArr = videoInfo.getJSONObject("data").getJSONObject("dash").getJSONArray("video");
            for (int i = 0; i < videoInfoArr.size(); i++) {
                if (videoInfoArr.getJSONObject(i).getInt("height") == 720) {
                    temp = i;
                    break;
                }
            }
            video.videoBaseUrl = videoInfoArr.getJSONObject(temp).getStr("baseUrl");
            video.videoBaseRange = videoInfoArr.getJSONObject(temp).getJSONObject("SegmentBase").getStr("Initialization");
            HttpResponse videoRes = HttpRequest.get(video.videoBaseUrl)
                    .header("Referer", url)
                    .header("Range", "bytes=" + video.videoBaseRange)
                    .header("User-Agent", USER_AGENT)
                    .timeout(2000)
                    .execute();
            int videoRetryTime = RETRY_TIME;
            while ((videoRes.getStatus() != 200 && videoRes.getStatus() != 206) && videoRetryTime > 0) {
                System.out.println("重试视频:" + video.videoName);
                videoRes = HttpRequest.get(video.videoBaseUrl)
                        .header("Referer", url)
                        .header("Range", "bytes=" + video.videoBaseRange)
                        .header("User-Agent", USER_AGENT)
                        .timeout(2000)
                        .execute();
                if (videoRes.getStatus() == 200 || videoRes.getStatus() == 206) {
                    break;
                }
                videoRetryTime--;
            }
            video.videoSize = videoRes.header("Content-Range").split("/")[1];

            //获取音频基本信息
            JSONArray audioInfoArr = videoInfo.getJSONObject("data").getJSONObject("dash").getJSONArray("audio");
            video.audioBaseUrl = audioInfoArr.getJSONObject(0).getStr("baseUrl");
            video.audioBaseRange = audioInfoArr.getJSONObject(0).getJSONObject("SegmentBase").getStr("Initialization");
            HttpResponse audioRes = HttpRequest.get(video.audioBaseUrl)
                    .header("Referer", url)
                    .header("Range", "bytes=" + video.audioBaseRange)
                    .header("User-Agent", USER_AGENT)
                    .timeout(2000)
                    .execute();
            int audioRetryTime = RETRY_TIME;
            while ((audioRes.getStatus() != 200 && audioRes.getStatus() != 206) && audioRetryTime > 0) {
                System.out.println("重试音频:" + video.videoName);
                audioRes = HttpRequest.get(video.audioBaseUrl)
                        .header("Referer", url)
                        .header("Range", "bytes=" + video.audioBaseRange)
                        .header("User-Agent", USER_AGENT)
                        .timeout(2000)
                        .execute();
                if (audioRes.getStatus() == 200 || audioRes.getStatus() == 206) {
                    break;
                }
                audioRetryTime--;
            }
            video.audioSize = audioRes.header("Content-Range").split("/")[1];
            downloadFile(video, url);
        } catch (Exception e) {
            System.out.println(e.getMessage() + "------" + video.videoName);
            failList.add(",href=" + url);
            return;
            //throw new RuntimeException("请求失败");
        }

        //downloadFile(video,url);
    }

    /**
     * 下载音频
     */
    private static void downloadFile(VideoInfo videoInfo, String url) throws Exception {
        //保存音视频的位置
        //SAVE_PATH="D:\\baidu\\BaiduNetdiskDownload\\asmr\\未发\\bilibili";
        File fileDir = new File(SAVE_PATH);
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }

        //下载视频
        File videoFile = new File(SAVE_PATH + File.separator + videoInfo.videoName + "_video.mp4");
        if (!videoFile.exists()) {
            System.out.println("开始下载视频:" + videoInfo.videoName);
            HttpResponse videoRes = HttpRequest.get(videoInfo.videoBaseUrl)
                    .header("Referer", url)
                    .header("Range", "bytes=0-" + videoInfo.videoSize)
                    .header("User-Agent", USER_AGENT)
                    .execute();
            videoRes.writeBody(videoFile);
            System.out.println("视频文件下载完成:" + videoInfo.videoName);
        }

        // 下载音频
        File audioFile = new File(SAVE_PATH + File.separator + videoInfo.videoName + "_audio.mp4");
        if (!audioFile.exists()) {
            System.out.println("开始下载音频文件:" + videoInfo.videoName);
            HttpResponse audioRes = HttpRequest.get(videoInfo.audioBaseUrl)
                    .header("Referer", url)
                    .header("Range", "bytes=0-" + videoInfo.audioSize)
                    .header("User-Agent", USER_AGENT)
                    .execute();
            audioRes.writeBody(audioFile);
            System.out.println("音频文件下载完成:" + videoInfo.videoName);
        }

        mergeFiles(videoFile, audioFile, videoInfo);
    }

    private static void mergeFiles(File videoFile, File audioFile, VideoInfo videoInfo) {
        System.out.println("--------------开始合并音视频--------------");
        String outFile = SAVE_PATH + File.separator + videoInfo.videoName + ".mp4";
        List<String> commend = new ArrayList<>();
        commend.add(FFMPEG_PATH);
        commend.add("-i");
        commend.add(videoFile.getAbsolutePath());
        commend.add("-i");
        commend.add(audioFile.getAbsolutePath());
        commend.add("-vcodec");
        commend.add("copy");
        commend.add("-acodec");
        commend.add("copy");
        commend.add(outFile);

        ProcessBuilder builder = new ProcessBuilder();
        builder.command(commend);
        try {
            builder.inheritIO().start().waitFor();
            System.out.println("--------------音视频合并完成--------------");
            FileUtil.del(videoFile);
            FileUtil.del(audioFile);
        } catch (InterruptedException | IOException e) {
            System.err.println("音视频合并失败！");
            e.printStackTrace();
        }

    }
}

class VideoInfo {
    public String videoName;
    public JSONObject videoInfo;
    public String videoBaseUrl;
    public String audioBaseUrl;
    public String videoBaseRange;
    public String audioBaseRange;
    public String videoSize;
    public String audioSize;
}

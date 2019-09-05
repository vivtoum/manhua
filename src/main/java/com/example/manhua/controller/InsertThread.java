package com.example.manhua.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CountDownLatch;

/**
 * @author YT.Hu
 * @version 0.0.1
 * @date 2019/9/6 1:31
 */
@Slf4j
public class InsertThread implements Runnable {

    /**
     * 数据集合
     */
    private int startIdx;

    private int endIdx;
    /**
     * 每个线程处理的起始数据
     */
    private CountDownLatch begin;
    /**
     * 每个线程处理的结束数据
     */
    private CountDownLatch end;

    public InsertThread() {
    }

    public InsertThread(int startIdx, int endIdx, CountDownLatch begin, CountDownLatch end) {
        this.startIdx = startIdx;
        this.begin = begin;
        this.end = end;
        this.endIdx = endIdx;
    }

    @Override
    public void run() {
        log.warn(Thread.currentThread().getName());
        try {
            // 创建httpclient实例
            CloseableHttpClient httpclient = HttpClients.createDefault();

            for (int i = startIdx; i <= endIdx; i++) {
//                String dirName = "第" + i + "集";
//                File dir = new File(getClass().getResource("/").getFile() + "static/img/" + dirName);
//                dir.mkdir();
                boolean flag = true;
                for (int j = 1; flag; j++) {
                    try {
                        String url = "http://img.17dm.com/wugengji/manhua/3b" + i + "/" + j + ".jpg";
                        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                            String fileName = url.substring(url.lastIndexOf("/") + 1);
                            InputStream inputStream = connection.getInputStream();
                            byte[] data = readInputStream(inputStream);
                            //new一个文件对象用来保存图片，默认保存当前工程根目录
                            File imageFile = new File(fileName);
                            //创建输出流
//                            FileOutputStream outStream = new FileOutputStream(getClass().getResource("/").getFile() + "static/img/" + dirName + "/" + i + "-" + fileName);
                            FileOutputStream outStream = new FileOutputStream(getClass().getResource("/").getFile() + "static/img/"  + "/" + i + "-" + fileName);
                            //写入数据
                            outStream.write(data);
                            //关闭输出流
                            outStream.close();
                        } else {
                            break;
                        }
                    } catch (Exception e) {
                        flag = false;
                    }
                }
            }
            httpclient.close(); // httpClient关闭

        } catch (Exception e) {

        } finally {

            // 当一个线程执行完了计数要减一不然这个线程会被一直挂起
            end.countDown();
        }
    }

    public static byte[] readInputStream(InputStream inStream) throws Exception {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        //创建一个Buffer字符串
        byte[] buffer = new byte[1024];
        //每次读取的字符串长度，如果为-1，代表全部读取完毕
        int len = 0;
        //使用一个输入流从buffer里把数据读取出来
        while ((len = inStream.read(buffer)) != -1) {
            //用输出流往buffer里写入数据，中间参数代表从哪个位置开始读，len代表读取的长度
            outStream.write(buffer, 0, len);
        }
        //关闭输入流
        inStream.close();
        //把outStream里的数据写入内存
        return outStream.toByteArray();
    }

}
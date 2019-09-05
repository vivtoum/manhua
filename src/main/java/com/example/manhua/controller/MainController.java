package com.example.manhua.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author YT.Hu
 * @version 0.0.1
 * @date 2019/9/6 0:36
 */

@RestController
@RequestMapping("main")
@Slf4j
public class MainController {

    @GetMapping("a")
    public void a() throws InterruptedException {
        batchByThread(80);
    }


    private void batchByThread(int count) throws InterruptedException {
        if (count == 0) {
            return;
        }
        //  一个线程处理300条数据
        int counts = 4;
        //  开启的线程数
        int runSize = (count / counts) + 1;
        //  存放每个线程的执行数据
        int newList = 0;
        //  创建一个线程池，数量和开启线程的数量一样
        ExecutorService executor = Executors.newFixedThreadPool(runSize);
        //  创建两个个计数器
        CountDownLatch begin = new CountDownLatch(1);
        CountDownLatch end = new CountDownLatch(runSize);

        for (int i = 0; i < runSize; i++) {
            int startIdx = 1;
            int endIdx = 0;
            /* 计算每个线程执行的数据 */
            if ((i + 1) == runSize) {
                startIdx += (i * counts);
                endIdx = count;
            } else {
                startIdx += (i * counts);
                endIdx = (i + 1) * counts;
            }
            InsertThread thread = new InsertThread(startIdx, endIdx, begin, end);

            executor.execute(thread);
        }
        begin.countDown();
        end.await();
        executor.shutdown();
        log.warn("完成");
    }


    @GetMapping("test")
    public void test() throws IOException {

    }




}

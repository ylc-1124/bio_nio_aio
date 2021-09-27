package com.ylc.four;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 处理Socket的线程池
 */
public class HandlerSocketThreadPool {
    //1、创建一个线程池成员变量存储一个线程池对象
    private ExecutorService executorService;

    //2、创建此类时，初始化线程池对象
    public HandlerSocketThreadPool(int maxThreadNum, int queueSize) {
        executorService = new ThreadPoolExecutor(
                3,
                maxThreadNum,
                120,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(queueSize)
        );
    }

    //3、提供一个方法提交任务给线程池任务队列来暂存，等线程池处理
    public void execute(Runnable target) {
        executorService.execute(target);
    }
}

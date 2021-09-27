package com.ylc.four;

import java.net.ServerSocket;
import java.net.Socket;

/**
 * 目标：开发实现伪异步通信架构
 */
public class Server {
    public static void main(String[] args) {
        try {
            //1、注册端口
            ServerSocket ss = new ServerSocket(9999);
            //2、定义循环接收客户端的连接请求
            // 初始化线程池对象
            HandlerSocketThreadPool pool =
                    new HandlerSocketThreadPool(3, 10);
            while (true) {
                Socket socket = ss.accept();
                //3、把socket交给线程池处理
                //把socket封装成任务对象交给线程池处理
                pool.execute(new ServerReaderThread(socket));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

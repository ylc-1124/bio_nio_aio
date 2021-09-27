package com.ylc.file;

import java.net.ServerSocket;
import java.net.Socket;

/**
 * 目标：服务端开发，可以接收客户端任意类型文件并保存到服务端磁盘
 */
public class Server {
    public static void main(String[] args) {
        try {
            ServerSocket ss = new ServerSocket(8888);
            while (true) {
                Socket socket = ss.accept();
                //交给独立的线程处理这个客户端和服务器通信的需求
                new Thread(new HandlerSocketRunnable(socket)).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

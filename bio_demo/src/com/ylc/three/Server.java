package com.ylc.three;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 目标：实现服务器端可以同时接收多个客户端的Socket通信需求
 * 思路：服务器端接收到一个Socket对象都开启一个线程来处理客户端数据交互需求
 */
public class Server {
    public static void main(String[] args) {
        try {
            //1、注册端口
            ServerSocket ss = new ServerSocket(9999);
            //2、监听客户端连接请求
            while (true) {
                Socket socket = ss.accept();
                //3、创建一个线程来处理需求
                new Thread(new ServerThreadReader(socket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

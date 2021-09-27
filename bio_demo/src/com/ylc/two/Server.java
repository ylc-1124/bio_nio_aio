package com.ylc.two;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 目标：服务端反复接收消息，客户端反复发送消息
 */
public class Server {
    public static void main(String[] args) {
        try {
            System.out.println("服务器端已启动...");
            //1、定义ServerSocket对象进行服务端的端口注册
            ServerSocket ss = new ServerSocket(9999);
            //2、监听客户端的Socket连接请求
            Socket socket = ss.accept();
            //3、从Socket管道中得到字节输入流对象
            InputStream is = socket.getInputStream();
            //4、把字节输入流通过转换流包装成--字符缓冲输入流
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(is));
            String msg;
            while ((msg = br.readLine()) != null) {
                System.out.println("接收到客户端消息:"+msg);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

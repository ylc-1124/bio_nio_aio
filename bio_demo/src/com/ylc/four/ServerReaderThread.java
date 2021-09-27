package com.ylc.four;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class ServerReaderThread implements Runnable {
    private Socket socket;

    public ServerReaderThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        //处理接收到的客户端Socket通信需求
        try {
            InputStream is = socket.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String msg;
            //线程会一直阻塞在这个readLine方法，等待接收消息
            //如果突然连接中断会抛出异常，可以人性化的处理一下这个异常，代表有客户端断开连接下线了
            while ((msg = br.readLine()) != null) {
                System.out.println("收到来自客户端的消息:" + msg);
            }
        } catch (IOException e) {
            System.out.println("有客户端下线了");
        }
    }
}

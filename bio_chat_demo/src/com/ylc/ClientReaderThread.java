package com.ylc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * 监听服务器发送到客户端消息
 */
public class ClientReaderThread implements Runnable {
    private Socket socket;

    public ClientReaderThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        //监听服务器发送回来的消息
        try {
            BufferedReader br =
                    new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String msg;
            while ((msg = br.readLine()) != null) {
                System.out.println("收到一条消息:"+msg);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

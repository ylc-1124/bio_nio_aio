package com.ylc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

/**
 * 服务器端处理客户端请求的类
 */
public class ServerReaderThread implements Runnable {
    private Socket socket;

    public ServerReaderThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            //1、从socket获取当前客户端的输入流
            BufferedReader br =
                    new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String msg;
            while ((msg = br.readLine()) != null) {
                System.out.println("接收到客户端消息:" + msg);
                //2、发送给给其他在线的客户端socket
                sendMsgToAllClient(msg);
            }
        } catch (Exception e) {
            System.out.println("有客户端下线了");
            Server.onlineSockets.remove(socket);
        }
    }

    /**
     * 把当前客户端发来的消息推送给全部其他在线的socket
     */
    private void sendMsgToAllClient(String msg) throws IOException {
        for (Socket onlineSocket : Server.onlineSockets) {
            if (onlineSocket != socket) {
                PrintStream ps = new PrintStream(onlineSocket.getOutputStream());
                ps.println(msg);
                ps.flush();
            }
        }
    }
}

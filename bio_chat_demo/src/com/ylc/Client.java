package com.ylc;

import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

/**
 * 客户端
 */
public class Client {
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("127.0.0.1", 9999);
            //开启一个独立线程监听服务器发送回来的消息
            new Thread(new ClientReaderThread(socket)).start();
            //发送数据给服务器
            PrintStream ps = new PrintStream(socket.getOutputStream());
            Scanner scanner = new Scanner(System.in);
            while (scanner.hasNextLine()) {
                String msg = scanner.nextLine();
                ps.println(msg);
                ps.flush();
            }
            socket.shutdownOutput();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

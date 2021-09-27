package com.ylc.three;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

/**
 * 客户端
 */
public class Client {
    public static void main(String[] args) {

        try {
            //1、创建Socket对象请求服务端连接
            Socket socket = new Socket("127.0.0.1", 9999);
            //2、获取打印流
            OutputStream os = socket.getOutputStream();
            PrintStream ps = new PrintStream(os);
            //3、不断发送消息给服务器接收
            Scanner scanner = new Scanner(System.in);
            while (scanner.hasNextLine()) {
                String msg = scanner.nextLine();
                ps.println(msg);
                ps.flush();
            }
            socket.shutdownOutput();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

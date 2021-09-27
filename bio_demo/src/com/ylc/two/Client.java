package com.ylc.two;

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
            //2、从Socket对象获取字节输入流
            OutputStream os = socket.getOutputStream();
            //3、通过扫描器监听键盘输入，获取数据由打印流发送到服务器端
            PrintStream ps = new PrintStream(os);
            Scanner scanner = new Scanner(System.in);
            while (scanner.hasNextLine()) {
                String msg = scanner.nextLine();
                ps.println(msg);
                ps.flush();
            }
            //4、发完要告诉服务器发送结束了
            socket.shutdownOutput();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

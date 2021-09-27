package com.ylc.one;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;

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
            //3、把字节输出流包装成打印流
            PrintStream ps = new PrintStream(os);
            ps.print("hello 服务器端你好");
            //4、发完要告诉服务器发送结束了
            socket.shutdownOutput();
            ps.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

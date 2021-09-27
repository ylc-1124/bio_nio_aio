package com.ylc.file;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

/**
 * 目标：实现客户端上传任意类型数据给服务器端保存起来
 */
public class Client {
    public static void main(String[] args) {


        try (
                InputStream is =
                        new FileInputStream("C:\\Users\\85370\\Pictures\\Screenshots\\a.png");
        ) {
            //1、请求与服务端的Socket连接
            Socket socket = new Socket("127.0.0.1", 8888);
            //2、把字节输出流包装成数据输出流
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            //3、先把发送上传文件的后缀给服务器
            dos.writeUTF(".png");
            //4、把文件数据发送给服务端接收
            byte[] buffer = new byte[1024];
            int len;
            while ((len = is.read(buffer)) != -1) {
                dos.write(buffer, 0, len);
            }
            dos.flush();
            socket.shutdownOutput();//通知服务端消息发送完毕了
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

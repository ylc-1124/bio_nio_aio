package com.ylc.file;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.net.Socket;
import java.util.UUID;

/**
 * 处理客户端请求的可运行任务类
 */
public class HandlerSocketRunnable implements Runnable {
    private Socket socket;

    public HandlerSocketRunnable(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            //1、得到数据输入流读取数据
            DataInputStream dis =
                    new DataInputStream(socket.getInputStream());
            //2、读取客户端发送的文件类型
            String suffix = dis.readUTF();
            System.out.println("服务器接收到文件类型:"+suffix);
            //3、定义一个数据输出流，把客户端发来的文件数据保存到硬盘
            BufferedOutputStream bos =
                    new BufferedOutputStream(new FileOutputStream("C:\\Users\\85370\\Pictures\\Saved Pictures\\" + UUID.randomUUID() + suffix));
            //4、从数据输入流中读取文件数据，写出到字节输出流中
            byte[] buffer = new byte[1024];
            int len;
            while ((len = dis.read(buffer)) != -1) {
                bos.write(buffer, 0, len);
            }
            bos.close();
            System.out.println("文件保存成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

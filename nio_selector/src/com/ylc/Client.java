package com.ylc;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.text.SimpleDateFormat;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) throws IOException {
        //1、获取通道
        SocketChannel sChannel =
                SocketChannel.open(new InetSocketAddress("127.0.0.1", 9999));
        //2、切换成非阻塞
        sChannel.configureBlocking(false);
        //3、分配缓冲区
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        //4、发送消息给服务器
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("请输入要发送的内容: ");
            String msg = scanner.nextLine();
            buffer.put(msg.getBytes());
            buffer.flip();
            sChannel.write(buffer);
            buffer.clear();
        }
    }
}

package com.ylc;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;

/**
 * 客户端
 */
public class Client {
    private static final String HOST = "127.0.0.1";
    private static final int PORT = 9999;
    private SocketChannel sChannel;
    private Selector selector;

    /**
     * 初始化一个客户端
     */
    public Client() {
        try {
            sChannel = SocketChannel.open(new InetSocketAddress(HOST, PORT));
            sChannel.configureBlocking(false);
            selector = Selector.open();
            //这里注册读事件是为了接收服务器转发的消息
            sChannel.register(selector, SelectionKey.OP_READ);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Client client = new Client();
        //开启一个线程监听收到的消息
        new Thread(() -> {
            try {
                client.readInfo();
            } catch (IOException e) {
                System.out.println("服务器宕机了");
            }
        }).start();
        //发送数据给服务器
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String msg = scanner.nextLine();
            client.sendToServer(msg);
        }
    }

    /**
     * 发送消息给服务器
     */
    private void sendToServer(String msg) {
        try {
            sChannel.write(ByteBuffer.wrap(msg.getBytes()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取服务器转发来的消息
     */
    private void readInfo() throws IOException {
        while (selector.select() > 0) {
            Iterator<SelectionKey> it = selector.selectedKeys().iterator();
            while (it.hasNext()) {
                SelectionKey sk = it.next();
                if (sk.isReadable()) {
                    ByteBuffer buffer = ByteBuffer.allocate(1024);
                    int len;
                    while ((len = sChannel.read(buffer)) > 0) {
                        buffer.flip();
                        System.out.println("from " + new String(buffer.array(), 0, len));
                        buffer.clear();
                    }
                }
                //事件完成要删除
                it.remove();
            }
        }
    }
}

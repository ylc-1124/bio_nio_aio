package com.ylc;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;

/**
 * 群聊系统--服务器端
 */
public class Server {
    private ServerSocketChannel ssChannel;
    private static final int PORT = 9999;
    private Selector selector;

    public Server() {
        try {
            //初始化服务器端属性
            ssChannel = ServerSocketChannel.open();
            ssChannel.configureBlocking(false);
            ssChannel.bind(new InetSocketAddress(PORT));
            selector = Selector.open();
            ssChannel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.listen();
    }

    /**
     * 监听事件
     */
    private void listen() {
        try {
            while (selector.select() > 0) {
                //遍历所有就绪事件
                Iterator<SelectionKey> it = selector.selectedKeys().iterator();
                while (it.hasNext()) {
                    SelectionKey sk = it.next();
                    //判断事件类型
                    if (sk.isAcceptable()) {
                        SocketChannel sChannel = ssChannel.accept();
                        sChannel.configureBlocking(false);
                        System.out.println(sChannel.getRemoteAddress()+"上线了");
                        sChannel.register(selector, SelectionKey.OP_READ);

                    } else if (sk.isReadable()) {
                        readClientData(sk);
                    }
                    it.remove();
                }
            }
        } catch (Exception e) {
           e.printStackTrace();
        }
    }

    /**
     * 读取客户端发来的消息
     */
    private void readClientData(SelectionKey sk) {
        SocketChannel sChannel = null;
        try {
            sChannel = (SocketChannel) sk.channel();
            //获取信道中的数据
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            int len;
            while ((len = sChannel.read(buffer)) > 0) {
                String msg = new String(buffer.array(), 0, len);
                System.out.println("from "+ sChannel.getRemoteAddress()+ " : " + msg);
                //将消息转发给其他在线客户端
                sendMsgToAllClient(msg,sChannel);
            }
        } catch (IOException e) {
            try {
                System.out.println(sChannel.getRemoteAddress()+"离线了");
                sk.cancel(); //将与这个离线客户端相关的事件取消注册
                sChannel.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * 将消息发送给所有其他在线的客户端
     */
    private void sendMsgToAllClient(String msg, SocketChannel sChannel) throws IOException {
       // System.out.println("服务器正在转发这条消息...");
        for (SelectionKey sk : selector.keys()) {
            SelectableChannel channel = sk.channel();
            if (channel instanceof SocketChannel
                    && channel != sChannel) {
                //这里给其他客户端转发消息时，将消息来自哪个进程的信息拼接在首部一起发送了
                ByteBuffer buffer =
                        ByteBuffer.wrap((sChannel.getRemoteAddress()+" : "+msg).getBytes());
                ((SocketChannel) channel).write(buffer);
            }
        }
    }


}

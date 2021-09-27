package com.ylc;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;

public class Server {
    public static void main(String[] args) throws IOException {
        System.out.println("===服务器端启动===");
        //1、获取ServerSocketChannel通道
        ServerSocketChannel ssChannel = ServerSocketChannel.open();
        //2、设置成非阻塞
        ssChannel.configureBlocking(false);
        //3、绑定连接
        ssChannel.bind(new InetSocketAddress(9999));
        //4、获取选择器
        Selector selector = Selector.open();
        //5、注册到选择器上,指定监听接收事件
        ssChannel.register(selector, SelectionKey.OP_ACCEPT);
        //6、轮询式获取选择器上已经准备就绪的事件
        while (selector.select() > 0) {
            //7、使用迭代器遍历选择器中所有注册的且准备就绪的事件
            Iterator<SelectionKey> it = selector.selectedKeys().iterator();
            while (it.hasNext()) {
                //8、获取准备就绪的事件
                SelectionKey sk = it.next();
                //9、判断当前获得的就绪事件具体是什么事件
                if (sk.isAcceptable()) {
                    //10、获取客户端连接
                    SocketChannel sChannel = ssChannel.accept();
                    //11、设置非阻塞
                    sChannel.configureBlocking(false);
                    //12、注册到选择器
                    sChannel.register(selector, SelectionKey.OP_READ);
                } else if (sk.isReadable()) {
                    //13、获取选择器上读就绪事件来自哪个通道
                    SocketChannel channel = (SocketChannel) sk.channel();
                    //14、创建缓冲区读取数据
                    ByteBuffer buffer = ByteBuffer.allocate(1024);
                    int len;
                    while ((len = channel.read(buffer)) > 0) {
                        buffer.flip();
                        System.out.println("收到客户端消息： " + new String(buffer.array(), 0, len));
                        buffer.clear();
                    }
                }
                //15、处理完的事件要删除
                it.remove();
            }
        }
    }
}

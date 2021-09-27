package com.ylc;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 目标：BIO模式下端口转发思想-服务器端实现
 * 思路：1、注册端口
 *      2、接收客户端的socket连接，交给独立的线程处理
 *      3、把当前连接的客户端socket存入一个所谓的在线socket集合中
 *      4、接收客户端的信息，然后转发给所有在线的socket接收
 */
public class Server {
    //定义一个静态的集合
    public static Set<Socket> onlineSockets = new HashSet<>();
    public static void main(String[] args) {
        try {
            ServerSocket ss = new ServerSocket(9999);
            while (true) {
                Socket socket = ss.accept();
                //把登录的客户端socket存入一个在线集合中
                onlineSockets.add(socket);
                //为当前登录成功的socket分配一个线程处理与之通信
                new Thread(new ServerReaderThread(socket)).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

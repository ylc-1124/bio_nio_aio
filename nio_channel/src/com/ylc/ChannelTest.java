package com.ylc;

import org.junit.Test;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class ChannelTest {
    @Test
    public void test2() throws IOException {
        //1、源文件输入流通道
        FileInputStream fis = new FileInputStream("data.txt");
        FileChannel fisChannel = fis.getChannel();
        //2、目标文件输出流通道
        FileOutputStream fos = new FileOutputStream("copy.txt");
        FileChannel fosChannel = fos.getChannel();
        //3、transferFrom复制数据
       // fosChannel.transferFrom(fisChannel, fisChannel.position(), fisChannel.size());
        fisChannel.transferTo(fisChannel.position(), fisChannel.size(), fosChannel);
        fisChannel.close();
        fosChannel.close();
    }
    @Test
    public void test() throws IOException {
        FileInputStream fis = new FileInputStream("data.txt");
        FileOutputStream fos = new FileOutputStream("copy.txt");
        FileChannel fisChannel = fis.getChannel();
        FileChannel fosChannel = fos.getChannel();
        //分散
        ByteBuffer buf1 = ByteBuffer.allocate(4);
        ByteBuffer buf2 = ByteBuffer.allocate(1024);
        ByteBuffer[] buffers = {buf1, buf2};
        fisChannel.read(buffers);
        //每个缓冲区都模式转换一下，顺便看看缓冲区的数据
        for (ByteBuffer buffer : buffers) {
            buffer.flip();
            System.out.println(new String(buffer.array(), 0, buffer.remaining()));
        }
        //聚集
        fosChannel.write(buffers);
        fisChannel.close();
        fosChannel.close();
        System.out.println("over");
    }
    @Test
    public void copy2() throws IOException {
        //获取源文件通道和目标文件通道
        RandomAccessFile srcFile =
                new RandomAccessFile("C:\\Users\\85370\\Pictures\\Screenshots\\a.png", "rw");
        FileChannel srcChannel = srcFile.getChannel();
        RandomAccessFile destFile =
                new RandomAccessFile("C:\\Users\\85370\\Pictures\\Saved Pictures\\copy.png", "rw");
        FileChannel destChannel = destFile.getChannel();
        //创建缓冲区
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        int flag;
        while ((flag = srcChannel.read(buffer)) != -1) {
            buffer.flip();
            destChannel.write(buffer);
            buffer.clear();
        }
        srcFile.close();
        destFile.close();
    }
    @Test
    public void copy() throws IOException {
        //源文件
        File src = new File("C:\\Users\\85370\\Pictures\\Screenshots\\a.png");
        //目标文件
        File dest = new File("C:\\Users\\85370\\Pictures\\Saved Pictures\\copy.png");
        //得到源文件的字节输入流
        FileInputStream fis = new FileInputStream(src);
        //得到目标文件的字节输出流
        FileOutputStream fos = new FileOutputStream(dest);
        //得到文件通道
        FileChannel fisChannel = fis.getChannel();
        FileChannel fosChannel = fos.getChannel();
        //分配缓冲区
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        //从源文件的字节输入流通道，读取数据到缓冲区，再写入目标文件字节输出流通道
        int flag;
        while ((flag = fisChannel.read(buffer)) != -1) {
            //切换模式，准备读取缓冲区数据并写入
            buffer.flip();
            fosChannel.write(buffer);
            buffer.clear(); //清空缓冲区
        }
        System.out.println("over");
        fisChannel.close();
        fosChannel.close();
    }
    @Test
    public void read() {
        try {
            //1、创建一个字节输入流对象与源文件产生联系
            FileInputStream fis = new FileInputStream("data.txt");
            FileChannel channel = fis.getChannel();
            //2、创建缓冲区读取文件中的数据
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            //3、从信道读取数据到缓冲区
            channel.read(buffer);
            //4、读的时候一定要切换到读模式，buffer.remaining()可以知道缓冲区有多少个数据
            buffer.flip();
            System.out.println(new String(buffer.array(),0,buffer.remaining()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Test
    public void write() {
        try {
            //1、创建字节输出流对象
            FileOutputStream fos = new FileOutputStream("data.txt");
            //2、获取信道
            FileChannel channel = fos.getChannel();
            //3、创建缓冲区
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            buffer.put("hello 你好 世界".getBytes());
            //4、缓冲区切换成读
            buffer.flip();
            channel.write(buffer);
            channel.close();
            System.out.println("over");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

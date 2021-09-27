package com.ylc;

import org.junit.Test;

import java.nio.ByteBuffer;

/**
 * 对缓冲区buffer常用api进行案例实现
 * Buffer clear() 清空缓冲区并返回对缓冲区的引用
 * Buffer flip() 为 将缓冲区的界限设置为当前位置，并将当前位置充值为 0
 * int capacity() 返回 Buffer 的 capacity 大小
 * boolean hasRemaining() 判断缓冲区中是否还有元素
 * int limit() 返回 Buffer 的界限(limit) 的位置
 * Buffer limit(int n) 将设置缓冲区界限为 n,并返回一个具有新 limit 的缓冲区对象
 * Buffer mark() 对缓冲区设置标记
 * int position() 返回缓冲区的当前位置 position
 * Buffer position(int n) 将设置缓冲区的当前位置为 n，并返回修改后的 Buffer 对象
 * int remaining() 返回 position 和 limit 之间的元素个数
 * Buffer reset() 将位置 position 转到以前设置的mark 所在的位置
 * Buffer rewind() 将位置设为为 0， 取消设置的 mark
 */
public class BuffetTest {
   @Test
   public void test3() {
      ByteBuffer buffer = ByteBuffer.allocateDirect(10);
      System.out.println(buffer.isDirect());

   }
   @Test
   public void test2() {
//      //1、分配一个缓冲区，容量设置为10
//      ByteBuffer buffer = ByteBuffer.allocate(10);
//      System.out.println(buffer.position()); //0
//      System.out.println(buffer.limit()); //10
//      System.out.println(buffer.capacity()); //10
//      System.out.println("===============");
//      buffer.put("sust".getBytes()); //默认UTF-8编码，一个中文三个字节
//      System.out.println(buffer.position()); //4
//      System.out.println(buffer.limit()); //10
//      System.out.println(buffer.capacity()); //10
//      System.out.println("===============");
//      //2、清除
//      buffer.clear();
//      System.out.println(buffer.position()); //0
//      System.out.println(buffer.limit()); //10
//      System.out.println(buffer.capacity()); //10
//      System.out.println("===============");
//      //清除只是把pos limit 复位 并没有删除数据
//      System.out.println(((char) buffer.get()));
      //3、定义一个缓冲区
      ByteBuffer buf = ByteBuffer.allocate(10);
      buf.put("sust".getBytes());
      buf.flip();
      byte[] bytes = new byte[2];
      buf.get(bytes);
      System.out.println(new String(bytes));
      System.out.println(buf.position()); //2
      System.out.println(buf.limit()); //4
      System.out.println(buf.capacity()); //10
      System.out.println("==============");
      buf.mark(); //标记当前位置

      byte[] b2 = new byte[2];
      buf.get(b2);
      System.out.println(new String(b2));
      System.out.println(buf.position()); //4
      System.out.println(buf.limit()); //4
      System.out.println(buf.capacity()); //10
      System.out.println("==============");

      //reset回到mark那
      buf.reset();
      if (buf.hasRemaining()) {
         System.out.println(buf.remaining());
      }

   }
   @Test
   public void test1() {
      //1、分配一个缓冲区，容量设置为10
      ByteBuffer buffer = ByteBuffer.allocate(10);
      System.out.println(buffer.position()); //0
      System.out.println(buffer.limit()); //10
      System.out.println(buffer.capacity()); //10
      System.out.println("===============");

      //2、put 往缓冲区中添加数据
      buffer.put("sust".getBytes()); //默认UTF-8编码，一个中文三个字节
      System.out.println(buffer.position()); //4
      System.out.println(buffer.limit()); //10
      System.out.println(buffer.capacity()); //10
      System.out.println("===============");

      //3、flip方法，转换成读模式 底层其实就是 limit=pos;  pos=0;
      buffer.flip();
      System.out.println(buffer.position()); //0
      System.out.println(buffer.limit()); //4
      System.out.println(buffer.capacity()); //10
      System.out.println("==================");

      //4、get读取数据
      char c = (char) buffer.get();
      System.out.println(c); // s
      System.out.println(buffer.position()); //1
      System.out.println(buffer.limit()); //4
      System.out.println(buffer.capacity()); //10
      System.out.println("==================");
   }
}

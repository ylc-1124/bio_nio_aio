# IO模型

## BIO

- 同步阻塞(传统阻塞型)，服务器实现模式为一个连接 一个线程，即客户端有连接请求时服务器端就需要启动 一个线程进行处理，如果这个连接不做任何事情会造成 不必要的线程开销

![在这里插入图片描述](https://img-blog.csdnimg.cn/20210323082329416.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3VuaXF1ZV9wZXJmZWN0,size_16,color_FFFFFF,t_70)

## NIO

- 同步非阻塞，服务器实现模式为一个线程处理 多个请求(连接)，即客户端发送的连接请求都会注册到多路复用器(selector)上，多路复用器轮询到连接有 I/O 请求就进行处理 

![在这里插入图片描述](https://img-blog.csdnimg.cn/20210323082552196.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3VuaXF1ZV9wZXJmZWN0,size_16,color_FFFFFF,t_70)

## AIO

- 异步非阻塞，服务器实现模式为一 个有效请求一个线程，客户端的I/O请求都是由OS先完成了 再通知服务器应用去启动线程进行处理，一般适用于连接数 较多且连接时间较长的应用

![在这里插入图片描述](https://img-blog.csdnimg.cn/20210323082936216.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3VuaXF1ZV9wZXJmZWN0,size_16,color_FFFFFF,t_70)

## BIO、NIO、AIO 适用场景分析

1. BIO方式适用于连接数目比较小且固定的架构，这种方式对服务器资源要求比较高，并发局限于应用中，JDK1.4以前的唯一选择，但程序简单易理解。
2.  NIO 方式适用于连接数目多且连接比较短（轻操作）的架构，比如聊天服务器，弹幕系统，服务器间通讯等。编程比较复杂，JDK1.4 开始支持。
3. AIO 方式使用于连接数目多且连接比较长（重操作）的架构，比如相册服务器，充分调用 OS 参与并发操作，编程比较复杂，JDK7 开始支持。
   

# JAVA BIO深入剖析

## Java BIO 基本介绍

- Java BIO 就是传统的 java io  编程，其相关的类和接口在 java.io
- BIO(blocking I/O) ： 同步阻塞，服务器实现模式为**一个连接一个线程**，即**客户端有连接请求时服务器端就需要启动一个线程进行处理**，如果这个连接不做任何事情会造成不必要的线程开销，可以通过线程池机制改善(实现多个客户连接服务器).

## Java BIO 工作机制

![image-20210925145538278](C:\Users\85370\AppData\Roaming\Typora\typora-user-images\image-20210925145538278.png)

- 对 BIO  编程流程的梳理 
- 1) 服务器端启动一个 ServerSocket，注册端口，调用accpet方 法监听客户端的Socket连接。 
  2)  客户端启动 Socket对服务器进行通信，默认情况下服务器端 需要对每个客户 建立一个线程与之通讯

## 传统的BIO编程实例回顾

- 服务器端代码

```java
/**
 * 目标：客户端发送消息，服务的接收消息
 */
public class Server {
    public static void main(String[] args) {
        try {
            //1、定义ServerSocket对象进行服务端的端口注册
            ServerSocket ss = new ServerSocket(9999);
            //2、监听客户端的Socket连接请求
            Socket socket = ss.accept();
            //3、从Socket管道中得到字节输入流对象
            InputStream is = socket.getInputStream();
            //4、把字节输入流通过转换流包装成--字符缓冲输入流
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(is));
            String msg;
            while ((msg = br.readLine()) != null) {
                System.out.println("服务端接收到:"+msg);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```

- 客户端代码

```java
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
            ps.println("hello 服务器端你好");
            //4、发完要告诉服务器发送结束了
            socket.shutdownOutput();
            ps.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
```

##  BIO模式下多发和多收消息

- 服务器端代码

```java
/**
 * 目标：服务端反复接收消息，客户端反复发送消息
 */
public class Server {
    public static void main(String[] args) {
        try {
            System.out.println("服务器端已启动...");
            //1、定义ServerSocket对象进行服务端的端口注册
            ServerSocket ss = new ServerSocket(9999);
            //2、监听客户端的Socket连接请求
            Socket socket = ss.accept();
            //3、从Socket管道中得到字节输入流对象
            InputStream is = socket.getInputStream();
            //4、把字节输入流通过转换流包装成--字符缓冲输入流
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(is));
            String msg;
            while ((msg = br.readLine()) != null) {
                System.out.println("接收到客户端消息:"+msg);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```

- 客户端代码

```java
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
            //3、通过扫描器监听键盘输入，获取数据由打印流发送到服务器端
            PrintStream ps = new PrintStream(os);
            Scanner scanner = new Scanner(System.in);
            while (scanner.hasNextLine()) {
                String msg = scanner.nextLine();
                ps.println(msg);
                ps.flush();
            }
            //4、发完要告诉服务器发送结束了
            socket.shutdownOutput();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
```

**小结** 

1. 在以上通信中，服务端会一致等待客户端的消息，如果客户 端没有进行消息的发送，服务端将一直进入阻塞状态。 
2. 同时服务端是按照行获取消息的，这意味着客户端也必须按 照行进行消息的发送，否则服务端将进入等待消息的阻塞状态！

## BIO模式下接收多个客户端

> 在上述的案例中，一个服务端只能接收一个客户端的通信请求，那么如果服务端需要处理很多个客户端的消息通信请求应该如何处理呢？

- 此时我们就需要在服务端引入线程了，也就是说客户端每发起一个请求，服务端就创建一个新的线程来处理这个客户端的请求，这样就实现了一个客户端一个线程的模型，图解模式如下：

![在这里插入图片描述](https://img-blog.csdnimg.cn/20210323092007730.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3VuaXF1ZV9wZXJmZWN0,size_16,color_FFFFFF,t_70)

- 服务器端代码

```java
/**
 * 目标：实现服务器端可以同时接收多个客户端的Socket通信需求
 * 思路：服务器端接收到一个Socket对象都开启一个线程来处理客户端数据交互需求
 */
public class Server {
    public static void main(String[] args) {
        try {
            //1、注册端口
            ServerSocket ss = new ServerSocket(9999);
            //2、监听客户端连接请求
            while (true) {
                Socket socket = ss.accept();
                //3、创建一个线程来处理需求
                new Thread(new ServerThreadReader(socket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```

- 服务器处理客户端业务逻辑类

```java
public class ServerThreadReader implements Runnable {
    private Socket socket;

    public ServerThreadReader(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        //从Socket对象中获取字节输入流
        try {
            InputStream is = socket.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String msg;
            while ((msg = br.readLine()) != null) {
                System.out.println("接收到客户端消息:" + msg);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```

- 客户端代码

```java
/**
 * 客户端
 */
public class Client {
    public static void main(String[] args) {

        try {
            //1、创建Socket对象请求服务端连接
            Socket socket = new Socket("127.0.0.1", 9999);
            //2、获取打印流
            OutputStream os = socket.getOutputStream();
            PrintStream ps = new PrintStream(os);
            //3、不断发送消息给服务器接收
            Scanner scanner = new Scanner(System.in);
            while (scanner.hasNextLine()) {
                String msg = scanner.nextLine();
                ps.println(msg);
                ps.flush();
            }
            socket.shutdownOutput();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```

**小结**

1.  每个Socket接收到，都会创建一个线程，线程的竞争、切换上下文影响性能；
2. 每个线程都会占用栈空间和CPU资源；
3. 并不是每个socket都进行IO操作，无意义的线程处理；  
4. 客户端的并发访问增加时。服务端将呈现1:1的线程开销，访问量越大，系统将发生线程栈溢出，线程创建失败，最终导致进程宕机或者僵死，从而不能对外提供服务。

## 伪异步I/O编程

### 概述

- 在上述案例中：客户端的并发访问增加时。服务端将呈现1:1的线程开销，访问量越大，系统将发生线程栈溢出，线程创建失败，最终导致进程宕机或者僵死，从而不能对外提供服务。

- 接下来我们采用一个伪异步I/O的通信框架，**采用线程池和任务队列实现**，当客户端接入时，将客户端的Socket封装成一个Task(该任务实现java.lang.Runnable线程任务接口)交给后端的线程池中进行处理。JDK的线程池维护一个消息队列和N个活跃的线程，对消息队列中Socket任务进行处理，由于线程池可以设置消息队列的大小和最大线程数，因此，它的资源占用是可控的，无论多少个客户端并发访问，都不会导致资源的耗尽和宕机。

![在这里插入图片描述](https://img-blog.csdnimg.cn/20210323141600962.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3VuaXF1ZV9wZXJmZWN0,size_16,color_FFFFFF,t_70)

- 服务器端

```java
/**
 * 目标：开发实现伪异步通信架构
 */
public class Server {
    public static void main(String[] args) {
        try {
            //1、注册端口
            ServerSocket ss = new ServerSocket(9999);
            //2、定义循环接收客户端的连接请求
            // 初始化线程池对象
            HandlerSocketThreadPool pool =
                    new HandlerSocketThreadPool(3, 10);
            while (true) {
                Socket socket = ss.accept();
                //3、把socket交给线程池处理
                //把socket封装成任务对象交给线程池处理
                pool.execute(new ServerRunnableTarget(socket));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

- 自定义一个创建线程池的类

```java
/**
 * 处理Socket的线程池
 */
public class HandlerSocketThreadPool {
    //1、创建一个线程池成员变量存储一个线程池对象
    private ExecutorService executorService;

    //2、创建此类时，初始化线程池对象
    public HandlerSocketThreadPool(int maxThreadNum, int queueSize) {
        executorService = new ThreadPoolExecutor(
                3,
                maxThreadNum,
                120,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(queueSize)
        );
    }

    //3、提供一个方法提交任务给线程池任务队列来暂存，等线程池处理
    public void execute(Runnable target) {
        executorService.execute(target);
    }
}

```

- 处理客户端发来的Socket的类

```java
public class ServerRunnableTarget implements Runnable {
    private Socket socket;

    public ServerRunnableTarget(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        //处理接收到的客户端Socket通信需求
        try {
            InputStream is = socket.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String msg;
            //线程会一直阻塞在这个readLine方法，等待接收消息
            //如果突然连接中断会抛出异常，可以人性化的处理一下这个异常，代表有客户端断开连接下线了
            while ((msg = br.readLine()) != null) {
                System.out.println("收到来自客户端的消息:" + msg);
            }
        } catch (IOException e) {
            System.out.println("有客户端下线了");
        }
    }
}
```

- 客户端

```java
/**
 * 客户端
 */
public class Client {
    public static void main(String[] args) {

        try {
            //1、创建Socket对象请求服务端连接
            Socket socket = new Socket("127.0.0.1", 9999);
            //2、获取打印流
            OutputStream os = socket.getOutputStream();
            PrintStream ps = new PrintStream(os);
            //3、不断发送消息给服务器接收
            Scanner scanner = new Scanner(System.in);
            while (scanner.hasNextLine()) {
                String msg = scanner.nextLine();
                ps.println(msg);
                ps.flush();
            }
            socket.shutdownOutput();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```

### 小结

- 伪异步io采用了线程池实现，因此避免了为每个请求创建一个
  独立线程造成线程资源耗尽的问题，但由于底层依然是采用
- 的同步阻塞模型，因此无法从根本上解决问题。
- 如果单个消息处理的缓慢，或者服务器线程池中的全部线程都
  被阻塞，那么后续socket的i/o消息都将在队列中排队。新的
  Socket请求将被拒绝，客户端会发生大量连接超时。

##  基于BIO形式下的文件上传

**支持任意类型文件形式的上传。**

- 客户端

```java
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
```

- 服务端

```java
/**
 * 目标：服务端开发，可以接收客户端任意类型文件并保存到服务端磁盘
 */
public class Server {
    public static void main(String[] args) {
        try {
            ServerSocket ss = new ServerSocket(8888);
            while (true) {
                Socket socket = ss.accept();
                //交给独立的线程处理这个客户端和服务器通信的需求
                new Thread(new HandlerSocketRunnable(socket)).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

- 处理客户端发送请求的可运行任务类

```java
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
```

## Java BIO模式下的端口转发思想

需求：需要实现一个客户端的消息可以发送给所有的客户端去接收。（群聊实现）

![在这里插入图片描述](https://img-blog.csdnimg.cn/20210323142048139.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3VuaXF1ZV9wZXJmZWN0,size_16,color_FFFFFF,t_70)

- 服务器端

```java
/**
 * 目标：BIO模式下端口转发思想-服务器端实现
 * 思路：1、注册端口
 *      2、接收客户端的socket连接，交给独立的线程处理
 *      3、把当前连接的客户端socket存入一个所谓的在线socket集合中
 *      4、接收客户端的信息，然后转发给所有在线的socket接收
 */
public class Server {
    //存储在线客户端socket的集合
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
```

- 服务器端开启独立线程处理客户端请求的类

```java
/**
 * 服务器端处理客户端请求的类
 */
public class ServerReaderThread implements Runnable {
    private Socket socket;

    public ServerReaderThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            //1、从socket获取当前客户端的输入流
            BufferedReader br =
                    new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String msg;
            while ((msg = br.readLine()) != null) {
                System.out.println("接收到客户端消息:" + msg);
                //2、发送给给其他在线的客户端socket
                sendMsgToAllClient(msg);
            }
        } catch (Exception e) {
            System.out.println("有客户端下线了");
            Server.onlineSockets.remove(socket);
        }
    }

    /**
     * 把当前客户端发来的消息推送给全部其他在线的socket
     */
    private void sendMsgToAllClient(String msg) throws IOException {
        for (Socket onlineSocket : Server.onlineSockets) {
            if (onlineSocket != socket) {
                PrintStream ps = new PrintStream(onlineSocket.getOutputStream());
                ps.println(msg);
                ps.flush();
            }
        }
    }
```

- 客户端

```java
/**
 * 客户端
 */
public class Client {
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("127.0.0.1", 9999);
            //开启一个独立线程监听服务器发送回来的消息
            new Thread(new ClientReaderThread(socket)).start();
            //发送数据给服务器
            PrintStream ps = new PrintStream(socket.getOutputStream());
            Scanner scanner = new Scanner(System.in);
            while (scanner.hasNextLine()) {
                String msg = scanner.nextLine();
                ps.println(msg);
                ps.flush();
            }
            socket.shutdownOutput();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

- 客户端开一个线程监听客户端发送来的消息

```java
/**
 * 监听服务器发送到客户端消息
 */
public class ClientReaderThread implements Runnable {
    private Socket socket;

    public ClientReaderThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        //监听服务器发送回来的消息
        try {
            BufferedReader br =
                    new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String msg;
            while ((msg = br.readLine()) != null) {
                System.out.println("收到一条消息:" + msg);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
```



# JAVA NIO深入剖析

## Java NIO 基本介绍

- Java NIO（New IO）也有人称之为 java non-blocking IO是从Java 1.4版本开始引入的一个新的IO API，可以替代标准的Java IO API。NIO与原来的IO有同样的作用和目的，但是使用的方式完全不同，**NIO支持面向缓冲区的、基于通道的IO操作**。NIO将以更加高效的方式进行文件的读写操作。**NIO可以理解为非阻塞IO**,传统的IO的read和write只能阻塞执行，线程在读写IO期间不能干其他事情，比如调用socket.read()时，如果服务器一直没有数据传输过来，线程就一直阻塞，而NIO中可以配置socket为非阻塞模式。


- NIO 相关类都被放在 java.nio 包及子包下，并且对原 java.io 包中的很多类进行改写。

- NIO 有三大核心部分：**Channel( 通道) ，Buffer( 缓冲区), Selector( 选择器)**

- Java NIO 的非阻塞模式，使一个线程从某通道发送请求或者读取数据，但是它仅能得到目前可用的数据，如果目前没有数据可用时，就什么都不会获取，而不是保持线程阻塞，所以直至数据变的可以读取之前，该线程可以继续做其他的事情。 非阻塞写也是如此，一个线程请求写入一些数据到某通道，但不需要等待它完全写入，这个线程同时可以去做别的事情。通俗理解：NIO 是可以做到用一个线程来处理多个操作的。假设有 1000 个请求过来,根据实际情况，可以分配20 或者 80个线程来处理。不像之前的阻塞 IO 那样，非得分配 1000 个。

## NIO 和 BIO 的比较

- BIO 以流的方式处理数据,而 NIO 以块的方式处理数据,块 I/O 的效率比流 I/O 高很多
- **BIO 是阻塞的，NIO 则是非阻塞的**
- **BIO 基于字节流和字符流进行操作**，而 **NIO 基于 Channel(通道)和Buffer(缓冲区)进行操作**，数据总是从通道读取到缓冲区中，或者从缓冲区写入到通道中。Selector(选择器)用于监听多个通道的事件（比如：连接请求，数据到达等），因此使用单个线程就可以监听多个客户端通道

| NIO                       | BIO                 |
| ------------------------- | ------------------- |
| 面向缓冲区（Buffer）      | 面向流（Stream）    |
| 非阻塞（Non Blocking IO） | 阻塞IO(Blocking IO) |
| 选择器（Selectors）       |                     |

## NIO 三大核心原理示意图

NIO 有三大核心部分： **Channel( 通道) ，Buffer( 缓冲区), Selector( 选择器)**

### **Buffer缓冲区**
缓冲区本质上是一块可以写入数据，然后可以从中读取数据的内存。这块内存被包装成NIO Buffer对象，并提供了一组方法，用来方便的访问该块内存。相比较直接对数组的操作，Buffer API更加容易操作和管理。

###  **Channel（通道）**
Java NIO的通道类似流，但又有些不同：既可以从通道中读取数据，又可以写数据到通道。但**流的（input或output)读写通常是单向**的。 **通道可以非阻塞读取和写入通道**，通道可以支持读取或写入缓冲区，也支持异步地读写。

### **Selector选择器**

Selector是 一个Java NIO组件，可以能够检查一个或多个 NIO 通道，并确定哪些通道已经准备好进行读取或写入。这样，一个单独的线程可以管理多个channel，从而管理多个网络连接，提高效率

![在这里插入图片描述](https://img-blog.csdnimg.cn/20210323145130359.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3VuaXF1ZV9wZXJmZWN0,size_16,color_FFFFFF,t_70)

- 每个 channel 都会对应一个 Buffer一个线程对应Selector ， 一个Selector对应多个 channel(连接)
- **程序切换到哪个 channel 是由事件决定的Selector 会根据不同的事件**，在各个通道上切换Buffer 就是一个内存块 ， 底层是一个数组
- 数据的读取写入是通过 Buffer完成的 , **BIO 中要么是输入流，或者是输出流, 不能双向，但是 NIO 的 Buffer 是可以读也可以写**。
- Java NIO系统的核心在于：**通道(Channel)和缓冲区 (Buffer)**。通道表示打开到 IO 设备(例如：文件、 套接字)的连接。若需要使用 NIO 系统，需要获取用于连接 IO 设备的通道以及用于容纳数据的缓冲区。然后操作缓冲区，对数据进行处理。简而言之，**Channel 负责传输， Buffer 负责存取数据**

##  NIO核心一：缓冲区(Buffer)

- 缓冲区（Buffer） 一个用于特定基本数据类型的容器。由 java.nio 包定义的，**所 有缓冲区 都是 Buffer 抽象类的子类**.。Java NIO 中 的 Buffer 主要用于与 NIO 通道进行交互，**数据是从通道读入缓冲区，从缓冲区写入通道中的**

![在这里插入图片描述](https://img-blog.csdnimg.cn/20210323153059284.png)

### Buffer 类及其子类

- Buffer就像一个数组，可以保存多个相同类型的数据。根据数据类型不同 ，有以下 Buffer 常用子类： 


ByteBuffer 
CharBuffer 
ShortBuffer 
IntBuffer 
LongBuffer 
FloatBuffer 
DoubleBuffer 

上述 Buffer 类他们都采用相似的方法进行管理数据，**只是各自管理的数据类型不同而已**。都是通过如下方法获取一个 Buffer 对象：

```java
static XxxBuffer allocate(int capacity) : 创建
一个容量为capacity 的 XxxBuffer 对象
```

### 缓冲区的基本属性

Buffer 中的重要概念： 

- **容量 (capacity)** ：作为一个内存块，Buffer具有一定的固定大小，也称为"容量"，缓冲区容量不能为负，并且创建后不能更改。 
  限制 (limit)：表示缓冲区中可以操作数据的大小（limit 后数据不能进行读写）。缓冲区的限制不能为负，并且不能大于其容量。 写入模式，限制等于buffer的容量。读取模式下，limit等于写入的数据量。

- **位置 (position)**：下一个要读取或写入的数据的索引。缓冲区的位置不能为 负，并且不能大于其限制 

- **标记 (mark)与重置 (reset)**：标记是一个索引，通过 Buffer 中的 mark() 方法 指定 Buffer 中一个特定的 position，之后可以通过调用 reset() 方法恢复到这 个 position.标记、位置、限制、容量遵守以下不变式： 0 <= mark <= position <= limit <= capacity

![在这里插入图片描述](https://img-blog.csdnimg.cn/2021032315392734.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3VuaXF1ZV9wZXJmZWN0,size_16,color_FFFFFF,t_70)

### Buffer常见方法

```java
Buffer clear() 清空缓冲区并返回对缓冲区的引用
Buffer flip() 为 将缓冲区的界限设置为当前位置，并将当前位置充值为 0
int capacity() 返回 Buffer 的 capacity 大小
boolean hasRemaining() 判断缓冲区中是否还有元素
int limit() 返回 Buffer 的界限(limit) 的位置
Buffer limit(int n) 将设置缓冲区界限为 n,并返回一个具有新 limit 的缓冲区对象
Buffer mark() 对缓冲区设置标记
int position() 返回缓冲区的当前位置 position
Buffer position(int n) 将设置缓冲区的当前位置为 n，并返回修改后的 Buffer 对象
int remaining() 返回 position 和 limit 之间的元素个数
Buffer reset() 将位置 position 转到以前设置的mark 所在的位置
Buffer rewind() 将位置设为为 0， 取消设置的 mark
```

### 缓冲区的数据操作

```java
Buffer 所有子类提供了两个用于数据操作的方法：get() put() 方法取获取 Buffer中的数据
get() ：读取单个字节
get(byte[] dst)：批量读取多个字节到 dst 中
get(int index)：读取指定索引位置的字节(不会移动 position)
    
数据放入到buffer中
put(byte b)：将给定单个字节写入缓冲区的当前位置
put(byte[] src)：将 src 中的字节写入缓冲区的当前位置
put(int index, byte b)：将指定字节写入缓冲区的索引位置(不会移动 position)
```

### 使用Buffer读写数据一般遵循以下四个步骤

1.  写入数据到Buffer 
2. 调用flip()方法，转换为读取模式 
3. 从Buffer中读取数据 
4. 调用buffer.clear()方法或者buffer.compact()方 法清除缓冲区

### 直接与非直接缓冲区

**什么是直接内存与非直接内存**

根据官方文档的描述：

- byte byffer可以是两种类型，**一种是基于直接内存（也就是非堆内存）；另一种是非直接内存（也就是堆内存）**。对于直接内存来说，JVM将会在IO操作上具有更高的性能，因为它直接作用于本地系统的IO操作。而非直接内存，也就是堆内存中的数据，如果要作IO操作，会先从本进程内存复制到直接内存，再利用本地IO处理。

- 从数据流的角度，非直接内存是下面这样的作用链：**本地IO-->直接内存-->非直接内存-->直接内存-->本地IO**


- 而直接内存是：**本地IO-->直接内存-->本地IO**

- 很明显，在做IO处理时，比如网络发送大量数据时，直接内存会具有更高的效率。直接内存使用allocateDirect创建，但是它比申请普通的堆内存需要耗费更高的性能。不过，这部分的数据是在JVM之外的，因此它不会占用应用的内存。所以呢，当你有很大的数据要缓存，并且它的生命周期又很长，那么就比较适合使用直接内存。只是一般来说，如果不是能带来很明显的性能提升，还是推荐直接使用堆内存。字节缓冲区是直接缓冲区还是非直接缓冲区可通过调用其 isDirect()  方法来确定。


**使用场景**

1. 有很大的数据需要存储，它的生命周期又很长
2. 适合频繁的IO操作，比如网络并发场景



## NIO核心二：通道(Channel)

通道Channe概述

- 通道（Channel）：由 java.nio.channels 包定义 的。Channel 表示 IO 源与目标打开的连接。 Channel 类似于传统的“流”。只不过 Channel 本身不能直接访问数据，Channel 只能与 Buffer 进行交互。


- NIO 的通道类似于流，但有些区别如下：


1. **通道可以同时进行读写**，而流只能读或者只能写通道可以实现异步读写数据通道可以从缓冲读数据，也可以写数据到缓冲:
2. **BIO 中的 stream 是单向的**，例如 FileInputStream 对象只能进行读取数据的操作，而 NIO 中的通道(Channel)是双向的，可以读操作，也可以写操作。
3. Channel 在 NIO 中是一个接口

```java
public interface Channel extends Closeable{}
```

### 常用的Channel实现类

- FileChannel：用于读取、写入、映射和操作文件的通道。
- DatagramChannel：通过 UDP 读写网络中的数据通道。
- SocketChannel：通过 TCP 读写网络中的数据。
- ServerSocketChannel：可以监听新进来的 TCP 连接，对每一个新进来的连接都会创建一个 SocketChannel。 
  【ServerSocketChanne 类似 ServerSocket , SocketChannel 类似 Socket】

### FileChannel 类

- 获取通道的一种方式是对支持通道的对象调用getChannel() 方法。
  支持通道的类如下：

  FileInputStream
  FileOutputStream
  RandomAccessFile
  DatagramSocket
  Socket
  ServerSocket

- 获取通道的其他方式是使用 Files 类的静态方法 newByteChannel() 获取字节通道。
- 或者通过通道的静态方法 open() 打开并返回指定通道

### FileChannel的常用方法

```java
int read(ByteBuffer dst) 从Channel到中读取数据到ByteBuffer
long  read(ByteBuffer[] dsts) 将Channel到中的数据“分散”到ByteBuffer[]
int  write(ByteBuffer src)将ByteBuffer 到中的数据写入到  Channel
long write(ByteBuffer[] srcs)将ByteBuffer[] 到中的数据“聚集”到  Channel
long position() 返回此通道的文件位置
FileChannel position(long p) 设置此通道的文件位置
long size() 返回此通道的文件的当前大小
FileChannel truncate(long s) 将此通道的文件截取为给定大小
void force(boolean metaData) 强制将所有对此通道的文件更新写入到存储设备中
```

### 案例1-本地文件写数据

```java
public class ChannelTest {
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
```

### 案例2-本地文件读数据

```java
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
```

### 案例3-使用Buffer完成文件复制

方式一

```java
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
```

方式二

```java
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
```

### 案例4-分散 (Scatter) 和聚集 (Gather)

```java
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
```

### 案例5-transferFrom()

```java
 //1、源文件输入流通道
 FileInputStream fis = new FileInputStream("data.txt");
 FileChannel fisChannel = fis.getChannel();
 //2、目标文件输出流通道
 FileOutputStream fos = new FileOutputStream("copy.txt");
 FileChannel fosChannel = fos.getChannel();
 //3、transferFrom复制数据
 fosChannel.transferFrom(fisChannel, fisChannel.position(), fisChannel.size());
 fisChannel.close();
 fosChannel.close();
```

### 案例6-transferTo()

```java
 //1、源文件输入流通道
 FileInputStream fis = new FileInputStream("data.txt");
 FileChannel fisChannel = fis.getChannel();
 //2、目标文件输出流通道
 FileOutputStream fos = new FileOutputStream("copy.txt");
 FileChannel fosChannel = fos.getChannel();
 //3、transferTo复制数据
 fisChannel.transferTo(fisChannel.position(), fisChannel.size(), fosChannel);
 fisChannel.close();
 fosChannel.close();
```

## NIO核心三：选择器(Selector)

### 选择器(Selector)概述

![image-20210926213125615](C:\Users\85370\AppData\Roaming\Typora\typora-user-images\image-20210926213125615.png)

- 选择器（Selector） 是 SelectableChannle 对象的多路复用器，Selector 可以同时监控多个 SelectableChannel 的 IO 状况，也就是说，**利用 Selector可使一个单独的线程管理多个 Channel**。**Selector 是非阻塞 IO 的核心**

- Java 的 NIO，用非阻塞的 IO 方式。可以用一个线程，处理多个的客户端连接，就会使用到 Selector(选择器)
- **Selector 能够检测多个注册的通道上是否有事件发生**(注意:多个 Channel 以事件的方式可以注册到同一个Selector)，**如果有事件发生，便获取事件然后针对每个事件进行相应的处理**。这样就可以只用一个单线程去管理多个通道，也就是管理多个连接和请求。
- **只有在 连接/通道 真正有读写事件发生时，才会进行读写**，就大大地减少了系统开销，并且不必为每个连接都创建一个线程，不用去维护多个线程避免了多线程之间的上下文切换导致的开销

### 选择器（Selector）的应用

- 创建 Selector ：通过调用 Selector.open() 方法创建一个 Selector对象。

```java
Selector selector = Selector.open();
向选择器注册通道：SelectableChannel.register(Selector sel, int ops)
```

```java
//1. 获取通道
ServerSocketChannel ssChannel = ServerSocketChannel.open();
//2. 切换非阻塞模式
ssChannel.configureBlocking(false);
//3. 绑定连接
ssChannel.bind(new InetSocketAddress(9898));
//4. 获取选择器
Selector selector = Selector.open();
//5. 将通道注册到选择器上, 并且指定“监听接收事件”
ssChannel.register(selector, SelectionKey.OP_ACCEPT);
```

```java
当调用 register(Selector sel, int ops) 将通道注册选择器时，选择器对通道的监听事件，需要通过第二个参数 ops 指定。可以监听的事件类型（可使用 SelectionKey 的四个常量表示）：

读 : SelectionKey.OP_READ （1）
写 : SelectionKey.OP_WRITE （4）
连接 : SelectionKey.OP_CONNECT （8）
接收 : SelectionKey.OP_ACCEPT （16）
    
若注册时不止监听一个事件，则可以使用“位或”操作符连接。
int interestSet = SelectionKey.OP_READ|SelectionKey.OP_WRITE 

```

### NIO非阻塞式网络通信原理分析

### Selector 示意图和特点说明

Selector可以实现： 

一个 I/O 线程可以并发处理 N 个客户 端连接和读写操作，这从根本上解决了传统同步阻塞 I/O 一 连接一线程模型，架构的性能、弹性伸缩能力和可靠性 都得到了极大的提升。

![在这里插入图片描述](https://img-blog.csdnimg.cn/20210323172636252.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3VuaXF1ZV9wZXJmZWN0,size_16,color_FFFFFF,t_70)

- 服务器

```java
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
```

- 客户端

```java
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
```

### NIO 网络编程应用实例-群聊系统

- 服务器端

```java
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
```

- 客户端

```java
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
```

# JAVA AIO并没有深入刨析

## AIO编程

- Java AIO(NIO.2) ： 异步非阻塞，服务器实现模式为一个有效请求一个线程，客户端的I/O请求都是由OS先完成了再通知服 务器应用去启动线程进行处理。

```java
AIO
异步非阻塞，基于NIO的，可以称之为NIO2.0
    BIO                   NIO                              AIO        
Socket                SocketChannel                    AsynchronousSocketChannel
ServerSocket          ServerSocketChannel	       AsynchronousServerSocketChannel
```

- 与NIO不同，当进行读写操作时，只须直接调用API的read或write方法即可, 这两种方法均为异步的，对于读操作而言，当有流可读取时，操作系统会将可读的流传入read方法的缓冲区,对于写操作而言，当操作系统将write方法传递的流写入完毕时，操作系统主动通知应用程序


- 即可以理解为，read/write方法都是异步的，完成后会主动调用回调函数。在JDK1.7中，这部分内容被称作NIO.2，主要在Java.nio.channels包下增加了下面四个异步通道：

```java
AsynchronousSocketChannel
AsynchronousServerSocketChannel
AsynchronousFileChannel
AsynchronousDatagramChannel
```

# 总结

**BIO、NIO、AIO：**

- **Java BIO** ： 同步并阻塞，服务器实现模式为一个连接一个线程，即客户端有连接请求时服务器端就需要启动一个线程进行处理，如果这个连接不做任何事情会造成不必要的线程开销，当然可以通过线程池机制改善。
- **Java NIO** ： 同步非阻塞，服务器实现模式为一个请求一个线程，即客户端发送的连接请求都会注册到多路复用器上，多路复用器轮询到连接有I/O请求时才启动一个线程进行处理。
- Java AIO(NIO.2) ： 异步非阻塞，服务器实现模式为一个有效请求一个线程，客户端的I/O请求都是由OS先完成了再通知服务器应用去启动线程进行处理。
  

## BIO、NIO、AIO适用场景分析:

- BIO方式适用于连接数目比较小且固定的架构，这种方式对服务器资源要求比较高，并发局限于应用中，JDK1.4以前的唯一选择，但程序直观简单易理解。NIO方式适用于连接数目多且连接比较短（轻操作）的架构，比如聊天服务器，并发局限于应用中，编程比较
  复杂，JDK1.4开始支持。
- AIO方式使用于连接数目多且连接比较长（重操作）的架构，比如相册服务器，充分调用OS参与并发操作，编程比较复杂，JDK7开始支持。Netty!
  

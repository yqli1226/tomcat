package tomcat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * @program: tomcat
 * @description: tomcat实现   NIO模式实现tomcat服务
 * @author: Mr.Li
 * @create: 2021-03-09 01:29
 **/
public class CannelSocketTest {

    public void start() throws IOException {
        //建立nio通道
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);//设置非阻塞状态
        ServerSocket socket = ssc.socket();
        System.out.println("启动web服务");
        socket.bind(new InetSocketAddress(8888));

        while(true){
            SocketChannel channel = ssc.accept();
            if(channel != null){
                Thread thread = new Thread(new HttpServerThread(channel));
                thread.start();
            }
        }
    }

    private class HttpServerThread implements Runnable{
        SocketChannel channel;

        HttpServerThread(SocketChannel channel){
            this.channel = channel;
        }

        @Override
        public void run() {
            if(channel != null){
                try{
                    ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                    InetSocketAddress remoteAddress = (InetSocketAddress) channel.getRemoteAddress();
                    channel.read(byteBuffer);
                    byteBuffer.flip();
                    while (byteBuffer.hasRemaining()){
                        char c = (char) byteBuffer.get();
                        System.out.println(c);
                    }
                    System.out.println(Thread.currentThread().getName() + "开始向web返回消息...");
                    ByteBuffer byteBuffer2 = ByteBuffer.allocate(1024);
                    String reply = "HTTP/1.1\n";//必须添加的响应头
                    reply += "Content-type:text/html\n\n";//必须添加的响应头
                    reply += "Server returns information";
                    byteBuffer2.put(new String(reply).getBytes());
                    byteBuffer2.flip();
                    channel.write(byteBuffer2);
                    channel.close();
                }catch(IOException e){
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {
        new CannelSocketTest().start();
    }
}

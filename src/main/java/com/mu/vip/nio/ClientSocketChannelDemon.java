package com.mu.vip.nio;

import com.mu.vip.Buffer.Buffers;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

public class ClientSocketChannelDemon {


    public static class ClientByNio implements Runnable {
        private String id;
        private InetSocketAddress inetSocketAddress;

        public ClientByNio(String id, InetSocketAddress inetSocketAddress) {
            this.id = id;
            this.inetSocketAddress = inetSocketAddress;
        }

        @Override
        public void run() {

            Selector selector ;
            Charset utf8 = Charset.forName("UTF-8");
            try {
                //得到socketchannel
                SocketChannel sc = SocketChannel.open();
                sc.configureBlocking(false);

                //得到selector
                selector = Selector.open();

                int interests = SelectionKey.OP_WRITE | SelectionKey.OP_READ;

                //注册socketchannel事件
                sc.register(selector, interests, new Buffers(256, 256));
                sc.connect(inetSocketAddress);
                //请求连接，一旦没有连接上，当事件准备好后，就会唤醒
                while (!sc.finishConnect()) {
                    //当没有事件准备好后，可以执行这个里面的内容，
                }
                System.out.println(id + "连接服务器成功");
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println(id + "连接服务器失败");
                return;
            }

            try {

                    int i = 1;
                while (!Thread.currentThread().isInterrupted()) {

                    //阻塞，只有等到有channel可用才会接通
                    selector.select();

                    Set<SelectionKey> Selectionkeys = selector.selectedKeys();
                    Iterator<SelectionKey> it = Selectionkeys.iterator();

                    while (it.hasNext()) {
                        SelectionKey selectionKey = it.next();
                        it.remove();
                        //获得缓冲区的流
                        Buffers buffers = (Buffers) selectionKey.attachment();
                        ByteBuffer bw = buffers.getWriteBuffer();
                        ByteBuffer br = buffers.getReadBuffer();

                        SocketChannel sc = (SocketChannel) selectionKey.channel();

                        if (selectionKey.isReadable()) {
                            sc.read(br);
                            br.flip();
                           CharBuffer cb= utf8.decode(br);
                            System.out.println(cb.array());
                            br.clear();
                        }
                        //可写数据
                        if (selectionKey.isWritable()) {
                            bw.put(("我是" + id + "第" + i + "次进来").getBytes("UTF-8"));
                            bw.flip();
                            sc.write(bw);
                            bw.clear();
                            i++;
                        }
                        //可读数据
                    }

                    Thread.sleep(500);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                try {
                    selector.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println(id + "selector关闭失败");
                }
            }
        }
    }

    public static void main(String[] args) {
        InetSocketAddress inetSocketAddress = new InetSocketAddress("127.0.0.1", 8888);
        Thread t1=new Thread(new ClientByNio("1客户端", inetSocketAddress));
        Thread t2=new Thread(new ClientByNio("2客户端", inetSocketAddress));
        Thread t3=new Thread(new ClientByNio("3客户端", inetSocketAddress));
        t1.start();
        t2.start();
        t3.start();

    }
}

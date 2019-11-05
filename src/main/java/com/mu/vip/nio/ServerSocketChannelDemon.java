package com.mu.vip.nio;

import com.mu.vip.Buffer.Buffers;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

public class ServerSocketChannelDemon {


    public static class ServerByNioimplements implements Runnable {

        //        private final int SERVER_port = 8888;
        InetSocketAddress localAddress;

        public ServerByNioimplements(int port) {

            this.localAddress = new InetSocketAddress(port);
        }
        @Override
        public void run() {
            Selector selector = null;
            ServerSocketChannel ssc = null;
            Charset utf8 = Charset.forName("UTF-8");
            try {
                selector = Selector.open();
                ssc = ServerSocketChannel.open();
                ssc.configureBlocking(false);
                ssc.bind(localAddress, 100);
//                int interest = SelectionKey.OP_READ | SelectionKey.OP_WRITE | SelectionKey.OP_CONNECT;
                ssc.register(selector, SelectionKey.OP_ACCEPT);

            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("开启服务失败");
            }
            System.out.println("开启服务成功，地址是：" + localAddress);

            try {
                //设置线程中断会退出
                while (!Thread.currentThread().isInterrupted()) {
                    int n = selector.select();
                    if (n == 0) {
                        continue;
                    }
                    Set<SelectionKey> selectionKeySet = selector.selectedKeys();
                    Iterator<SelectionKey> it = selectionKeySet.iterator();
                    SelectionKey selectionKey=null;
                    while (it.hasNext()) {
                        selectionKey = it.next();
                        it.remove();
                        try {
                            if (selectionKey.isAcceptable()) {
                                SocketChannel sc = ssc.accept();
                                sc.configureBlocking(false);
//                                int interestSet=SelectionKey.OP_READ;
                                sc.register(selector, SelectionKey.OP_READ, new Buffers(256, 256));
                                System.out.println("接收到来自：" + sc.getRemoteAddress() + "请求");
                            }
                            if (selectionKey.isReadable()) {
                                Buffers buffers = (Buffers) selectionKey.attachment();
                                ByteBuffer br = buffers.getReadBuffer();
                                ByteBuffer bw = buffers.getWriteBuffer();

                                SocketChannel sc = (SocketChannel) selectionKey.channel();
                                sc.read(br);
                                br.flip();
                                CharBuffer cbl = utf8.decode(br);
                                System.out.println("收到客户端发的内容是：" + cbl.array());
                                br.rewind();
                                //只是准备信息并写入到channel缓冲区
                                bw.put(("客户端已经收到你的：" + cbl.array() + "信息").getBytes("UTF-8"));
                                bw.put(br);
                                br.clear();
                                //设置channel可写
                                selectionKey.interestOps(selectionKey.interestOps() | SelectionKey.OP_WRITE);
                            }
                            if (selectionKey.isWritable()) {
                                Buffers buffers = (Buffers) selectionKey.attachment();
                                ByteBuffer bw = buffers.getWriteBuffer();
                                bw.flip();
                                SocketChannel sc = (SocketChannel) selectionKey.channel();
                                int len = 0;
                                while (bw.hasRemaining()) {
                                    len = sc.write(bw);

                                    //写完到莫问，返回为0
                                    if (len == 0) {
                                        break;
                                    }
                                }
                                bw.compact();
                                //一直写不完，认为是连接错误，取消通道
                                if (len != 0) {
                                    selectionKey.interestOps(selectionKey.interestOps() & (~SelectionKey.OP_WRITE));
                                }
                            }
                        } catch (IOException e) {
                            System.out.println("服务端通道连接异常");
                            selectionKey.channel();
                            selectionKey.channel().close();
                        }
                    }
                    Thread.sleep(50);
                }
            } catch (ClosedChannelException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                try {
                    selector.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("selector关闭失败");
                }
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Thread thread = new Thread(new ServerByNioimplements(8888));
        thread.start();
//        thread.sleep(20000);
//        thread.interrupt();

    }
}

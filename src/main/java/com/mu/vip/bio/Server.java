package com.mu.vip.bio;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private final static int SERVER_PORT = 7777;
    private static ServerSocket server;

    public static void start() throws IOException {
        start(SERVER_PORT);
    }

    public synchronized static void start(int port) {

        try {
            server = new ServerSocket(port);
        System.out.println("1号上线");
        while (true) {
            //如果没有客户端接入，将阻塞在accept上
            Socket socket = server.accept();
            new Thread(new ServerHandler(socket)).start();

        }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (server!=null){
                try {
                    server.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

    }
}

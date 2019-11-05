package com.mu.vip.bio;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private static int CLIENT_PORT = 7777;
    private static String CLIENT_IP = "127.0.0.1";

    public static void sent() {
        start(CLIENT_IP, CLIENT_PORT);
    }

    public static void start(String ip, int port) {
        Socket socket = null;
        PrintWriter out = null;

        try {
            while (true) {
                socket = new Socket(CLIENT_IP, CLIENT_PORT);
                System.out.println("请输入信息：");
                Scanner sc = new Scanner(System.in);
                String message = sc.next();
                out = new PrintWriter(socket.getOutputStream(), true);
                out.println(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                out.close();
                out = null;
            }
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                socket = null;
            }
        }


    }
}

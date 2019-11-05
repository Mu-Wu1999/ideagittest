package com.mu.vip.bio;

import java.io.IOException;

public class Test {
    public static void main(String[] args) {
        new Thread(new Runnable() {
            public void run() {

                try {
                    Server.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();



        new Thread(new Runnable() {
            public void run() {
                    Client.sent();
            }
        }).start();
    }
}

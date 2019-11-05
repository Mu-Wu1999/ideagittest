package com.mu.vip.bio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ServerHandler implements Runnable {
    private Socket socket;

    public ServerHandler(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        BufferedReader in=null;
        try {

            String message;
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while (true) {
                if ((message = in.readLine()) == null) break;
                System.out.println(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(in!=null){
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                in=null;
            }
        }

    }
}

package com.mu.vip.Buffer;

import java.nio.ByteBuffer;

public class Buffers {
    ByteBuffer ReadBuffer;
    ByteBuffer WriteBuffer;


    public Buffers(int capacity1,int capacity2) {
        ReadBuffer=ByteBuffer.allocate(capacity1);
        WriteBuffer=ByteBuffer.allocate(capacity2);

    }
    public ByteBuffer getReadBuffer(){
        return ReadBuffer;
    }
    public ByteBuffer getWriteBuffer(){
        return WriteBuffer;
    }
}

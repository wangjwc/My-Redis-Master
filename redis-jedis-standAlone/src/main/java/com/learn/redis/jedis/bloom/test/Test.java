package com.learn.redis.jedis.bloom.test;

import com.sun.tools.internal.ws.wsdl.document.Output;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.*;

/**
 * @author wangjingwang
 * @version v1.0
 */
public class Test {

    public static void main(String[] args) throws Exception {

//        File file = new File("/Users/wangjingwang/work/IdeaProjects/learn/Redis/My-Redis-Master/t.txt");
//
//        OutputStream outputStream = new FileOutputStream(file);
//        for (long i=0; i<128; i++) {
//            Long l = i + 10000;
//            System.out.println(l);
//            outputStream.write(LongToBytes(l));
//        }
//        outputStream.close();

    }

    public static byte[] LongToBytes(long values) {
        byte[] buffer = new byte[8];
        for (int i = 0; i < 8; i++) {
            int offset = 64 - (i + 1) * 8;
            buffer[i] = (byte) ((values >> offset) & 0xff);
        }
        return buffer;
    }
}

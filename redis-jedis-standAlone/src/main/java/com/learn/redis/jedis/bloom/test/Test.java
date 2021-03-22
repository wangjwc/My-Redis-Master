package com.learn.redis.jedis.bloom.test;

import java.util.*;

/**
 * @author wangjingwang
 * @version v1.0
 */
public class Test {
    public static List<String> randomList(int size) {
        List<String> list = new ArrayList<>(size);
//        Random random = new Random(47);
//        for (int i=0; i<size; i++) {
//            StringBuilder sb = new StringBuilder();
//            sb.append(random.nextInt());
//            sb.append(random.nextInt());
//            sb.append(random.nextInt());
//            sb.append("_").append(i);
//            list.add(sb.toString());
//        }
        for (int i = 0; i < size; i++) {
            list.add(UUID.randomUUID().toString());
        }
        return list;
    }

    public static void main(String[] args){
        List<String> l = randomList(10);
        List<String> l2 = randomList(10);

        for (String s : l) {
            System.out.println(s);
        }


        l.addAll(l2);
        System.out.println(new HashSet<>(l).size());

    }
}

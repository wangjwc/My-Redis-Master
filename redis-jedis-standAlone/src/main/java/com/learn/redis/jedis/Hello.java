package com.learn.redis.jedis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.HashMap;
import java.util.Map;

public class Hello {

	public static void key() {

		JedisPoolConfig poolConfig = new JedisPoolConfig();

		JedisPool jedisPool = new JedisPool(poolConfig,"192.168.25.153", 6379, 2, null);

		//Jedis jedis = new Jedis("localhost",6379,2);

		Jedis jedis = jedisPool.getResource();

		jedis.hset("user","user_01","{id:user_01,name:user_1}");

		jedis.hgetAll("user");

		if(!jedis.ping().equals("PONG")){
			System.out.println("Connection to server error");
			return;
		}
		System.out.println("Connection to server sucessfully");

		Map<String,String> user =  new HashMap<String,String>();
		user.put("name" ,  "cd" );
		jedis.hmset( "user" , user);


		jedis.lpush( "listDemo" ,  "A" );
		jedis.lpush( "listDemo" ,  "B" );
		jedis.lpush( "listDemo" ,  "C" );

		jedis.set("key","testKey");//设置key
		Out.out("get key",jedis.get("key"));//获取键值
		Out.out("dump key",jedis.dump("key"));//序列化的键值
		Out.out("exists key",jedis.exists("key"));//判断键是否存在
		Out.out("keys k*",jedis.keys("k*"));//查找与指定模式匹配的所有键

		jedis.expire("key",10);//设置键n秒后到期

		jedis.setex("setex",5,"123");

		Out.out("pttl key",jedis.pttl("key"));//获取剩余到期时间(毫秒)
		Out.out("ttl key",jedis.ttl("key"));  //获取剩余到期时间(间秒)
		Out.out("type key",jedis.type("key"));//返回键的数据类型
		Out.out("strlen key",jedis.strlen("key"));//返回指定键值的长度

		jedis.rename("key","newKey");
		Out.out("get key",jedis.get("key"),jedis.get("newKey"));//获取键值

		Out.out("randomKey",jedis.randomKey());//随机返回一个键（鸟用？）

		jedis.del("newKey");//删除key
		Out.out("after del",jedis.get("key"),jedis.get("newKey"));//获取键值

		jedis.del("key1","key2","key3","key4");
		jedis.set("key1","value1");
		jedis.set("key2","value2");
		jedis.set("key3","value3");
		Out.out("mget",jedis.mget("key1","key2","key3","key4"));//得到所有的给定键的值
		jedis.set("key2","nx2");
		jedis.setnx("key3","nx3");	//当键不存在时设置
		jedis.setnx("key4","nx4");
		Out.out("mget",jedis.mget("key1","key2","key3","key4"));

		jedis.mset("key1","mset1","key2","mset2","key3","mset3");
		Out.out("mget",jedis.mget("key1","key2","key3","key4"));
		jedis.append("key1"," append str");
		Out.out("get key1",jedis.get("key1"));

		jedis.close();
	}


	public static void main(String[] args) throws InterruptedException {
		key();
		//Jedis jedis = new Jedis("localhost");
		//System.out.println("Connection to server sucessfully "+jedis.isConnected());
//		System.out.println("Server is running: " + jedis.ping());
		// jedis.set("key1", "key1_value");
	     //System.out.println("key1:"+ jedis.get("key1"));
//	     /jedis.del("key1");
//	     System.out.println("key1:"+ jedis.get("key1"));
//
//	     System.out.println("key1:"+ jedis.exists("key1"));
//	     for(int i=0;i<10;i++){
//	    	 jedis.lpush("testLs", "testLs_"+i);
//	     }
//
//	     List<String> list = jedis.lrange("testLs", 0 ,9);
//	     for(int i=0; i<list.size(); i++) {
//	      // System.out.println("Stored string in redis:: "+list.get(i));
//	     }

	}
}

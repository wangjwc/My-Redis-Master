package com.learn.redis.jedis;

import java.lang.reflect.Array;
import java.util.Collection;

public class Out {
	public static void out(Collection<?> c){
		if(null!=c && c.size() > 0){
			Object[] arr = c.toArray();
			for(Object o:arr){
				System.err.println(null == o?"null":o.toString());
			}
		}
	}
	public static void out(Object... msgs ){
		//System.err.println(arrToString(msgs));
		outArr(msgs);
	}
	
	private static void outArr(Object[] msgs ){
		if(null != msgs){
			for(int n =0;n<msgs.length;n++){
				Object o = msgs[n];
				if(null != o){
					if(o.getClass().isArray()){
						System.err.print(arrayObjToString(o));
					}else{
						System.err.print(null == o?"null":o.toString());
					}
				}else{
					System.err.print("null");
				}
				
				System.err.print(n == msgs.length-1?"\n":",");
			}			
		}
	}
	private static String arrayObjToString(Object arrayObj){
		StringBuffer s = new StringBuffer();
		int length,index,lastIndex;
		if(null == arrayObj){
			s.append("[null");
		}else if(!arrayObj.getClass().getComponentType().isPrimitive()){
			Object[] arr = (Object[]) arrayObj;
			length = arr.length;
			lastIndex = length - 1;
			s.append("[");
			for(index=0;index<length;index++){
				Object o = arr[index];
				s.append(null == o?"null":(String)o);
				s.append(index==lastIndex?"":",");
			}
			s.append("]");
		}else{//基本类型的数组
			length = Array.getLength(arrayObj);
			lastIndex = length - 1;
			s.append("[");
			for(index=0;index < length;index++){
				Object o = Array.get(arrayObj, index);
				s.append(null == o?"null":String.valueOf(o));
				s.append(index==lastIndex?"":",");
			}
			s.append("]");
			
		}
		return s.toString();
	}
}

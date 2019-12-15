package com.vcarecity.utils;

import com.vcarecity.ssdb.Cluster;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

//java代码：生成一个从x到y之间的随机数（整数）
/**
 * 一、思路：
 * 1、生成随机数需要使用到Java工具类中的Random类。
 * 2、要求是随机x到y之间的整数，即指定范围，则使用Random类中的nextInt(int n)方法。
 * 3、该方法生成从0（包括）到n（不包括）之间的随机整数，是一个伪随机数，并不是真正的随机数。
 * 4、若x不为0，则需要在随机结果后加上x。参数n的值也需要加上1后减去x。最后结果才符合要求的范围。
 * 二、实现：
 * 1、定义x和y的值，修改该值可以随机不同范围的整数。
 * 2、调用Random中的nextInt(int n)方法，计算随机数。
 * 3、将结果打印到控制台。
 * */
public class RandomUtil {
    public static int getRandom(int start,int end) {
        //创建Random类对象
        Random random = new Random();
        //产生随机数
        int number = random.nextInt(end - start + 1) + start;
        return number;
    }

    public static int getRandomKey(Map<Integer, Object> map) {
        Integer[] keys = map.keySet().toArray(new Integer[0]);
        Random random = new Random();
        Integer randomKey = keys[random.nextInt(keys.length)];
        System.out.println(randomKey);
        return randomKey;
    }


    public static void main(String[] args) {
/*        for(int i=0;i<100;i++){
            System.out.println(getRandom(1,10));
        }
        Map<Integer, Object> map = new HashMap<Integer, Object>();
        map.put(33, 333);
        map.put(123, 1234);
        map.put(321, 4321);
        map.put(555, 5555);
        for(int i=0;i<5;i++){
            System.out.println(getRandomKey(map));
        }
*/
        ConcurrentHashMap<String, Cluster> writableCluster=new ConcurrentHashMap<String,Cluster>();
        for(int i=1;i<5;i++){
            Cluster myCluster= new Cluster();
            myCluster.setId(String.valueOf(i));
            System.out.println("myCluster.getId():"+myCluster.getId());
            writableCluster.put(String.valueOf(i),myCluster);
        }
        for(Map.Entry<String, Cluster> entry : writableCluster.entrySet()){
            String mapKey = entry.getKey();
            Cluster cluster = entry.getValue();
            System.out.println("mapKey:"+mapKey);
            System.out.println("cluster.getId():"+cluster.getId());
        }

    }

}

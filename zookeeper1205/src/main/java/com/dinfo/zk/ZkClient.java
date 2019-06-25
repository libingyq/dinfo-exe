package com.dinfo.zk;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by lbin8521 on 2019/3/9.
 */
public class ZkClient {
    private ZooKeeper zkClient;
    private String connection = "hadoop102:2181,hadoop103:2181,hadoop104:2181";
    private int sessionTime = 2000;

    @Before
    public void init() throws IOException {
        zkClient= new ZooKeeper(connection, sessionTime, new Watcher() {
            public void process(WatchedEvent event) {
//                System.out.println("回调函数中的获取的内容---》"+event.getPath());
                try {
                    List<String> children = zkClient.getChildren("/", true);
                    for (String child : children) {
                        System.out.println(child);
                    }
                    System.out.println("*************************************");
                } catch (KeeperException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Test
    public void  lsTest() throws KeeperException, InterruptedException {
        List<String> children = zkClient.getChildren("/", true);
        for (String child : children) {
            System.out.println(child);
        }
        Thread.sleep(Long.MAX_VALUE);
    }


    @Test
    public void getTest() throws KeeperException, InterruptedException {
        register();
        Thread.sleep(Long.MAX_VALUE);
    }

    public void register() throws KeeperException, InterruptedException {
        byte[] data = zkClient.getData("/mydata", new Watcher() {
            public void process(WatchedEvent event) {
                try {
                    register();
                } catch (KeeperException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, new Stat());
        String str = new String(data);
        System.out.println(str);

    }

    @Test
    public void createTest() throws KeeperException, InterruptedException {
        String content = zkClient.create("/testCreate", "测试数据".getBytes(),
                ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT_SEQUENTIAL);
        System.out.println(content);

    }

    @Test
    public void deleteTest() throws KeeperException, InterruptedException {
        Stat exists = zkClient.exists("/mydata1", false);
        if (exists==null){
            System.out.println("节点不存在，需要创建");
            String s = zkClient.create("/mydata1", "new create".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            if (s!=null){
                System.out.println("新节点创建成功");
                Stat exists1 = zkClient.exists("/mydata1", false);
                zkClient.delete("/mydata1",exists1.getVersion());
            }
        }else{
            System.out.println("判断的节点已存在，可以进行删除操作");
            zkClient.delete("/mydata1",exists.getVersion());
        }
        Thread.sleep(Long.MAX_VALUE);
    }

}

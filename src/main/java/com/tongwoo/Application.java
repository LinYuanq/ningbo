package com.tongwoo;

import com.tongwoo.activemq.Topic;

public class Application {

    public static void main(String args[]) {
        Topic topic = new Topic();
            Thread t1 = new Thread(new Runnable() {
                @Override
                public void run() {
                    topic.consumer("SH_HSJTJ_BICYCLE", "failover:(tcp://10.68.130.33:10011)?timeout=30000");
//                    topic.consumer("huangwei", "failover:(tcp://localhost:61616)?timeout=3000");
                }
            });
            t1.start();
    }
}

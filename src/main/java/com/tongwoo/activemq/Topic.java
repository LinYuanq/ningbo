package com.tongwoo.activemq;

//import com.listener.Mylistener;

import com.tongwoo.listener.Mylistener;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.util.ArrayList;
import java.util.List;


@Slf4j
public class Topic {

    public static List<String> list = new ArrayList<>();
    Mylistener mylistener = new Mylistener(list);
    /**
     * @author linyuanqing
     * @Description
     * @param times
     * @param interval
     * @param topicname
     * @param url
     * @param context
     * @param username
     * @param password
     * @date 2019/6/6
     * @return void
     */
    public void producer(int times, int interval, String topicname, String url, String context, String username, String password) {
        try {
            //创建连接工厂
            ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(username, password, url);
            //创建连接
            Connection connection = connectionFactory.createConnection();
            connection.start();
            //创建会话
            Session session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);
            //创建目的地
            Destination destination = session.createTopic(topicname);
            //创建生产者
            MessageProducer messageProducer = session.createProducer(destination);
            //发送次数
            for (int i = 1; i < times + 1; i++) {
                //创建消息
                TextMessage textMessage = session.createTextMessage(context + i);
                messageProducer.send(textMessage);
                System.out.println("发送消息：" + textMessage.getText());
                session.commit();
                Thread.sleep(interval);
            }
            if (connection != null) {
                connection.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * @author linyuanqing
     * @Description 消费者方法，由于当开启多线程异步时，访问redis的连接工厂容易出错，
     * 因为连接工厂的bean是单例的，当一个线程结束时，bean的生命周期结束，而此时被另
     * 一个bean访问则会报cannot create bean错误，所以关闭该异步线程
     * @param topicname
     * @param url
     * @date 2019/6/6
     * @return void
     */
    public void consumer(String topicname, String url) {
        log.info("");
        try {
            ConnectionFactory connectionFactory = new ActiveMQConnectionFactory( url);
            Connection connection = connectionFactory.createConnection();
            connection.start();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination destination = session.createTopic(topicname);
            MessageConsumer messageConsumer = session.createConsumer(destination);
            while (true) {
                messageConsumer.setMessageListener(mylistener);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        }
    }



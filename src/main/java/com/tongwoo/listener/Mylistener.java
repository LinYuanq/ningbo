package com.tongwoo.listener;


import com.tongwoo.config.DataSoueceConfig;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;

import javax.jms.*;
import java.sql.*;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class Mylistener implements MessageListener {

    public Mylistener(List<String> list) {
        this.list = list;
    }

    public static List<String> list = new ArrayList<>();
    public int count = 0;


    /**
     * @Author linyuanqing
     * @Description
     * @Date 11:19 2019/6/24
     * @Param * @param message :
     * *@return void
     **/
    @Override
    public void onMessage(Message message) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet resultSet = null;
        if (message instanceof TextMessage) {
            count++;
            System.out.println("-------------------------------" + count);
            TextMessage textMessage = (TextMessage) message;

            try {
                System.out.println("收到的内容：" + textMessage.getText());
                log.info("textMessage:" + textMessage.getText());
                //将收到的数据存入list中，后续执行批处理
//                list.add(textMessage.getText());
//                if (list.size() == 1000) {
                conn = DataSoueceConfig.getInstance().getConnection();
                conn.setAutoCommit(false);
//                stmt = conn.createStatement();
//                    for (int i = 0; i < list.size(); i++) {
                JSONObject jsonObject = new JSONObject(textMessage.getText());
                String tid = (String) jsonObject.get("tid");
                String stopSn = (String) jsonObject.get("stopSn");
                int available = (int) jsonObject.get("available");
                String status = JSONObject.valueToString((Object) jsonObject.get("status"));
                String scanTime = (String) jsonObject.get("scanTime");
                String sql2 = "select * from tb_bicycle_stop_status_update where stopSn = " +stopSn;
                stmt = conn.prepareStatement(sql2);
                resultSet = stmt.executeQuery(sql2);
                if(resultSet.next()){
                    String sql3 = "update tb_bicycle_stop_status_update set stopSn=?,available=?,status= ?,scanTime= ?where stopSn =?";
                    stmt = conn.prepareStatement(sql3);
//                    stmt.executeUpdate(sql3);
//                    stmt.setString(1,tid)
                    stmt.setString(1,stopSn);
                    stmt.setInt(2,available);
                    stmt.setString(3,status);
                    stmt.setString(4,scanTime);
                    stmt.setString(5,stopSn);
                    stmt.executeUpdate();
                    log.info("update表更新成功");
                }else {

                    String sql1 = "insert into tb_bicycle_stop_status_update(tid,stopSn,available,status,scanTime) values ('" +
                            tid + "','" +
                            stopSn + "','" +
                            available + "','" +
                            status + "','" +
                            scanTime + "'" +
                            ")";
                    stmt = conn.prepareStatement(sql1);
                    stmt.executeUpdate(sql1);
                    log.info("update表插入成功");
                }



                String sql = "insert ignore into tb_bicycle_stop_status(tid,stopSn,available,status,scanTime) values ('" +
                        tid + "','" +
                        stopSn + "','" +
                        available + "','" +
                        status + "','" +
                        scanTime + "'" +
                        ")";
//                        System.out.println("正在执行");
                stmt = conn.prepareStatement(sql);
                stmt.addBatch(sql);
//                    }
                stmt.executeBatch();
                conn.commit();
                log.info("数据入库");
                stmt.close();
                conn.close();
//                    list.clear();
//                }
            } catch (JMSException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("数据库初始化错误");
            }

        } else if (message instanceof BytesMessage) {
            System.out.println("------Received BytesMessage------");
            log.info("------Received BytesMessage------");
            BytesMessage bytesMessage = (BytesMessage) message;
            byte[] byteContent = new byte[1024];
            while (true) {
                try {
                    if (!((bytesMessage.readBytes(byteContent)) != -1)) break;
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        } else if (message instanceof MapMessage) {
            MapMessage mm = (MapMessage) message;
            try {
                System.out.println("MapMessage" + mm.getString("msgId"));
                log.info("MapMessage" + mm.getString("msgId"));
            } catch (JMSException e) {
                e.printStackTrace();
            }
        } else if (message instanceof ObjectMessage) {
            ObjectMessage om = (ObjectMessage) message;
            System.out.println("ObjectMessage");
            log.info("ObjectMessage");
        } else if (message instanceof StreamMessage) {
            StreamMessage sm = (StreamMessage) message;
            try {
                System.out.println(sm.readString());
                System.out.println(sm.readInt());
                log.info("StreamMessage" + sm.readString());
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }
}

package com.hp.lct.mqtt;

import com.hp.lct.service.MsgHandler;
import com.hp.lct.utils.DataTool;
import com.hp.lct.utils.RedisTool;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by jackl on 2016/11/11.
 */
public class MsgServerTask implements Runnable{

    private String host;
    private String port;
    private  String subscribeTopic;
    private  String publishTopicPrefix;
    private  String clientId ;
    private  String username;
    private  String password;
    private MsgHandler msgHandler;
    private RedisTool redisTool;

    private  String hostName;
    private static MqttClient client ;
    private static MemoryPersistence persistence;
    private Logger _logger = LoggerFactory.getLogger(MsgServerTask.class);

    public MsgServerTask() {
    }

    public MsgServerTask(String host, String port, String subscribeTopic, String publishTopicPrefix, String clientId, String username, String password, MsgHandler msgHandler, RedisTool redisTool,DataTool dataTool) {
        this.host = host;
        this.port = port;
        this.subscribeTopic = subscribeTopic;
        this.publishTopicPrefix = publishTopicPrefix;
        this.clientId = clientId;
        this.username = username;
        this.password = password;
        this.msgHandler = msgHandler;
        this.redisTool = redisTool;
    }

    @Override
    public void run() {
        subscribe();
        testSend();
    }
    public  String subscribe() {
        try {
            hostName="tcp://"+host+":"+port;
            //创建MqttClient
            client=new MqttClient(hostName,clientId);
            persistence = new MemoryPersistence();
            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable throwable) {
                    _logger.info("MQTT连接断开");
                }

                @Override
                public void messageArrived(String s, MqttMessage message) throws Exception {
                    try {
                        String msg= message.toString();
                     //   replay("testtest","receive msg:"+msg);
                        msgHandler.handleReq(client,msg);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
                }
            });
            MqttConnectOptions conOptions = new MqttConnectOptions();
            conOptions.setUserName(username);
            conOptions.setPassword(password.toCharArray());
            conOptions.setCleanSession(true);
            conOptions.setWill(subscribeTopic, "will msg".getBytes(), 1, true);
            client.connect(conOptions);
            client.subscribe(subscribeTopic, 1);
            boolean isSuccess =client.isConnected();
            if(isSuccess){
                _logger.info("已经成功连接到MQTT服务器.");
            }
            //client.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            return "failed";
        }
        return "success";
    }

    public void testSend(){
        replay(client,"/d/lv8918/868516020035370","测试发送");
    }

    public  void replay(MqttClient client,String topic,String meaasge){
        try {
            MqttTopic _replayTopic = client.getTopic(topic);
            _logger.info("发出的消息:" + meaasge);
            MqttMessage message = new MqttMessage(meaasge.getBytes());
            message.setQos(0);
            MqttDeliveryToken token = _replayTopic.publish(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

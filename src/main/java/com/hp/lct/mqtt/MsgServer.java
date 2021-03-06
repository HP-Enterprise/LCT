package com.hp.lct.mqtt;

import com.hp.lct.service.MsgHandler;
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
@Component
public class MsgServer {

    @Value("${mqtt.host}")
    private String host;
    @Value("${mqtt.port}")
    private String port;

    private  String hostName;
    @Value("${mqtt.subscribeTopic}")
    private  String subscribeTopic;
    @Value("${mqtt.publishTopicPrefix}")
    private  String publishTopicPrefix;
   @Value("${mqtt.clientId}")
    private  String clientId ;
    @Value("${mqtt.userName}")
    private  String username;
    @Value("${mqtt.password}")
    private  String password;

    @Autowired
    private MsgHandler msgHandler;

    private static MqttClient client ;
    private static MemoryPersistence persistence;
    private Logger _logger = LoggerFactory.getLogger(MsgServer.class);
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

}

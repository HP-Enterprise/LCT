package com.hp.lct.service;
import com.hp.lct.entity.Body;
import com.hp.lct.entity.Head;
import com.hp.lct.entity.MsgBean;
import com.hp.lct.utils.DataTool;
import com.hp.lct.utils.RedisTool;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.alibaba.fastjson.JSON;
/**
 * Created by jackl on 2016/11/11.
 */

@Component
public class MsgHandler {

    @Value("${mqtt.publishTopicPrefix}")
    private  String publishTopicPrefix;
    @Value("${mqtt.subscribeTopic}")
    private  String subscribeTopic;
    @Autowired
    private RedisTool redisTool;
    @Autowired
    private DataTool dataTool;


    private Logger _logger = LoggerFactory.getLogger(MsgHandler.class);

    public String handleReq (MqttClient client,String msg){
        _logger.info("receive msg:"+msg);
        String replayMsg=null;
        MsgBean bean =null;
        try {
            bean = JSON.parseObject(msg, MsgBean.class);
        }catch (Exception e){
            //e.printStackTrace();
        }
        if(bean!=null){
            int version=bean.getHead().getVersion();
            long id=bean.getHead().getId();
            String from=bean.getHead().getFrom();
            int code=bean.getHead().getCode();
            int type=bean.getHead().getType();
            String imei="";
            switch (code){
                case 1:
                     imei=bean.getBody().getImei();
                    _logger.info("设备"+imei+"上线");
                    redisTool.saveHashString(dataTool.onlineDeviceHash,imei,imei,-1);
                     replayMsg=buildResp(version,id,subscribeTopic,code,2,"OK",null);
                    break;
                case 2:
                case 3:
                     imei=bean.getBody().getImei();
                    _logger.info("设备"+imei+"下线");
                    redisTool.deleteHashString(dataTool.onlineDeviceHash, imei);
                    replayMsg=buildResp(version,id,subscribeTopic,code,2,"OK",null);
                    break;
                case 100://获取上报
                case 101://主动上报
                    //设备信息上报

                    if(code==101) {
                        replayMsg = buildResp(version, id, subscribeTopic, code, 2, "OK", null);
                    }
                    break;

                case 102://获取上报
                case 103://主动上报
                    //状态信息上报
                    imei=bean.getBody().getImei();
                    double longitude=bean.getBody().getLongitude();
                    double latitude=bean.getBody().getLatitude();
                    System.out.println(">>"+imei+"-"+longitude+"-"+latitude);
                    if(code==103) {
                        replayMsg = buildResp(version, id, subscribeTopic, code, 2, "OK", null);
                    }
                    break;

            }
            //回复消息
            String replayTopic=publishTopicPrefix+"/"+bean.getBody().getImei();
            if(replayMsg!=null) {
                replay(client, replayTopic, replayMsg);
            }
        }else{
            _logger.info("不符合协议的消息");
        }

        return "";
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

    public String buildResp(int version,long id, String from,int code, int type, String msg,Body body){
        String replayStr=null;
        Head head=new Head(version,id,from,code,type,msg);

        MsgBean msgBean=new MsgBean(head,body);
        try {
            replayStr = JSON.toJSONString(msgBean);
        }catch (Exception e){e.printStackTrace();}
        return replayStr;
    }


}

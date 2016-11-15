package com.hp.lct.service;
import com.hp.lct.entity.*;
import com.hp.lct.repository.AppVersionRepository;
import com.hp.lct.repository.DeviceRepository;
import com.hp.lct.repository.DeviceStatusDataRepository;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    @Autowired
    DeviceRepository deviceRepository;
    @Autowired
    DeviceStatusDataRepository deviceStatusDataRepository;
    @Autowired
    AppVersionRepository appVersionRepository;



    private Logger _logger = LoggerFactory.getLogger(MsgHandler.class);

    public String handleReq (MqttClient client,String msg){
        _logger.info("收到的消息:" +msg);
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

                case 6:
                    //设备查询APP升级信息
                    List<App> reqApps=bean.getBody().getApps();
                    List<App> respApps=new ArrayList<App>();
                    for (int i = 0; i <reqApps.size(); i++) {
                        App app=reqApps.get(i);
                        AppVersion appVersion= appVersionRepository.findTopByAppIdAndVersionGreaterThanOrderByPublishTimeDesc(app.getAppId(), app.getVersion());
                        if(appVersion!=null) {
                            App _app = new App(appVersion.getAppId(),appVersion.getVersion(),appVersion.getUrl(),appVersion.getAppSize(),appVersion.getMd5(),appVersion.getAppDesc());
                            respApps.add(_app);
                        }
                    }
                    Body body=new Body();
                    body.setApps(respApps);
                    replayMsg = buildResp(version, id, subscribeTopic, code, 2, "OK", body);
                    break;
                case 100://获取上报
                case 101://主动上报
                    //设备信息上报
                    imei=bean.getBody().getImei();
                    Device device=deviceRepository.findByImei(imei);
                    if(device==null){
                        device=new Device();
                    }
                    device.setImei(bean.getBody().getImei());
                    device.setImsi(bean.getBody().getImsi());
                    device.setModel(bean.getBody().getModel());
                    device.setOdmModel(bean.getBody().getOdmModel());
                    device.setHwVer(bean.getBody().getHwver());
                    device.setSwVer(bean.getBody().getSwver());
                    device.setOdmSwVer(bean.getBody().getOdmSwver());
                    device.setWifiMac(bean.getBody().getWifimac());
                    device.setBtMac(bean.getBody().getBtmac());
                    device.setBrandName(bean.getBody().getBrandName());
                    device.setVendor(bean.getBody().getVendor());
                    device.setReceiveTime(new Date());
                    deviceRepository.save(device);
                    if(code==101) {
                        replayMsg = buildResp(version, id, subscribeTopic, code, 2, "OK", null);
                    }
                    break;

                case 102://获取上报
                case 103://主动上报
                    //状态信息上报
                    imei=bean.getBody().getImei();
                    DeviceStatusData deviceStatusData=new DeviceStatusData();
                    deviceStatusData.setSequenceId(String.valueOf(bean.getHead().getId()));
                    deviceStatusData.setImei(imei);
                    deviceStatusData.setType((short) 1);
                    deviceStatusData.setLatitude(bean.getBody().getLatitude());
                    deviceStatusData.setLongitude(bean.getBody().getLongitude());
                    deviceStatusData.setReceiveTime(new Date());
                    deviceStatusDataRepository.save(deviceStatusData);
                    if(code==103) {
                        replayMsg = buildResp(version, id, subscribeTopic, code, 2, "OK", null);
                    }
                    break;
                case 106://控制
                    imei=bean.getBody().getImei();
                    String sequenceId=String.valueOf(bean.getHead().getId());
                    String result=bean.getBody().getResult();
                    if(result!=null){
                        if(result.equals("OK")){
                            //更新控制记录
                            System.out.println("控制结果>>>>>"+imei+"-"+sequenceId+"-"+result);
                        }
                    }
                    break;
                case 107://碰撞上报
                case 108://移动上报
                    imei=bean.getBody().getImei();
                    deviceStatusData=new DeviceStatusData();
                    deviceStatusData.setSequenceId(String.valueOf(bean.getHead().getId()));
                    deviceStatusData.setImei(imei);
                    if(code==107) {
                        deviceStatusData.setType((short) 2);
                        deviceStatusData.setActionTime(dataTool.parseStrToDate(bean.getBody().getCollisionTime()));
                    }else{
                        deviceStatusData.setType((short) 3);
                        deviceStatusData.setActionTime(dataTool.parseStrToDate(bean.getBody().getMoveTime()));
                    }

                    deviceStatusData.setLatitude(bean.getBody().getLatitude());
                    deviceStatusData.setLongitude(bean.getBody().getLongitude());
                    deviceStatusData.setReceiveTime(new Date());
                    deviceStatusDataRepository.save(deviceStatusData);
                    replayMsg = buildResp(version, id, subscribeTopic, code, 2, "OK", null);
                    break;
                case 109://休眠请求
                    imei=bean.getBody().getImei();
                    String sleepTime =bean.getBody().getSleepTime();
                    _logger.info("设备"+imei+"休眠,休眠时间"+sleepTime);
                    break;
            }
            //回复消息
            String replayTopic=bean.getHead().getFrom();
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

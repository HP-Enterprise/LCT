package com.hp.lct;

import com.alibaba.fastjson.JSON;
import com.hp.lct.entity.ObjectResult;
import com.hp.lct.entity.RemoteControl;
import com.hp.lct.entity.RemoteControlBody;
import com.hp.lct.repository.RemoteControlRepository;
import com.hp.lct.utils.DataTool;
import com.hp.lct.utils.RedisTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Created by jackl on 2016/11/15.
 */
@Component
public class DeviceDataService {

    @Autowired
    private RedisTool redisTool;
    @Autowired
    private DataTool dataTool;
    @Autowired
    private RemoteControlRepository remoteControlRepository;

    private Logger _logger = LoggerFactory.getLogger(DeviceDataService.class);

    public ObjectResult handleRemoteControl(RemoteControlBody remoteControlBody){
        ObjectResult re=null;
        if(remoteControlBody!=null) {
            String commandJson= JSON.toJSONString(remoteControlBody);
            _logger.info("开始处理控制指令:"+commandJson);
            if (!isDeviceOnline(remoteControlBody.getImei())) {
                //todo 保存到数据库
                RemoteControl remoteControl=new RemoteControl();
                remoteControl.setImei(remoteControlBody.getImei());
                remoteControl.setSequenceId(dataTool.getSequenceId());
                remoteControl.setSendingTime(new Date());
                remoteControl.setCommand(dataTool.getCommandDesc(remoteControlBody.getCommand(),remoteControlBody.getOperate()));
                remoteControl.setParams("");
                remoteControl.setStatus((short)0);
                remoteControl.setRemark("设备不在线,无法继续。");
                remoteControlRepository.save(remoteControl);

                _logger.info("设备"+remoteControlBody.getImei()+"不在线,无法继续。");
                re=new ObjectResult(false,"设备不在线");
            } else {
                sendCommand(remoteControlBody);
                re=new ObjectResult(true,"命令已经下发");
            }
        }else{
            re=new ObjectResult(false,"参数无效");
        }

        return re ;
    }


    public boolean isDeviceOnline(String imei){
        boolean exist=redisTool.existHashString(dataTool.onlineDeviceHash,imei);
        _logger.info("检查设备"+imei+"是否在线:"+exist);
        return exist;
    }

    public void sendCommand(RemoteControlBody remoteControlBody){
        String command=remoteControlBody.getCommand();
        String operate=remoteControlBody.getOperate();
        String imei=remoteControlBody.getImei();
        String dest=remoteControlBody.getDest();
        int time=remoteControlBody.getTime();
        String sequenceId=dataTool.getSequenceId();
        String commandKey=dataTool.outCmdPreStr+imei+":"+command+":"+sequenceId;
        String  commandValue="";
        if(command.equals("106")){
            commandValue=operate;
            if(operate.equals("NAVIGATE")){
                commandValue=operate+","+dest;//108.868095, 34.19328
            }else if(operate.equals("VIDEO")){
                commandValue=operate+","+time;//15
            }
            //todo 保存到数据库
            RemoteControl remoteControl=new RemoteControl();
            remoteControl.setImei(imei);
            remoteControl.setSequenceId(String.valueOf(sequenceId));
            remoteControl.setSendingTime(new Date());
            remoteControl.setCommand(dataTool.getCommandDesc(remoteControlBody.getCommand(),remoteControlBody.getOperate()));
            remoteControl.setParams(commandValue);
            remoteControl.setStatus((short)0);
            remoteControl.setRemark("命令已发出");
            remoteControlRepository.save(remoteControl);

            redisTool.saveSetString(commandKey, commandValue,-1);
        }else if(command.equals("101")||command.equals("103")){
            RemoteControl remoteControl=new RemoteControl();
            remoteControl.setImei(imei);
            remoteControl.setSequenceId(String.valueOf(sequenceId));
            remoteControl.setSendingTime(new Date());
            remoteControl.setCommand(dataTool.getCommandDesc(remoteControlBody.getCommand(),remoteControlBody.getOperate()));
            remoteControl.setParams(commandValue);
            remoteControl.setStatus((short)0);
            remoteControl.setRemark("命令已发出");
            remoteControlRepository.save(remoteControl);
            redisTool.saveSetString(commandKey,commandValue,-1);
        }else{
            _logger.info(command+"命令暂不支持,支持（101,103,106）");
        }


    }

}

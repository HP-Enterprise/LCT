package com.hp.lct;

import com.alibaba.fastjson.JSON;
import com.hp.lct.entity.ObjectResult;
import com.hp.lct.entity.RemoteControlBody;
import com.hp.lct.utils.DataTool;
import com.hp.lct.utils.RedisTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by jackl on 2016/11/15.
 */
@Component
public class DeviceDataService {

    @Autowired
    private RedisTool redisTool;
    @Autowired
    private DataTool dataTool;
    private Logger _logger = LoggerFactory.getLogger(DeviceDataService.class);

    public ObjectResult handleRemnoteControl(RemoteControlBody remoteControlBody){
        ObjectResult re=null;
        if(remoteControlBody!=null) {
            if (!isDeviceOnline(remoteControlBody.getImei())) {
                re=new ObjectResult(false,"设备不在线");
            } else {
                String commandJson= JSON.toJSONString(remoteControlBody);
                _logger.info("开始处理控制指令:"+commandJson);


                re=new ObjectResult(true,"命令已经下发");
            }
        }else{
            re=new ObjectResult(false,"参数无效");
        }

        return re ;
    }


    public boolean isDeviceOnline(String imei){
        boolean exist=redisTool.existHashString(dataTool.onlineDeviceHash,imei);
        _logger.info("设备"+imei+"是否在线:"+exist);
        return exist;
    }


}

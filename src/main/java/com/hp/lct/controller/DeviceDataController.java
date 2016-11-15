package com.hp.lct.controller;

import com.hp.lct.DeviceDataService;
import com.hp.lct.entity.Device;
import com.hp.lct.entity.RemoteControlBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.RequestContext;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by jackl on 2016/11/15.
 */

@RestController
@RequestMapping("/api")
public class DeviceDataController {

    @Autowired
    private DeviceDataService deviceDataService;

    @RequestMapping(value = "/service/model/{model}/device/{imei}/remoteControl",method = RequestMethod.POST)
    public Object sentRemoteSettingToVehicle(@PathVariable("model") String model,@PathVariable("imei") String imei,@RequestBody RemoteControlBody remoteControlBody,HttpServletRequest request){
        RequestContext requestContext = new RequestContext(request);
        Object result=deviceDataService.handleRemnoteControl(remoteControlBody);
        return result;
    }
}

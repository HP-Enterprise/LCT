package com.hp.lct.service;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.alibaba.fastjson.JSON;
/**
 * Created by jackl on 2016/11/11.
 */



@Component
public class MsgHandler {
    private Logger _logger = LoggerFactory.getLogger(MsgHandler.class);

    public String handle(String msg){
        Object  group = JSON.parseObject(msg, Object.class);
        _logger.info("msg");
        return "";
    }


}

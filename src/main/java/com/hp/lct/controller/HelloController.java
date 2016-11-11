package com.hp.lct.controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * Created by jackl on 2016/11/11.
 */

@RestController
@RequestMapping("/api")
public class HelloController {


    @RequestMapping(value="/hello",method = RequestMethod.GET)
    public String test(){
        return "test service"+new Date().getTime();
    }

}

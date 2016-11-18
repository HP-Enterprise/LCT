package com.hp.lct;

/**
 * Created by jackl on 2016/11/11.
 */
import com.hp.lct.mqtt.MsgServer;
import com.hp.lct.mqtt.MsgServerTask;
import com.hp.lct.service.MsgHandler;
import com.hp.lct.utils.DataTool;
import com.hp.lct.utils.RedisTool;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;

import javax.servlet.MultipartConfigElement;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class Application implements CommandLineRunner {
    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxFileSize("20MB");
        factory.setMaxRequestSize("20MB");
        //还需要在数据库上配置支持的max_allowed_packet
        return factory.createMultipartConfig();
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    // 日志
    private Logger _logger;
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
    @Autowired
    private RedisTool redisTool;
    @Autowired
    private DataTool dataTool;

    ScheduledExecutorService dataHandlerScheduledService = Executors.newSingleThreadScheduledExecutor();


    public void run(String... args) throws Exception{
        this._logger = LoggerFactory.getLogger(Application.class);
        this._logger.info("Application is running...");
        //redisTool.deleteHashAllString(dataTool.onlineDeviceHash);//清理redis里面的全部连接记录
        dataHandlerScheduledService.schedule(new MsgServerTask(host,port,subscribeTopic,publishTopicPrefix,clientId,username,password,msgHandler,redisTool,dataTool),10, TimeUnit.MILLISECONDS);
    }
}
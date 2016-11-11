package com.hp.lct;

/**
 * Created by jackl on 2016/11/11.
 */
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application implements CommandLineRunner {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    // 日志
    private Logger _logger;

    private boolean _disabled;

    public void run(String... args) throws Exception{
        this._logger = LoggerFactory.getLogger(Application.class);
        this._logger.info("Application is running...");

    }
}
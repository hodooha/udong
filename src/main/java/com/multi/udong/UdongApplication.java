package com.multi.udong;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class UdongApplication {

    public static void main(String[] args) {
        SpringApplication.run(UdongApplication.class, args);
    }

}

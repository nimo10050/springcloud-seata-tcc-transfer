package com.nimo.transfer;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@MapperScan("com.nimo.transfer.mapper")
@EnableDiscoveryClient
public class TransferOutApplication {

    public static void main(String[] args) {
        SpringApplication.run(TransferOutApplication.class, args);
    }

}

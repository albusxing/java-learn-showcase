package com.albusxing.basis.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class BasisGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(BasisGatewayApplication.class, args);
    }

}

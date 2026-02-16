package com.peachtree;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.peachtree.mapper")
public class PeachAdoptionApplication {
    public static void main(String[] args) {
        SpringApplication.run(PeachAdoptionApplication.class, args);
    }
}

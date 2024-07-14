package com.board;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//@EnableJpaRepositories
@SpringBootApplication
public class Application {
    public static void main(String[] args){//내장 WAS 실행
        SpringApplication.run(Application.class,args);
    }
}

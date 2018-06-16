package com.jw.infomgr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class InfoMgrApplication {

    public static void main(String[] args) {
        SpringApplication.run(InfoMgrApplication.class, args);
    }
}

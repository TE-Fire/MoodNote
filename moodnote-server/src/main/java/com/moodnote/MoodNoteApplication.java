package com.moodnote;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.moodnote.mapper")
public class MoodNoteApplication {

    public static void main(String[] args) {
        SpringApplication.run(MoodNoteApplication.class, args);
    }
}
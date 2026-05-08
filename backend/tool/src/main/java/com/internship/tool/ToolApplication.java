package com.internship.tool;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableScheduling
@EnableAspectJAutoProxy
public class ToolApplication {

    public static void main(String[] args) {

        SpringApplication.run(
                ToolApplication.class,
                args
        );

        System.out.println(
                "✅ Tool Application Started Successfully"
        );
    }
}
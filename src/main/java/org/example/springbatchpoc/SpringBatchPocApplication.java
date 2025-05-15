package org.example.springbatchpoc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class SpringBatchPocApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBatchPocApplication.class, args);
    }

}

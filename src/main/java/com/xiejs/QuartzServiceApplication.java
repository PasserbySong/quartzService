package com.xiejs;

import com.xiejs.config.MybatisConfig;
import com.xiejs.config.SchedulerConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({SchedulerConfig.class, MybatisConfig.class})
public class QuartzServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(QuartzServiceApplication.class, args);
    }
}

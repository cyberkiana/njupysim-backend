package org.njupt.njuptphysim.test;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

@Configuration
public class B {


        @Bean  // 👈 这个注解的意思：把下面方法返回的对象，交给 Spring 容器管理
        public CommandLineRunner printRandomNumber(Integer randomNumber) {
            return args -> {
                System.out.println("幸运数字是：" + randomNumber);
            };

    }
}

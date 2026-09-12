package org.njupt.njuptphysim.test;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Random;

@Configuration
public class BeanTest {

    public Integer randomPlay(){
        Random random = new Random();
        return random.nextInt(100)+1;
    }

    @Bean
    public Integer t(){
        return randomPlay();
    }

}





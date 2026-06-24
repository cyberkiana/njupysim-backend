package org.njupt.njuptphysim;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class NjuptPhySimApplication {

    public static void main(String[] args) {
        SpringApplication.run(NjuptPhySimApplication.class, args);
        log.info("server started");
    }

}

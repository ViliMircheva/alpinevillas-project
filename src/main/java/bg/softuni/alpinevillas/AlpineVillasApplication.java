package bg.softuni.alpinevillas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableFeignClients
@EnableCaching
@EnableScheduling
@SpringBootApplication
public class AlpineVillasApplication {

    public static void main(String[] args) {
        SpringApplication.run(AlpineVillasApplication.class, args);
    }

}

package person.clp.stationeryback;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("person.clp.stationeryback.mapper")
public class StationeryBackApplication {

    public static void main(String[] args) {
        SpringApplication.run(StationeryBackApplication.class, args);
    }

}

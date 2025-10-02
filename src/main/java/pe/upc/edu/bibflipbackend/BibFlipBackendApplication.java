package pe.upc.edu.bibflipbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class BibFlipBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BibFlipBackendApplication.class, args);
    }

}

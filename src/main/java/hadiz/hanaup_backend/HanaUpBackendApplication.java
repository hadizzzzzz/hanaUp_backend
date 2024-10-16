package hadiz.hanaup_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = "hadiz.hanaup_backend.domain")
public class HanaUpBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(HanaUpBackendApplication.class, args);
	}

}

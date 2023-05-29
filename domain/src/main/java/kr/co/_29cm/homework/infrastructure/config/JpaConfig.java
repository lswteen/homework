package kr.co._29cm.homework.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(basePackages = "kr.co._29cm.homework.domain.repository")
@Configuration
public class JpaConfig {

}

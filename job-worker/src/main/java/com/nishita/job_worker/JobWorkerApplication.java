package com.nishita.job_worker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EntityScan(basePackages = "com.nishita.job_common.entity")
@EnableJpaRepositories(basePackages = "com.nishita.job_worker.repository")
@EnableKafka
public class JobWorkerApplication {

	public static void main(String[] args) {
		SpringApplication.run(JobWorkerApplication.class, args);
	}

}

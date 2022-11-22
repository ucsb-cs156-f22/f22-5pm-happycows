package edu.ucsb.cs156.happiercows;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.task.DelegatingSecurityContextAsyncTaskExecutor;

@SpringBootApplication
@EnableJpaAuditing
@EnableAsync
public class HappierCowsApplication {

  public static void main(String[] args) {
    SpringApplication.run(HappierCowsApplication.class, args);
  }

  // See: https://www.baeldung.com/spring-security-async-principal-propagation
  @Bean
  public DelegatingSecurityContextAsyncTaskExecutor taskExecutor(ThreadPoolTaskExecutor delegate) {
    return new DelegatingSecurityContextAsyncTaskExecutor(delegate);
  }

  // See: https://www.baeldung.com/spring-security-async-principal-propagation
  @Bean
  public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(2);
    executor.setMaxPoolSize(2);
    executor.setQueueCapacity(500);
    executor.setThreadNamePrefix("HappierCows-");
    executor.initialize();
    return executor;
  }

}
//test

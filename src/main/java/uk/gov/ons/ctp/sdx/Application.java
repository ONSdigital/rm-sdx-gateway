package uk.gov.ons.ctp.sdx;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import uk.gov.ons.ctp.sdx.quarantine.common.CustomObjectMapper;

/** The main application class */
@Slf4j
@IntegrationComponentScan
@EnableAsync
@EnableScheduling
@EnableCaching
@SpringBootApplication
public class Application {

  /**
   * This method is the entry point to the Spring Boot application.
   *
   * @param args These are the optional command line arguments
   */
  public static void main(String[] args) {
    log.debug("About to start the SDX Gateway application...");
    SpringApplication.run(Application.class, args);
  }

  /**
   * Custom Object Mapper
   *
   * @return CustomObjectMapper
   */
  @Bean
  @Primary
  public CustomObjectMapper customObjectMapper() {
    return new CustomObjectMapper();
  }
}

package uk.gov.ons.ctp.sdx;

import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Primary;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import uk.gov.ons.ctp.sdx.config.AppConfig;
import uk.gov.ons.ctp.sdx.quarantine.common.CustomObjectMapper;
import uk.gov.ons.ctp.sdx.quarantine.common.DistributedLockManager;
import uk.gov.ons.ctp.sdx.quarantine.common.DistributedLockManagerRedissonImpl;
import uk.gov.ons.ctp.sdx.quarantine.common.RestExceptionHandler;

/** The main application class */
@Slf4j
@IntegrationComponentScan
@EnableAsync
@EnableScheduling
@EnableCaching
@SpringBootApplication
public class Application {

  private static final String ACTION_EXECUTION_LOCK = "actionexport.request.execution";

  @Autowired private AppConfig appConfig;

  /**
   * This method is the entry point to the Spring Boot application.
   *
   * @param args These are the optional command line arguments
   */
  public static void main(String[] args) {
    log.debug("About to start the SDX Gateway application...");
    SpringApplication.run(Application.class, args);
  }

  @Bean
  public DistributedLockManager actionExportExecutionLockManager(RedissonClient redissonClient) {
    return new DistributedLockManagerRedissonImpl(
        ACTION_EXECUTION_LOCK, redissonClient, appConfig.getDataGrid().getLockTimeToLiveSeconds());
  }

  @Bean
  public RedissonClient redissonClient() {
    Config config = new Config();
    config
        .useSingleServer()
        .setAddress(appConfig.getDataGrid().getAddress())
        .setPassword(appConfig.getDataGrid().getPassword());
    return Redisson.create(config);
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

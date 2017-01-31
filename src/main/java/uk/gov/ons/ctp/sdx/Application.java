package uk.gov.ons.ctp.sdx;

import javax.inject.Inject;
import javax.inject.Named;

import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import lombok.extern.slf4j.Slf4j;
import uk.gov.ons.ctp.common.distributed.DistributedInstanceManager;
import uk.gov.ons.ctp.common.distributed.DistributedInstanceManagerRedissonImpl;
import uk.gov.ons.ctp.common.distributed.DistributedLockManager;
import uk.gov.ons.ctp.common.distributed.DistributedLockManagerRedissonImpl;
import uk.gov.ons.ctp.common.jaxrs.CTPMessageBodyReader;
import uk.gov.ons.ctp.common.jaxrs.JAXRSRegister;
import uk.gov.ons.ctp.sdx.config.AppConfig;
import uk.gov.ons.ctp.sdx.endpoint.PaperReceiptEndpoint;
import uk.gov.ons.ctp.sdx.endpoint.ReceiptEndpoint;
import uk.gov.ons.ctp.sdx.representation.ReceiptDTO;

/**
 * The main application class
 */
@Slf4j
@IntegrationComponentScan
@EnableAsync
@EnableScheduling
@EnableCaching
@ImportResource("springintegration/main.xml")
@SpringBootApplication
public class Application {
  
  
  public static final String ACTION_EXECUTION_LOCK = "actionexport.request.execution";

  @Inject
  private AppConfig appConfig;
  /**
   * To register classes in the JAX-RS world.
   */
  @Named
  public static class JerseyConfig extends ResourceConfig {
    /**
     * Its public constructor.
     */
    public JerseyConfig() {
      log.debug("entering the JerseyConfig constructor...");
      JAXRSRegister.listCommonTypes().forEach(t->register(t));

      register(ReceiptEndpoint.class);
      register(new CTPMessageBodyReader<ReceiptDTO>(ReceiptDTO.class) { });

      register(MultiPartFeature.class);
      register(PaperReceiptEndpoint.class);

      System.setProperty("ma.glasnost.orika.writeSourceFiles", "false");
      System.setProperty("ma.glasnost.orika.writeClassFiles", "false");
    }
  }

  /**
   * This method is the entry point to the Spring Boot application.
   * @param args These are the optional command line arguments
   */
  public static void main(String[] args) {
    log.debug("About to start the SDX Gateway application...");
    SpringApplication.run(Application.class, args);
  }

  @Bean
  public DistributedLockManager actionExportExecutionLockManager(RedissonClient redissonClient) {
    return new DistributedLockManagerRedissonImpl(ACTION_EXECUTION_LOCK, redissonClient,
        appConfig.getDataGrid().getLockTimeToLiveSeconds());
  }

  @Bean
  public RedissonClient redissonClient() {
    Config config = new Config();
    config.useSingleServer()
        .setAddress(appConfig.getDataGrid().getAddress())
        .setPassword(appConfig.getDataGrid().getPassword());
    return Redisson.create(config);
  }
}

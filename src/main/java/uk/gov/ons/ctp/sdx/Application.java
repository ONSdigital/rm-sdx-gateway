package uk.gov.ons.ctp.sdx;

import lombok.extern.slf4j.Slf4j;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ImportResource;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import uk.gov.ons.ctp.common.jaxrs.CTPMessageBodyReader;
import uk.gov.ons.ctp.common.jaxrs.JAXRSRegister;
import uk.gov.ons.ctp.sdx.endpoint.ReceiptEndpoint;
import uk.gov.ons.ctp.sdx.representation.ReceiptDTO;

import javax.inject.Named;

/**
 * The main application class
 */
@Slf4j
@IntegrationComponentScan
@EnableAsync
@EnableScheduling
@EnableCaching
@ImportResource("main-int.xml")
@SpringBootApplication
public class Application {
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
}

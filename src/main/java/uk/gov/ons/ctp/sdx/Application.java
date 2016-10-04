package uk.gov.ons.ctp.sdx;

import lombok.extern.slf4j.Slf4j;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import uk.gov.ons.ctp.common.jaxrs.CTPXmlMessageBodyReader;
import uk.gov.ons.ctp.common.jaxrs.JAXRSRegister;
import uk.gov.ons.ctp.sdx.domain.Receipt;
import uk.gov.ons.ctp.sdx.endpoint.ReceiptEndpoint;
import uk.gov.ons.ctp.sdx.representation.ReceiptDTO;

import javax.inject.Named;

@Slf4j
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
      register(new CTPXmlMessageBodyReader<ReceiptDTO>(ReceiptDTO.class) { });

      System.setProperty("ma.glasnost.orika.writeSourceFiles", "false");
      System.setProperty("ma.glasnost.orika.writeClassFiles", "false");
    }
  }
  public static void main(String[] args) {
    log.debug("About to start the SDX Gateway application...");
    SpringApplication.run(Application.class, args);
  }
}

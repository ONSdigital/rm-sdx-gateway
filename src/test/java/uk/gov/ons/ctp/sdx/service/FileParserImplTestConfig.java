package uk.gov.ons.ctp.sdx.service;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import uk.gov.ons.ctp.sdx.service.impl.FileParserImpl;

/**
 * Tests File parser
 */
@SpringBootConfiguration
public class FileParserImplTestConfig {

  /**
   * File parser
   * @return FileParser
   */
  @Bean
  public FileParser fileParser() {
    return new FileParserImpl();
  }
}

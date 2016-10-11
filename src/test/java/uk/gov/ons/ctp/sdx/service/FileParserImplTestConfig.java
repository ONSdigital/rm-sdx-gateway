package uk.gov.ons.ctp.sdx.service;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import uk.gov.ons.ctp.sdx.service.impl.FileParserImpl;

@SpringBootConfiguration
public class FileParserImplTestConfig {
  @Bean
  public FileParser fileParser() {
    return new FileParserImpl();
  }
}

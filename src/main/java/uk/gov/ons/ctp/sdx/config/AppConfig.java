package uk.gov.ons.ctp.sdx.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/** The apps main holder for centralized config read from application.yml or env vars */
@Configuration
@ConfigurationProperties
@Data
public class AppConfig {
  private DataGrid dataGrid;
  private Sftp sftp;
}

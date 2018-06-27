package uk.gov.ons.ctp.sdx.config;

import lombok.Data;

/** Config POJO for sftp */
@Data
public class Sftp {
  private String host;
  private Integer port;
  private String username;
  private String password;
  private String directory;
  private String filepattern;
}

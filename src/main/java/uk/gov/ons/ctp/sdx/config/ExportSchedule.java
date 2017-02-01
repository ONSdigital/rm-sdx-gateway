package uk.gov.ons.ctp.sdx.config;

import lombok.Data;

/**
 * Config for ExportScheduler
 *
 */
@Data
public class ExportSchedule {
  private String cronExpression;

}

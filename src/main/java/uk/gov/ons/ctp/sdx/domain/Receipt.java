package uk.gov.ons.ctp.sdx.domain;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class Receipt {
  @NotNull
  @Min(1)
  private final Integer caseId;
}

package uk.gov.ons.ctp.sdx.representation;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotBlank;

/** The representation receipt object to acknowledge responses */
@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class ReceiptDTO {
  @NotBlank private String caseId;
  private String userId;
  private String caseRef;
}

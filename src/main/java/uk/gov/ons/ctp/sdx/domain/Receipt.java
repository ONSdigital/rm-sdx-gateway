package uk.gov.ons.ctp.sdx.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotBlank;
import uk.gov.ons.ctp.sdx.quarantine.casesvc.InboundChannel;

/** The domain receipt object to acknowledge responses */
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Data
public class Receipt {
  @NotBlank private String caseId;
  private String caseRef;
  private String userId;
  private InboundChannel inboundChannel;
}

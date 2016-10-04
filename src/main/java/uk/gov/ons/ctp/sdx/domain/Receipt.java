package uk.gov.ons.ctp.sdx.domain;

import lombok.*;
import org.hibernate.validator.constraints.NotBlank;
import uk.gov.ons.ctp.response.casesvc.message.feedback.InboundChannel;

/**
 * The domain receipt object to acknowledge responses
 */
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Data
public class Receipt {
  @NotBlank
  private String caseRef;
  private InboundChannel inboundChannel;
}

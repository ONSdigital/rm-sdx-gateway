package uk.gov.ons.ctp.sdx.domain;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Data
public class Receipt {
  private String caseRef;
}

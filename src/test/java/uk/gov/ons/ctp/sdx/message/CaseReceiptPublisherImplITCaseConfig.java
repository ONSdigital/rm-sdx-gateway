package uk.gov.ons.ctp.sdx.message;


import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.ImportResource;

@SpringBootConfiguration
@ImportResource(locations = { "classpath:CaseReceiptPublisherImplITCase-context.xml" })
public class CaseReceiptPublisherImplITCaseConfig {
}
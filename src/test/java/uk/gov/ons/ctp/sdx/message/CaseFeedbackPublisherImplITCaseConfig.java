package uk.gov.ons.ctp.sdx.message;


import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.ImportResource;

@SpringBootConfiguration
@ImportResource(locations = { "classpath:CaseFeedbackPublisherImplITCase-context.xml" })
public class CaseFeedbackPublisherImplITCaseConfig {
}

package uk.gov.ons.ctp.sdx.config;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CaseReceiptConfig {

    @Bean
    public Jackson2JsonMessageConverter CaseReceiptMarshaller(){
        return new Jackson2JsonMessageConverter();
    }
}

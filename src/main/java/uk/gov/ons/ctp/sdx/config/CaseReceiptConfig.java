package uk.gov.ons.ctp.sdx.config;

import org.springframework.amqp.support.converter.MarshallingMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

@Configuration
public class CaseReceiptConfig {

    @Bean
    public Jaxb2Marshaller CaseReceiptMarshaller(){
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath("uk.gov.ons.ctp.response.casesvc.message.feedback");
        return marshaller;
    }
    
    @Bean
    public MarshallingMessageConverter caseReceiptMarshallingMessageConverter(){
        MarshallingMessageConverter marshallingMessageConverter = new MarshallingMessageConverter();
        marshallingMessageConverter.setContentType("text/xml");
        marshallingMessageConverter.setMarshaller(CaseReceiptMarshaller());
        marshallingMessageConverter.setUnmarshaller(CaseReceiptMarshaller());
        return marshallingMessageConverter;
    }
}

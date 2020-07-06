package uk.gov.ons.ctp.sdx.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MarshallingMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

@Configuration
public class RabbitMQConfig {
  private String username;
  private String password;
  private String hostname;
  private int port;
  private String virtualHost;

  @Value("${messaging.pubMaxAttempts}") 
  private int pubMaxAttempts;

  public RabbitMQConfig (
        @Value("${rabbitmq.username}") String username,
        @Value("${rabbitmq.password}") String password,
        @Value("${rabbitmq.host}") String hostname,
        @Value("${rabbitmq.port}") int port,
        @Value("${rabbitmq.virtualhost}") String virtualHost) {

        this.username = username;
        this.password = password;
        this.hostname = hostname;
        this.port = port;
        this.virtualHost = virtualHost;
    }

    // Connection factories
    public static CachingConnectionFactory createConnectionFactory(int port, String hostname, String virtualHost,
        String password, String username) {
        CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory(hostname, port);

        cachingConnectionFactory.setVirtualHost(virtualHost);
        cachingConnectionFactory.setPassword(password);
        cachingConnectionFactory.setUsername(username);

        return cachingConnectionFactory;
    }
    
    @Bean
    @Primary
    public ConnectionFactory connectionFactory() {
        return createConnectionFactory(port, hostname, virtualHost, password, username);
    }

    // AMQP templates
    @Bean
    public RabbitTemplate amqpTemplate() {
        RabbitTemplate amqpTemplate = new RabbitTemplate(connectionFactory());
        amqpTemplate.setRetryTemplate(retryTemplate());
        return amqpTemplate;
    }

    @Bean
    public RabbitTemplate caseReceiptRabbitTemplate(ConnectionFactory connectionFactory, 
        MarshallingMessageConverter caseReceiptMarshallingMessageConverter) {
        RabbitTemplate caseReceiptRabbitTemplate = new RabbitTemplate(connectionFactory);
        caseReceiptRabbitTemplate.setExchange("case-outbound-exchange");
        caseReceiptRabbitTemplate.setRoutingKey("x-dead-letter-routing-key");
        caseReceiptRabbitTemplate.setMessageConverter(caseReceiptMarshallingMessageConverter);
        caseReceiptRabbitTemplate.setChannelTransacted(true);
        return caseReceiptRabbitTemplate;
    }

    // Retry templates
    @Bean
    public RetryTemplate retryTemplate() {
        RetryTemplate retryTemplate = new RetryTemplate();
    
        ExponentialBackOffPolicy exponentialBackOffPolicy = new ExponentialBackOffPolicy();
        exponentialBackOffPolicy.setInitialInterval(1000L);
        exponentialBackOffPolicy.setMultiplier(3D);
        exponentialBackOffPolicy.setMaxInterval(30000L);
        retryTemplate.setBackOffPolicy(exponentialBackOffPolicy);
        
        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        retryPolicy.setMaxAttempts(pubMaxAttempts);
        retryTemplate.setRetryPolicy(retryPolicy);
        return retryTemplate;
    }
    
    // Exchanges
    @Bean
    public DirectExchange caseOutboundExchange() {
        return new DirectExchange("case-outbound-exchange");
    }

    // Queues
    @Bean
    public Queue caseResponsesQueue() {
        return QueueBuilder.durable("Case.Responses").withArgument("x-dead-letter-exchange", "case-deadletter-exchange")
          .withArgument("x-dead-letter-routing-key", "Case.Responses.binding").build();
    }

    // Bindings
    @Bean
     public Binding xDeadLetterBinding(Queue caseResponsesQueue, DirectExchange caseOutboundExchange) {
    Binding binding = BindingBuilder.bind(caseResponsesQueue).to(caseOutboundExchange)
      .with("x-dead-letter-routing-key");
    return binding;
    }

    @Bean
     public Binding caseResponsesBinding(Queue caseResponsesQueue, DirectExchange caseOutboundExchange) {
    Binding binding = BindingBuilder.bind(caseResponsesQueue).to(caseOutboundExchange)
      .with("Case.Responses.binding");
    return binding;
    }
}

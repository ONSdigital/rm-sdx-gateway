package uk.gov.ons.ctp.sdx.config;

import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

@Configuration
public class RabbitMQConfig {
private final String username;
private final String password;
private final String hostname;
private final int port;
private final String virtualHost;

@Value("${messaging.pubMaxAttempts}") 
private int pubMaxAttempts;

    public RabbitMQConfig (
        @Value("${rabbitmq.username}") final  String username,
        @Value("${rabbitmq.password}") final  String password,
        @Value("${rabbitmq.host}") final  String hostname,
        @Value("${rabbitmq.port}") final int port,
        @Value("${rabbitmq.virtualhost}") final String virtualHost) {

        this.username = username;
        this.password = password;
        this.hostname = hostname;
        this.port = port;
        this.virtualHost = virtualHost;
    }

    // Connection factories
    public static CachingConnectionFactory createConnectionFactory(final int port, final String hostname, final String virtualHost,
        final String password, final String username) {
        final CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory(hostname, port);

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

    @Bean
    public RetryTemplate retryTemplate() {
        final RetryTemplate retryTemplate = new RetryTemplate();
         
        final ExponentialBackOffPolicy exponentialBackOffPolicy = new ExponentialBackOffPolicy();
        exponentialBackOffPolicy.setInitialInterval(1000L);
        exponentialBackOffPolicy.setMultiplier(3D);
        exponentialBackOffPolicy.setMaxInterval(30000L);
        retryTemplate.setBackOffPolicy(exponentialBackOffPolicy);
 
        final SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        retryPolicy.setMaxAttempts(pubMaxAttempts);
        retryTemplate.setRetryPolicy(retryPolicy);
         
        return retryTemplate;
    }
    
    // Exchanges
    @Bean
	DirectExchange caseOutboundExchange() {
		return new DirectExchange("case-outbound-exchange");
    }

    // Queues
    @Bean
	public Queue caseResponsesQueue() {
		return QueueBuilder.durable("Case.Responses").withArgument("x-dead-letter-exchange", "case-deadletter-exchange")
				.withArgument("x-dead-letter-routing-key", "Case.Responses").build();
    }

    // Bindings
    @Bean
    public Binding caseResponsesBinding(final Queue caseResponsesQueue, final DirectExchange caseOutboundExchange) {
    final Binding binding = BindingBuilder.bind(caseResponsesQueue).to(caseOutboundExchange)
        .with("x-dead-letter-routing-key");
    return binding;
    }
}

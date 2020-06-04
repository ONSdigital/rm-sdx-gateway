package uk.gov.ons.ctp.sdx.config;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.server.NetworkTrafficServerConnector;
import org.eclipse.jetty.server.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.jetty.JettyEmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.jetty.JettyServerCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class HttpServerConfig {

    @Value("${server.http.port}")
    private int httpPort;

    @Bean
    public EmbeddedServletContainerCustomizer containerCustomizer() {
        return container -> {
            if (container instanceof JettyEmbeddedServletContainerFactory) {
                JettyEmbeddedServletContainerFactory containerFactory =
                        (JettyEmbeddedServletContainerFactory) container;

                // Add customized Jetty configuration with non blocking connection handler
                containerFactory.addServerCustomizers(new JettyServerCustomizer() {
                    @Override
                    public void customize(final Server server) {
                            final NetworkTrafficServerConnector connector = new NetworkTrafficServerConnector(server);
                            log.info("Starting HTTP connector on port " + httpPort);
                            connector.setPort(httpPort);
                            server.addConnector(connector);
                    }
                });
            }
        };
    }
}
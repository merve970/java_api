package com.deneme.java_api.security;

import org.apache.catalina.connector.Connector;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TomcatConfig {

    @Bean
    public WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> connectorCustomizer() {
        return factory -> {
            if (factory instanceof TomcatServletWebServerFactory tomcat) {
                Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
                connector.setPort(8081);
                tomcat.addAdditionalTomcatConnectors(connector);
            }
        };
    }
}
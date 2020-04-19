package org.mendirl.service.console;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

import java.net.InetAddress;

@EnableAdminServer
@EnableDiscoveryClient
@SpringBootApplication
public class ConsoleApplication {

    private static final Logger log = LoggerFactory.getLogger(ConsoleApplication.class);

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(ConsoleApplication.class);

        ConfigurableApplicationContext context = app.run(args);

        Environment env = context.getEnvironment();

        String protocol = "http";
        if (env.getProperty("server.ssl.key-store") != null) {
            protocol = "https";
        }
        String hostAddress = "localhost";
        try {
            hostAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            log.warn("The host name could not be determined, using `localhost` as fallback");
        }
        log.info("\n----------------------------------------------------------\n\t" +
                "Application '{}' is running! Access URLs:\n\t" +
                "Local: \t\t{}://localhost:{}{}{}\n\t" +
                "External: \t{}://{}:{}{}{}\n\t" +
                "Profile(s): \t{}\n----------------------------------------------------------",
            env.getProperty("spring.application.name"),
            protocol,
            env.getProperty("server.port"),
            env.getProperty("server.servlet.context-path") != null ? env.getProperty("server.servlet.context-path") : "",
            env.getProperty("spring.boot.admin.context-path") != null ? env.getProperty("spring.boot.admin.context-path") : "",
            protocol,
            hostAddress,
            env.getProperty("server.port"),
            env.getProperty("server.servlet.context-path") != null ? env.getProperty("server.servlet.context-path") : "",
            env.getProperty("spring.boot.admin.context-path") != null ? env.getProperty("spring.boot.admin.context-path") : "",
            env.getActiveProfiles());
    }
}

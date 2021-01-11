package io.mendirl.spring.server.properties;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@Data
@RequiredArgsConstructor
@ConstructorBinding
@ConfigurationProperties(prefix = "eco2mix")
public class Eco2MixProperties {

    private final String url;
    private final Energie energie;
    private final Puissance puissance;

    @Data
    @RequiredArgsConstructor
    public static class Energie {
        private final String pathYear;
        private final String pathMonth;

    }

    @Data
    @RequiredArgsConstructor
    public static class Puissance {
        private final String pathday;
    }
}

package io.mendirl.spring.server.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConstructorBinding
@ConfigurationProperties(prefix = "eco2mix")
public class Eco2MixProperties {

    private final String url;

    private final String pathYear;
    private final String pathMonth;
    private final String pathDay;

    public Eco2MixProperties(String url, String pathYear, String pathMonth, String pathDay) {
        this.url = url;
        this.pathYear = pathYear;
        this.pathMonth = pathMonth;
        this.pathDay = pathDay;
    }


    public String getUrl() {
        return url;
    }

    public String getPathYear() {
        return pathYear;
    }

    public String getPathMonth() {
        return pathMonth;
    }

    public String getPathDay() {
        return pathDay;
    }

    @Override
    public String toString() {
        return "Eco2MixProperties{" +
            "url='" + url + '\'' +
            ", pathYear='" + pathYear + '\'' +
            ", pathMonth='" + pathMonth + '\'' +
            ", pathDay='" + pathDay + '\'' +
            '}';
    }
}

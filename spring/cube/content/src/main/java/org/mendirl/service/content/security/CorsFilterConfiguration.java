package org.mendirl.service.content.security;

import com.qfs.security.cfg.ICorsFilterConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import javax.servlet.Filter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

@Configuration
public class CorsFilterConfiguration implements ICorsFilterConfig {

    @Bean
    @Override
    public Filter corsFilter() {
        return new CorsFilter(corsConfigurationSource());
    }

    private CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource corsConfigurationSource = new UrlBasedCorsConfigurationSource();
        corsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration());
        return corsConfigurationSource;
    }

    private CorsConfiguration corsConfiguration() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowCredentials(true);

        getAllowedOrigins().forEach(corsConfiguration::addAllowedOrigin);
        getAllowedHeader().forEach(corsConfiguration::addAllowedHeader);
        getAllowedMethod().forEach(corsConfiguration::addAllowedMethod);
        getExposedHeader().forEach(corsConfiguration::addExposedHeader);

        return corsConfiguration;
    }

    @Override
    public Collection<String> getAllowedOrigins() {
        return Collections.singletonList(CorsConfiguration.ALL);
    }

    private Collection<String> getAllowedHeader() {
        return Arrays.asList(HttpHeaders.ORIGIN,
            HttpHeaders.ACCEPT,
            HttpHeaders.CONTENT_TYPE,
            HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD,
            HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS,
            HttpHeaders.AUTHORIZATION,
            "X-Requested-With",
            "X-ActiveUI-Version");
    }

    private Collection<HttpMethod> getAllowedMethod() {
        return Arrays.asList(HttpMethod.GET,
            HttpMethod.POST,
            HttpMethod.PUT,
            HttpMethod.DELETE,
            HttpMethod.OPTIONS,
            HttpMethod.HEAD);
    }

    private Collection<String> getExposedHeader() {
        return Collections.singletonList(HttpHeaders.LOCATION);
    }
}

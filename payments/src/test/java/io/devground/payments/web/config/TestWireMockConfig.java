package io.devground.payments.web.config;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.*;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import com.github.tomakehurst.wiremock.WireMockServer;

@TestConfiguration
public class TestWireMockConfig {

    @Bean(initMethod = "start", destroyMethod = "stop")
    public WireMockServer mockUserMicroService() {
        return new WireMockServer(options().port(8881));
    }

}

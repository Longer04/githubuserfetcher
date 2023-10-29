package com.example.githubuserfetcher.configuration;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.util.concurrent.TimeUnit;

@Configuration
public class WebClientConfig {

    @Value("${github.api.connect.timeout:5000}")
    private int connectTimeout;

    @Value("${github.api.read.timeout:10000}")
    private int readTimeout;

    @Value("${github.api.url:https://api.github.com}")
    private String githubApiUrl;

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl(githubApiUrl)
                .defaultHeader(HttpHeaders.USER_AGENT, "GithubUserFetcherApplication")
                .clientConnector(new ReactorClientHttpConnector(
                        HttpClient.create()
                                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeout)
                                .doOnConnected(conn -> conn
                                        .addHandlerLast(new ReadTimeoutHandler(readTimeout, TimeUnit.MILLISECONDS))
                                        .addHandlerLast(new WriteTimeoutHandler(readTimeout, TimeUnit.MILLISECONDS))
                                )
                ))
                .codecs(configurer -> configurer.defaultCodecs().jackson2JsonDecoder(
                        new Jackson2JsonDecoder(
                                new ObjectMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                        )
                ))
                .build();
    }
}

package com.silouks.infoly;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.util.MimeType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

@Configuration
public class ITunesApiConfig {

    @Value("${itunes.api.url}")
    private String iTunesApiUrl;

    @Bean
    public WebClient iTunesWebClient() {
        HttpClient httpClient = HttpClient.create();
        return WebClient.builder()
                .baseUrl(iTunesApiUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .codecs(clientDefaultCodecsConfigurer -> {
                    clientDefaultCodecsConfigurer.defaultCodecs().jackson2JsonEncoder(new Jackson2JsonEncoder(new ObjectMapper(), new MimeType("text", "javascript")));
                    clientDefaultCodecsConfigurer.defaultCodecs().jackson2JsonDecoder(new Jackson2JsonDecoder(new ObjectMapper(), new MimeType("text", "javascript")));
                })
                .build();
    }
}

package com.silouks.infoly.favorites.service;

import org.springframework.http.codec.HttpMessageWriter;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.http.server.reactive.MockServerHttpResponse;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.reactive.function.server.HandlerStrategies;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.reactive.result.view.ViewResolver;

import java.util.Collections;
import java.util.List;

// https://stackoverflow.com/questions/60740572/how-to-get-body-as-string-from-serverresponse-for-test/63164161#63164161
public class ServerResponseExtractor {

    public static String serverResponseAsString(ServerResponse serverResponse) {
        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/foo/foo"));

        DebugServerContext debugServerContext = new DebugServerContext();
        serverResponse.writeTo(exchange, debugServerContext).block();

        MockServerHttpResponse response = exchange.getResponse();
        return response.getBodyAsString().block();

    }

    private static class DebugServerContext implements ServerResponse.Context {
        @Override
        public List<HttpMessageWriter<?>> messageWriters() {
            return HandlerStrategies.withDefaults().messageWriters();
        }

        @Override
        public List<ViewResolver> viewResolvers() {
            return Collections.emptyList();
        }
    }

}

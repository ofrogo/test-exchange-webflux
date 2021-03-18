package com.shatalovd.testexchangewebflux.services;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.annotation.NonNull;


@Service
public class ExchangeService {

    private final WebClient webClient = WebClient.create("https://api.exchangeratesapi.io/");

    public Mono<String> getCurrencyRate(@NonNull String currency, @NonNull String date) {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/" + date)
                        .queryParam("symbols", currency)
                        .build()
                )
                .retrieve()
                .bodyToMono(String.class);
    }

    public Mono<String> getCurrencyRate(@NonNull String currency, @NonNull String startAt, @NonNull String endAt) {
        return webClient
                .get()
                .uri( uriBuilder -> uriBuilder
                        .path("/history")
                        .queryParam("start_at", startAt)
                        .queryParam("end_at", endAt)
                        .queryParam("symbols", currency)
                        .build()
                )
                .retrieve()
                .bodyToMono(String.class);
    }


}

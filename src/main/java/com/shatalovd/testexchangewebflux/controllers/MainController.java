package com.shatalovd.testexchangewebflux.controllers;

import com.shatalovd.testexchangewebflux.services.DispersionService;
import com.shatalovd.testexchangewebflux.services.ExchangeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RestController
public class MainController {

    private final ExchangeService exchangeService;
    private final DispersionService dispersionService;

    MainController(ExchangeService exchangeService, DispersionService dispersionService) {
        this.exchangeService = exchangeService;
        this.dispersionService = dispersionService;
    }

    @GetMapping(path = "/dispersion/{date:^(?:[0-9]{4})-(?:1[0-2]|0[1-9])-(?:3[01]|0[1-9]|[12][0-9])$}")
    public Mono<Double> dispersion(@PathVariable String date) {
        return exchangeService.getCurrencyRate("USD", date, LocalDate.now().format(DateTimeFormatter.ISO_DATE))
                .flatMap(s -> {
                    List<Double> result = new ArrayList<>();

                    int lastIndex = 0;
                    while (true) {
                        int firstIndex = s.indexOf("\"USD\":", lastIndex);
                        if (firstIndex > 0) {
                            int secondIndex = s.indexOf("}", firstIndex);
                            if (secondIndex > 0) {
                                result.add(Double.parseDouble(s.substring(firstIndex + 7, secondIndex)));
                                lastIndex = secondIndex;
                            } else {
                                break;
                            }
                        } else {
                            break;
                        }
                    }
                    return Mono.just(result).publishOn(Schedulers.parallel());
                })
                .flatMap(doubles -> Mono.just(dispersionService.getDispersion(doubles)).publishOn(Schedulers.parallel()))
                .subscribeOn(Schedulers.parallel());
    }

    @GetMapping(path = "/rate/{date:^(?:[0-9]{4})-(?:1[0-2]|0[1-9])-(?:3[01]|0[1-9]|[12][0-9])$}")
    public Mono<String> rate(@PathVariable String date) {
        return exchangeService.getCurrencyRate("USD", date);
    }

}

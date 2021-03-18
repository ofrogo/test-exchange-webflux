package com.shatalovd.testexchangewebflux.services;

import org.springframework.stereotype.Service;
import reactor.util.annotation.NonNull;

import java.util.List;

@Service
public class DispersionService {

    public Double getDispersion(@NonNull List<Double> values) {
        Double mo = values.stream().reduce(Double::sum).get() / values.size();
        return values.stream()
                .map(xi -> (xi - mo) * (xi - mo))
                .reduce(Double::sum)
                .get() / values.size();
    }
}

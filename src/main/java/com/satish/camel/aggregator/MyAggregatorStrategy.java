package com.satish.camel.aggregator;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@Slf4j
public class MyAggregatorStrategy implements AggregationStrategy {

    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        if (Objects.isNull(oldExchange)) {
            return newExchange;
        }
        String oldBody = oldExchange.getIn().getBody(String.class);
        String newBody = newExchange.getIn().getBody(String.class);

        String body = oldBody.concat(" ").concat(newBody);
        oldExchange.getIn().setBody(body);
        return oldExchange;
    }
}


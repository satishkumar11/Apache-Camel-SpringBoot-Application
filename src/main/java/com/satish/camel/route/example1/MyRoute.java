package com.satish.camel.route.example1;

import com.satish.camel.aggregator.MyAggregatorStrategy;
import org.apache.camel.Message;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Random;

@Component
public class MyRoute extends RouteBuilder {

    private static final String CORRELATION_ID = "correlationId";

    @Autowired
    private MyAggregatorStrategy myAggregatorStrategy;

    @Override
    public void configure() throws Exception {

        from("timer:example-timer-test?period=10000")
                .process(exchange -> {
                    Message message = exchange.getMessage();
                    message.setHeader(CORRELATION_ID, new Random().nextInt(5));
                    message.setBody(new Date().toString());
                })
                .log("correlationId : " + "${header." + CORRELATION_ID + "}" + ", body : " + "${body}")
                .aggregate(header(CORRELATION_ID), myAggregatorStrategy)
                .completionSize(5)
                .log("---------x------------x---------x----------x")
                .log("correlationId : " + "${header." + CORRELATION_ID + "}" + ", aggregated body : " + "${body}")
                .log("---------x------------x---------x----------x");
    }
}
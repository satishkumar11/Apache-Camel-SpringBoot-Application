package com.satish.camel.route.example2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.satish.camel.aggregator.MyAggregatorStrategy;
import com.satish.camel.dto.Order;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.support.DefaultMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class RabbitMQRoute extends RouteBuilder {

    private static final String CORRELATION_ID = "correlationId";

    @Autowired
    private MyAggregatorStrategy myAggregatorStrategy;

    @Override
    public void configure() throws Exception {
        from("rabbitmq:amq.direct?queue=order&routingKey=order&autoDelete=false")
                .unmarshal().json(JsonLibrary.Jackson, Order.class)
                .process(this::deserializeObject)
                .marshal().json(JsonLibrary.Jackson, Order.class)
                .to("rabbitmq:amq.direct?queue=order-history&routingKey=order-history&autoDelete=false")
                .process(exchange -> {
                    String obj = exchange.getIn().getBody(String.class);
                    ObjectMapper mapper = new ObjectMapper();
                    Map<Integer, String> map = mapper.readValue(obj, Map.class);
                    Message message = exchange.getMessage();
                    message.setHeader(CORRELATION_ID, map.get("customerName"));
                })
                .log("correlationId : " + "${header." + CORRELATION_ID + "}" + ", body : " + "${body}")
                .aggregate(header(CORRELATION_ID), myAggregatorStrategy)
                .completionSize(5)
                .completionInterval(10000)
                .process(exchange -> {
                    Message message = exchange.getMessage();
                    log.info("agg message : "+message.getBody());
                })
                .to("file:///Users/abc/Downloads/camel/src/main/resources/?fileName=orders.txt&fileExist=Append")
                .log("---------x------------x---------x----------x")
                .log("correlationId : " + "${header." + CORRELATION_ID + "}" + ", aggregated body : " + "${body}")
                .log("---------x------------x---------x----------x");

    }

    private void deserializeObject(Exchange exchange) {
        Order dto = exchange.getMessage().getBody(Order.class);
        Message message = new DefaultMessage(exchange);
        message.setBody(dto);
        exchange.setMessage(message);
    }
}

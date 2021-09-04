package com.satish.camel.route.example1;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class MoveFile extends RouteBuilder {
    @Override
    public void configure() throws Exception {

//        from("file:/Users/abc/Desktop/CamelExample/input-folder")
//                .to("file:/Users/abc/Desktop/CamelExample/output-folder");

//        from("file:input-folder").to("file:output-folder");
//        from("file:input-folder?noop=true").to("file:output-folder");
        from("file:input-folder?delete=true").to("file:output-folder");

    }
}

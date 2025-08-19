package com.som.orderengine.web;

import com.som.orderengine.domain.Order;
import com.som.orderengine.events.BaseEvent;
import com.som.orderengine.ingest.EventParser;
import com.som.orderengine.processing.EventProcessor;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/events")
public class EventController {

    private final EventParser parser;
    private final EventProcessor processor;

    public EventController(EventParser parser, EventProcessor processor) {
        this.parser = parser;
        this.processor = processor;
    }

   
    @PostMapping
    public ResponseEntity<?> ingestEvent(@RequestBody JsonNode rawJson) {
        Optional<BaseEvent> evtOpt = parser.parseLine(rawJson.toString());
        if (evtOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Unknown or invalid event type");
        }

        BaseEvent event = evtOpt.get();
        processor.process(event);

     
        if (event instanceof com.som.orderengine.events.OrderCreatedEvent oce) {
            return ResponseEntity.ok(processor.getOrder(oce.getOrderId()));
        } else if (event instanceof com.som.orderengine.events.PaymentReceivedEvent pre) {
            return ResponseEntity.ok(processor.getOrder(pre.getOrderId()));
        } else if (event instanceof com.som.orderengine.events.ShippingScheduledEvent sse) {
            return ResponseEntity.ok(processor.getOrder(sse.getOrderId()));
        } else if (event instanceof com.som.orderengine.events.OrderCancelledEvent oce2) {
            return ResponseEntity.ok(processor.getOrder(oce2.getOrderId()));
        }

        return ResponseEntity.ok("Event processed");
    }

    
    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrder(@PathVariable String orderId) {
        Order order = processor.getOrder(orderId);
        if (order == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(order);
    }

    
    @GetMapping
    public ResponseEntity<?> listOrders() {
        return ResponseEntity.ok(processor.getAllOrders());
    }
}
